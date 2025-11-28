<template>
  <div class="qcm-test-page">
    <!-- Test Header -->
    <div class="test-header">
      <div class="test-info">
        <h1 class="test-title">{{ testData.test?.nom || 'QCM' }}</h1>
        <div class="test-progress">
          <span v-if="!testCompleted">
            Question {{ currentQuestionNumber }} sur {{ testData.totalQuestions }}
          </span>
          <span v-else>Test terminé</span>
        </div>
      </div>
      <div class="test-stats">
        <div class="stat-item">
          <span class="stat-label">Score:</span>
          <span class="stat-value">{{ currentScore }}/{{ testData.totalPoints || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <h3>Chargement de la question...</h3>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-state">
      <div class="error-icon">⚠️</div>
      <h3>Erreur</h3>
      <p>{{ error }}</p>
      <button @click="loadNextQuestion" class="btn btn-primary">Réessayer</button>
    </div>

    <!-- Test Completed -->
    <div v-else-if="testCompleted" class="completion-state">
      <div class="completion-icon">🎉</div>
      <h2>Test terminé !</h2>
      <div class="final-results">
        <div class="result-item">
          <span class="result-label">Score final:</span>
          <span class="result-value">{{ finalScore }}/{{ testData.totalPoints }}</span>
        </div>
        <div class="result-item">
          <span class="result-label">Pourcentage:</span>
          <span class="result-value">{{ finalPercentage }}%</span>
        </div>
        <div class="result-item">
          <span class="result-label">Questions répondues:</span>
          <span class="result-value">{{ testData.totalQuestions }}</span>
        </div>
      </div>
      <div class="completion-actions">
        <button @click="viewDetailedResults" class="btn btn-primary">
          Voir les résultats détaillés
        </button>
        <button @click="$router.push('/mes-qcm')" class="btn btn-secondary">
          Retour aux QCM
        </button>
      </div>
    </div>

    <!-- Question Display -->
    <div v-else-if="currentQuestion" class="question-container">
      <div class="question-card">
        <div class="question-header">
          <div class="question-number">
            Question {{ currentQuestionNumber }}
          </div>
          <div class="question-points">
            {{ currentQuestion.points }} point{{ currentQuestion.points > 1 ? 's' : '' }}
          </div>
        </div>
        
        <div class="question-text">
          {{ currentQuestion.question }}
        </div>

        <div class="choices-container">
          <div 
            v-for="choice in questionChoices" 
            :key="choice.id"
            class="choice-option"
            :class="{ 'selected': selectedChoice === choice.id }"
            @click="selectChoice(choice.id)"
          >
            <div class="choice-radio">
              <input 
                type="radio" 
                :id="'choice-' + choice.id"
                :value="choice.id"
                v-model="selectedChoice"
                class="choice-input"
              />
              <label :for="'choice-' + choice.id" class="choice-label">
                {{ choice.texte }}
              </label>
            </div>
          </div>
        </div>

        <div class="question-actions">
          <button 
            @click="submitAnswer" 
            :disabled="!selectedChoice || submitting"
            class="btn btn-primary"
          >
            <span v-if="submitting">Envoi...</span>
            <span v-else>Valider la réponse</span>
          </button>
        </div>
      </div>

      <!-- Progress Bar -->
      <div class="progress-container">
        <div class="progress-bar">
          <div 
            class="progress-fill" 
            :style="{ width: progressPercentage + '%' }"
          ></div>
        </div>
        <div class="progress-text">
          {{ answeredQuestions }}/{{ testData.totalQuestions }} questions répondues
        </div>
      </div>
    </div>

    <!-- Back Button -->
    <div class="back-actions">
      <button @click="goBack" class="btn btn-secondary">
        Retour
      </button>
    </div>
  </div>
</template>

<script>
import { useCandidateAuthStore } from '../../stores/candidateAuth'
import axios from 'axios'

export default {
  name: 'TakeQcmView',
  setup() {
    const candidateAuthStore = useCandidateAuthStore()
    return {
      candidateAuthStore
    }
  },
  data() {
    return {
      testId: null,
      testData: {},
      currentQuestion: null,
      questionChoices: [],
      selectedChoice: null,
      currentQuestionNumber: 1,
      answeredQuestions: 0,
      currentScore: 0,
      loading: true,
      error: null,
      submitting: false,
      testCompleted: false,
      finalScore: 0,
      finalPercentage: 0
    }
  },
  computed: {
    progressPercentage() {
      if (!this.testData.totalQuestions) return 0
      return (this.answeredQuestions / this.testData.totalQuestions) * 100
    }
  },
  async mounted() {
    if (!this.candidateAuthStore.candidate) {
      this.$router.push('/')
      return
    }
    
    this.testId = this.$route.params.id
    await this.loadTestData()
    await this.loadNextQuestion()
  },
  methods: {
    async loadTestData() {
      try {
        const candidatId = this.candidateAuthStore.candidate.id
        const response = await axios.get(
          `http://localhost:8080/api/qcm/test/${this.testId}/candidat/${candidatId}`
        )
        this.testData = response.data
        this.currentScore = this.testData.currentScore || 0
        this.answeredQuestions = this.testData.answeredQuestions || 0
      } catch (error) {
        console.error('Error loading test data:', error)
        this.error = 'Erreur lors du chargement du test'
      }
    },

    async loadNextQuestion() {
      this.loading = true
      this.error = null
      this.selectedChoice = null

      try {
        const candidatId = this.candidateAuthStore.candidate.id
        const response = await axios.get(
          `http://localhost:8080/api/qcm/test/${this.testId}/candidat/${candidatId}/next-question`
        )

        const data = response.data

        if (data.completed) {
          this.testCompleted = true
          this.finalScore = data.finalScore || 0
          this.finalPercentage = this.testData.totalPoints > 0 
            ? Math.round((this.finalScore / this.testData.totalPoints) * 100) 
            : 0
        } else {
          this.currentQuestion = data.question
          this.questionChoices = data.choices
          this.currentQuestionNumber = data.questionNumber
          this.answeredQuestions = data.questionNumber - 1
        }
      } catch (error) {
        console.error('Error loading next question:', error)
        this.error = 'Erreur lors du chargement de la question'
      } finally {
        this.loading = false
      }
    },

    selectChoice(choiceId) {
      this.selectedChoice = choiceId
    },

    async submitAnswer() {
      if (!this.selectedChoice || this.submitting) return

      this.submitting = true

      try {
        const candidatId = this.candidateAuthStore.candidate.id
        const answerData = {
          candidatId: candidatId,
          testId: parseInt(this.testId),
          questionId: this.currentQuestion.id,
          choixId: this.selectedChoice
        }

        console.log('Submitting answer data:', answerData)
        const response = await axios.post('http://localhost:8080/api/qcm/answer', answerData)
        const result = response.data

        if (result.success) {
          this.currentScore = result.currentScore
          this.answeredQuestions = result.answeredQuestions

          // Show brief feedback
          this.showAnswerFeedback(result.pointsObtained)

          // Load next question after a short delay
          setTimeout(async () => {
            if (result.isCompleted) {
              this.testCompleted = true
              this.finalScore = result.currentScore
              this.finalPercentage = this.testData.totalPoints > 0 
                ? Math.round((this.finalScore / this.testData.totalPoints) * 100) 
                : 0
            } else {
              await this.loadNextQuestion()
            }
          }, 1500)
        } else {
          this.error = result.message || 'Erreur lors de l\'envoi de la réponse'
        }
      } catch (error) {
        console.error('Error submitting answer:', error)
        console.error('Error details:', error.response?.data)
        this.error = error.response?.data?.message || 'Erreur lors de l\'envoi de la réponse'
      } finally {
        this.submitting = false
      }
    },

    showAnswerFeedback(pointsObtained) {
      // Create a temporary feedback element
      const feedback = document.createElement('div')
      feedback.className = 'answer-feedback'
      feedback.textContent = pointsObtained > 0 
        ? `✅ Correct ! +${pointsObtained} point${pointsObtained > 1 ? 's' : ''}` 
        : '❌ Incorrect'
      
      document.body.appendChild(feedback)
      
      // Remove after animation
      setTimeout(() => {
        document.body.removeChild(feedback)
      }, 1500)
    },

    viewDetailedResults() {
      this.$router.push(`/qcm/${this.testId}/results`)
    },

    goBack() {
      if (this.testCompleted || this.answeredQuestions === 0) {
        this.$router.push('/mes-qcm')
      } else {
        // Warn user about losing progress
        if (confirm('Êtes-vous sûr de vouloir quitter ? Votre progression sera sauvegardée.')) {
          this.$router.push('/mes-qcm')
        }
      }
    }
  }
}
</script>

<style scoped>
.qcm-test-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f9f7 0%, #eefaf6 100%);
  padding: 0;
  margin: 0;
}

.test-header {
  background: #ffffff;
  color: #065f46;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06);
  border-bottom: 1px solid #e2ece8;
}

.test-title {
  margin: 0 0 5px 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: #065f46;
}

.test-progress {
  font-size: 1rem;
  color: #0f766e;
}

.test-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 0.9rem;
  color: #0f766e;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: #065f46;
}

.loading-state, .error-state, .completion-state {
  text-align: center;
  padding: 60px 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  margin: 40px 20px;
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

.error-icon, .completion-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.question-container {
  width: 100%;
  max-width: none;
  margin: 24px auto;
  padding: 0 24px;
}

.question-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
  margin-bottom: 20px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(5, 150, 105, 0.15);
  grid-column: 1 / -1;
}

.question-number {
  font-size: 1.2rem;
  font-weight: 600;
  color: #065f46;
}

.question-points {
  background: #059669;
  color: #ffffff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.question-text {
  font-size: 1.3rem;
  color: #065f46;
  margin-bottom: 25px;
  line-height: 1.6;
  font-weight: 500;
}

.choices-container {
  margin-bottom: 25px;
}

.choice-option {
  margin-bottom: 15px;
  padding: 15px;
  border: 2px solid rgba(5, 150, 105, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.choice-option:hover {
  border-color: #059669;
  background: rgba(5, 150, 105, 0.06);
}

.choice-option.selected {
  border-color: #059669;
  background: rgba(5, 150, 105, 0.12);
}

.choice-radio {
  display: flex;
  align-items: center;
  gap: 12px;
}

.choice-input {
  width: 18px;
  height: 18px;
  accent-color: #059669;
}

.choice-label {
  font-size: 1.1rem;
  color: #065f46;
  cursor: pointer;
  flex: 1;
}

.question-actions {
  text-align: center;
}

.progress-container {
  width: 100%;
  margin: 0 auto;
  padding: 0 24px;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background: rgba(5, 150, 105, 0.15);
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: #059669;
  transition: width 0.3s ease;
}

.progress-text {
  text-align: center;
  color: #0f766e;
  font-size: 0.9rem;
}

.final-results {
  margin: 30px 0;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(5, 150, 105, 0.15);
}

.result-label {
  font-weight: 600;
  color: #065f46;
}

.result-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #065f46;
}

.completion-actions {
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

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: #059669;
  color: #ffffff;
}

.btn-primary:hover:not(:disabled) {
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

/* Answer Feedback Animation */
:global(.answer-feedback) {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #065f46;
  color: #dcfce7;
  padding: 20px 30px;
  border-radius: 8px;
  font-size: 1.2rem;
  font-weight: 600;
  z-index: 1000;
  animation: feedbackPulse 1.5s ease-in-out;
}

@keyframes feedbackPulse {
  0% { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
  50% { opacity: 1; transform: translate(-50%, -50%) scale(1.1); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(1); }
}

/* Wide layout: split question and choices into two columns */
@media (min-width: 1200px) {
  .question-card {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
  }
  .question-text { grid-column: 1 / -1; }
}

@media (max-width: 768px) {
  .test-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .completion-actions {
    flex-direction: column;
  }
  
  .question-card {
    padding: 20px;
  }
  
  .question-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>
