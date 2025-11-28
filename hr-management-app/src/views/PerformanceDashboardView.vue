<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">Performance des employés</h1>
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
        <div class="field">
          <label>Département</label>
          <select v-model="selectedDept" class="form-input">
            <option value="ALL">Tous les départements</option>
            <option v-for="d in deptOptions" :key="d" :value="d">{{ d }}</option>
          </select>
        </div>
        <div class="actions">
          <button class="btn btn-primary" @click="reload">Actualiser</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="status">Chargement des performances...</div>
    <div v-else>
      <!-- Synthèse décisionnelle -->
      <div class="summary-grid">
        <div class="summary-card">
          <div class="summary-label">Score global moyen</div>
          <div class="summary-value">{{ fmtNum(stats.globalMoyen) }} / 100</div>
          <div class="summary-sub">Tous employés ({{ employees.length }})</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Employés à fort potentiel</div>
          <div class="summary-value highlight">{{ stats.nbPromotionPotentielle }}</div>
          <div class="summary-sub">Score &gt;= 85 (promotion / responsabilités)</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Besoin de formation</div>
          <div class="summary-value warn">{{ stats.nbFormation }}</div>
          <div class="summary-sub">Ponctualité &lt; 70 ou productivité &lt; 70</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Cas à surveiller (sanctions)</div>
          <div class="summary-value danger">{{ stats.nbSanction }}</div>
          <div class="summary-sub">Score global &lt; 50</div>
        </div>
      </div>

      <!-- Courbes d'évolution des performances par département -->
      <div class="charts-grid">
        <div class="chart-card">
          <div class="chart-title">Évolution du score global moyen par département ({{ historyLabel }})</div>
          <canvas ref="deptHistoryChart"></canvas>
        </div>
      </div>

      <div class="content-grid">
        <!-- Classement des employés -->
        <div class="table-card">
          <div class="table-title">Classement des employés (score global)</div>
          <table class="table">
            <thead>
              <tr>
                <th>#</th>
                <th>Matricule</th>
                <th>Nom</th>
                <th>Département</th>
                <th class="right">Ponctualité</th>
                <th class="right">Productivité</th>
                <th class="right">Score global</th>
                <th>Synthèse</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="sortedEmployees.length === 0">
                <td colspan="8">Aucune donnée de performance pour cette période.</td>
              </tr>
              <tr v-for="(e, idx) in sortedEmployees" :key="e.id || idx">
                <td>{{ idx + 1 }}</td>
                <td>{{ e.matricule }}</td>
                <td>{{ e.nom }} {{ e.prenom }}</td>
                <td>{{ e.departement }}</td>
                <td class="right">{{ fmtNum(e.scorePonctualite) }}</td>
                <td class="right">{{ fmtNum(e.scoreProductivite) }}</td>
                <td class="right">{{ fmtNum(e.scoreGlobal) }}</td>
                <td>
                  <span v-if="e.scoreGlobal >= 85" class="badge green">Promotion</span>
                  <span v-else-if="e.scorePonctualite < 70 || e.scoreProductivite < 70" class="badge orange">Formation</span>
                  <span v-else-if="e.scoreGlobal < 50" class="badge red">Sanction</span>
                  <span v-else class="badge neutral">Stable</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Synthèse par département -->
        <div class="table-card">
          <div class="table-title">Synthèse par département</div>
          <table class="table">
            <thead>
              <tr>
                <th>Département</th>
                <th class="right">Nb employés</th>
                <th class="right">Ponctualité moy.</th>
                <th class="right">Productivité moy.</th>
                <th class="right">Score global moy.</th>
                <th>Alertes</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredDepartments.length === 0">
                <td colspan="6">Aucune donnée agrégée pour cette période.</td>
              </tr>
              <tr v-for="(d, idx) in filteredDepartments" :key="idx">
                <td>{{ d.departement }}</td>
                <td class="right">{{ d.nbEmployes }}</td>
                <td class="right">{{ fmtNum(d.scorePonctualiteMoyen) }}</td>
                <td class="right">{{ fmtNum(d.scoreProductiviteMoyen) }}</td>
                <td class="right">{{ fmtNum(d.scoreGlobalMoyen) }}</td>
                <td>
                  <span v-if="d.scoreGlobalMoyen >= 85" class="badge green">Excellence</span>
                  <span v-else-if="d.scoreGlobalMoyen < 60" class="badge red">À risque</span>
                  <span v-else class="badge neutral">Correct</span>
                </td>
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
  name: 'PerformanceDashboardView',
  data(){
    const now = new Date()
    return {
      mois: now.getMonth() + 1,
      annee: now.getFullYear(),
      moisOptions: ['Jan','Fév','Mar','Avr','Mai','Juin','Juil','Aoû','Sep','Oct','Nov','Déc'],
      loading: false,
      employees: [],
      departments: [],
      history: [],
      selectedDept: 'ALL',
      deptHistoryChartInstance: null
    }
  },
  computed: {
    deptOptions(){
      const set = new Set()
      for (const e of this.employees){
        if (e.departement) set.add(e.departement)
      }
      return Array.from(set).sort()
    },
    filteredEmployees(){
      if (this.selectedDept === 'ALL') return this.employees
      return this.employees.filter(e => e.departement === this.selectedDept)
    },
    sortedEmployees(){
      return [...this.filteredEmployees].sort((a,b) => (b.scoreGlobal || 0) - (a.scoreGlobal || 0))
    },
    stats(){
      const n = this.filteredEmployees.length
      if (!n) return { globalMoyen: 0, nbPromotionPotentielle: 0, nbFormation: 0, nbSanction: 0 }
      let sum = 0, promo = 0, form = 0, sanc = 0
      for (const e of this.filteredEmployees){
        const g = Number(e.scoreGlobal || 0)
        const p = Number(e.scorePonctualite || 0)
        const prod = Number(e.scoreProductivite || 0)
        sum += g
        if (g >= 85) promo++
        if (p < 70 || prod < 70) form++
        if (g < 50) sanc++
      }
      return {
        globalMoyen: sum / n,
        nbPromotionPotentielle: promo,
        nbFormation: form,
        nbSanction: sanc
      }
    },
    filteredDepartments(){
      if (this.selectedDept === 'ALL') return this.departments
      return this.departments.filter(d => d.departement === this.selectedDept)
    },
    historyLabel(){
      if (!this.history.length) return ''
      const first = this.history[0]
      const last = this.history[this.history.length - 1]
      return `${first.label} → ${last.label}`
    }
  },
  methods: {
    fmtNum(v){ const n = Number(v||0); return n.toLocaleString('fr-FR', { maximumFractionDigits: 1 }) },
    async reload(){
      await this.loadData()
      await this.loadHistory()
    },
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
        const [empRes, deptRes] = await Promise.all([
          fetch(`/api/dashboard/performance/employes?mois=${this.mois}&annee=${this.annee}`),
          fetch(`/api/dashboard/performance/departements?mois=${this.mois}&annee=${this.annee}`)
        ])
        this.employees = await empRes.json()
        this.departments = await deptRes.json()
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    async loadHistory(){
      // Charger l'évolution des scores globaux moyens par département sur les 6 derniers mois
      const points = []
      const base = new Date(this.annee, this.mois - 1, 1)
      for (let i = 5; i >= 0; i--){
        const d = new Date(base)
        d.setMonth(d.getMonth() - i)
        const m = d.getMonth() + 1
        const y = d.getFullYear()
        try {
          const res = await fetch(`/api/dashboard/performance/departements?mois=${m}&annee=${y}`)
          const depts = await res.json()
          points.push({
            label: `${this.moisOptions[m-1]} ${y}`,
            data: depts
          })
        } catch (e) {
          console.error(e)
        }
      }
      this.history = points
      await this.renderDeptHistoryChart()
    },
    destroyCharts(){
      if (this.deptHistoryChartInstance){ this.deptHistoryChartInstance.destroy(); this.deptHistoryChartInstance = null }
    },
    async renderDeptHistoryChart(){
      if (!this.history.length) return
      await this.ensureChartJs()
      this.destroyCharts()

      const ctx = this.$refs.deptHistoryChart?.getContext('2d')
      if (!ctx) return

      // Récupérer la liste unique des départements sur toute l'historique
      const deptSet = new Set()
      for (const p of this.history){
        for (const d of p.data || []){
          if (d.departement) deptSet.add(d.departement)
        }
      }
      let departments = Array.from(deptSet)
      if (this.selectedDept !== 'ALL'){
        departments = departments.filter(d => d === this.selectedDept)
      }
      const labels = this.history.map(p => p.label)

      const colors = ['#10b981','#3b82f6','#f59e0b','#6366f1','#ef4444','#8b5cf6','#14b8a6','#ec4899']

      const datasets = departments.map((dept, idx) => {
        const color = colors[idx % colors.length]
        const data = this.history.map(p => {
          const found = (p.data || []).find(d => d.departement === dept)
          return found ? Number(found.scoreGlobalMoyen || 0) : 0
        })
        return {
          label: dept,
          data,
          borderColor: color,
          backgroundColor: color,
          tension: 0.3
        }
      })

      this.deptHistoryChartInstance = new window.Chart(ctx, {
        type: 'line',
        data: {
          labels,
          datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { position: 'bottom' }
          },
          scales: {
            y: { beginAtZero: true, max: 100 }
          }
        }
      })
    }
  },
  async mounted(){
    await this.loadData()
    await this.loadHistory()
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

.summary-grid { display:grid; grid-template-columns: repeat(4, minmax(0,1fr)); gap:12px; margin-bottom:16px; }
.summary-card { border:1px solid #e5e7eb; border-radius:8px; padding:10px 12px; background:#fff; }
.summary-label { font-size:13px; color:#6b7280; margin-bottom:4px; }
.summary-value { font-size:20px; font-weight:700; color:#111827; }
.summary-sub { font-size:12px; color:#6b7280; margin-top:4px; }
.summary-value.highlight { color:#166534; }
.summary-value.warn { color:#b45309; }
.summary-value.danger { color:#b91c1c; }

.charts-grid { display:grid; grid-template-columns: minmax(0,1fr); gap:16px; margin-bottom:16px; }
.chart-card { border:1px solid #e5e7eb; border-radius:8px; padding:10px 12px; background:#fff; }
.chart-title { font-weight:600; margin-bottom:8px; }
.chart-card canvas { width:100% !important; height:260px !important; border:1px solid #e5e7eb; background:#f9fafb; }

.content-grid { display:grid; grid-template-columns: 2fr 1.5fr; gap:16px; }
.table-card { border:1px solid #e5e7eb; border-radius:8px; background:#fff; overflow:hidden; }
.table-title { background:#f9fafb; padding:8px 12px; font-weight:600; border-bottom:1px solid #e5e7eb; }
.table { width:100%; border-collapse:collapse; font-size:13px; }
.table th, .table td { padding:6px 8px; border-bottom:1px solid #e5e7eb; }
.table thead th { background:#f9fafb; }
.right { text-align:right; }

.badge { font-size:11px; font-weight:600; padding:2px 8px; border-radius:999px; display:inline-block; }
.badge.green { background:#dcfce7; color:#166534; }
.badge.orange { background:#ffedd5; color:#c05621; }
.badge.red { background:#fee2e2; color:#b91c1c; }
.badge.neutral { background:#e5e7eb; color:#374151; }

@media (max-width: 1024px) {
  .summary-grid { grid-template-columns: repeat(2, minmax(0,1fr)); }
  .content-grid { grid-template-columns: 1fr; }
}

@media (max-width: 640px) {
  .summary-grid { grid-template-columns: 1fr; }
}
</style>
