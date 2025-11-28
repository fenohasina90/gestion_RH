<template>
  <div class="qcm-results-view">
    <div class="page-header">
      <h1 class="page-title">Résultats des Tests QCM</h1>
      <div class="page-actions">
        <router-link to="/qcm/create" class="btn btn-primary">
          ➕ Créer un nouveau test
        </router-link>
        <button @click="exportResults" class="btn btn-secondary">
          📄 Exporter PDF
        </button>
      </div>
    </div>

    <div class="filters-section">
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Rechercher par nom, test ou email..."
          class="form-input search-input"
        />
      </div>
      
      <div class="filters">
        <select v-model="selectedTest" class="form-input filter-select">
          <option value="">Tous les tests</option>
          <option v-for="test in availableTests" :key="test.id" :value="test.id">
            {{ test.title }}
          </option>
        </select>
        
        <select v-model="selectedStatus" class="form-input filter-select">
          <option value="">Tous les statuts</option>
          <option value="passed">Réussi</option>
          <option value="failed">Échoué</option>
        </select>
        
        <select v-model="selectedPeriod" @change="onPeriodChange" class="form-input filter-select">
          <option value="">Toutes les périodes</option>
          <option value="today">Aujourd'hui</option>
          <option value="week">Cette semaine</option>
          <option value="month">Ce mois</option>
        </select>

        <div class="date-range">
          <label>De</label>
          <input type="date" v-model="dateFrom" @change="onDateRangeChange" class="form-input date-input" />
          <label>à</label>
          <input type="date" v-model="dateTo" @change="onDateRangeChange" class="form-input date-input" />
          <button class="btn btn-secondary btn-sm" @click="clearDateRange">Effacer</button>
        </div>
      </div>
    </div>

    <!-- Statistiques globales -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <h3>{{ totalResults }}</h3>
            <p>Tests passés</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <h3>{{ passedResults }}</h3>
            <p>Réussis</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">❌</div>
          <div class="stat-content">
            <h3>{{ failedResults }}</h3>
            <p>Échoués</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <h3>{{ averageScore }}%</h3>
            <p>Score moyen</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Liste des résultats -->
    <div class="results-section">
      <div class="results-list">
        <div 
          v-for="result in filteredResults" 
          :key="result.id"
          class="result-card"
          :class="result.passed ? 'passed' : 'failed'"
        >
          <div class="result-header">
            <div class="candidate-info">
              <div class="candidate-avatar">
                {{ result.candidateName.split(' ').map(n => n[0]).join('') }}
              </div>
              <div class="candidate-details">
                <h3>{{ result.candidateName }}</h3>
                <p>{{ result.candidateEmail }}</p>
                <p class="test-title">{{ result.testTitle }}</p>
              </div>
            </div>
            
            <div class="result-score">
              <div class="score-display" :class="result.passed ? 'passed' : 'failed'">
                {{ result.score }}%
              </div>
              <div class="result-status">
                <span class="status-badge" :class="result.passed ? 'passed' : 'failed'">
                  {{ result.passed ? '✅ Réussi' : '❌ Échoué' }}
                </span>
              </div>
            </div>
          </div>

          <div class="result-details">
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">Date:</span>
                <span class="detail-value">{{ formatDate(result.completedAt) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Durée:</span>
                <span class="detail-value">{{ formatDuration(result.duration) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Questions:</span>
                <span class="detail-value">{{ result.correctAnswers }} / {{ result.totalQuestions }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Seuil:</span>
                <span class="detail-value">{{ result.passingScore }}%</span>
              </div>
            </div>
          </div>

          <div class="result-breakdown" v-if="result.categoryBreakdown">
            <h4>Détail par catégorie:</h4>
            <div class="breakdown-list">
              <div 
                v-for="category in result.categoryBreakdown" 
                :key="category.name"
                class="breakdown-item"
              >
                <span class="category-name">{{ category.name }}</span>
                <div class="category-progress">
                  <div class="progress-bar">
                    <div 
                      class="progress-fill" 
                      :style="{ width: category.percentage + '%' }"
                      :class="category.percentage >= 70 ? 'good' : category.percentage >= 50 ? 'average' : 'poor'"
                    ></div>
                  </div>
                  <span class="category-score">{{ category.score }} / {{ category.total }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="result-actions">
            <button @click="viewDetails(result)" class="btn btn-secondary btn-sm">
              👁️ Voir détails
            </button>
            <button @click="viewCandidateProfile(result)" class="btn btn-info btn-sm">
              👤 Profil candidat
            </button>
            <button @click="contactCandidate(result)" class="btn btn-primary btn-sm">
              ✉️ Contacter
            </button>
            <button 
              @click="scheduleInterview(result)" 
              class="btn btn-success btn-sm" 
              v-if="result.candidat && result.candidat.idstatut && result.candidat.idstatut.nom === 'Entretien'"
            >
              📅 Programmer entretien
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de programmation d'entretien -->
    <div v-if="showInterviewModal" class="modal-overlay" @click="showInterviewModal = false">
      <div class="modal interview-modal" @click.stop>
        <div class="modal-header">
          <h3>Programmer un entretien</h3>
          <button @click="showInterviewModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-content">
          <div class="form-group">
            <label for="interview-date">Date de l'entretien :</label>
            <input 
              type="date" 
              id="interview-date" 
              v-model="interviewForm.date" 
              class="form-input"
              :min="getTodayDate()"
            />
          </div>
          <div class="form-group">
            <label for="interview-time">Heure de l'entretien :</label>
            <input 
              type="time" 
              id="interview-time" 
              v-model="interviewForm.time" 
              class="form-input"
            />
          </div>
          <div class="candidate-info" v-if="selectedCandidate">
            <p><strong>Candidat :</strong> {{ selectedCandidate.prenom }} {{ selectedCandidate.nom }}</p>
            <p><strong>Score QCM :</strong> {{ selectedResult?.scorePercentage }}%</p>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="confirmScheduleInterview" class="btn btn-primary" :disabled="!interviewForm.date || !interviewForm.time">
            📅 Confirmer
          </button>
          <button @click="showInterviewModal = false" class="btn btn-secondary">
            Annuler
          </button>
        </div>
      </div>
    </div>

    <div v-if="filteredResults.length === 0" class="empty-state">
      <div class="empty-icon">📊</div>
      <h3>Aucun résultat trouvé</h3>
      <p>{{ searchQuery ? 'Aucun résultat ne correspond à votre recherche.' : 'Aucun test n\'a encore été passé.' }}</p>
    </div>

    <!-- Modal de détails -->
    <div v-if="showDetailsModal" class="modal-overlay" @click="showDetailsModal = false">
      <div class="modal details-modal" @click.stop>
        <div class="modal-header">
          <h3>Détails du test: {{ selectedResult?.testTitle }}</h3>
          <button @click="showDetailsModal = false" class="modal-close">×</button>
        </div>
        
        <div class="modal-content">
          <div class="candidate-summary">
            <h4>{{ selectedResult?.candidateName }}</h4>
            <p>{{ selectedResult?.candidateEmail }}</p>
            <p>Test passé le {{ formatDate(selectedResult?.completedAt) }}</p>
          </div>

          <div class="score-summary">
            <div class="score-item">
              <span>Score final:</span>
              <span class="score-value" :class="selectedResult?.passed ? 'passed' : 'failed'">
                {{ selectedResult?.score }}%
              </span>
            </div>
            <div class="score-item">
              <span>Bonnes réponses:</span>
              <span>{{ selectedResult?.correctAnswers }} / {{ selectedResult?.totalQuestions }}</span>
            </div>
            <div class="score-item">
              <span>Temps utilisé:</span>
              <span>{{ formatDuration(selectedResult?.duration) }}</span>
            </div>
          </div>

          <div class="questions-review" v-if="selectedResult?.questionDetails">
            <h4>Révision des questions:</h4>
            <div class="questions-list">
              <div 
                v-for="(question, index) in selectedResult.questionDetails" 
                :key="index"
                class="question-item"
                :class="question.correct ? 'correct' : 'incorrect'"
              >
                <div class="question-header">
                  <span class="question-number">Q{{ index + 1 }}</span>
                  <span class="question-result">
                    {{ question.correct ? '✅' : '❌' }}
                  </span>
                </div>
                <div class="question-text">{{ question.text }}</div>
                <div class="question-answers">
                  <div class="answer-given">
                    <strong>Réponse donnée:</strong> {{ question.userAnswer }}
                  </div>
                  <div class="answer-correct" v-if="!question.correct">
                    <strong>Bonne réponse:</strong> {{ question.correctAnswer }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="showDetailsModal = false" class="btn btn-secondary">
            Fermer
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'QCMResultsView',
  data() {
    return {
      searchQuery: '',
      selectedTest: '',
      selectedStatus: '',
      selectedPeriod: '',
      dateFrom: '',
      dateTo: '',
      loading: true,
      error: null,
      results: [],
      availableTests: [],
      showInterviewModal: false,
      selectedCandidate: null,
      selectedResult: null,
      interviewForm: {
        date: '',
        time: ''
      }
    }
  },
  async mounted() {
    await this.loadResults()
  },
  computed: {
    filteredResults() {
      let filtered = this.results

      // Filtrage par recherche
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filtered = filtered.filter(result => 
          result.candidateName.toLowerCase().includes(query) ||
          result.candidateEmail.toLowerCase().includes(query) ||
          result.testTitle.toLowerCase().includes(query)
        )
      }

      // Filtrage par test
      if (this.selectedTest) {
        filtered = filtered.filter(result => 
          result.testId === parseInt(this.selectedTest)
        )
      }

      // Filtrage par statut
      if (this.selectedStatus) {
        const passed = this.selectedStatus === 'passed'
        filtered = filtered.filter(result => result.passed === passed)
      }

      // Filtrage par période prédéfinie ou plage de dates
      const inRange = (d, fromStr, toStr) => {
        const date = new Date(d)
        if (fromStr) {
          const from = new Date(fromStr)
          // normaliser début de journée
          from.setHours(0,0,0,0)
          if (date < from) return false
        }
        if (toStr) {
          const to = new Date(toStr)
          // inclure toute la journée de fin
          to.setHours(23,59,59,999)
          if (date > to) return false
        }
        return true
      }

      if (this.dateFrom || this.dateTo) {
        filtered = filtered.filter(r => inRange(r.completedAt, this.dateFrom, this.dateTo))
      } else if (this.selectedPeriod) {
        const now = new Date()
        let from = null
        let to = new Date()
        to.setHours(23,59,59,999)
        if (this.selectedPeriod === 'today') {
          from = new Date()
          from.setHours(0,0,0,0)
        } else if (this.selectedPeriod === 'week') {
          from = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 6)
          from.setHours(0,0,0,0)
        } else if (this.selectedPeriod === 'month') {
          from = new Date(now.getFullYear(), now.getMonth(), 1)
          from.setHours(0,0,0,0)
        }
        filtered = filtered.filter(r => inRange(r.completedAt, from?.toISOString().slice(0,10), to.toISOString().slice(0,10)))
      }

      return filtered.sort((a, b) => new Date(b.completedAt) - new Date(a.completedAt))
    },
    totalResults() {
      return this.results.length
    },
    passedResults() {
      return this.results.filter(r => r.passed).length
    },
    failedResults() {
      return this.results.filter(r => !r.passed).length
    },
    averageScore() {
      if (this.results.length === 0) return 0
      const total = this.results.reduce((sum, r) => sum + r.score, 0)
      return Math.round(total / this.results.length)
    }
  },
  methods: {
    onPeriodChange() {
      const today = new Date()
      const to = today.toISOString().slice(0,10)
      let from = ''
      if (this.selectedPeriod === 'today') {
        from = to
      } else if (this.selectedPeriod === 'week') {
        const d = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 6)
        from = d.toISOString().slice(0,10)
      } else if (this.selectedPeriod === 'month') {
        const d = new Date(today.getFullYear(), today.getMonth(), 1)
        from = d.toISOString().slice(0,10)
      } else {
        // aucune période
        this.dateFrom = ''
        this.dateTo = ''
        return
      }
      this.dateFrom = from
      this.dateTo = to
    },
    onDateRangeChange() {
      // Si l'utilisateur saisit manuellement une plage, annuler la période prédéfinie
      if (this.dateFrom || this.dateTo) {
        this.selectedPeriod = ''
      }
    },
    clearDateRange() {
      this.dateFrom = ''
      this.dateTo = ''
      this.selectedPeriod = ''
    },
    async loadResults() {
      try {
        this.loading = true
        const response = await axios.get('/api/qcm/admin/results')
        
        if (response.data.success) {
          this.results = response.data.results
          this.availableTests = response.data.tests
        } else {
          this.error = response.data.message
        }
      } catch (error) {
        console.error('Erreur lors du chargement des résultats:', error)
        this.error = 'Erreur lors du chargement des résultats'
      } finally {
        this.loading = false
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatDuration(seconds) {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}min ${remainingSeconds}s`
    },
    viewDetails(result) {
      this.selectedResult = result
      this.showDetailsModal = true
    },
    viewCandidateProfile(result) {
      // Redirection vers le profil du candidat
      this.$router.push(`/candidats/${result.candidatId}`)
    },
    contactCandidate(result) {
      // Simulation d'envoi d'email
      alert(`Email envoyé à ${result.candidat.prenom} ${result.candidat.nom}`)
    },
    scheduleInterview(result) {
      this.selectedCandidate = result.candidat
      this.selectedResult = result
      this.interviewForm.date = ''
      this.interviewForm.time = ''
      this.showInterviewModal = true
    },
    async confirmScheduleInterview() {
      try {
        const dateTime = `${this.interviewForm.date}T${this.interviewForm.time}:00`
        const response = await axios.post('/api/entretiens/create', {
          candidatId: this.selectedCandidate.id,
          annonceId: this.selectedResult.annonceId,
          date: this.interviewForm.date,
          time: this.interviewForm.time
        })
        
        alert(`Entretien programmé pour ${this.selectedCandidate.prenom} ${this.selectedCandidate.nom} le ${this.interviewForm.date} à ${this.interviewForm.time}`)
        this.showInterviewModal = false
        
        // Optionally reload results to reflect changes
        await this.loadResults()
      } catch (error) {
        console.error('Erreur lors de la programmation de l\'entretien:', error)
        if (error.response && error.response.data && error.response.data.error) {
          alert(`Erreur: ${error.response.data.error}`)
        } else {
          alert('Erreur lors de la programmation de l\'entretien')
        }
      }
    },
    getTodayDate() {
      const today = new Date()
      return today.toISOString().split('T')[0]
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-actions {
  display: flex;
  gap: 10px;
}

.filters-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  max-width: 400px;
}

.filters {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-select {
  min-width: 180px;
}

.stats-section {
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  font-size: 32px;
  opacity: 0.8;
}

.stat-content h3 {
  margin: 0 0 5px 0;
  font-size: 28px;
  color: #2c3e50;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  border-left: 4px solid #059669;
}

.result-card.passed {
  border-left-color: #27ae60;
}

.result-card.failed {
  border-left-color: #e74c3c;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.candidate-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.candidate-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #059669, #047857);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.candidate-details h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 18px;
}

.candidate-details p {
  margin: 0 0 3px 0;
  color: #666;
  font-size: 14px;
}

.test-title {
  color: #059669 !important;
  font-weight: 600 !important;
}

.result-score {
  text-align: center;
}

.score-display {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.score-display.passed {
  color: #27ae60;
}

.score-display.failed {
  color: #e74c3c;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.passed {
  background: #d4edda;
  color: #155724;
}

.status-badge.failed {
  background: #f8d7da;
  color: #721c24;
}

.result-details {
  margin-bottom: 20px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-label {
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.detail-value {
  color: #333;
}

.result-breakdown {
  margin-bottom: 20px;
}

.result-breakdown h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.breakdown-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.category-name {
  min-width: 120px;
  font-size: 14px;
  color: #555;
}

.category-progress {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.progress-fill.good {
  background: #27ae60;
}

.progress-fill.average {
  background: #f39c12;
}

.progress-fill.poor {
  background: #e74c3c;
}

.category-score {
  font-size: 14px;
  color: #666;
  min-width: 60px;
}

.result-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-state p {
  color: #666;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  padding: 20px;
}

.interview-modal {
  max-width: 500px;
  width: 90%;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
  color: #2c3e50;
}

.form-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-input:focus {
  outline: none;
  border-color: #059669;
  box-shadow: 0 0 0 2px rgba(5, 150, 105, 0.2);
}

.candidate-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-top: 15px;
}

.candidate-info p {
  margin: 5px 0;
  color: #2c3e50;
}

.modal-actions {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .page-actions {
    justify-content: center;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .result-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .breakdown-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .category-name {
    min-width: auto;
  }
  
  .result-actions {
    justify-content: center;
  }
  
  .details-modal {
    width: 95%;
    max-height: 90vh;
  }
  
  .score-item {
    flex-direction: column;
    gap: 5px;
  }
}
</style>
