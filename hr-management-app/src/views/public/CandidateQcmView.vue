<template>
  <div class="qcm-page">
    <div class="page-header">
      <h1 class="page-title">Mes QCM</h1>
      <p class="page-subtitle">Tests disponibles pour vos candidatures</p>
    </div>

    <div class="qcm-container">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <h3>Chargement des QCM...</h3>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <div class="error-icon">⚠️</div>
        <h3>Erreur</h3>
        <p>{{ error }}</p>
        <button @click="loadQcmTests" class="btn btn-primary">Réessayer</button>
      </div>

      <!-- Empty State -->
      <div v-else-if="qcmTests.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>Aucun QCM disponible</h3>
        <p>Vous n'avez pas encore de tests à passer pour vos candidatures.</p>
        <button @click="$router.push('/jobs')" class="btn btn-primary">Voir les offres</button>
      </div>

      <!-- QCM Tests Grid -->
      <div v-else class="qcm-grid">
        <div v-for="test in qcmTests" :key="test.id" class="qcm-card">
          <div class="card-header">
            <h3 class="test-title">{{ test.nom }}</h3>
            <div class="test-status" :class="getStatusClass(test)">
              {{ getStatusText(test) }}
            </div>
          </div>

          <div class="card-content">
            <div class="test-info">
              <div class="info-item">
                <span class="info-label">Questions:</span>
                <span class="info-value">{{ test.totalQuestions || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Points totaux:</span>
                <span class="info-value">{{ test.totalPoints || 0 }}</span>
              </div>
              <div v-if="test.hasStarted" class="info-item">
                <span class="info-label">Progression:</span>
                <span class="info-value">{{ test.answeredQuestions }}/{{ test.totalQuestions }}</span>
              </div>
              <div v-if="test.hasStarted" class="info-item">
                <span class="info-label">Score actuel:</span>
                <span class="info-value">{{ test.currentScore }}/{{ test.totalPoints }}</span>
              </div>
            </div>

            <div v-if="test.hasStarted && !test.isCompleted" class="progress-bar">
              <div class="progress-fill" :style="{ width: getProgressPercentage(test) + '%' }"></div>
            </div>

            <div v-if="test.isCompleted" class="completion-info">
              <div class="final-score">
                Score final: {{ test.currentScore }}/{{ test.totalPoints }}
                ({{ Math.round((test.currentScore / test.totalPoints) * 100) }}%)
              </div>
            </div>
          </div>

          <div class="card-actions">
            <button 
              v-if="!test.hasStarted" 
              @click="startTest(test)" 
              class="btn btn-primary"
            >
              Commencer
            </button>
            <button 
              v-else-if="!test.isCompleted" 
              @click="continueTest(test)" 
              class="btn btn-primary"
            >
              Continuer
            </button>
            <button 
              v-else 
              @click="viewResults(test)" 
              class="btn btn-secondary"
            >
              Voir les résultats
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Back to Home Button -->
    <div class="back-actions">
      <button @click="$router.push('/')" class="btn btn-secondary">
        Retour à l'accueil
      </button>
    </div>
  </div>
</template>

<script>
import { useCandidateAuthStore } from '../../stores/candidateAuth'
import axios from 'axios'

export default {
  name: 'CandidateQcmView',
  setup() {
    const candidateAuthStore = useCandidateAuthStore()
    return {
      candidateAuthStore
    }
  },
  data() {
    return {
      qcmTests: [],
      loading: true,
      error: null
    }
  },
  async mounted() {
    if (!this.candidateAuthStore.candidate) {
      this.$router.push('/')
      return
    }
    await this.loadQcmTests()
  },
  methods: {
    async loadQcmTests() {
      this.loading = true
      this.error = null
      
      try {
        const candidatId = this.candidateAuthStore.candidate.id
        
        // Get available QCM tests
        const testsResponse = await axios.get(`http://localhost:8080/api/qcm/candidat/${candidatId}`)
        const tests = testsResponse.data
        
        // Get details for each test
        const testDetails = await Promise.all(
          tests.map(async (test) => {
            try {
              const detailsResponse = await axios.get(
                `http://localhost:8080/api/qcm/test/${test.id}/candidat/${candidatId}`
              )
              return {
                ...test,
                ...detailsResponse.data
              }
            } catch (error) {
              console.error(`Error loading details for test ${test.id}:`, error)
              return test
            }
          })
        )
        
        this.qcmTests = testDetails
      } catch (error) {
        console.error('Error loading QCM tests:', error)
        this.error = 'Erreur lors du chargement des QCM'
      } finally {
        this.loading = false
      }
    },
    
    startTest(test) {
      this.$router.push(`/qcm/${test.id}`)
    },
    
    continueTest(test) {
      this.$router.push(`/qcm/${test.id}`)
    },
    
    viewResults(test) {
      this.$router.push(`/qcm/${test.id}/results`)
    },
    
    getStatusClass(test) {
      if (test.isCompleted) return 'status-completed'
      if (test.hasStarted) return 'status-in-progress'
      return 'status-not-started'
    },
    
    getStatusText(test) {
      if (test.isCompleted) return 'Terminé'
      if (test.hasStarted) return 'En cours'
      return 'Non commencé'
    },
    
    getProgressPercentage(test) {
      if (!test.totalQuestions || test.totalQuestions === 0) return 0
      return (test.answeredQuestions / test.totalQuestions) * 100
    }
  }
}
</script>

<style scoped>
.qcm-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f9f7 0%, #eefaf6 100%);
  padding: 0;
  margin: 0;
}

.page-header {
  text-align: center;
  margin: 0 0 40px 0;
  padding: 40px 20px;
  background: #ffffff;
  color: #065f46;
  box-shadow: 0 2px 20px rgba(0,0,0,0.06);
}

.page-title {
  font-size: 2.5rem;
  margin: 0 0 10px 0;
  font-weight: 700;
  color: #065f46;
}

.page-subtitle {
  font-size: 1.2rem;
  margin: 0;
  color: #0f766e;
}

.qcm-container {
  width: 100%;
  max-width: none;
  margin: 0 auto;
  padding: 0 24px;
}

.loading-state, .error-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  margin: 0 20px;
  color: #065f46;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(5, 150, 105, 0.25);
  border-top: 4px solid #059669;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive grid breakpoints */
@media (max-width: 1400px) {
  .qcm-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 1024px) {
  .qcm-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .qcm-grid { grid-template-columns: 1fr; }
}

.error-icon, .empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.qcm-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  padding: 20px 0;
}

@media (min-width: 1600px) {
  .qcm-grid { grid-template-columns: repeat(5, 1fr); }
}

.qcm-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-left: 4px solid #059669;
  border: 1px solid #e2ece8;
}

.qcm-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.test-title {
  margin: 0;
  color: #065f46;
  font-size: 1.4rem;
  font-weight: 600;
}

.test-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-not-started {
  background: rgba(5, 150, 105, 0.15);
  color: #065f46;
}

.status-in-progress {
  background: #059669;
  color: #ffffff;
}

.status-completed {
  background: #065f46;
  color: #dcfce7;
}

.card-content {
  margin-bottom: 20px;
}

.test-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-weight: 600;
  color: #065f46;
}

.info-value {
  color: #0f766e;
  font-weight: 500;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: rgba(5, 150, 105, 0.15);
  border-radius: 4px;
  overflow: hidden;
  margin: 15px 0;
}

.progress-fill {
  height: 100%;
  background: #059669;
  transition: width 0.3s ease;
}

.completion-info {
  margin: 15px 0;
}

.final-score {
  font-weight: 600;
  color: #065f46;
  font-size: 1.1rem;
  text-align: center;
  padding: 10px;
  background: rgba(5, 150, 105, 0.1);
  border-radius: 6px;
}

.card-actions {
  display: flex;
  justify-content: center;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  font-size: 14px;
}

.btn-primary {
  background: #059669;
  color: #ffffff;
}

.btn-primary:hover {
  background: #047857;
  color: #ffffff;
  transform: translateY(-2px);
}

.btn-secondary {
  background: transparent;
  color: #059669;
  border: 2px solid #059669;
}

.btn-secondary:hover {
  background: #059669;
  color: #ffffff;
}

.back-actions {
  text-align: center;
  padding: 40px 20px;
}
</style>
