<template>
  <div class="applications-view">
    <div class="page-header">
      <h1 class="page-title">
        {{ announcementId ? `Candidatures - ${announcementTitle}` : 'Gestion des Candidatures' }}
      </h1>
      <div class="page-actions">
        <router-link to="/announcements" class="btn btn-secondary" v-if="announcementId">
          ← Retour aux annonces
        </router-link>
        <button @click="exportApplications" class="btn btn-secondary">
          📄 Exporter PDF
        </button>
      </div>
    </div>

    <div class="filters-section">
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Rechercher par nom, email ou poste..."
          class="form-input search-input"
        />
      </div>
      
      <div class="filters">
        <select v-model="selectedAnnouncement" class="form-input filter-select" v-if="!announcementId">
          <option value="">Toutes les annonces</option>
          <option v-for="announcement in announcements" :key="announcement.id" :value="announcement.id">
            {{ formatAnnouncementTitle(announcement) }}
          </option>
        </select>
        
        <select v-model="selectedDate" class="form-input filter-select">
          <option value="">Toutes les dates</option>
          <option value="today">Aujourd'hui</option>
          <option value="week">Cette semaine</option>
          <option value="month">Ce mois</option>
        </select>

        <select v-model="selectedProvince" class="form-input filter-select">
          <option value="">Toutes les provinces</option>
          <option v-for="prov in provinces" :key="prov.id" :value="prov.id">
            {{ prov.nom }}
          </option>
        </select>

        <select v-model="selectedDiplomeId" class="form-input filter-select">
          <option value="">Tous les diplômes (candidat)</option>
          <option v-for="d in diplomes" :key="d.id" :value="d.id">
            {{ d.nom }}
          </option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Chargement des candidatures...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <div class="error-icon">⚠️</div>
      <h3>Erreur de chargement</h3>
      <p>{{ error }}</p>
      <button @click="loadApplications" class="btn btn-primary">
        Réessayer
      </button>
    </div>

    <div v-else class="applications-list">
      <div 
        v-for="application in filteredApplications" 
        :key="application.id" 
        class="application-card"
      >
        <div class="application-header">
          <div class="candidate-info">
            <div class="candidate-avatar">
              {{ application.prenom.charAt(0) }}{{ application.nom.charAt(0) }}
            </div>
            <div class="candidate-details">
              <h3 class="candidate-name">{{ application.prenom }} {{ application.nom }}</h3>
              <p class="candidate-email">Email non disponible</p>
              <p class="candidate-phone">{{ application.adresse || 'Adresse non disponible' }}</p>
            </div>
          </div>
          <div class="application-meta">
            <span class="application-date">{{ formatDate(application.datenaissance) }}</span>
          </div>
        </div>

        <div class="application-content">
          <div class="application-info">
            <div class="info-item">
              <span class="info-label">Poste:</span>
              <span class="info-value">{{ application.idannonce ? (application.idannonce.nomposte || application.idannonce.idprofil?.nom || 'Non défini') : 'Non défini' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Date de naissance:</span>
              <span class="info-value">{{ formatDate(application.datenaissance) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Adresse:</span>
              <span class="info-value">{{ application.adresse || 'Non renseignée' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Province:</span>
              <span class="info-value">{{ application.idprovince ? application.idprovince.nom : 'Non définie' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Diplôme (candidat):</span>
              <span class="info-value">{{ application.iddiplome ? application.iddiplome.nom : 'Non renseigné' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Prétention salariale:</span>
              <span class="info-value">{{ formatCurrency(application.salaire) }}</span>
            </div>
          </div>

          <div class="application-cv" v-if="application.cv">
            <h4>CV:</h4>
            <div class="cv-preview">
              <p>{{ application.cv.substring(0, 200) }}{{ application.cv.length > 200 ? '...' : '' }}</p>
            </div>
          </div>
        </div>

        <div class="application-actions">
          <button 
            @click="viewCV(application)" 
            class="btn btn-secondary btn-sm"
          >
            📄 Voir CV
          </button>
          <button 
            @click="sendEmail(application)" 
            class="btn btn-secondary btn-sm"
          >
            ✉️ Contacter
          </button>
        </div>
      </div>
    </div>

    <div v-if="filteredApplications.length === 0" class="empty-state">
      <div class="empty-icon">📄</div>
      <h3>Aucune candidature trouvée</h3>
      <p>{{ searchQuery ? 'Aucune candidature ne correspond à votre recherche.' : 'Aucune candidature reçue pour le moment.' }}</p>
    </div>

    <!-- Modal de visualisation CV -->
    <div v-if="showCVModal" class="modal-overlay" @click="showCVModal = false">
      <div class="modal cv-modal" @click.stop>
        <div class="modal-header">
          <h3>CV de {{ selectedApplication?.prenom }} {{ selectedApplication?.nom }}</h3>
          <button @click="showCVModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-content">
          <div class="cv-preview">
            <p>Aperçu du CV (simulation)</p>
            <div class="cv-placeholder">
              <div class="cv-section">
                <h4>Informations personnelles</h4>
                <p><strong>Nom:</strong> {{ selectedApplication?.prenom }} {{ selectedApplication?.nom }}</p>
                <p><strong>Date de naissance:</strong> {{ formatDate(selectedApplication?.datenaissance) }}</p>
                <p><strong>Adresse:</strong> {{ selectedApplication?.adresse || 'Non renseignée' }}</p>
              </div>
              <div class="cv-section" v-if="selectedApplication?.cv">
                <h4>CV Complet</h4>
                <div class="cv-content">
                  {{ selectedApplication.cv }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="downloadCV" class="btn btn-primary">
            📥 Télécharger CV
          </button>
          <button @click="showCVModal = false" class="btn btn-secondary">
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
  name: 'ApplicationsView',
  data() {
    return {
      searchQuery: '',
      selectedAnnouncement: '',
      selectedDate: '',
      selectedProvince: '',
      selectedDiplomeId: '',
      showCVModal: false,
      selectedApplication: null,
      announcements: [],
      applications: [],
      provinces: [],
      diplomes: [],
      loading: true,
      error: null,
      announcementId: null,
      announcementTitle: ''
    }
  },
  async mounted() {
    // Récupérer les paramètres de l'URL
    this.announcementId = this.$route.query.announcementId
    this.announcementTitle = this.$route.query.announcementTitle || 'Annonce'
    
    await Promise.all([this.loadAnnouncements(), this.loadApplications(), this.loadProvinces(), this.loadDiplomes()])
  },
  watch: {
    selectedAnnouncement() {
      // Si un filtre d'annonce est choisi, on interroge le backend ciblé
      this.loadApplications()
    }
  },
  computed: {
    filteredApplications() {
      let filtered = this.applications

      // Filtrage par recherche
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filtered = filtered.filter(application => 
          application.nom.toLowerCase().includes(query) ||
          application.prenom.toLowerCase().includes(query) ||
          (application.nom + ' ' + application.prenom).toLowerCase().includes(query)
        )
      }

      // Filtrage par annonce
      if (this.selectedAnnouncement) {
        filtered = filtered.filter(a => a.idannonce && a.idannonce.id == this.selectedAnnouncement)
      }

      // Filtrage par province du candidat
      if (this.selectedProvince) {
        filtered = filtered.filter(a => a.idprovince && String(a.idprovince.id) === String(this.selectedProvince))
      }

      // Filtrage par diplôme du candidat avec hiérarchie (BACC < Licence < Master < Doctorat)
      if (this.selectedDiplomeId) {
        const selectedDiplome = this.diplomes.find(d => String(d.id) === String(this.selectedDiplomeId))
        if (selectedDiplome) {
          const selectedRank = this.getDiplomeRank(selectedDiplome.nom)
          filtered = filtered.filter(a => {
            if (!a.iddiplome) return false
            const candidateRank = this.getDiplomeRank(a.iddiplome.nom)
            return candidateRank >= selectedRank
          })
        }
      }

      // Filtrage par date (sur date de naissance à défaut de date de candidature)
      if (this.selectedDate) {
        const now = new Date()
        const start = new Date()
        if (this.selectedDate === 'today') {
          start.setHours(0,0,0,0)
          filtered = filtered.filter(a => a.datenaissance && new Date(a.datenaissance) >= start)
        } else if (this.selectedDate === 'week') {
          const day = now.getDay() || 7
          start.setDate(now.getDate() - day + 1)
          start.setHours(0,0,0,0)
          filtered = filtered.filter(a => a.datenaissance && new Date(a.datenaissance) >= start)
        } else if (this.selectedDate === 'month') {
          start.setDate(1)
          start.setHours(0,0,0,0)
          filtered = filtered.filter(a => a.datenaissance && new Date(a.datenaissance) >= start)
        }
      }

      return filtered
    }
  },
  methods: {
    getDiplomeRank(diplomeName) {
      if (!diplomeName) return 0
      const name = diplomeName.toLowerCase().trim()
      if (name === 'bacc') return 1
      if (name === 'licence') return 2
      if (name === 'master') return 3
      if (name === 'doctorat') return 4
      return 0
    },
    async loadDiplomes() {
      try {
        const resp = await axios.get('/api/diplomes')
        this.diplomes = Array.isArray(resp.data) ? resp.data : []
      } catch (e) {
        console.warn('Impossible de charger les diplomes', e)
        this.diplomes = []
      }
    },
    async loadProvinces() {
      try {
        const resp = await axios.get('/api/provinces')
        this.provinces = resp.data || []
      } catch (e) {
        console.warn('Impossible de charger les provinces', e)
        this.provinces = []
      }
    },
    async loadAnnouncements() {
      try {
        const resp = await axios.get('/api/annonces')
        this.announcements = resp.data || []
      } catch (e) {
        console.warn('Impossible de charger les annonces', e)
        this.announcements = []
      }
    },
    async loadApplications() {
      try {
        this.loading = true
        this.error = null
        
        if (this.announcementId) {
          // Charger les candidats pour une annonce spécifique
          const response = await axios.get(`/api/candidats/annonce/${this.announcementId}`)
          this.applications = response.data
        } else if (this.selectedAnnouncement) {
          const response = await axios.get(`/api/candidats/annonce/${this.selectedAnnouncement}`)
          this.applications = response.data
        } else {
          // Charger tous les candidats
          const response = await axios.get('/api/candidats')
          this.applications = response.data
        }
        
      } catch (error) {
        console.error('Erreur lors du chargement des candidatures:', error)
        this.error = 'Impossible de charger les candidatures. Vérifiez que le serveur backend est démarré.'
      } finally {
        this.loading = false
      }
    },
    
    formatCurrency(value) {
      if (value === null || value === undefined || value === '') return 'Non renseignée'
      try {
        return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(Number(value))
      } catch {
        return String(value)
      }
    },
    formatAnnouncementTitle(a) {
      if (!a) return 'Annonce'
      return a.nomposte || a.title || (a.idprofil && a.idprofil.nom) || `Annonce #${a.id}`
    },
    
    formatDate(dateString) {
      if (!dateString) return 'Non définie'
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR')
    },
    
    viewCV(application) {
      this.selectedApplication = application
      this.showCVModal = true
    },
    
    downloadCV() {
      // Simulation du téléchargement
      alert(`Téléchargement du CV de ${this.selectedApplication.prenom} ${this.selectedApplication.nom}`)
    },
    
    scheduleInterview(application) {
      // Redirection vers le calendrier d'entretiens
      this.$router.push(`/interviews/calendar?candidate=${application.id}`)
    },
    
    async updateStatus(application, newStatus) {
      try {
        // Ici vous pourriez faire un appel API pour mettre à jour le statut
        // await axios.put(`/api/candidats/${application.id}/status`, { status: newStatus })
        
        const statusLabels = {
          accepted: 'acceptée',
          rejected: 'rejetée'
        }
        alert(`Candidature ${statusLabels[newStatus] || 'mise à jour'}`)
      } catch (error) {
        console.error('Erreur lors de la mise à jour du statut:', error)
        alert('Erreur lors de la mise à jour du statut')
      }
    },
    
    sendEmail(application) {
      // Simulation d'envoi d'email
      alert(`Email envoyé à ${application.nom} ${application.prenom}`)
    },
    
    exportApplications() {
      // Simulation d'export PDF
      alert('Export PDF des candidatures en cours...')
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

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-left: 4px solid #059669;
}

.application-card.status-pending {
  border-left-color: #f39c12;
}

.application-card.status-reviewed {
  border-left-color: #3498db;
}

.application-card.status-interview {
  border-left-color: #9b59b6;
}

.application-card.status-accepted {
  border-left-color: #059669;
}

.application-card.status-rejected {
  border-left-color: #e74c3c;
}

.application-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.application-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.candidate-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.candidate-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #059669, #047857);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.candidate-name {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 20px;
}

.candidate-email {
  margin: 0 0 5px 0;
  color: #059669;
  font-size: 14px;
}

.candidate-phone {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.application-meta {
  text-align: right;
}

.application-date {
  display: block;
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-pending {
  background: #fef9e7;
  color: #f39c12;
}

.status-reviewed {
  background: #ebf3fd;
  color: #3498db;
}

.status-interview {
  background: #f4ecf7;
  color: #9b59b6;
}

.status-accepted {
  background: #d1f7c4;
  color: #059669;
}

.status-rejected {
  background: #fdf2f2;
  color: #e74c3c;
}

.application-content {
  margin-bottom: 20px;
}

.application-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
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

.info-value {
  color: #333;
}

.application-skills h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 16px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.skill-tag {
  background: #d1f7c4;
  color: #047857;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.application-message h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 16px;
}

.application-message p {
  color: #555;
  line-height: 1.6;
  font-style: italic;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin: 0;
}

.application-actions {
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
  margin-bottom: 30px;
}

.loading-state, .error-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
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

.error-state h3 {
  color: #e74c3c;
  margin-bottom: 10px;
}

.error-state p {
  color: #666;
  margin-bottom: 30px;
}

.cv-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
  font-family: monospace;
  font-size: 14px;
  line-height: 1.4;
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

.cv-modal {
  max-width: 600px;
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

.cv-preview {
  text-align: center;
}

.cv-placeholder {
  background: #f8f9fa;
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 30px;
  margin-top: 20px;
}

.cv-section {
  margin-bottom: 20px;
  text-align: left;
}

.cv-section h4 {
  color: #2c3e50;
  margin-bottom: 10px;
  border-bottom: 1px solid #eee;
  padding-bottom: 5px;
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
  
  .filters {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .application-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .application-info {
    grid-template-columns: 1fr;
  }
  
  .application-actions {
    justify-content: center;
  }
  
  .cv-modal {
    width: 95%;
    max-height: 90vh;
  }
}
</style>
