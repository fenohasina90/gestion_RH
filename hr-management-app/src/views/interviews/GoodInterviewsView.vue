<template>
  <div class="good-interviews-view">
    <div class="page-header">
      <h1 class="page-title">Entretiens - Bon Niveau</h1>
      <div class="page-actions">
        <router-link to="/interviews/calendar" class="btn btn-secondary">
          📅 Voir le calendrier
        </router-link>
      </div>
    </div>

    <div class="stats-card">
      <div class="stat-item">
        <span class="stat-number">{{ interviews.length }}</span>
        <span class="stat-label">Entretiens avec bon niveau</span>
      </div>
    </div>

    <div class="interviews-list">
      <div v-if="isLoading" class="loading">
        Chargement des entretiens...
      </div>

      <div v-else-if="interviews.length === 0" class="empty-state">
        <h3>Aucun entretien avec "Bon niveau"</h3>
        <p>Il n'y a actuellement aucun entretien évalué avec le résultat "Bon niveau".</p>
      </div>

      <div v-else class="interviews-grid">
        <div 
          v-for="interview in interviews" 
          :key="interview.id"
          class="interview-card"
        >
          <div class="interview-header">
            <h3>{{ interview.candidateName }}</h3>
            <span class="interview-status" :class="`status-${interview.status}`">
              {{ getStatusLabel(interview.status) }}
            </span>
          </div>

          <div class="interview-details">
            <div class="detail-row">
              <span class="detail-label">📧 Email:</span>
              <span>{{ interview.candidateEmail }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">💼 Poste:</span>
              <span>{{ interview.position }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">📅 Date:</span>
              <span>{{ formatDate(interview.date) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">⏰ Heure:</span>
              <span>{{ formatTime(interview.startTime) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">⭐ Résultat:</span>
              <span class="result-badge">{{ interview.resultNote }}/5 - {{ interview.resultAppreciation }}</span>
            </div>
          </div>

          <div class="interview-actions">
            <button @click="viewDetails(interview)" class="btn btn-primary btn-sm">
              👁️ Voir détails
            </button>
            <button @click="contactCandidate(interview)" class="btn btn-success btn-sm">
              📞 Contacter
            </button>
            <button @click="prepareContract(interview)" class="btn btn-warning btn-sm">
              📄 Prendre et préparer contrat d'essai
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de détails -->
    <div v-if="showDetailsModal" class="modal-overlay" @click="showDetailsModal = false">
      <div class="modal interview-details-modal" @click.stop>
        <div class="modal-header">
          <h3>Détails de l'entretien</h3>
          <button @click="showDetailsModal = false" class="modal-close">×</button>
        </div>
        
        <div class="modal-content" v-if="selectedInterview">
          <div class="candidate-info">
            <h4>{{ selectedInterview.candidateName }}</h4>
            <p><strong>Email:</strong> {{ selectedInterview.candidateEmail }}</p>
            <p><strong>Poste:</strong> {{ selectedInterview.position }}</p>
          </div>

          <div class="interview-info">
            <h4>Informations de l'entretien</h4>
            <p><strong>Date:</strong> {{ formatDate(selectedInterview.date) }}</p>
            <p><strong>Heure:</strong> {{ formatTime(selectedInterview.startTime) }}</p>
            <p><strong>Statut:</strong> {{ getStatusLabel(selectedInterview.status) }}</p>
          </div>

          <div class="result-info">
            <h4>Résultat</h4>
            <div class="result-display">
              <span class="result-score">{{ selectedInterview.resultNote }}/5</span>
              <span class="result-appreciation">{{ selectedInterview.resultAppreciation }}</span>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="contactCandidate(selectedInterview)" class="btn btn-success">
            📞 Contacter le candidat
          </button>
          <button @click="showDetailsModal = false" class="btn btn-secondary">
            Fermer
          </button>
        </div>
      </div>
    </div>

    <!-- Modal de préparation de contrat -->
    <div v-if="showContractModal" class="modal-overlay" @click="showContractModal = false">
      <div class="modal contract-modal" @click.stop>
        <div class="modal-header">
          <h3>Préparer le contrat d'essai</h3>
          <button @click="showContractModal = false" class="close-btn">&times;</button>
        </div>
        <div class="modal-body">
          <div class="candidate-info">
            <h4>Candidat sélectionné</h4>
            <p><strong>Nom:</strong> {{ selectedInterview?.candidateName || (selectedInterview?.idcandidat ? `${selectedInterview.idcandidat.nom} ${selectedInterview.idcandidat.prenom}` : 'N/A') }}</p>
            <p><strong>Poste:</strong> {{ selectedInterview?.position || getJobPosition(selectedInterview) }}</p>
          </div>
          
          <form @submit.prevent="generateContract">
            <div class="form-group">
              <label for="startDate">Date de début du contrat:</label>
              <input 
                type="date" 
                id="startDate" 
                v-model="contractForm.startDate" 
                required 
                class="form-control"
              >
            </div>
            
            <div class="form-group">
              <label for="duration">Durée du contrat (en mois):</label>
              <select 
                id="duration" 
                v-model="contractForm.duration" 
                required 
                class="form-control"
              >
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
              <button type="button" @click="showContractModal = false" class="btn btn-secondary">
                Annuler
              </button>
              <button type="submit" class="btn btn-primary">
                Générer le contrat
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GoodInterviewsView',
  data() {
    return {
      interviews: [],
      isLoading: false,
      showDetailsModal: false,
      showContractModal: false,
      selectedInterview: null,
      contractForm: {
        startDate: '',
        duration: ''
      }
    }
  },
  methods: {
    async fetchGoodInterviews() {
      this.isLoading = true
      try {
        const response = await fetch('http://localhost:8080/api/entretiens/bon-niveau')
        if (response.ok) {
          const entretiens = await response.json()
          this.interviews = entretiens.map(entretien => ({
            id: entretien.id,
            candidateName: entretien.idcandidat ? `${entretien.idcandidat.nom} ${entretien.idcandidat.prenom}` : 'N/A',
            candidateEmail: entretien.idcandidat?.idcomptecandidat?.email || 'N/A',
            position: entretien.idannonce?.nomposte || entretien.idcandidat?.idannonce?.nomposte || 'N/A',
            date: entretien.dateheure ? entretien.dateheure.split('T')[0] : '',
            startTime: entretien.dateheure ? entretien.dateheure.split('T')[1].substring(0, 5) : '',
            status: entretien.idstatut?.nom?.toLowerCase() === 'planifie' ? 'scheduled' : 
                   entretien.idstatut?.nom?.toLowerCase() === 'en cours' ? 'confirmed' :
                   entretien.idstatut?.nom?.toLowerCase() === 'termine' ? 'completed' : 'scheduled',
            resultNote: entretien.idresultat?.note || 'N/A',
            resultAppreciation: entretien.idresultat?.appreciation || 'N/A',
            // Garder les données originales pour la modal
            idcandidat: entretien.idcandidat,
            idannonce: entretien.idannonce
          }))
        }
      } catch (error) {
        console.error('Erreur lors du chargement des entretiens:', error)
      } finally {
        this.isLoading = false
      }
    },
    viewDetails(interview) {
      this.selectedInterview = interview
      this.showDetailsModal = true
    },
    contactCandidate(interview) {
      const candidat = interview.idcandidat
      const subject = `Félicitations pour votre entretien - ${this.getJobPosition(interview)}`
      const body = `Bonjour ${candidat.nom} ${candidat.prenom},\n\nNous avons le plaisir de vous informer que votre entretien pour le poste de ${this.getJobPosition(interview)} s'est très bien déroulé.\n\nNous vous recontacterons prochainement pour la suite du processus de recrutement.\n\nCordialement,\nL'équipe RH`
      
      const mailtoLink = `mailto:${candidat.email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`
      window.open(mailtoLink)
    },

    prepareContract(interview) {
      this.selectedInterview = interview
      this.contractForm.startDate = ''
      this.contractForm.duration = ''
      this.showContractModal = true
    },

    async generateContract() {
      try {
        console.log('Selected interview:', this.selectedInterview)
        console.log('Contract form:', this.contractForm)
        
        const contractData = {
          candidatId: this.selectedInterview.idcandidat?.id,
          startDate: this.contractForm.startDate,
          duration: parseInt(this.contractForm.duration),
          poste: this.selectedInterview?.position || this.getJobPosition(this.selectedInterview)
        }

        console.log('Contract data to send:', contractData)

        const response = await fetch('http://localhost:8080/api/contrats/generate', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(contractData)
        })

        if (response.ok) {
          const contract = await response.json()
          this.showContractModal = false
          // Rediriger vers la page de contrat avec l'ID du contrat généré
          this.$router.push(`/contracts/view/${contract.id}`)
        } else {
          const errorText = await response.text()
          console.error('Erreur response:', errorText)
          alert(`Erreur lors de la génération du contrat: ${errorText}`)
        }
      } catch (error) {
        console.error('Erreur:', error)
        alert('Erreur lors de la génération du contrat')
      }
    },
    getStatusLabel(status) {
      const statusLabels = {
        'scheduled': 'Programmé',
        'confirmed': 'Confirmé', 
        'completed': 'Terminé',
        'cancelled': 'Annulé'
      }
      return statusLabels[status] || status
    },
    formatDate(dateStr) {
      if (!dateStr) return 'N/A'
      const date = new Date(dateStr)
      return date.toLocaleDateString('fr-FR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    formatTime(timeStr) {
      if (!timeStr) return 'N/A'
      return timeStr
    },
    getJobPosition(interview) {
      // Essayer d'abord d'obtenir le poste depuis l'annonce de l'entretien
      if (interview.idannonce && interview.idannonce.nomposte) {
        return interview.idannonce.nomposte
      }
      
      // Sinon, essayer d'obtenir depuis l'annonce du candidat
      if (interview.idcandidat && interview.idcandidat.idannonce && interview.idcandidat.idannonce.nomposte) {
        return interview.idcandidat.idannonce.nomposte
      }
      
      // Valeur par défaut
      return 'Poste non spécifié'
    }
  },
  mounted() {
    this.fetchGoodInterviews()
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

.page-title {
  color: #2c3e50;
  margin: 0;
}

.page-actions {
  display: flex;
  gap: 10px;
}

.stats-card {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(46, 204, 113, 0.3);
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 1.1rem;
  opacity: 0.9;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.interviews-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.interview-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.interview-card:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.interview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e9ecef;
}

.interview-header h3 {
  margin: 0;
  color: #2c3e50;
}

.interview-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-completed {
  background: #d4edda;
  color: #155724;
}

.status-confirmed {
  background: #cce5ff;
  color: #004085;
}

.status-scheduled {
  background: #fff3cd;
  color: #856404;
}

.interview-details {
  margin-bottom: 20px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  align-items: center;
}

.detail-label {
  font-weight: 600;
  color: #555;
  min-width: 100px;
}

.result-badge {
  background: #27ae60;
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.interview-actions .btn {
  margin-right: 8px;
  margin-bottom: 4px;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #059669;
  color: white;
}

.btn-primary:hover {
  background: #047857;
}

.btn-success {
  background: #27ae60;
  color: white;
}

.btn-success:hover {
  background: #229954;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-warning {
  background: #f39c12;
  color: white;
}

.btn-warning:hover {
  background: #e67e22;
}

/* Modal styles pour le contrat */
.contract-modal {
  max-width: 500px;
  width: 90%;
}

.candidate-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.candidate-info h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.candidate-info p {
  margin: 5px 0;
  color: #34495e;
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

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-control:focus {
  outline: none;
  border-color: #059669;
  box-shadow: 0 0 0 2px rgba(5, 150, 105, 0.2);
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
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

.modal {
  background: white;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
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
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  padding: 20px;
}

.candidate-info, .interview-info, .result-info {
  margin-bottom: 20px;
}

.candidate-info h4, .interview-info h4, .result-info h4 {
  color: #2c3e50;
  margin-bottom: 10px;
  border-bottom: 2px solid #059669;
  padding-bottom: 5px;
}

.result-display {
  display: flex;
  align-items: center;
  gap: 15px;
}

.result-score {
  background: #27ae60;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
}

.result-appreciation {
  background: #f8f9fa;
  padding: 8px 16px;
  border-radius: 8px;
  color: #2c3e50;
  font-weight: 600;
}

.modal-actions {
  display: flex;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #e9ecef;
  justify-content: flex-end;
}
</style>
