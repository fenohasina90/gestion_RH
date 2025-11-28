<template>
  <div class="interview-results-view">
    <div class="page-header">
      <h1 class="page-title">Résultats des Entretiens</h1>
      <div class="page-actions">
        <router-link to="/interviews/calendar" class="btn btn-secondary">
          📅 Calendrier
        </router-link>
        <button @click="exportResults" class="btn btn-primary">
          📄 Exporter PDF
        </button>
      </div>
    </div>

    <!-- Filtres -->
    <div class="filters-section">
      <div class="search-bar">
        <input
          type="text"
          v-model="filters.candidate"
          @input="debouncedApplyFilters"
          placeholder="Rechercher par candidat..."
          class="form-input search-input"
        />
      </div>
      
      <div class="filters">
        <select v-model="filters.position" @change="applyFilters" class="form-input filter-select">
          <option value="">Tous les postes</option>
          <option v-for="position in availablePositions" :key="position" :value="position">
            {{ position }}
          </option>
        </select>

        <select v-model="filters.status" @change="applyFilters" class="form-input filter-select">
          <option value="">Tous les statuts</option>
          <option value="Planifie">Planifié</option>
          <option value="En cours">En cours</option>
          <option value="Termine">Terminé</option>
        </select>

        <select v-model="filters.result" @change="applyFilters" class="form-input filter-select">
          <option value="">Tous les résultats</option>
          <option v-for="r in availableResults" :key="r" :value="r">{{ r }}</option>
        </select>

        <select v-model="period" @change="onPeriodChange" class="form-input filter-select">
          <option value="">Toutes les périodes</option>
          <option value="today">Aujourd'hui</option>
          <option value="week">Cette semaine</option>
          <option value="month">Ce mois</option>
        </select>

        <div class="date-range">
          <label>De</label>
          <input type="date" v-model="filters.dateFrom" @change="onDateRangeChange" class="form-input date-input" />
          <label>à</label>
          <input type="date" v-model="filters.dateTo" @change="onDateRangeChange" class="form-input date-input" />
          <button class="btn btn-secondary btn-sm" @click="clearDateRange">Effacer</button>
        </div>
      </div>
    </div>

    <!-- Statistiques -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">🎯</div>
          <div class="stat-content">
            <h3>{{ totalInterviews }}</h3>
            <p>Entretiens menés</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <h3>{{ hireRecommendations }}</h3>
            <p>Recommandés</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-content">
            <h3>{{ averageRating }}/5</h3>
            <p>Note moyenne</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <h3>{{ successRate }}%</h3>
            <p>Taux de réussite</p>
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
          :class="`recommendation-${result.recommendation}`"
        >
          <div class="result-header">
            <div class="candidate-info">
              <div class="candidate-avatar">
                {{ result.candidateName.split(' ').map(n => n[0]).join('') }}
              </div>
              <div class="candidate-details">
                <h3>{{ result.candidateName }}</h3>
                <p class="position">{{ result.position }}</p>
                <p class="interview-date">{{ formatDate(result.interviewDate) }}</p>
              </div>
            </div>
            
            <div class="result-summary">
              <div class="rating-display">
                <div class="stars">
                  <span 
                    v-for="i in 5" 
                    :key="i"
                    class="star"
                    :class="{ filled: i <= result.overallRating }"
                  >
                    ⭐
                  </span>
                </div>
                <span class="rating-text">{{ result.overallRating }}/5</span>
              </div>
              <div class="recommendation-badge" :class="`recommendation-${result.recommendation}`">
                {{ getRecommendationLabel(result.recommendation) }}
              </div>
            </div>
          </div>

          <div class="result-details">
            <div class="interview-info">
              <div class="info-item">
                <span class="info-label">Interviewer:</span>
                <span>{{ result.interviewer }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Durée:</span>
                <span>{{ result.duration }} minutes</span>
              </div>
              <div class="info-item">
                <span class="info-label">Type:</span>
                <span>{{ result.interviewType }}</span>
              </div>
            </div>

            <div class="skills-assessment" v-if="result.skillsRating">
              <h4>Évaluation des compétences:</h4>
              <div class="skills-grid">
                <div 
                  v-for="skill in result.skillsRating" 
                  :key="skill.name"
                  class="skill-item"
                >
                  <span class="skill-name">{{ skill.name }}</span>
                  <div class="skill-rating">
                    <div class="rating-bar">
                      <div 
                        class="rating-fill" 
                        :style="{ width: (skill.rating / 5) * 100 + '%' }"
                        :class="getRatingClass(skill.rating)"
                      ></div>
                    </div>
                    <span class="rating-value">{{ skill.rating }}/5</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="feedback-section" v-if="result.feedback">
              <h4>Commentaires:</h4>
              <div class="feedback-content">
                <div class="feedback-item" v-if="result.feedback.strengths">
                  <strong>Points forts:</strong>
                  <p>{{ result.feedback.strengths }}</p>
                </div>
                <div class="feedback-item" v-if="result.feedback.weaknesses">
                  <strong>Points d'amélioration:</strong>
                  <p>{{ result.feedback.weaknesses }}</p>
                </div>
                <div class="feedback-item" v-if="result.feedback.notes">
                  <strong>Notes générales:</strong>
                  <p>{{ result.feedback.notes }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="result-actions">
            <button @click="viewDetails(result)" class="btn btn-secondary btn-sm">
              👁️ Voir détails
            </button>
            <button @click="contactCandidate(result)" class="btn btn-primary btn-sm">
              ✉️ Contacter
            </button>
            <button 
              @click="scheduleNextInterview(result)" 
              class="btn btn-success btn-sm"
              v-if="result.recommendation === 'hire' || result.recommendation === 'maybe'"
            >
              📅 Entretien suivant
            </button>
            <button 
              @click="prepareContract(result)" 
              class="btn btn-success btn-sm"
              v-if="result.recommendation === 'hire'"
            >
              📄 Générer contrat
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="filteredResults.length === 0" class="empty-state">
      <div class="empty-icon">📊</div>
      <h3>Aucun résultat trouvé</h3>
      <p>{{ searchQuery ? 'Aucun résultat ne correspond à votre recherche.' : 'Aucun entretien n\'a encore été évalué.' }}</p>
    </div>

    <!-- Modal de détails -->
    <div v-if="showDetailsModal" class="modal-overlay" @click="showDetailsModal = false">
      <div class="modal details-modal" @click.stop>
        <div class="modal-header">
          <h3>Détails de l'entretien: {{ selectedResult?.candidateName }}</h3>
          <button @click="showDetailsModal = false" class="modal-close">×</button>
        </div>
        
        <div class="modal-content" v-if="selectedResult">
          <div class="candidate-summary">
            <h4>{{ selectedResult.candidateName }}</h4>
            <p><strong>Poste:</strong> {{ selectedResult.position }}</p>
            <p><strong>Date:</strong> {{ formatDate(selectedResult.interviewDate) }}</p>
            <p><strong>Interviewer:</strong> {{ selectedResult.interviewer }}</p>
            <p><strong>Durée:</strong> {{ selectedResult.duration }} minutes</p>
          </div>

          <div class="detailed-ratings">
            <h4>Évaluations détaillées:</h4>
            
            <div class="rating-category">
              <h5>Compétences techniques:</h5>
              <div class="skills-list">
                <div 
                  v-for="skill in selectedResult.skillsRating" 
                  :key="skill.name"
                  class="skill-detail"
                >
                  <span class="skill-name">{{ skill.name }}</span>
                  <div class="skill-rating">
                    <div class="stars">
                      <span 
                        v-for="i in 5" 
                        :key="i"
                        class="star"
                        :class="{ filled: i <= skill.rating }"
                      >
                        ⭐
                      </span>
                    </div>
                    <span class="rating-text">{{ skill.rating }}/5</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="rating-category" v-if="selectedResult.softSkillsRating">
              <h5>Compétences comportementales:</h5>
              <div class="skills-list">
                <div 
                  v-for="skill in selectedResult.softSkillsRating" 
                  :key="skill.name"
                  class="skill-detail"
                >
                  <span class="skill-name">{{ skill.name }}</span>
                  <div class="skill-rating">
                    <div class="stars">
                      <span 
                        v-for="i in 5" 
                        :key="i"
                        class="star"
                        :class="{ filled: i <= skill.rating }"
                      >
                        ⭐
                      </span>
                    </div>
                    <span class="rating-text">{{ skill.rating }}/5</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="detailed-feedback" v-if="selectedResult.feedback">
            <h4>Feedback détaillé:</h4>
            <div class="feedback-sections">
              <div class="feedback-section" v-if="selectedResult.feedback.strengths">
                <h5>Points forts:</h5>
                <p>{{ selectedResult.feedback.strengths }}</p>
              </div>
              <div class="feedback-section" v-if="selectedResult.feedback.weaknesses">
                <h5>Points d'amélioration:</h5>
                <p>{{ selectedResult.feedback.weaknesses }}</p>
              </div>
              <div class="feedback-section" v-if="selectedResult.feedback.notes">
                <h5>Notes générales:</h5>
                <p>{{ selectedResult.feedback.notes }}</p>
              </div>
              <div class="feedback-section" v-if="selectedResult.feedback.recommendation">
                <h5>Recommandation:</h5>
                <p>{{ selectedResult.feedback.recommendation }}</p>
              </div>
            </div>
          </div>

          <div class="final-recommendation">
            <h4>Recommandation finale:</h4>
            <div class="recommendation-summary">
              <div class="recommendation-badge large" :class="`recommendation-${selectedResult.recommendation}`">
                {{ getRecommendationLabel(selectedResult.recommendation) }}
              </div>
              <div class="overall-rating">
                <span>Note globale: </span>
                <div class="stars">
                  <span 
                    v-for="i in 5" 
                    :key="i"
                    class="star"
                    :class="{ filled: i <= selectedResult.overallRating }"
                  >
                    ⭐
                  </span>
                </div>
                <span class="rating-text">{{ selectedResult.overallRating }}/5</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="showDetailsModal = false" class="btn btn-secondary">
            Fermer
          </button>
          <button @click="contactCandidate(selectedResult)" class="btn btn-primary">
            ✉️ Contacter le candidat
          </button>
        </div>
      </div>
    </div>

    <!-- Modal de préparation de contrat -->
    <div v-if="showContractModal" class="modal-overlay" @click="showContractModal = false">
      <div class="modal details-modal" @click.stop>
        <div class="modal-header">
          <h3>Préparer le contrat d'essai</h3>
          <button @click="showContractModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-content">
          <div class="candidate-summary" v-if="selectedResult">
            <h4>{{ selectedResult.candidateName }}</h4>
            <p><strong>Poste:</strong> {{ selectedResult.position }}</p>
          </div>
          <form @submit.prevent="generateContract">
            <div class="form-group">
              <label for="startDate">Date de début du contrat:</label>
              <input type="date" id="startDate" v-model="contractForm.startDate" required class="form-input" />
            </div>
            <div class="form-group">
              <label for="duration">Durée du contrat (en mois):</label>
              <select id="duration" v-model="contractForm.duration" required class="form-input">
                <option value="">Sélectionner la durée</option>
                <option value="1">1 mois</option>
                <option value="2">2 mois</option>
                <option value="3">3 mois</option>
                <option value="4">4 mois</option>
                <option value="6">6 mois</option>
                <option value="12">12 mois</option>
              </select>
            </div>
            <div class="modal-actions">
              <button type="button" @click="showContractModal = false" class="btn btn-secondary">Annuler</button>
              <button type="submit" class="btn btn-primary">Générer le contrat</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'InterviewResultsView',
  data() {
    return {
      filters: {
        candidate: '',
        position: '',
        status: '',
        result: '',
        dateFrom: '',
        dateTo: ''
      },
      period: '',
      showDetailsModal: false,
      selectedResult: null,
      availablePositions: [],
      availableResults: [],
      results: [],
      loading: false,
      error: null,
      debounceHandle: null,
      showContractModal: false,
      contractForm: {
        startDate: '',
        duration: ''
      }
    }
  },
  async mounted() {
    await this.applyFilters()
  },
  computed: {
    filteredResults() {
      // Les résultats sont déjà filtrés côté backend, on se contente de trier par date
      return [...this.results].sort((a, b) => new Date(b.dateheure || b.interviewDate) - new Date(a.dateheure || a.interviewDate))
    },
    totalInterviews() {
      return this.results.length
    },
    hireRecommendations() {
      return this.results.filter(r => r.recommendation === 'hire').length
    },
    averageRating() {
      if (this.results.length === 0) return 0
      const total = this.results.reduce((sum, r) => sum + r.overallRating, 0)
      return (total / this.results.length).toFixed(1)
    },
    successRate() {
      if (this.results.length === 0) return 0
      const successful = this.results.filter(r => r.recommendation === 'hire' || r.recommendation === 'maybe').length
      return Math.round((successful / this.results.length) * 100)
    }
  },
  methods: {
    async applyFilters() {
      try {
        this.loading = true
        this.error = null
        const params = new URLSearchParams()
        Object.entries(this.filters).forEach(([k, v]) => {
          if (v && v !== '') params.append(k, v)
        })
        const url = params.toString() ? `/api/entretiens/results?${params.toString()}` : '/api/entretiens/results'
        const res = await fetch(url)
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        // Adapter les données côté front: mapper API Entretien -> vue
        this.results = (data || []).map(e => ({
          id: e.id,
          candidateName: e.idcandidat ? `${e.idcandidat.prenom || ''} ${e.idcandidat.nom || ''}`.trim() : 'Candidat',
          position: e.idannonce?.nomposte || 'Poste',
          interviewer: e.idstatut?.nom || '',
          interviewDate: e.dateheure,
          duration: 30,
          interviewType: 'Présentiel',
          overallRating: e.idresultat?.note || e.idresultat?.score || null,
          recommendation: e.idresultat?.appreciation ? (e.idresultat.appreciation.toLowerCase().includes('bon') ? 'hire' : e.idresultat.appreciation.toLowerCase().includes('moyen') ? 'maybe' : 'reject') : 'maybe',
          skillsRating: null,
          softSkillsRating: null,
          feedback: null,
          candidat: e.idcandidat,
        }))
        // Construire la liste des postes disponibles à partir des résultats
        this.availablePositions = Array.from(new Set(this.results.map(r => r.position).filter(Boolean)))
        // Construire la liste des résultats (appréciations) disponibles à partir des données
        this.availableResults = Array.from(new Set((data || [])
          .map(e => e.idresultat?.appreciation)
          .filter(v => v && v.trim() !== '')))
      } catch (err) {
        console.error('Erreur chargement résultats entretiens:', err)
        this.error = 'Impossible de charger les résultats.'
      } finally {
        this.loading = false
      }
    },
    debouncedApplyFilters() {
      if (this.debounceHandle) clearTimeout(this.debounceHandle)
      this.debounceHandle = setTimeout(() => this.applyFilters(), 400)
    },
    onPeriodChange() {
      const today = new Date()
      const to = today.toISOString().slice(0,10)
      let from = ''
      if (this.period === 'today') {
        from = to
      } else if (this.period === 'week') {
        const d = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 6)
        from = d.toISOString().slice(0,10)
      } else if (this.period === 'month') {
        const d = new Date(today.getFullYear(), today.getMonth(), 1)
        from = d.toISOString().slice(0,10)
      } else {
        this.filters.dateFrom = ''
        this.filters.dateTo = ''
        this.applyFilters()
        return
      }
      this.filters.dateFrom = from
      this.filters.dateTo = to
      this.applyFilters()
    },
    onDateRangeChange() {
      if (this.filters.dateFrom || this.filters.dateTo) {
        this.period = ''
      }
      this.applyFilters()
    },
    clearDateRange() {
      this.filters.dateFrom = ''
      this.filters.dateTo = ''
      this.period = ''
      this.applyFilters()
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
    getRecommendationLabel(recommendation) {
      const labels = {
        hire: '✅ Embaucher',
        maybe: '🤔 À considérer',
        reject: '❌ Rejeter'
      }
      return labels[recommendation] || recommendation
    },
    getRatingClass(rating) {
      if (rating >= 4) return 'excellent'
      if (rating >= 3) return 'good'
      if (rating >= 2) return 'average'
      return 'poor'
    },
    viewDetails(result) {
      this.selectedResult = result
      this.showDetailsModal = true
    },
    contactCandidate(result) {
      // Simulation d'envoi d'email
      alert(`Email envoyé à ${result.candidateName}`)
    },
    scheduleNextInterview(result) {
      // Redirection vers le calendrier
      this.$router.push(`/interviews/calendar?candidate=${result.candidateName}`)
    },
    prepareContract(result) {
      this.selectedResult = result
      this.contractForm.startDate = ''
      this.contractForm.duration = ''
      this.showContractModal = true
    },
    async generateContract() {
      try {
        const payload = {
          candidatId: this.selectedResult?.candidat?.id,
          startDate: this.contractForm.startDate,
          duration: parseInt(this.contractForm.duration),
          poste: this.selectedResult?.position
        }
        const res = await fetch('/api/contrats/generate', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        })
        if (!res.ok) {
          const msg = await res.text()
          throw new Error(msg || `HTTP ${res.status}`)
        }
        const contract = await res.json()
        this.showContractModal = false
        this.$router.push(`/contracts/view/${contract.id}`)
      } catch (e) {
        console.error('Erreur génération contrat:', e)
        alert('Erreur lors de la génération du contrat')
      }
    },
    exportResults() {
      // Simulation d'export PDF
      alert('Export PDF des résultats en cours...')
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

.result-card.recommendation-hire {
  border-left-color: #27ae60;
}

.result-card.recommendation-maybe {
  border-left-color: #f39c12;
}

.result-card.recommendation-reject {
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

.position {
  color: #059669 !important;
  font-weight: 600 !important;
}

.result-summary {
  text-align: center;
}

.rating-display {
  margin-bottom: 10px;
}

.stars {
  display: flex;
  gap: 2px;
  justify-content: center;
  margin-bottom: 5px;
}

.star {
  font-size: 16px;
  opacity: 0.3;
  transition: opacity 0.3s;
}

.star.filled {
  opacity: 1;
}

.rating-text {
  font-size: 14px;
  color: #666;
  font-weight: 600;
}

.recommendation-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.recommendation-badge.recommendation-hire {
  background: #d4edda;
  color: #155724;
}

.recommendation-badge.recommendation-maybe {
  background: #fff3cd;
  color: #856404;
}

.recommendation-badge.recommendation-reject {
  background: #f8d7da;
  color: #721c24;
}

.recommendation-badge.large {
  padding: 10px 20px;
  font-size: 16px;
}

.result-details {
  margin-bottom: 20px;
}

.interview-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-label {
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.skills-assessment h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.skill-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.skill-name {
  font-size: 14px;
  color: #555;
  min-width: 100px;
}

.skill-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-bar {
  width: 60px;
  height: 8px;
  background: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
}

.rating-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.rating-fill.excellent {
  background: #27ae60;
}

.rating-fill.good {
  background: #059669;
}

.rating-fill.average {
  background: #f39c12;
}

.rating-fill.poor {
  background: #e74c3c;
}

.rating-value {
  font-size: 12px;
  color: #666;
  min-width: 30px;
}

.feedback-section h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
}

.feedback-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.feedback-item {
  margin-bottom: 15px;
}

.feedback-item:last-child {
  margin-bottom: 0;
}

.feedback-item strong {
  color: #2c3e50;
  display: block;
  margin-bottom: 5px;
}

.feedback-item p {
  margin: 0;
  color: #555;
  line-height: 1.6;
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

/* Modal styles */
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

.details-modal {
  max-width: 800px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 0;
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

.candidate-summary {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.candidate-summary h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.candidate-summary p {
  margin: 0 0 5px 0;
  color: #555;
}

.detailed-ratings {
  margin-bottom: 30px;
}

.detailed-ratings h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

.rating-category {
  margin-bottom: 25px;
}

.rating-category h5 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.skill-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.skill-detail .skill-name {
  font-weight: 600;
  color: #2c3e50;
}

.skill-detail .skill-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detailed-feedback {
  margin-bottom: 30px;
}

.detailed-feedback h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

.feedback-sections {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feedback-sections .feedback-section {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.feedback-sections h5 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.feedback-sections p {
  margin: 0;
  color: #555;
  line-height: 1.6;
}

.final-recommendation {
  margin-bottom: 20px;
}

.final-recommendation h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.recommendation-summary {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.overall-rating {
  display: flex;
  align-items: center;
  gap: 10px;
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
  
  .interview-info {
    grid-template-columns: 1fr;
  }
  
  .skills-grid {
    grid-template-columns: 1fr;
  }
  
  .skill-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .skill-name {
    min-width: auto;
  }
  
  .result-actions {
    justify-content: center;
  }
  
  .details-modal {
    width: 95%;
    max-height: 90vh;
  }
  
  .recommendation-summary {
    flex-direction: column;
    text-align: center;
  }
  
  .skill-detail {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>
