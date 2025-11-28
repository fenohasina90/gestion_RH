<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">Tableau de bord RH</h1>
      <div class="filters">
        <div class="field">
          <label>Mois</label>
          <select v-model.number="mois" class="form-input">
            <option v-for="(m,i) in moisOptions" :key="i" :value="i+1">{{ m }}</option>
          </select>
        </div>
        <div class="field">
          <label>Année</label>
          <input v-model.number="annee" type="number" class="form-input" />
        </div>
        <div class="actions">
          <button class="btn btn-primary" @click="loadData">Actualiser</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="status">Chargement des indicateurs...</div>
    <div v-else>
      <div class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-label">Effectif total</div>
          <div class="kpi-value">{{ data.totalEmployes || 0 }}</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Ancienneté moyenne</div>
          <div class="kpi-value">{{ fmtNum(data.ancienneteMoyenne) }} ans</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Turnover ({{ annee }})</div>
          <div class="kpi-sub">Départs: {{ data.turnover?.departures || 0 }}</div>
          <div class="kpi-value">{{ fmtNum(data.turnover?.taux || 0) }} %</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Heures d'absence ({{ moisLabel(mois) }} {{ annee }})</div>
          <div class="kpi-value">{{ fmtNum(data.absenteisme?.heuresAbsence || 0) }} h</div>
        </div>
      </div>

      <div class="charts-grid">
        <div class="chart-card">
          <div class="chart-title">Effectif par département</div>
          <canvas ref="deptChart"></canvas>
        </div>
        <div class="chart-card">
          <div class="chart-title">Effectif par catégorie</div>
          <canvas ref="catChart"></canvas>
        </div>
      </div>

      <div class="tables-grid">
        <div class="table-card">
          <div class="table-title">Alertes fin de contrat (30 jours)</div>
          <table class="table">
            <thead>
              <tr>
                <th>Matricule</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Type contrat</th>
                <th>Date fin</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!data.alertesFinContrat || data.alertesFinContrat.length === 0">
                <td colspan="5">Aucune alerte</td>
              </tr>
              <tr v-for="(a,i) in data.alertesFinContrat" :key="i">
                <td>{{ a.matricule }}</td>
                <td>{{ a.nom }}</td>
                <td>{{ a.prenom }}</td>
                <td>{{ a.typecontrat }}</td>
                <td>{{ formatDate(a.datefin) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="table-card">
          <div class="table-title">Congés non pris ({{ annee }})</div>
          <table class="table">
            <thead>
              <tr>
                <th>Matricule</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Type congé</th>
                <th>Jours restants</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!data.alertesCongesNonPris || data.alertesCongesNonPris.length === 0">
                <td colspan="5">Aucun solde à consommer</td>
              </tr>
              <tr v-for="(c,i) in data.alertesCongesNonPris" :key="i">
                <td>{{ c.matricule }}</td>
                <td>{{ c.nom }}</td>
                <td>{{ c.prenom }}</td>
                <td>{{ c.typeconge }}</td>
                <td class="right">{{ fmtNum(c.joursrestants) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RhDashboardView',
  data(){
    const now = new Date()
    return {
      mois: now.getMonth() + 1,
      annee: now.getFullYear(),
      moisOptions: ['Jan','Fév','Mar','Avr','Mai','Juin','Juil','Aoû','Sep','Oct','Nov','Déc'],
      loading: false,
      data: {},
      chartJsLoaded: false,
      deptChartInstance: null,
      catChartInstance: null
    }
  },
  methods: {
    moisLabel(m){ return this.moisOptions[m-1] || m },
    fmtNum(v){ const n = Number(v||0); return n.toLocaleString('fr-FR', { maximumFractionDigits: 2 }) },
    formatDate(v){ if (!v) return ''; try { return new Date(v).toLocaleDateString('fr-FR') } catch(e){ return v } },
    async ensureChartJs(){
      if (window.Chart) return
      await new Promise((resolve, reject) => {
        const s = document.createElement('script')
        s.src = 'https://cdn.jsdelivr.net/npm/chart.js'
        s.onload = resolve
        s.onerror = reject
        document.body.appendChild(s)
      })
    },
    async loadData(){
      this.loading = true
      try {
        const res = await fetch(`/api/dashboard/rh?mois=${this.mois}&annee=${this.annee}`)
        const json = await res.json()
        this.data = json
        await this.renderCharts()
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    destroyCharts(){
      if (this.deptChartInstance) { this.deptChartInstance.destroy(); this.deptChartInstance = null }
      if (this.catChartInstance) { this.catChartInstance.destroy(); this.catChartInstance = null }
    },
    async renderCharts(){
      if (!this.data) return
      await this.ensureChartJs()
      this.destroyCharts()

      const deptCtx = this.$refs.deptChart?.getContext('2d')
      const catCtx = this.$refs.catChart?.getContext('2d')

      if (deptCtx && this.data.effectifParDepartement) {
        const labels = this.data.effectifParDepartement.map(d => d.label)
        const values = this.data.effectifParDepartement.map(d => d.value)
        this.deptChartInstance = new window.Chart(deptCtx, {
          type: 'bar',
          data: {
            labels,
            datasets: [{
              label: 'Effectif',
              data: values,
              backgroundColor: '#10b981'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            layout: { padding: 10 },
            scales: {
              x: { ticks: { autoSkip: false } },
              y: { beginAtZero: true, ticks: { precision: 0 } }
            }
          }
        })
      }

      if (catCtx && this.data.effectifParCategorie) {
        const labels = this.data.effectifParCategorie.map(d => d.label)
        const values = this.data.effectifParCategorie.map(d => d.value)
        this.catChartInstance = new window.Chart(catCtx, {
          type: 'pie',
          data: {
            labels,
            datasets: [{
              data: values,
              backgroundColor: ['#10b981','#3b82f6','#6366f1','#f59e0b','#ef4444','#8b5cf6']
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            layout: { padding: 10 },
            plugins: { legend: { position: 'bottom' } }
          }
        })
      }
    }
  },
  mounted(){
    this.loadData()
  },
  beforeUnmount(){
    this.destroyCharts()
  }
}
</script>

<style scoped>
.dashboard { padding: 16px; }
.page-header { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:16px; }
.page-title { font-size:22px; font-weight:700; }
.filters { display:flex; gap:10px; flex-wrap:wrap; align-items:flex-end; }
.field { display:flex; flex-direction:column; min-width:150px; }
.actions { display:flex; gap:8px; }
.status { margin-top: 12px; }

.kpi-grid { display:grid; grid-template-columns: repeat(4, minmax(0,1fr)); gap:12px; margin-bottom:16px; }
.kpi-card { border:1px solid #e5e7eb; border-radius:8px; padding:10px 12px; background:#fff; }
.kpi-label { font-size:13px; color:#6b7280; margin-bottom:4px; }
.kpi-sub { font-size:12px; color:#6b7280; }
.kpi-value { font-size:20px; font-weight:700; color:#111827; }

.charts-grid { display:grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap:16px; margin-bottom:16px; }
.chart-card { border:1px solid #e5e7eb; border-radius:8px; padding:10px 12px; background:#fff; }
.chart-title { font-weight:600; margin-bottom:8px; }
.chart-card canvas { width:100% !important; height:260px !important; border:1px solid #e5e7eb; }

.tables-grid { display:grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap:16px; }
.table-card { border:1px solid #e5e7eb; border-radius:8px; background:#fff; overflow:hidden; }
.table-title { background:#f9fafb; padding:8px 12px; font-weight:600; border-bottom:1px solid #e5e7eb; }
.table { width:100%; border-collapse:collapse; font-size:13px; }
.table th, .table td { padding:6px 8px; border-bottom:1px solid #e5e7eb; }
.table thead th { background:#f9fafb; }
.right { text-align:right; }

@media (max-width: 1024px) {
  .kpi-grid { grid-template-columns: repeat(2, minmax(0,1fr)); }
  .charts-grid { grid-template-columns: 1fr; }
  .tables-grid { grid-template-columns: 1fr; }
}

@media (max-width: 640px) {
  .kpi-grid { grid-template-columns: 1fr; }
}
</style>
