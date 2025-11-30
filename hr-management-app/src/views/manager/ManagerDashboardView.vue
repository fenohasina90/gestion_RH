<template>
  <div class="mgr-layout">
    <ManagerSidebar />
    <div class="mgr-content">
      <div class="mgr-container">
        <div class="mgr-header">
          <h1 class="mgr-title">Tableau de bord du département</h1>
          <p class="mgr-subtitle">Suivi des performances et de l'absentéisme de votre équipe</p>
        </div>

        <div class="mgr-filters">
          <div class="mgr-filter-group">
            <label class="mgr-filter-label">Mois</label>
            <input v-model.number="mois" type="number" min="1" max="12" class="mgr-filter-input" />
          </div>
          <div class="mgr-filter-group">
            <label class="mgr-filter-label">Année</label>
            <input v-model.number="annee" type="number" min="2000" max="2100" class="mgr-filter-input" />
          </div>
          <div class="mgr-filter-actions">
            <button class="mgr-btn" @click="loadDashboard">Actualiser</button>
          </div>
        </div>

        <div v-if="loading" class="mgr-loading">
          <div class="mgr-spinner"></div>
          <span>Chargement du tableau de bord...</span>
        </div>

        <div v-else>
          <div class="mgr-kpis" v-if="dashboard && dashboard.success">
            <div class="mgr-kpi-card">
              <h3>Département</h3>
              <p class="mgr-kpi-main">{{ dashboard.departement || 'N/A' }}</p>
            </div>
            <div class="mgr-kpi-card">
              <h3>Taux d'absence</h3>
              <p class="mgr-kpi-main">
                {{ formatPourcentage(dashboard.absenteisme?.taux_absence) }}
              </p>
              <p class="mgr-kpi-sub">
                {{ dashboard.absenteisme?.nb_absences || 0 }} absences sur la période
              </p>
            </div>
            <div class="mgr-kpi-card">
              <h3>Employés touchés</h3>
              <p class="mgr-kpi-main">
                {{ dashboard.absenteisme?.employes_touches || 0 }}
              </p>
              <p class="mgr-kpi-sub">ayant au moins une absence</p>
            </div>
          </div>

          <div class="mgr-panels" v-if="dashboard && dashboard.success">
            <div class="mgr-panel">
              <div class="mgr-panel-header">
                <h2>Performances des employés</h2>
              </div>
              <div v-if="(dashboard.performancesEmployes || []).length === 0" class="mgr-empty">
                Aucune donnée de performance pour cette période.
              </div>
              <div v-else class="mgr-table">
                <table>
                  <thead>
                    <tr>
                      <th>Employé</th>
                      <th>Jours travaillés</th>
                      <th>Jours d'absence</th>
                      <th>Retards</th>
                      <th>Heures sup.</th>
                      <th>Score global</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="e in dashboard.performancesEmployes" :key="e.id">
                      <td>
                        <div class="mgr-emp-name">{{ e.nom }} {{ e.prenom }}</div>
                        <div class="mgr-emp-meta">{{ e.matricule }}</div>
                      </td>
                      <td>{{ e.jours_trav }}</td>
                      <td>{{ e.jours_abs }}</td>
                      <td>{{ e.retards }}</td>
                      <td>{{ e.heures_sup }}</td>
                      <td>
                        <div class="mgr-score-bar">
                          <div class="mgr-score-fill" :style="{ width: (e.scoreGlobal || 0) + '%' }"></div>
                        </div>
                        <span class="mgr-score-label">{{ formatPourcentage(e.scoreGlobal) }}</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div class="mgr-panel">
              <div class="mgr-panel-header">
                <h2>Absentéisme du département</h2>
              </div>
              <div v-if="!dashboard.absenteisme" class="mgr-empty">
                Aucune donnée d'absentéisme pour cette période.
              </div>
              <div v-else class="mgr-abs-details">
                <div class="mgr-abs-item">
                  <span class="label">Heures d'absence</span>
                  <span class="value">{{ dashboard.absenteisme.heures_absence || 0 }}</span>
                </div>
                <div class="mgr-abs-item">
                  <span class="label">Nombre d'absences</span>
                  <span class="value">{{ dashboard.absenteisme.nb_absences || 0 }}</span>
                </div>
                <div class="mgr-abs-item">
                  <span class="label">Employés touchés</span>
                  <span class="value">{{ dashboard.absenteisme.employes_touches || 0 }}</span>
                </div>
                <div class="mgr-abs-item">
                  <span class="label">Taux d'absence</span>
                  <span class="value">{{ formatPourcentage(dashboard.absenteisme.taux_absence) }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="mgr-panels" v-if="dashboard && dashboard.success">
            <div class="mgr-panel">
              <div class="mgr-panel-header">
                <h2>Top {{ topN }} employés les plus absents</h2>
              </div>
              <div v-if="topAbsents.length === 0" class="mgr-empty">
                Aucune donnée d'absence pour cette période.
              </div>
              <div v-else class="mgr-top-absents">
                <div
                  v-for="(e, index) in topAbsents"
                  :key="e.id"
                  class="mgr-top-item"
                >
                  <div class="mgr-top-info">
                    <div class="mgr-emp-name">{{ e.nom }} {{ e.prenom }}</div>
                    <div class="mgr-emp-meta">{{ e.matricule }}</div>
                  </div>
                  <div class="mgr-top-bar-wrapper">
                    <div class="mgr-top-bar-bg">
                      <div class="mgr-top-bar-fill" :style="{ width: e._absRatio + '%' }"></div>
                    </div>
                    <div class="mgr-top-badge">
                      {{ e.jours_abs }} j
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="mgr-panels" v-if="dashboard && dashboard.success">
            <div class="mgr-panel">
              <div class="mgr-panel-header">
                <h2>Évolution du score moyen (6 derniers mois)</h2>
                <button class="mgr-btn-sm" @click="loadEvolution" :disabled="evolutionLoading">
                  {{ evolutionLoading ? 'Chargement...' : 'Rafraîchir' }}
                </button>
              </div>
              <div v-if="evolutionLoading" class="mgr-loading">
                <div class="mgr-spinner"></div>
                <span>Chargement de l'évolution...</span>
              </div>
              <div v-else-if="evolution.length === 0" class="mgr-empty">
                Aucune donnée d'évolution disponible.
              </div>
              <div v-else class="mgr-evolution-chart">
                <div
                  v-for="pt in evolution"
                  :key="pt.label"
                  class="mgr-evo-col"
                >
                  <div class="mgr-evo-bars">
                    <div class="mgr-evo-bar-bg">
                      <div class="mgr-evo-bar-fill mgr-evo-bar-score" :style="{ height: pt.ratioScore + '%' }"></div>
                    </div>
                    <div class="mgr-evo-bar-bg">
                      <div class="mgr-evo-bar-fill mgr-evo-bar-abs" :style="{ height: pt.ratioAbs + '%' }"></div>
                    </div>
                  </div>
                  <div class="mgr-evo-value">
                    {{ formatPourcentage(pt.score) }} / {{ formatPourcentage(pt.absence) }}
                  </div>
                  <div class="mgr-evo-label">{{ pt.label }}</div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="dashboard && !dashboard.success" class="mgr-global-message mgr-global-error">
            {{ dashboard.message || 'Erreur lors du chargement du tableau de bord' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ManagerSidebar from '../../components/ManagerSidebar.vue'

export default {
  name: 'ManagerDashboardView',
  components: { ManagerSidebar },
  data() {
    const now = new Date()
    return {
      mois: now.getMonth() + 1,
      annee: now.getFullYear(),
      loading: false,
      dashboard: null,
      topN: 5,
      evolution: [],
      evolutionLoading: false
    }
  },
  created() {
    const stored = localStorage.getItem('managerUser')
    if (!stored) {
      this.$router.push('/espace-manager/login')
      return
    }
    this.loadDashboard()
  },
  methods: {
    async loadDashboard() {
      const stored = localStorage.getItem('managerUser')
      if (!stored) {
        this.$router.push('/espace-manager/login')
        return
      }
      const mgr = JSON.parse(stored)
      this.loading = true
      try {
        const params = new URLSearchParams()
        params.append('idmanager', mgr.id)
        if (this.mois) params.append('mois', this.mois)
        if (this.annee) params.append('annee', this.annee)
        const res = await fetch(`http://localhost:8080/api/manager/dashboard?${params.toString()}`)
        if (!res.ok) throw new Error('Erreur serveur')
        this.dashboard = await res.json()
        this.computeTopAbsents()
      } catch (e) {
        console.error(e)
        this.dashboard = { success: false, message: "Erreur lors du chargement du tableau de bord" }
      } finally {
        this.loading = false
      }
    },
    formatPourcentage(v) {
      if (v == null || isNaN(v)) return '0%'
      const n = Number(v)
      return `${n.toFixed(1)}%`
    },
    computeTopAbsents() {
      if (!this.dashboard || !this.dashboard.performancesEmployes) return
      const list = [...this.dashboard.performancesEmployes]
      // Convertir les jours d'absence en nombre et trier desc
      list.forEach(e => {
        const v = Number(e.jours_abs || 0)
        e._joursAbsNum = isNaN(v) ? 0 : v
      })
      list.sort((a, b) => b._joursAbsNum - a._joursAbsNum)
      const top = list.filter(e => e._joursAbsNum > 0).slice(0, this.topN)
      const maxAbs = top.length ? Math.max(...top.map(e => e._joursAbsNum)) : 0
      top.forEach(e => {
        e._absRatio = maxAbs > 0 ? (e._joursAbsNum * 100) / maxAbs : 0
      })
      this.topAbsents = top
    },
    async loadEvolution() {
      const stored = localStorage.getItem('managerUser')
      if (!stored) {
        this.$router.push('/espace-manager/login')
        return
      }
      const mgr = JSON.parse(stored)
      this.evolutionLoading = true
      this.evolution = []
      try {
        const points = []
        const base = new Date(this.annee, this.mois - 1, 1)
        for (let i = 5; i >= 0; i--) {
          const d = new Date(base)
          d.setMonth(d.getMonth() - i)
          const m = d.getMonth() + 1
          const y = d.getFullYear()
          const params = new URLSearchParams()
          params.append('idmanager', mgr.id)
          params.append('mois', m)
          params.append('annee', y)
          const res = await fetch(`http://localhost:8080/api/manager/dashboard?${params.toString()}`)
          if (!res.ok) continue
          const data = await res.json()
          let avgScore = 0
          let avgAbs = 0

          if (data && data.success && Array.isArray(data.performancesEmployes) && data.performancesEmployes.length > 0) {
            const scores = data.performancesEmployes
              .map(e => Number(e.scoreGlobal || 0))
              .filter(v => !isNaN(v))
            avgScore = scores.length ? (scores.reduce((a, b) => a + b, 0) / scores.length) : 0
          }

          if (data && data.success && data.absenteisme && data.absenteisme.taux_absence != null) {
            const v = Number(data.absenteisme.taux_absence)
            avgAbs = isNaN(v) ? 0 : v
          }

          points.push({
            label: `${String(m).padStart(2, '0')}/${y}`,
            score: avgScore,
            absence: avgAbs
          })
        }
        const maxScore = points.length ? Math.max(...points.map(p => p.score)) : 0
        const maxAbs = points.length ? Math.max(...points.map(p => p.absence)) : 0
        this.evolution = points.map(p => ({
          ...p,
          ratioScore: maxScore > 0 ? (p.score * 100) / maxScore : 0,
          ratioAbs: maxAbs > 0 ? (p.absence * 100) / maxAbs : 0
        }))
      } catch (e) {
        console.error(e)
        this.evolution = []
      } finally {
        this.evolutionLoading = false
      }
    }
  }
}
</script>

<style scoped>
.mgr-layout {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
}
.mgr-content {
  flex: 1;
  padding: 24px;
}
.mgr-container {
  max-width: 1200px;
  margin: 0 auto;
}
.mgr-header {
  margin-bottom: 16px;
}
.mgr-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}
.mgr-subtitle {
  font-size: 14px;
  color: #6b7280;
}
.mgr-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
  padding: 12px 16px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
}
.mgr-filter-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.mgr-filter-label {
  font-size: 12px;
  color: #4b5563;
}
.mgr-filter-input {
  border-radius: 8px;
  border: 1px solid #d1d5db;
  padding: 6px 8px;
  font-size: 13px;
}
.mgr-filter-actions {
  margin-left: auto;
}
.mgr-btn {
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  border: none;
  background: #0f766e;
  color: white;
}
.mgr-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
}
.mgr-spinner {
  width: 18px;
  height: 18px;
  border-radius: 999px;
  border: 2px solid #e5e7eb;
  border-top-color: #0f766e;
  animation: spin 1s linear infinite;
}
.mgr-kpis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}
.mgr-kpi-card {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 14px 16px;
}
.mgr-kpi-card h3 {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: #6b7280;
}
.mgr-kpi-main {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}
.mgr-kpi-sub {
  font-size: 12px;
  color: #6b7280;
}
.mgr-panels {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}
.mgr-panel {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 14px 16px;
}
.mgr-panel-header {
  margin-bottom: 8px;
}
.mgr-panel-header h2 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}
.mgr-empty {
  padding: 16px;
  text-align: center;
  color: #6b7280;
}
.mgr-table {
  margin-top: 4px;
}
.mgr-table table {
  width: 100%;
  border-collapse: collapse;
}
.mgr-table th,
.mgr-table td {
  padding: 8px 10px;
  font-size: 13px;
  border-bottom: 1px solid #e5e7eb;
}
.mgr-table th {
  background: #f9fafb;
  text-align: left;
  font-weight: 600;
  color: #4b5563;
}
.mgr-emp-name {
  font-weight: 600;
  color: #111827;
}
.mgr-emp-meta {
  font-size: 12px;
  color: #6b7280;
}
.mgr-score-bar {
  width: 100%;
  height: 8px;
  background: #e5e7eb;
  border-radius: 999px;
  overflow: hidden;
  margin-bottom: 2px;
}
.mgr-score-fill {
  height: 100%;
  background: linear-gradient(90deg, #22c55e, #16a34a);
}
.mgr-score-label {
  font-size: 11px;
  color: #4b5563;
}
.mgr-abs-details {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
  font-size: 13px;
}
.mgr-abs-item {
  display: flex;
  justify-content: space-between;
}
.mgr-abs-item .label {
  color: #6b7280;
}
.mgr-abs-item .value {
  font-weight: 600;
  color: #111827;
}
.mgr-evolution-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8px;
  height: 180px;
}
.mgr-evo-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.mgr-evo-bars {
  display: flex;
  gap: 4px;
  width: 100%;
  flex: 1;
}
.mgr-evo-bar-bg {
  width: 100%;
  flex: 1;
  background: #e5e7eb;
  border-radius: 8px;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}
.mgr-evo-bar-fill {
  width: 100%;
}
.mgr-evo-bar-score {
  background: linear-gradient(180deg, #22c55e, #16a34a);
}
.mgr-evo-bar-abs {
  background: linear-gradient(180deg, #f97316, #ea580c);
}
.mgr-evo-value {
  font-size: 11px;
  color: #4b5563;
}
.mgr-evo-label {
  font-size: 11px;
  color: #6b7280;
}
.mgr-top-absents {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.mgr-top-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.mgr-top-info {
  min-width: 160px;
}
.mgr-top-bar-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}
.mgr-top-bar-bg {
  flex: 1;
  height: 8px;
  background: #e5e7eb;
  border-radius: 999px;
  overflow: hidden;
}
.mgr-top-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #f97316, #ea580c);
}
.mgr-top-badge {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 999px;
  background: #ffedd5;
  color: #9a3412;
}
.mgr-global-message {
  margin-top: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 13px;
}
.mgr-global-error {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .mgr-panels {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .mgr-layout {
    flex-direction: column;
  }
  .mgr-content {
    padding: 16px;
  }
}
</style>
