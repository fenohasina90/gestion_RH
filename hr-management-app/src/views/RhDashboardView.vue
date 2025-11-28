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
      <div class="tabs">
        <button
          class="tab-link"
          :class="{ 'tab-link-active': activeTab === 'graph' }"
          @click="setTab('graph')"
        >
          Statistiques graphiques
        </button>
        <button
          class="tab-link"
          :class="{ 'tab-link-active': activeTab === 'other' }"
          @click="setTab('other')"
        >
          Autres indicateurs
        </button>
      </div>
    </div>

    <div v-if="loading" class="status">Chargement des indicateurs...</div>
    <div v-else>
      <div v-if="activeTab === 'graph'">
        <div class="charts-grid charts-grid-visual">
          <div class="chart-card">
            <div class="chart-title">Répartition par genre</div>
            <canvas ref="genreChart"></canvas>
          </div>
          <div class="chart-card">
            <div class="chart-title">Répartition par âge</div>
            <canvas ref="ageChart"></canvas>
          </div>
          <div class="chart-card">
            <div class="chart-title">Effectif par type de contrat</div>
            <canvas ref="contratChart"></canvas>
          </div>
        </div>
      </div>

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
            <div class="stat-list">
              <div v-if="!data.effectifParDepartement || data.effectifParDepartement.length === 0" class="stat-empty">
                Aucune donnée
              </div>
              <div v-for="(d,i) in data.effectifParDepartement" :key="i" class="stat-row">
                <div class="stat-label">{{ d.label }}</div>
                <div class="stat-value">{{ d.value }}</div>
              </div>
            </div>
          </div>
          <div class="chart-card">
            <div class="chart-title">Effectif par catégorie</div>
            <div class="stat-list">
              <div v-if="!data.effectifParCategorie || data.effectifParCategorie.length === 0" class="stat-empty">
                Aucune donnée
              </div>
              <div v-for="(c,i) in data.effectifParCategorie" :key="i" class="stat-row">
                <div class="stat-label">{{ c.label }}</div>
                <div class="stat-value">{{ c.value }}</div>
              </div>
            </div>
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
      activeTab: 'graph',
      chartJsLoaded: false,
      genreChartInstance: null,
      ageChartInstance: null,
      contratChartInstance: null
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
    setTab(tab){
      this.activeTab = tab
      if (tab === 'graph' && this.data && Object.keys(this.data).length){
        this.renderCharts()
      }
    },
    destroyCharts(){
      if (this.genreChartInstance) { this.genreChartInstance.destroy(); this.genreChartInstance = null }
      if (this.ageChartInstance) { this.ageChartInstance.destroy(); this.ageChartInstance = null }
      if (this.contratChartInstance) { this.contratChartInstance.destroy(); this.contratChartInstance = null }
    },
    async renderCharts(){
      if (!this.data) return
      await this.ensureChartJs()
      this.destroyCharts()

      const genreCtx = this.$refs.genreChart?.getContext('2d')
      const ageCtx = this.$refs.ageChart?.getContext('2d')
      const contratCtx = this.$refs.contratChart?.getContext('2d')

      // Genre
      if (genreCtx && this.data.effectifParGenre) {
        const labels = this.data.effectifParGenre.map(d => d.label)
        const values = this.data.effectifParGenre.map(d => d.value)
        const colors = ['#3b82f6','#ec4899','#9ca3af']
        this.genreChartInstance = new window.Chart(genreCtx, {
          type: 'doughnut',
          data: {
            labels,
            datasets: [{
              data: values,
              backgroundColor: colors,
              borderColor: '#ffffff',
              borderWidth: 1
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

      // Âge
      if (ageCtx && this.data.effectifParAge) {
        const labels = this.data.effectifParAge.map(d => d.label)
        const values = this.data.effectifParAge.map(d => d.value)
        this.ageChartInstance = new window.Chart(ageCtx, {
          type: 'bar',
          data: {
            labels,
            datasets: [{
              label: 'Effectif',
              data: values,
              backgroundColor: 'rgba(99, 102, 241, 0.8)',
              borderColor: '#4f46e5',
              borderWidth: 1
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

      // Type de contrat (contrats en cours)
      if (contratCtx && this.data.effectifParTypeContrat) {
        const labels = this.data.effectifParTypeContrat.map(d => d.label)
        const values = this.data.effectifParTypeContrat.map(d => d.value)
        const colors = ['#10b981','#f59e0b','#6366f1','#ef4444']
        this.contratChartInstance = new window.Chart(contratCtx, {
          type: 'pie',
          data: {
            labels,
            datasets: [{
              data: values,
              backgroundColor: colors,
              borderColor: '#ffffff',
              borderWidth: 1
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

.tabs { margin-top: 12px; display:flex; gap:8px; flex-wrap:wrap; }
.tab-link { padding:6px 10px; font-size:13px; border-radius:999px; border:1px solid #e5e7eb; background:#f9fafb; cursor:pointer; color:#4b5563; }
.tab-link-active { background:#111827; color:#f9fafb; border-color:#111827; }

.kpi-grid { display:grid; grid-template-columns: repeat(4, minmax(0,1fr)); gap:12px; margin-bottom:16px; }
.kpi-card { border:1px solid #e5e7eb; border-radius:8px; padding:10px 12px; background:#fff; }
.kpi-label { font-size:13px; color:#6b7280; margin-bottom:4px; }
.kpi-sub { font-size:12px; color:#6b7280; }
.kpi-value { font-size:20px; font-weight:700; color:#111827; }

.charts-grid { display:grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap:16px; margin-bottom:16px; }
.chart-card { border:1px solid #e5e7eb; border-radius:8px; padding:10px 12px; background:#fff; }
.chart-title { font-weight:600; margin-bottom:8px; }
.chart-card canvas { width:100% !important; height:260px !important; border:1px solid #e5e7eb; background:#f9fafb; }

.stat-list { display:flex; flex-direction:column; gap:6px; margin-top:4px; }
.stat-row { display:flex; justify-content:space-between; align-items:center; padding:6px 8px; border-radius:6px; background:#f9fafb; font-size:13px; }
.stat-row:nth-child(odd) { background:#f3f4f6; }
.stat-label { color:#374151; font-weight:500; }
.stat-value { min-width:32px; text-align:right; padding:2px 8px; border-radius:999px; background:#dcfce7; color:#166534; font-weight:600; }
.stat-empty { padding:6px 8px; font-size:13px; color:#6b7280; font-style:italic; }

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
