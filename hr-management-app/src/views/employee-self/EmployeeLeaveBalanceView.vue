<template>
  <div class="emp-layout">
    <EmployeeSidebar />
    <div class="emp-content">
      <div class="emp-container">
        <div class="emp-header">
          <h1 class="emp-title">Mon solde de congés</h1>
          <p class="emp-subtitle">Consultez vos jours de congés disponibles</p>
        </div>

        <div class="emp-controls-card">
          <div class="emp-controls-header">
            <h3 class="emp-controls-title">Sélection de l'année</h3>
          </div>
          <div class="emp-controls-filters">
            <div class="emp-filter-group">
              <label class="emp-filter-label">Année de référence</label>
              <select v-model.number="annee" class="emp-filter-select" @change="loadSolde">
                <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
              </select>
            </div>
            <button @click="loadSolde" class="emp-refresh-button">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M23 4v6h-6"></path>
                <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path>
              </svg>
              Actualiser
            </button>
          </div>
        </div>

        <div v-if="loadingSolde" class="emp-loading">
          <div class="emp-spinner"></div>
          <span>Chargement de votre solde...</span>
        </div>

        <div v-else>
          <div v-if="soldes.length" class="emp-solde-grid">
            <div v-for="s in soldes" :key="s.id" class="emp-solde-card">
              <div class="emp-solde-header">
                <h3 class="emp-solde-type">{{ s.typeconge }}</h3>
                <div class="emp-solde-year">{{ s.annee }}</div>
              </div>
              
              <div class="emp-solde-stats">
                <div class="emp-stat">
                  <div class="emp-stat-label">Jours acquis</div>
                  <div class="emp-stat-value emp-stat-acquis">{{ s.joursacquis }}</div>
                </div>
                <div class="emp-stat">
                  <div class="emp-stat-label">Jours pris</div>
                  <div class="emp-stat-value emp-stat-pris">{{ s.jourspris }}</div>
                </div>
                <div class="emp-stat">
                  <div class="emp-stat-label">Jours restants</div>
                  <div class="emp-stat-value emp-stat-restants">{{ s.joursrestants }}</div>
                </div>
              </div>

              <div class="emp-solde-progress">
                <div class="emp-progress-bar">
                  <div class="emp-progress-fill" :style="{ width: progressPercentage(s) + '%' }"></div>
                </div>
                <div class="emp-progress-text">
                  {{ Math.round(progressPercentage(s)) }}% utilisés
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="emp-empty-state">
            <div class="emp-empty-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
            </div>
            <h3 class="emp-empty-title">Aucun solde trouvé</h3>
            <p class="emp-empty-description">Aucun solde de congé n'est disponible pour l'année sélectionnée.</p>
          </div>
        </div>

        <div class="emp-actions-section">
          <router-link to="/espace-employe/demande-conge" class="emp-action-button emp-action-primary">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Nouvelle demande de congé
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EmployeeSidebar from '../../components/EmployeeSidebar.vue'

export default {
  name: 'EmployeeLeaveBalanceView',
  components: { EmployeeSidebar },
  data() {
    const currentYear = new Date().getFullYear()
    return {
      annee: currentYear,
      soldes: [],
      loadingSolde: false,
      availableYears: [currentYear - 1, currentYear, currentYear + 1]
    }
  },
  created() {
    const stored = localStorage.getItem('selfEmployee')
    if (!stored) {
      this.$router.push('/espace-employe/login')
      return
    }
    this.loadSolde()
  },
  methods: {
    async loadSolde() {
      const stored = localStorage.getItem('selfEmployee')
      if (!stored) {
        this.$router.push('/espace-employe/login')
        return
      }
      const emp = JSON.parse(stored)
      this.loadingSolde = true
      try {
        const res = await fetch(`http://localhost:8080/api/employe/self/conges/solde?idemploye=${emp.id}&annee=${this.annee}`)
        if (!res.ok) throw new Error('Erreur serveur')
        this.soldes = await res.json()
      } catch (e) {
        console.error(e)
        this.soldes = []
      } finally {
        this.loadingSolde = false
      }
    },
    progressPercentage(solde) {
      if (!solde.joursacquis || solde.joursacquis === 0) return 0
      return (solde.jourspris / solde.joursacquis) * 100
    }
  }
}
</script>

<style scoped>
.emp-layout {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
}

.emp-content {
  flex: 1;
  padding: 0;
}

.emp-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 24px;
}

.emp-header {
  margin-bottom: 32px;
  text-align: center;
}

.emp-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.emp-subtitle {
  font-size: 16px;
  color: #6b7280;
}

.emp-controls-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}

.emp-controls-header {
  margin-bottom: 20px;
}

.emp-controls-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.emp-controls-filters {
  display: flex;
  align-items: end;
  gap: 16px;
  flex-wrap: wrap;
}

.emp-filter-group {
  flex: 1;
  min-width: 200px;
}

.emp-filter-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.emp-filter-select {
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: #fafafa;
}

.emp-filter-select:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.emp-refresh-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.emp-refresh-button:hover {
  background: #e5e7eb;
}

.emp-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 60px 20px;
  color: #6b7280;
}

.emp-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top: 3px solid #10b981;
  border-radius: 50%;
  animation: emp-spin 1s linear infinite;
}

.emp-solde-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.emp-solde-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 24px;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.emp-solde-card:hover {
  border-color: #10b981;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.1);
}

.emp-solde-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #10b981, #059669);
}

.emp-solde-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 20px;
}

.emp-solde-type {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.emp-solde-year {
  background: #f3f4f6;
  color: #6b7280;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.emp-solde-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.emp-stat {
  text-align: center;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.emp-stat-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 6px;
  font-weight: 500;
}

.emp-stat-value {
  font-size: 20px;
  font-weight: 700;
}

.emp-stat-acquis {
  color: #059669;
}

.emp-stat-pris {
  color: #dc2626;
}

.emp-stat-restants {
  color: #1f2937;
}

.emp-solde-progress {
  space-y: 8px;
}

.emp-progress-bar {
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.emp-progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.emp-progress-text {
  font-size: 12px;
  color: #6b7280;
  text-align: center;
  font-weight: 500;
}

.emp-empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #ffffff;
  border-radius: 16px;
  border: 2px dashed #e5e7eb;
  margin-bottom: 40px;
}

.emp-empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.emp-empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.emp-empty-description {
  font-size: 14px;
  color: #6b7280;
  max-width: 300px;
  margin: 0 auto;
}

.emp-actions-section {
  text-align: center;
  padding-top: 32px;
  border-top: 1px solid #e5e7eb;
}

.emp-action-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease;
  border: none;
  cursor: pointer;
}

.emp-action-primary {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.emp-action-primary:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.4);
}

@keyframes emp-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .emp-container {
    padding: 24px 16px;
  }
  
  .emp-controls-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .emp-solde-grid {
    grid-template-columns: 1fr;
  }
  
  .emp-solde-stats {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .emp-solde-card {
    padding: 20px;
  }
}
</style>