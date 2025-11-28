<template>
  <div class="results-page">
    <div class="page-header">
      <h1 class="page-title">Résultats du QCM</h1>
      <p class="page-subtitle">{{ testResults.test?.nom }}</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <h3>Chargement des résultats...</h3>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-state">
      <div class="error-icon">⚠️</div>
      <h3>Erreur</h3>
      <p>{{ error }}</p>
      <button @click="loadResults" class="btn btn-primary">Réessayer</button>
    </div>

    <!-- Results Display -->
    <div v-else class="results-container">
      <!-- Summary Card -->
      <div class="summary-card">
        <div class="summary-header">
          <h2>Résumé de votre performance</h2>
        </div>
        <div class="summary-stats">
          <div class="stat-item large">
            <div class="stat-value">{{ testResults.percentage }}%</div>
            <div class="stat-label">Score final</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ testResults.finalScore }}</div>
            <div class="stat-label">Points obtenus</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ testResults.totalPoints }}</div>
            <div class="stat-label">Points totaux</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ testResults.totalQuestions }}</div>
            <div class="stat-label">Questions</div>
          </div>
        </div>
        <div class="performance-indicator" :class="getPerformanceClass()">
          {{ getPerformanceText() }}
        </div>
      </div>

      <!-- Detailed Results -->
      <div class="detailed-results">
        <h3>Détail des réponses</h3>
        <div class="questions-list">
          <div 
            v-for="(response, index) in testResults.responses" 
            :key="response.id"
            class="question-result"
            :class="{ 'correct': response.pointsobtenus > 0, 'incorrect': response.pointsobtenus === 0 }"
          >
            <div class="question-header">
              <div class="question-number">
                Question {{ index + 1 }}
              </div>
              <div class="question-score">
                {{ response.pointsobtenus }}/{{ response.idquestion.points }} points
              </div>
            </div>
            
            <div class="question-text">
              {{ response.idquestion.question }}
            </div>

            <div class="answer-info">
              <div class="your-answer">
                <strong>Votre réponse:</strong>
                <span class="answer-text" :class="{ 'correct': response.pointsobtenus > 0, 'incorrect': response.pointsobtenus === 0 }">
                  {{ response.idchoix?.texte || 'Aucune réponse' }}
                </span>
              </div>
              
              <div v-if="response.pointsobtenus === 0" class="correct-answer">
                <strong>Réponse correcte:</strong>
                <span class="answer-text correct">
                  {{ getCorrectAnswer(response.idquestion.id) }}
                </span>
              </div>
            </div>

            <div class="result-indicator">
              <span v-if="response.pointsobtenus > 0" class="correct-indicator">
                ✅ Correct
              </span>
              <span v-else class="incorrect-indicator">
                ❌ Incorrect
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="results-actions">
        <button @click="$router.push('/mes-qcm')" class="btn btn-primary">
          Retour aux QCM
        </button>
        <button @click="$router.push('/')" class="btn btn-secondary">
          Retour à l'accueil
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { useCandidateAuthStore } from '../../stores/candidateAuth'
import axios from 'axios'

export default {
  name: 'QcmResultsView',
  setup() {
    const candidateAuthStore = useCandidateAuthStore()
    return {
      candidateAuthStore
    }
  },
  data() {
    return {
      testId: null,
      testResults: {},
      correctAnswers: {},
      loading: true,
      error: null
    }
  },
  async mounted() {
    if (!this.candidateAuthStore.candidate) {
      this.$router.push('/')
      return
    }
    
    this.testId = this.$route.params.id
    await this.loadResults()
  },
  methods: {
    async loadResults() {
      this.loading = true
      this.error = null
      
      try {
        const candidatId = this.candidateAuthStore.candidate.id
        
        // Load test results
        const resultsResponse = await axios.get(
          `http://localhost:8080/api/qcm/test/${this.testId}/candidat/${candidatId}/results`
        )
        this.testResults = resultsResponse.data
        
        // Load correct answers for incorrect responses
        await this.loadCorrectAnswers()
        
      } catch (error) {
        console.error('Error loading results:', error)
        this.error = 'Erreur lors du chargement des résultats'
      } finally {
        this.loading = false
      }
    },
    
    async loadCorrectAnswers() {
      try {
        const incorrectResponses = this.testResults.responses.filter(r => r.pointsobtenus === 0)
        
        for (const response of incorrectResponses) {
          const questionId = response.idquestion.id
          const choicesResponse = await axios.get(
            `http://localhost:8080/api/qcm/question/${questionId}/choices`
          )
          const choices = choicesResponse.data
          const correctChoice = choices.find(c => c.estcorrect)
          
          if (correctChoice) {
            this.correctAnswers[questionId] = correctChoice.texte
          }
        }
      } catch (error) {
        console.error('Error loading correct answers:', error)
      }
    },
    
    getCorrectAnswer(questionId) {
      return this.correctAnswers[questionId] || 'Non disponible'
    },
    
    getPerformanceClass() {
      const percentage = this.testResults.percentage || 0
      if (percentage >= 80) return 'excellent'
      if (percentage >= 60) return 'good'
      if (percentage >= 40) return 'average'
      return 'poor'
    },
    
    getPerformanceText() {
      const percentage = this.testResults.percentage || 0
      if (percentage >= 80) return 'Excellent travail ! 🎉'
      if (percentage >= 60) return 'Bon travail ! 👍'
      if (percentage >= 40) return 'Peut mieux faire 📚'
      return 'Il faut réviser 📖'
    }
  }
}
</script>

<style scoped>
.results-page {
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

.loading-state, .error-state {
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

.error-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.results-container {
  width: 100%;
  max-width: none;
  margin: 0 auto;
  padding: 0 24px;
}

.summary-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
  margin-bottom: 30px;
  text-align: center;
}

.summary-header h2 {
  margin: 0 0 25px 0;
  color: #065f46;
  font-size: 1.8rem;
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 20px;
  margin-bottom: 25px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item.large .stat-value {
  font-size: 3rem;
  font-weight: 800;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #065f46;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: #0f766e;
}

.performance-indicator {
  padding: 15px 25px;
  border-radius: 25px;
  font-size: 1.2rem;
  font-weight: 600;
  margin-top: 20px;
}

.performance-indicator.excellent {
  background: #059669;
  color: #ffffff;
}

.performance-indicator.good {
  background: rgba(5, 150, 105, 0.85);
  color: #ffffff;
}

.performance-indicator.average {
  background: rgba(5, 150, 105, 0.65);
  color: #ffffff;
}

.performance-indicator.poor {
  background: rgba(5, 150, 105, 0.35);
  color: #065f46;
}

.detailed-results {
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
  margin-bottom: 30px;
}

.detailed-results h3 {
  margin: 0 0 25px 0;
  color: #065f46;
  font-size: 1.5rem;
}

.questions-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

@media (min-width: 1600px) {
  .questions-list { grid-template-columns: repeat(5, 1fr); }
}
@media (max-width: 1400px) {
  .questions-list { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 1024px) {
  .questions-list { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .questions-list { grid-template-columns: 1fr; }
}

.question-result {
  background: #f7fbf9;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid;
}

.question-result.correct {
  border-left-color: #059669;
}

.question-result.incorrect {
  border-left-color: rgba(5, 150, 105, 0.4);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.question-number {
  font-weight: 600;
  color: #065f46;
  font-size: 1.1rem;
}

.question-score {
  background: #059669;
  color: #ffffff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.question-text {
  font-size: 1.1rem;
  color: #065f46;
  margin-bottom: 15px;
  font-weight: 500;
}

.answer-info {
  margin-bottom: 15px;
}

.your-answer, .correct-answer {
  margin-bottom: 10px;
}

.your-answer strong, .correct-answer strong {
  color: #065f46;
  margin-right: 8px;
}

.answer-text {
  padding: 5px 10px;
  border-radius: 4px;
  font-weight: 500;
}

.answer-text.correct {
  background: rgba(5, 150, 105, 0.2);
  color: #065f46;
}

.answer-text.incorrect {
  background: rgba(5, 150, 105, 0.1);
  color: rgba(6, 95, 70, 0.7);
}

.result-indicator {
  display: flex;
  justify-content: flex-end;
}

.correct-indicator {
  color: #065f46;
  font-weight: 600;
}

.incorrect-indicator {
  color: rgba(6, 95, 70, 0.7);
  font-weight: 600;
}

.results-actions {
  text-align: center;
  padding: 40px 0;
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
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
  border: 2px solid #059669;
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

@media (max-width: 768px) {
  .summary-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .question-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .results-actions {
    flex-direction: column;
  }
}
</style>
