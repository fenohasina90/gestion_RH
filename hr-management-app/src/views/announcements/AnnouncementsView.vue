<template>
  <div class="announcements-view">
    <div class="page-header">
      <h1 class="page-title">Gestion des Annonces</h1>
      <div class="page-actions">
        <router-link to="/announcements/create" class="btn btn-primary">
          ➕ Créer une annonce
        </router-link>
      </div>
    </div>

    <div class="filters-section">
      <div class="search-bar">
        <input
          type="text"
          v-model="filters.nomposte"
          placeholder="Rechercher par nom de poste..."
          class="form-input search-input"
          @input="applyFilters"
        />
        <input
          type="text"
          v-model="filters.description"
          placeholder="Rechercher dans la description..."
          class="form-input search-input"
          @input="applyFilters"
        />
      </div>
      
      <div class="filters">
        <select v-model="filters.departementId" class="form-input filter-select" @change="applyFilters">
          <option value="">Tous les départements</option>
          <option v-for="dept in departements" :key="dept.id" :value="dept.id">
            {{ dept.nom }}
          </option>
        </select>
        
        <select v-model="filters.typeAnnonceId" class="form-input filter-select" @change="applyFilters">
          <option value="">Tous les types</option>
          <option v-for="type in typesAnnonce" :key="type.id" :value="type.id">
            {{ type.libelle }}
          </option>
        </select>
        
        <select v-model="filters.profilId" class="form-input filter-select" @change="applyFilters">
          <option value="">Tous les profils</option>
          <option v-for="profil in profils" :key="profil.id" :value="profil.id">
            {{ profil.nom }}
          </option>
        </select>
        
        <select v-model="filters.provinceId" class="form-input filter-select" @change="applyFilters">
          <option value="">Toutes les provinces</option>
          <option v-for="prov in provinces" :key="prov.id" :value="prov.id">
            {{ prov.nom }}
          </option>
        </select>

        <select v-model="filters.diplomeId" class="form-input filter-select" @change="applyFilters">
          <option value="">Tous les diplômes</option>
          <option v-for="d in diplomes" :key="d.id" :value="d.id">
            {{ d.nom }}
          </option>
        </select>
      </div>
      
      <div class="date-filters">
        <div class="date-group">
          <label>Date de début:</label>
          <input type="date" v-model="filters.dateDebutFrom" @change="applyFilters" class="form-input date-input">
          <span>à</span>
          <input type="date" v-model="filters.dateDebutTo" @change="applyFilters" class="form-input date-input">
        </div>
        
        <div class="date-group">
          <label>Date de fin:</label>
          <input type="date" v-model="filters.dateFinFrom" @change="applyFilters" class="form-input date-input">
          <span>à</span>
          <input type="date" v-model="filters.dateFinTo" @change="applyFilters" class="form-input date-input">
        </div>
        
        <div class="date-group">
          <label>Date de publication:</label>
          <input type="date" v-model="filters.datePublicationFrom" @change="applyFilters" class="form-input date-input">
          <span>à</span>
          <input type="date" v-model="filters.datePublicationTo" @change="applyFilters" class="form-input date-input">
        </div>
        
        <button @click="clearFilters" class="btn btn-secondary btn-sm">
          🗑️ Effacer les filtres
        </button>
      </div>
    </div>

    <div class="announcements-grid">
      <div 
        v-for="announcement in announcements" 
        :key="announcement.id" 
        class="announcement-card"
      >
        <div class="announcement-header">
          <div class="announcement-info">
            <h3 class="announcement-title">{{ announcement.nomposte || (announcement.idprofil ? announcement.idprofil.nom : 'Poste non défini') }}</h3>
            <p class="announcement-company">{{ announcement.iddepartement ? announcement.iddepartement.nom : 'Département non défini' }}</p>
            <div class="announcement-meta">
              <span class="meta-item">
                🏢 {{ announcement.iddepartement ? announcement.iddepartement.nom : 'Département non défini' }}
              </span>
              <span class="meta-item">
                💼 {{ announcement.idtypeannonce ? announcement.idtypeannonce.libelle.toUpperCase() : 'TYPE NON DÉFINI' }}
              </span>
              <span class="meta-item">
                📅 Du {{ formatDate(announcement.datedebut) }} au {{ formatDate(announcement.datefin) }}
              </span>
              <span class="meta-item">
                📍 {{ announcement.idprovince ? announcement.idprovince.nom : 'Province non définie' }}
              </span>
            </div>
          </div>
          
        </div>

        <div class="announcement-description">
          <p>{{ announcement.description || 'Aucune description disponible' }}</p>
        </div>

        <div v-if="announcement.idprofil" class="announcement-requirements">
          <h4>Compétences requises:</h4>
          <div class="skills-section">
            
            <div v-if="announcement.criteres && announcement.criteres.length > 0" class="criteria-tags">
              <span 
                v-for="critere in announcement.criteres" 
                :key="critere.id" 
                class="skill-tag"
                :title="getFullCriteriaInfo(critere)"
              >
                {{ getCriteriaDisplayText(critere) }}
              </span>
            </div>
            <div v-else class="no-criteria">
              <span class="empty-message">Aucun critère défini</span>
            </div>
            
            <div v-if="announcement.diplomes && announcement.diplomes.length > 0" class="diploma-tags">
              <h5>Diplômes requis:</h5>
              <span 
                v-for="diplome in announcement.diplomes" 
                :key="diplome.id" 
                class="diploma-tag"
              >
                {{ diplome.iddiplome ? diplome.iddiplome.nom : 'Diplôme non défini' }}
              </span>
            </div>
            <div v-else class="no-diplomas">
              <span class="empty-message">Aucun diplôme défini</span>
            </div>
          </div>
        </div>

        <div class="announcement-details">
          <div class="detail-row">
            <span class="detail-label">Date de publication:</span>
            <span class="detail-value">{{ formatDate(announcement.datepublication) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Date de début:</span>
            <span class="detail-value">{{ formatDate(announcement.datedebut) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Date de fin:</span>
            <span class="detail-value">{{ formatDate(announcement.datefin) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Candidatures reçues:</span>
            <span class="detail-value">{{ announcement.applicationsCount || 0 }}</span>
          </div>
        </div>

        <div class="announcement-actions">
          <button 
            @click="viewApplications(announcement)" 
            class="btn btn-secondary btn-sm"
          >
            👥 Candidatures ({{ announcement.applicationsCount }})
          </button>
          <button 
            @click="editAnnouncement(announcement)" 
            class="btn btn-primary btn-sm"
          >
            ✏️ Modifier
          </button>
          
          <button 
            @click="deleteAnnouncement(announcement.id)" 
            class="btn btn-danger btn-sm"
          >
            🗑️ Supprimer
          </button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Chargement des annonces...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <div class="error-icon">⚠️</div>
      <h3>Erreur de chargement</h3>
      <p>{{ error }}</p>
      <button @click="loadAnnouncements" class="btn btn-primary">
        Réessayer
      </button>
    </div>

    <div v-else-if="announcements.length === 0" class="empty-state">
      <div class="empty-icon">📢</div>
      <h3>Aucune annonce trouvée</h3>
      <p>{{ hasActiveFilters ? 'Aucune annonce ne correspond à vos critères de recherche.' : 'Commencez par créer votre première annonce.' }}</p>
      <router-link to="/announcements/create" class="btn btn-primary">
        Créer une annonce
      </router-link>
    </div>

    <!-- Modal de confirmation de suppression -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal" @click.stop>
        <h3>Confirmer la suppression</h3>
        <p>Êtes-vous sûr de vouloir supprimer cette annonce ? Cette action est irréversible.</p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="btn btn-secondary">
            Annuler
          </button>
          <button @click="confirmDelete" class="btn btn-danger">
            Supprimer
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AnnouncementsView',
  data() {
    return {
      filters: {
        nomposte: '',
        description: '',
        departementId: '',
        typeAnnonceId: '',
        profilId: '',
        provinceId: '',
        diplomeId: '',
        dateDebutFrom: '',
        dateDebutTo: '',
        dateFinFrom: '',
        dateFinTo: '',
        datePublicationFrom: '',
        datePublicationTo: ''
      },
      showDeleteModal: false,
      announcementToDelete: null,
      announcements: [],
      departements: [],
      typesAnnonce: [],
      profils: [],
      provinces: [],
      diplomes: [],
      loading: true,
      error: null,
      filterTimeout: null
    }
  },
  async mounted() {
    await Promise.all([
      this.loadFilterOptions(),
      this.loadAnnouncements()
    ])
  },
  computed: {
    hasActiveFilters() {
      return Object.values(this.filters).some(value => value !== '')
    }
  },
  methods: {
    async loadFilterOptions() {
      try {
        const [deptsResponse, typesResponse, profilsResponse, provincesResponse, diplomesResponse] = await Promise.all([
          axios.get('/api/departements'),
          axios.get('/api/typeannonces'),
          axios.get('/api/profils'),
          axios.get('/api/provinces'),
          axios.get('/api/diplomes')
        ])
        
        this.departements = deptsResponse.data
        this.typesAnnonce = typesResponse.data
        this.profils = profilsResponse.data
        this.provinces = provincesResponse.data
        this.diplomes = diplomesResponse.data
      } catch (error) {
        console.warn('Erreur lors du chargement des options de filtrage:', error)
      }
    },
    
    async loadAnnouncements() {
      try {
        this.loading = true
        this.error = null
        
        // Construire les paramètres de filtrage
        const params = new URLSearchParams()
        Object.entries(this.filters).forEach(([key, value]) => {
          if (value && value !== '') {
            params.append(key, value)
          }
        })
        
        const url = params.toString() ? `/api/annonces/filter?${params.toString()}` : '/api/annonces'
        const response = await axios.get(url)
        
        // Charger les compteurs de candidatures et les critères/diplômes pour chaque annonce
        let announcementsWithCounts = await Promise.all(
          response.data.map(async (announcement) => {
            try {
              const countResponse = await axios.get(`/api/annonces/${announcement.id}/candidatures/count`)
              
              // Charger les critères et diplômes du profil si l'annonce a un profil
              let criteres = []
              let diplomes = []
              
              if (announcement.idprofil && announcement.idprofil.id) {
                try {
                  const criteresResponse = await axios.get(`/api/profils/${announcement.idprofil.id}/criteres`)
                  criteres = criteresResponse.data
                } catch (error) {
                  console.warn(`Erreur lors du chargement des critères pour le profil ${announcement.idprofil.id}:`, error)
                }
                
                try {
                  const diplomesResponse = await axios.get(`/api/profils/${announcement.idprofil.id}/diplomes`)
                  diplomes = diplomesResponse.data
                } catch (error) {
                  console.warn(`Erreur lors du chargement des diplômes pour le profil ${announcement.idprofil.id}:`, error)
                }
              }
              
              return {
                ...announcement,
                applicationsCount: countResponse.data,
                criteres: criteres,
                diplomes: diplomes
              }
            } catch (error) {
              console.warn(`Erreur lors du chargement du compteur pour l'annonce ${announcement.id}:`, error)
              return {
                ...announcement,
                applicationsCount: 0,
                criteres: [],
                diplomes: []
              }
            }
          })
        )
        // Filtrage client par province si backend ne le gère pas
        if (this.filters.provinceId) {
          announcementsWithCounts = announcementsWithCounts.filter(a => a.idprovince && String(a.idprovince.id) === String(this.filters.provinceId))
        }
        this.announcements = announcementsWithCounts
      } catch (error) {
        console.error('Erreur lors du chargement des annonces:', error)
        this.error = 'Impossible de charger les annonces. Vérifiez que le serveur backend est démarré.'
      } finally {
        this.loading = false
      }
    },
    
    applyFilters() {
      // Debounce pour éviter trop d'appels API
      if (this.filterTimeout) {
        clearTimeout(this.filterTimeout)
      }
      
      this.filterTimeout = setTimeout(() => {
        this.loadAnnouncements()
      }, 500)
    },
    
    clearFilters() {
      this.filters = {
        nomposte: '',
        description: '',
        departementId: '',
        typeAnnonceId: '',
        profilId: '',
        provinceId: '',
        diplomeId: '',
        dateDebutFrom: '',
        dateDebutTo: '',
        dateFinFrom: '',
        dateFinTo: '',
        datePublicationFrom: '',
        datePublicationTo: ''
      }
      this.loadAnnouncements()
    },
    
    getAnnouncementStatus(announcement) {
      const now = new Date()
      const endDate = new Date(announcement.datefin)
      
      if (endDate < now) {
        return 'expired'
      }
      return 'active'
    },
    
    getStatusLabel(status) {
      const labels = {
        active: 'Active',
        inactive: 'Inactive',
        expired: 'Expirée'
      }
      return labels[status] || status
    },
    
    formatDate(dateString) {
      if (!dateString) return 'Non définie'
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR')
    },
    
    viewApplications(announcement) {
      // Redirection vers la page des candidatures pour cette annonce
      this.$router.push(`/applications?announcementId=${announcement.id}&announcementTitle=${encodeURIComponent(announcement.nomposte || announcement.idprofil?.nom || 'Annonce')}`)
    },
    
    editAnnouncement(announcement) {
      // Redirection vers la page d'édition
      this.$router.push(`/announcements/edit/${announcement.id}`)
    },
    
    async toggleStatus(announcement) {
      // Pour le moment, on simule juste le changement de statut
      // Dans une vraie application, on ferait un appel API pour modifier le statut
      console.log('Toggle status pour annonce:', announcement.id)
    },
    
    deleteAnnouncement(announcementId) {
      this.announcementToDelete = announcementId
      this.showDeleteModal = true
    },
    
    async confirmDelete() {
      if (this.announcementToDelete) {
        try {
          await axios.delete(`/api/annonces/${this.announcementToDelete}`)
          await this.loadAnnouncements() // Recharger la liste
          this.showDeleteModal = false
          this.announcementToDelete = null
        } catch (error) {
          console.error('Erreur lors de la suppression:', error)
          alert('Erreur lors de la suppression de l\'annonce')
        }
      }
    },
    
    getCriteriaDisplayText(critere) {
      if (!critere || !critere.idcritere) return 'Critère non défini'
      
      const critereName = critere.idcritere.nom
      let value = ''
      
      // Déterminer la valeur selon le type
      if (critere.valeurdouble !== null) {
        value = critere.valeurdouble
      } else if (critere.valeurvarchar !== null) {
        value = critere.valeurvarchar
      } else if (critere.valeurbool !== null) {
        value = critere.valeurbool ? 'Oui' : 'Non'
      }
      
      // Formatage selon le type de critère
      if (critereName.toLowerCase().includes('expérience')) {
        return `${critereName}: ${value} an${value > 1 ? 's' : ''}`
      } else if (value) {
        return `${critereName}: ${value}`
      } else {
        return critereName
      }
    },
    
    getFullCriteriaInfo(critere) {
      if (!critere || !critere.idcritere) return 'Critère non défini'
      
      const obligatoire = critere.obligatoire ? ' (Obligatoire)' : ' (Optionnel)'
      return this.getCriteriaDisplayText(critere) + obligatoire
    }
  }
}
</script>

<style scoped>
.btn-primary {
  background: #059669;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.btn-primary:hover:not(:disabled) {
  background: #047857;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(5, 150, 105, 0.3);
}

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
  margin-bottom: 10px;
}

.date-filters {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.date-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.date-group:last-child {
  margin-bottom: 0;
}

.date-group label {
  min-width: 140px;
  font-weight: 600;
  color: #495057;
  font-size: 14px;
}

.date-input {
  max-width: 150px;
}

.date-group span {
  color: #6c757d;
  font-weight: 500;
}

.filters {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-select {
  min-width: 150px;
}

.announcements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 20px;
}

.announcement-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-left: 4px solid #059669;
}

.announcement-card.inactive {
  opacity: 0.7;
  border-left-color: #95a5a6;
}

.announcement-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.announcement-title {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 20px;
}

.announcement-company {
  margin: 0 0 10px 0;
  color: #059669;
  font-weight: 600;
  font-size: 16px;
}

.announcement-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-item {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 5px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-active {
  background: #d1f7c4;
  color: #047857;
}

.status-inactive {
  background: #f8d7da;
  color: #721c24;
}

.status-expired {
  background: #d1ecf1;
  color: #0c5460;
}

.announcement-description {
  margin-bottom: 20px;
}

.announcement-description p {
  color: #555;
  line-height: 1.6;
  margin: 0;
}

.announcement-requirements h4 {
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

.profile-tag {
  background: #d1f7c4;
  color: #059669;
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.skills-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.criteria-tags, .diploma-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.diploma-tags h5 {
  width: 100%;
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.diploma-tag {
  background: #ecfdf5;
  color: #059669;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid #a7f3d0;
}

.empty-message {
  color: #999;
  font-style: italic;
  font-size: 14px;
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

.announcement-details {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 600;
  color: #555;
}

.detail-value {
  color: #333;
}

.announcement-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.btn-warning {
  background-color: #f39c12;
  color: white;
}

.btn-warning:hover {
  background-color: #e67e22;
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
  padding: 30px;
  border-radius: 12px;
  max-width: 400px;
  width: 90%;
}

.modal h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.modal p {
  margin: 0 0 25px 0;
  color: #666;
}

.modal-actions {
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
  
  .announcements-grid {
    grid-template-columns: 1fr;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .announcement-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .announcement-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .announcement-actions {
    justify-content: center;
  }
  
  .detail-row {
    flex-direction: column;
    gap: 5px;
  }
}
</style>
