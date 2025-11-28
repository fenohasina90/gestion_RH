<template>
  <div class="take-qcm-view">
    <!-- Sélection du test -->
    <div v-if="!selectedTest && !testInProgress" class="test-selection">
      <div class="page-header">
        <h1 class="page-title">Tests QCM Disponibles</h1>
        <p class="page-subtitle">Choisissez un test pour évaluer vos compétences</p>
      </div>

      <div class="tests-grid">
        <div 
          v-for="test in availableTests" 
          :key="test.id"
          class="test-card"
          @click="selectTest(test)"
        >
          <div class="test-header">
            <h3>{{ test.title }}</h3>
            <span class="test-category">{{ getCategoryLabel(test.category) }}</span>
          </div>
          
          <div class="test-info">
            <div class="info-item">
              <span class="info-icon">⏱️</span>
              <span>{{ test.duration }} minutes</span>
            </div>
            <div class="info-item">
              <span class="info-icon">❓</span>
              <span>{{ test.questionsCount }} questions</span>
            </div>
            <div class="info-item">
              <span class="info-icon">🎯</span>
              <span>{{ test.passingScore }}% pour réussir</span>
            </div>
          </div>

          <div class="test-description">
            <p>{{ test.description }}</p>
          </div>

          <div class="test-actions">
            <button class="btn btn-primary">
              ▶️ Commencer le test
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Instructions avant le test -->
    <div v-if="selectedTest && !testInProgress" class="test-instructions">
      <div class="instructions-card">
        <h2>Instructions pour le test: {{ selectedTest.title }}</h2>
        
        <div class="test-details">
          <div class="detail-item">
            <strong>Durée:</strong> {{ selectedTest.duration }} minutes
          </div>
          <div class="detail-item">
            <strong>Nombre de questions:</strong> {{ selectedTest.questionsCount }}
          </div>
          <div class="detail-item">
            <strong>Score de réussite:</strong> {{ selectedTest.passingScore }}%
          </div>
          <div class="detail-item">
            <strong>Type:</strong> {{ getCategoryLabel(selectedTest.category) }}
          </div>
        </div>

        <div class="instructions-content">
          <h3>Règles importantes:</h3>
          <ul>
            <li>Une fois le test commencé, vous ne pourrez pas le mettre en pause</li>
            <li>Chaque question doit être répondue avant de passer à la suivante</li>
            <li>Vous ne pourrez pas revenir en arrière</li>
            <li>Le test se terminera automatiquement à la fin du temps imparti</li>
            <li>Assurez-vous d'avoir une connexion internet stable</li>
          </ul>
        </div>

        <div class="instructions-actions">
          <button @click="selectedTest = null" class="btn btn-secondary">
            ← Choisir un autre test
          </button>
          <button @click="startTest" class="btn btn-primary">
            🚀 Commencer maintenant
          </button>
        </div>
      </div>
    </div>

    <!-- Test en cours -->
    <div v-if="testInProgress" class="test-interface">
      <div class="test-header">
        <div class="test-info">
          <h2>{{ selectedTest.title }}</h2>
          <div class="progress-info">
            Question {{ currentQuestionIndex + 1 }} sur {{ currentTest.questions.length }}
          </div>
        </div>
        
        <div class="test-timer">
          <div class="timer-display" :class="{ warning: timeRemaining < 300 }">
            ⏰ {{ formatTime(timeRemaining) }}
          </div>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: progressPercentage + '%' }"
            ></div>
          </div>
        </div>
      </div>

      <div class="question-container">
        <div class="question-card">
          <div class="question-number">
            Question {{ currentQuestionIndex + 1 }}
          </div>
          
          <div class="question-text">
            {{ currentQuestion.text }}
          </div>

          <div class="answers-container">
            <div 
              v-for="(answer, index) in currentQuestion.answers" 
              :key="index"
              class="answer-option"
              :class="{ selected: isAnswerSelected(index) }"
              @click="selectAnswer(index)"
            >
              <div class="answer-radio">
                <input 
                  v-if="currentQuestion.type === 'single'"
                  type="radio" 
                  :name="`question-${currentQuestionIndex}`"
                  :value="index"
                  v-model="userAnswers[currentQuestionIndex]"
                />
                <input 
                  v-else
                  type="checkbox" 
                  :value="index"
                  v-model="userAnswers[currentQuestionIndex]"
                />
              </div>
              <div class="answer-text">
                {{ answer.text }}
              </div>
            </div>
          </div>

          <div class="question-actions">
            <button 
              @click="nextQuestion" 
              class="btn btn-primary"
              :disabled="!hasAnswered"
            >
              <span v-if="currentQuestionIndex < currentTest.questions.length - 1">
                Suivant →
              </span>
              <span v-else>
                Terminer le test
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Résultats -->
    <div v-if="showResults" class="test-results">
      <div class="results-card">
        <div class="results-header">
          <h2>Résultats du test: {{ selectedTest.title }}</h2>
          <div class="score-display" :class="{ passed: testPassed, failed: !testPassed }">
            {{ finalScore }}%
          </div>
        </div>

        <div class="results-summary">
          <div class="summary-item">
            <span class="summary-label">Score obtenu:</span>
            <span class="summary-value">{{ correctAnswers }} / {{ currentTest.questions.length }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Pourcentage:</span>
            <span class="summary-value">{{ finalScore }}%</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Temps utilisé:</span>
            <span class="summary-value">{{ formatTime(selectedTest.duration * 60 - timeRemaining) }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Statut:</span>
            <span class="summary-value" :class="{ passed: testPassed, failed: !testPassed }">
              {{ testPassed ? '✅ Réussi' : '❌ Échoué' }}
            </span>
          </div>
        </div>

        <div class="results-message">
          <div v-if="testPassed" class="success-message">
            <h3>🎉 Félicitations !</h3>
            <p>Vous avez réussi ce test avec un score de {{ finalScore }}%. Votre performance démontre une bonne maîtrise du sujet.</p>
          </div>
          <div v-else class="failure-message">
            <h3>📚 Continuez vos efforts</h3>
            <p>Vous avez obtenu {{ finalScore }}%, ce qui est en dessous du seuil de réussite de {{ selectedTest.passingScore }}%. N'hésitez pas à réviser et à retenter le test.</p>
          </div>
        </div>

        <div class="results-actions">
          <button @click="resetTest" class="btn btn-secondary">
            🔄 Passer un autre test
          </button>
          <button @click="retakeTest" class="btn btn-primary" v-if="!testPassed">
            🔁 Retenter ce test
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TakeQCMView',
  data() {
    return {
      selectedTest: null,
      testInProgress: false,
      showResults: false,
      currentQuestionIndex: 0,
      userAnswers: [],
      timeRemaining: 0,
      timer: null,
      currentTest: null,
      availableTests: [
        {
          id: 1,
          title: 'JavaScript Fondamentaux',
          category: 'technical',
          description: 'Testez vos connaissances de base en JavaScript: variables, fonctions, objets et DOM.',
          duration: 30,
          questionsCount: 15,
          passingScore: 70,
          questions: [
            {
              text: 'Quelle est la différence entre let et var en JavaScript ?',
              type: 'single',
              answers: [
                { text: 'Il n\'y a aucune différence' },
                { text: 'let a une portée de bloc, var a une portée de fonction' },
                { text: 'var est plus récent que let' },
                { text: 'let ne peut pas être redéclaré' }
              ],
              correctAnswer: 1,
              points: 1
            },
            {
              text: 'Quelles sont les méthodes pour créer un objet en JavaScript ?',
              type: 'multiple',
              answers: [
                { text: 'Object literal {}', isCorrect: true },
                { text: 'new Object()', isCorrect: true },
                { text: 'Object.create()', isCorrect: true },
                { text: 'function constructor', isCorrect: true }
              ],
              points: 2
            }
          ]
        },
        {
          id: 2,
          title: 'Vue.js Avancé',
          category: 'technical',
          description: 'Évaluez vos compétences avancées en Vue.js: composants, réactivité et écosystème.',
          duration: 45,
          questionsCount: 20,
          passingScore: 75,
          questions: [
            {
              text: 'Qu\'est-ce que la réactivité dans Vue.js ?',
              type: 'single',
              answers: [
                { text: 'La capacité à réagir aux événements utilisateur' },
                { text: 'La mise à jour automatique de l\'interface quand les données changent' },
                { text: 'La gestion des animations' },
                { text: 'La communication entre composants' }
              ],
              correctAnswer: 1,
              points: 1
            }
          ]
        },
        {
          id: 3,
          title: 'Communication et Leadership',
          category: 'soft-skills',
          description: 'Évaluez vos compétences en communication, leadership et travail en équipe.',
          duration: 25,
          questionsCount: 12,
          passingScore: 65,
          questions: [
            {
              text: 'Quelle est la meilleure approche pour résoudre un conflit en équipe ?',
              type: 'single',
              answers: [
                { text: 'Éviter le conflit' },
                { text: 'Imposer sa solution' },
                { text: 'Écouter toutes les parties et chercher un compromis' },
                { text: 'Laisser l\'équipe se débrouiller' }
              ],
              correctAnswer: 2,
              points: 1
            }
          ]
        }
      ]
    }
  },
  computed: {
    currentQuestion() {
      if (!this.currentTest || !this.currentTest.questions) return null
      return this.currentTest.questions[this.currentQuestionIndex]
    },
    hasAnswered() {
      const answer = this.userAnswers[this.currentQuestionIndex]
      if (this.currentQuestion?.type === 'single') {
        return answer !== null && answer !== undefined
      } else {
        return Array.isArray(answer) && answer.length > 0
      }
    },
    progressPercentage() {
      if (!this.currentTest) return 0
      return ((this.currentQuestionIndex + 1) / this.currentTest.questions.length) * 100
    },
    correctAnswers() {
      if (!this.currentTest) return 0
      let correct = 0
      
      this.currentTest.questions.forEach((question, index) => {
        const userAnswer = this.userAnswers[index]
        
        if (question.type === 'single') {
          if (userAnswer === question.correctAnswer) {
            correct++
          }
        } else {
          // Pour les questions à choix multiples
          const correctAnswers = question.answers
            .map((answer, i) => answer.isCorrect ? i : -1)
            .filter(i => i !== -1)
          
          if (Array.isArray(userAnswer) && 
              userAnswer.length === correctAnswers.length &&
              userAnswer.every(ans => correctAnswers.includes(ans))) {
            correct++
          }
        }
      })
      
      return correct
    },
    finalScore() {
      if (!this.currentTest) return 0
      return Math.round((this.correctAnswers / this.currentTest.questions.length) * 100)
    },
    testPassed() {
      return this.finalScore >= this.selectedTest.passingScore
    }
  },
  methods: {
    getCategoryLabel(category) {
      const labels = {
        technical: 'Technique',
        'soft-skills': 'Compétences comportementales',
        language: 'Langue',
        general: 'Culture générale'
      }
      return labels[category] || category
    },
    selectTest(test) {
      this.selectedTest = test
    },
    startTest() {
      this.testInProgress = true
      this.currentTest = { ...this.selectedTest }
      this.timeRemaining = this.selectedTest.duration * 60 // Convert to seconds
      this.userAnswers = new Array(this.currentTest.questions.length).fill(null)
      
      // Initialize answers for multiple choice questions
      this.currentTest.questions.forEach((question, index) => {
        if (question.type === 'multiple') {
          this.userAnswers[index] = []
        }
      })
      
      this.startTimer()
    },
    startTimer() {
      this.timer = setInterval(() => {
        this.timeRemaining--
        if (this.timeRemaining <= 0) {
          this.finishTest()
        }
      }, 1000)
    },
    stopTimer() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    },
    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
    },
    isAnswerSelected(index) {
      const answer = this.userAnswers[this.currentQuestionIndex]
      if (this.currentQuestion?.type === 'single') {
        return answer === index
      } else {
        return Array.isArray(answer) && answer.includes(index)
      }
    },
    selectAnswer(index) {
      if (this.currentQuestion?.type === 'single') {
        this.userAnswers[this.currentQuestionIndex] = index
      } else {
        if (!Array.isArray(this.userAnswers[this.currentQuestionIndex])) {
          this.userAnswers[this.currentQuestionIndex] = []
        }
        
        const answers = this.userAnswers[this.currentQuestionIndex]
        const answerIndex = answers.indexOf(index)
        
        if (answerIndex > -1) {
          answers.splice(answerIndex, 1)
        } else {
          answers.push(index)
        }
      }
    },
    nextQuestion() {
      if (this.currentQuestionIndex < this.currentTest.questions.length - 1) {
        this.currentQuestionIndex++
      } else {
        this.finishTest()
      }
    },
    finishTest() {
      this.stopTimer()
      this.testInProgress = false
      this.showResults = true
    },
    resetTest() {
      this.selectedTest = null
      this.testInProgress = false
      this.showResults = false
      this.currentQuestionIndex = 0
      this.userAnswers = []
      this.currentTest = null
      this.stopTimer()
    },
    retakeTest() {
      this.testInProgress = false
      this.showResults = false
      this.currentQuestionIndex = 0
      this.userAnswers = []
      this.stopTimer()
    }
  },
  beforeUnmount() {
    this.stopTimer()
  }
}
</script>

<style scoped>
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-subtitle {
  color: #666;
  font-size: 18px;
  margin-top: 10px;
}

.tests-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.test-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.test-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
  border-color: #3498db;
}

.test-header {
  margin-bottom: 15px;
}

.test-header h3 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 20px;
}

.test-category {
  background: #3498db;
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.test-info {
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.info-icon {
  font-size: 16px;
}

.test-description {
  margin: 20px 0;
}

.test-description p {
  color: #555;
  line-height: 1.6;
  margin: 0;
}

.test-actions {
  margin-top: 20px;
}

.instructions-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  max-width: 800px;
  margin: 0 auto;
}

.instructions-card h2 {
  color: #2c3e50;
  margin-bottom: 30px;
  text-align: center;
}

.test-details {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.detail-item {
  color: #555;
}

.instructions-content h3 {
  color: #2c3e50;
  margin-bottom: 15px;
}

.instructions-content ul {
  color: #555;
  line-height: 1.8;
  padding-left: 20px;
}

.instructions-content li {
  margin-bottom: 8px;
}

.instructions-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 30px;
}

.test-interface {
  max-width: 900px;
  margin: 0 auto;
}

.test-header {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-info h2 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.progress-info {
  color: #666;
  font-size: 14px;
}

.test-timer {
  text-align: right;
}

.timer-display {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 10px;
}

.timer-display.warning {
  color: #e74c3c;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.progress-bar {
  width: 200px;
  height: 8px;
  background: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3498db, #2980b9);
  transition: width 0.3s ease;
}

.question-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.question-number {
  color: #3498db;
  font-weight: bold;
  margin-bottom: 15px;
}

.question-text {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 30px;
  line-height: 1.6;
}

.answers-container {
  margin-bottom: 30px;
}

.answer-option {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.answer-option:hover {
  border-color: #3498db;
  background: #f8fbff;
}

.answer-option.selected {
  border-color: #3498db;
  background: #f8fbff;
}

.answer-radio input {
  margin: 0;
}

.answer-text {
  flex: 1;
  color: #2c3e50;
}

.question-actions {
  text-align: center;
}

.results-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  max-width: 600px;
  margin: 0 auto;
}

.results-header {
  text-align: center;
  margin-bottom: 30px;
}

.results-header h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.score-display {
  font-size: 48px;
  font-weight: bold;
  padding: 20px;
  border-radius: 50%;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.score-display.passed {
  background: #d4edda;
  color: #155724;
}

.score-display.failed {
  background: #f8d7da;
  color: #721c24;
}

.results-summary {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-label {
  font-weight: 600;
  color: #555;
}

.summary-value.passed {
  color: #27ae60;
  font-weight: bold;
}

.summary-value.failed {
  color: #e74c3c;
  font-weight: bold;
}

.results-message {
  margin-bottom: 30px;
}

.success-message, .failure-message {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
}

.success-message {
  background: #d4edda;
  color: #155724;
}

.failure-message {
  background: #f8d7da;
  color: #721c24;
}

.success-message h3, .failure-message h3 {
  margin: 0 0 10px 0;
}

.success-message p, .failure-message p {
  margin: 0;
  line-height: 1.6;
}

.results-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

@media (max-width: 768px) {
  .tests-grid {
    grid-template-columns: 1fr;
  }
  
  .test-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .test-details {
    grid-template-columns: 1fr;
  }
  
  .instructions-actions {
    flex-direction: column;
  }
  
  .results-actions {
    flex-direction: column;
  }
  
  .progress-bar {
    width: 100%;
  }
}
</style>
