<template>
  <div class="job-offers-view public-page">
    <div class="page-header">
      <h1 class="page-title">Offres d'emploi</h1>
      <p class="page-subtitle">Découvrez nos opportunités de carrière</p>
    </div>

    <div class="filters-toggle">
      <button class="btn btn-secondary" @click="showFilters = !showFilters">
        {{ showFilters ? 'Masquer les filtres' : 'Afficher les filtres' }}
      </button>
    </div>

    <div class="filters-section" v-show="showFilters">
      <div class="search-bar">
        <input
          type="text"
          v-model="filters.nomposte"
          placeholder="Rechercher par titre de poste..."
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
          <option v-for="dep in departements" :key="dep.id" :value="dep.id">{{ dep.nom }}</option>
        </select>
        
        <select v-model="filters.typeAnnonceId" class="form-input filter-select" @change="applyFilters">
          <option value="">Tous les types</option>
          <option v-for="type in typesAnnonce" :key="type.id" :value="type.id">{{ type.libelle }}</option>
        </select>
        
        <select v-model="filters.profilId" class="form-input filter-select" @change="applyFilters">
          <option value="">Tous les profils</option>
          <option v-for="profil in profils" :key="profil.id" :value="profil.id">{{ profil.nom }}</option>
        </select>

        <select v-model="filters.provinceId" class="form-input filter-select" @change="applyFilters">
          <option value="">Toutes les provinces</option>
          <option v-for="prov in provinces" :key="prov.id" :value="prov.id">{{ prov.nom }}</option>
        </select>

        <select v-model="filters.diplomeId" class="form-input filter-select" @change="applyFilters">
          <option value="">Tous les diplômes</option>
          <option v-for="d in diplomes" :key="d.id" :value="d.id">{{ d.nom }}</option>
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
        <button @click="clearFilters" class="btn btn-secondary btn-sm">🗑️ Effacer les filtres</button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Chargement des offres...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <div class="error-icon">⚠️</div>
      <h3>Erreur de chargement</h3>
      <p>{{ error }}</p>
      <button @click="loadJobOffers" class="btn btn-primary">
        Réessayer
      </button>
    </div>

    <div v-else class="job-offers-grid">
      <div 
        v-for="offer in filteredOffers" 
        :key="offer.id" 
        class="job-offer-card"
        :class="{ 'inactive': getOfferStatus(offer) !== 'active' }"
      >
        <div class="offer-header">
          <div class="offer-info">
            <h3 class="offer-title">{{ offer.nomposte || (offer.idprofil ? offer.idprofil.nom : 'Poste non défini') }}</h3>
            <p class="offer-department">{{ offer.iddepartement ? offer.iddepartement.nom : 'Département non défini' }}</p>
            <div class="offer-meta">
              <span class="meta-item">
                🏢 {{ offer.iddepartement ? offer.iddepartement.nom : 'Département non défini' }}
              </span>
              <span class="meta-item">
                💼 {{ offer.idtypeannonce ? offer.idtypeannonce.libelle.toUpperCase() : 'TYPE NON DÉFINI' }}
              </span>
              <span class="meta-item">
                📅 Du {{ formatDate(offer.datedebut) }} au {{ formatDate(offer.datefin) }}
              </span>
              <span class="meta-item">
                📍 {{ offer.idprovince ? offer.idprovince.nom : 'Province non définie' }}
              </span>
            </div>
          </div>
          <div class="offer-status">
            <span 
              class="status-badge" 
              :class="`status-${getOfferStatus(offer)}`"
            >
              {{ getStatusLabel(getOfferStatus(offer)) }}
            </span>
          </div>
        </div>

        <div class="offer-description">
          <p>{{ offer.description || 'Aucune description disponible' }}</p>
        </div>

        <div v-if="offer.idprofil" class="offer-requirements">
          <h4>Compétences requises:</h4>
          <div class="skills-section">
            
            <div v-if="offer.criteres && offer.criteres.length > 0" class="criteria-tags">
              <span 
                v-for="critere in offer.criteres" 
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
            
            <div v-if="offer.diplomes && offer.diplomes.length > 0" class="diploma-tags">
              <h5>Diplômes requis:</h5>
              <span 
                v-for="diplome in offer.diplomes" 
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

        <div class="offer-details">
          <div class="detail-row">
            <span class="detail-label">Date de publication:</span>
            <span class="detail-value">{{ formatDate(offer.datepublication) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Date de début:</span>
            <span class="detail-value">{{ formatDate(offer.datedebut) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Date de fin:</span>
            <span class="detail-value">{{ formatDate(offer.datefin) }}</span>
          </div>
        </div>

        <div class="offer-actions">
          <button 
            @click="applyToJob(offer)" 
            class="btn btn-primary btn-apply"
            :disabled="getOfferStatus(offer) !== 'active'"
          >
            📝 Postuler
          </button>
        </div>
      </div>
    </div>

    <div v-if="filteredOffers.length === 0 && !loading && !error" class="empty-state">
      <div class="empty-icon">💼</div>
      <h3>Aucune offre trouvée</h3>
      <p>{{ searchQuery ? 'Aucune offre ne correspond à votre recherche.' : 'Aucune offre disponible pour le moment.' }}</p>
    </div>

    <!-- Modal de candidature -->
    <div v-if="showApplicationModal" class="modal-overlay" @click="showApplicationModal = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Postuler pour: {{ selectedOffer?.nomposte || selectedOffer?.idprofil?.nom }}</h3>
          <button @click="showApplicationModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-content">
          <form @submit.prevent="submitApplication">
            <div class="form-group">
              <label>Nom *</label>
              <input type="text" v-model="applicationForm.nom" required class="form-input">
            </div>
            <div class="form-group">
              <label>Prénom *</label>
              <input type="text" v-model="applicationForm.prenom" required class="form-input">
            </div>
            <div class="form-group">
              <label>Date de naissance</label>
              <input type="date" v-model="applicationForm.datenaissance" class="form-input">
            </div>
            <div class="form-group">
              <label>Adresse</label>
              <textarea v-model="applicationForm.adresse" class="form-input" rows="3"></textarea>
            </div>
            
            <!-- Champs dynamiques basés sur les critères du profil -->
            <div v-if="dynamicFields.length > 0" class="dynamic-fields-section">
              <h4>Informations spécifiques au poste</h4>
              <div v-for="field in dynamicFields" :key="field.id" class="form-group">
                <label>
                  {{ field.idcritere ? field.idcritere.nom : 'Critère' }}
                  <span v-if="getExpectedValue(field)" class="expected-value">({{ getExpectedValue(field) }})</span>
                  <span v-if="field.estobligatoire" class="required-asterisk">*</span>
                </label>
                
                <!-- Input text/number/email/date -->
                <input 
                  v-if="['text', 'number', 'email', 'date'].includes(getFieldType(field))"
                  :type="getFieldType(field)"
                  v-model="field.value"
                  :required="field.estobligatoire"
                  class="form-input"
                />
                
                <!-- Textarea -->
                <textarea 
                  v-else-if="getFieldType(field) === 'textarea'"
                  v-model="field.value"
                  :required="field.estobligatoire"
                  class="form-input"
                  rows="3"
                ></textarea>
                
                <!-- Checkbox -->
                <div v-else-if="getFieldType(field) === 'checkbox'" class="checkbox-group">
                  <label class="checkbox-label">
                    <input 
                      type="checkbox" 
                      v-model="field.value"
                      :required="field.estobligatoire"
                    />
                    <span class="checkbox-text">Oui</span>
                  </label>
                </div>
                
                <!-- Radio buttons -->
                <div v-else-if="getFieldType(field) === 'radio'" class="radio-group">
                  <label class="radio-label">
                    <input type="radio" :name="field.fieldName" value="oui" v-model="field.value" :required="field.estobligatoire" />
                    <span>Oui</span>
                  </label>
                  <label class="radio-label">
                    <input type="radio" :name="field.fieldName" value="non" v-model="field.value" :required="field.estobligatoire" />
                    <span>Non</span>
                  </label>
                </div>
                
                <!-- Select -->
                <select 
                  v-else-if="getFieldType(field) === 'select'"
                  v-model="field.value"
                  :required="field.estobligatoire"
                  class="form-input"
                >
                  <option value="">Choisir...</option>
                  <option value="oui">Oui</option>
                  <option value="non">Non</option>
                </select>
              </div>
            </div>
            
            <div class="form-group">
              <label>CV / Lettre de motivation</label>
              <textarea v-model="applicationForm.cv" class="form-input" rows="6" placeholder="Décrivez votre expérience, vos compétences et votre motivation..."></textarea>
            </div>
          </form>
        </div>
        <div class="modal-actions">
          <button @click="showApplicationModal = false" class="btn btn-secondary">
            Annuler
          </button>
          <button @click="submitApplication" class="btn btn-primary">
            Envoyer ma candidature
          </button>
        </div>
      </div>
    </div>

    <!-- Modal d'authentification -->
    <div v-if="showAuthModal" class="modal-overlay" @click="showAuthModal = false">
      <div class="modal auth-modal" @click.stop>
        <div class="modal-header">
          <h3>Connexion requise</h3>
          <button @click="showAuthModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-content">
          <p>Vous devez être connecté pour postuler à cette offre.</p>
          <div class="auth-options">
            <button @click="redirectToLogin" class="btn btn-primary btn-auth">
              Se connecter
            </button>
            <button @click="redirectToRegister" class="btn btn-secondary btn-auth">
              Créer un compte
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useAuthStore } from '../../stores/auth'

export default {
  name: 'JobOffersView',
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
      departements: [],
      typesAnnonce: [],
      profils: [],
      provinces: [],
      diplomes: [],
      offers: [],
      loading: true,
      error: null,
      showFilters: false,
      showApplicationModal: false,
      showAuthModal: false,
      selectedOffer: null,
      applicationForm: {
        nom: '',
        prenom: '',
        datenaissance: '',
        adresse: '',
        cv: ''
      },
      dynamicFields: [],
      authStore: useAuthStore()
    }
  },
  async mounted() {
    await Promise.all([this.loadFilterOptions(), this.loadJobOffers()])
  },
  computed: {
    filteredOffers() {
      // Le backend renvoie déjà les offres filtrées; on affiche TOUTES les offres
      // (y compris celles expirées). Le statut n'est utilisé que pour le badge/style.
      return this.offers
    }
  },
  methods: {
    async loadFilterOptions() {
      try {
        const [deptsRes, typesRes, profilsRes, provsRes, diplRes] = await Promise.all([
          axios.get('/api/departements'),
          axios.get('/api/typeannonces'),
          axios.get('/api/profils'),
          axios.get('/api/provinces'),
          axios.get('/api/diplomes')
        ])
        this.departements = Array.isArray(deptsRes.data) ? deptsRes.data : []
        this.typesAnnonce = Array.isArray(typesRes.data) ? typesRes.data : []
        this.profils = Array.isArray(profilsRes.data) ? profilsRes.data : []
        this.provinces = Array.isArray(provsRes.data) ? provsRes.data : []
        this.diplomes = Array.isArray(diplRes.data) ? diplRes.data : []
      } catch (e) {
        console.warn('Erreur lors du chargement des options de filtrage:', e)
      }
    },
    async loadJobOffers() {
      try {
        this.loading = true
        this.error = null
        // Construire les paramètres de filtrage côté backend
        const params = new URLSearchParams()
        Object.entries(this.filters).forEach(([key, value]) => {
          if (value && value !== '') params.append(key, value)
        })
        const url = params.toString() ? `/api/annonces/filter?${params.toString()}` : '/api/annonces'
        const response = await axios.get(url)
        
        // Charger les critères et diplômes pour chaque offre
        const offersWithDetails = await Promise.all(
          response.data.map(async (offer) => {
            try {
              // Charger les critères et diplômes du profil si l'offre a un profil
              let criteres = []
              let diplomes = []
              
              if (offer.idprofil && offer.idprofil.id) {
                try {
                  const criteresResponse = await axios.get(`/api/profils/${offer.idprofil.id}/criteres`)
                  criteres = criteresResponse.data
                } catch (error) {
                  console.warn(`Erreur lors du chargement des critères pour le profil ${offer.idprofil.id}:`, error)
                }
                
                try {
                  const diplomesResponse = await axios.get(`/api/profils/${offer.idprofil.id}/diplomes`)
                  diplomes = diplomesResponse.data
                } catch (error) {
                  console.warn(`Erreur lors du chargement des diplômes pour le profil ${offer.idprofil.id}:`, error)
                }
              }
              
              return {
                ...offer,
                criteres: criteres,
                diplomes: diplomes
              }
            } catch (error) {
              console.warn(`Erreur lors du chargement des détails pour l'offre ${offer.id}:`, error)
              return {
                ...offer,
                criteres: [],
                diplomes: []
              }
            }
          })
        )
        
        this.offers = offersWithDetails
      } catch (error) {
        console.error('Erreur lors du chargement des offres:', error)
        this.error = 'Impossible de charger les offres d\'emploi.'
      } finally {
        this.loading = false
      }
    },
    applyFilters() {
      if (this.filterTimeout) clearTimeout(this.filterTimeout)
      this.filterTimeout = setTimeout(() => {
        this.loadJobOffers()
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
      this.loadJobOffers()
    },
    
    getOfferStatus(offer) {
      const now = new Date()
      // Si pas de date de fin (contrat ouvert), on considère l'offre comme active
      if (!offer || !offer.datefin) {
        return 'active'
      }
      const endDate = new Date(offer.datefin)
      if (isNaN(endDate.getTime())) {
        // Date invalide -> ne pas exclure
        return 'active'
      }
      return endDate < now ? 'expired' : 'active'
    },
    
    getStatusLabel(status) {
      const labels = {
        active: 'Disponible',
        expired: 'Expirée'
      }
      return labels[status] || status
    },
    
    formatDate(dateString) {
      if (!dateString) return 'Non définie'
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR')
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
    },
    
    applyToJob(offer) {
      // Rediriger vers la page de candidature
      this.$router.push(`/jobs/${offer.id}/apply`)
    },
    
    async loadDynamicFields(offer) {
      try {
        if (offer.idprofil && offer.idprofil.id) {
          const response = await axios.get(`/api/profils/${offer.idprofil.id}/criteres`)
          this.dynamicFields = response.data.map(critere => ({
            ...critere,
            value: null,
            fieldName: `critere_${critere.id}`
          }))
        } else {
          this.dynamicFields = []
        }
      } catch (error) {
        console.error('Erreur lors du chargement des champs dynamiques:', error)
        this.dynamicFields = []
      }
    },
    
    redirectToLogin() {
      this.showAuthModal = false
      this.$router.push('/login')
    },
    
    redirectToRegister() {
      this.showAuthModal = false  
      this.$router.push('/register')
    },
    
    getFieldType(critere) {
      if (!critere || !critere.idcritere || !critere.idcritere.idtypechamp || !critere.idcritere.idtypechamp.libelle) return 'text'
      
      const type = critere.idcritere.idtypechamp.libelle.toLowerCase()
      switch (type) {
        case 'radio':
        case 'select':
        case 'checkbox':
        case 'textarea':
          return type
        case 'number':
          return 'number'
        case 'date':
          return 'date'
        case 'email':
          return 'email'
        default:
          return 'text'
      }
    },
    
    getExpectedValue(critere) {
      if (!critere) return ''
      
      let expected = ''
      if (critere.valeurdouble !== null) {
        expected = `Attendu: ${critere.valeurdouble}`
      } else if (critere.valeurvarchar !== null) {
        expected = `Attendu: ${critere.valeurvarchar}`
      } else if (critere.valeurbool !== null) {
        expected = `Attendu: ${critere.valeurbool ? 'Oui' : 'Non'}`
      }
      
      if (critere.estobligatoire) {
        expected += ' (Obligatoire)'
      }
      
      return expected
    },
    
    async submitApplication() {
      try {
        if (!this.applicationForm.nom || !this.applicationForm.prenom) {
          alert('Veuillez remplir au moins le nom et le prénom.')
          return
        }
        
        const candidatureData = {
          nom: this.applicationForm.nom,
          prenom: this.applicationForm.prenom,
          datenaissance: this.applicationForm.datenaissance || null,
          adresse: this.applicationForm.adresse || null,
          cv: this.applicationForm.cv || null,
          idannonce: {
            id: this.selectedOffer.id
          }
        }
        
        await axios.post('/api/candidats', candidatureData)
        
        alert('Votre candidature a été envoyée avec succès !')
        this.showApplicationModal = false
        
      } catch (error) {
        console.error('Erreur lors de l\'envoi de la candidature:', error)
        alert('Erreur lors de l\'envoi de votre candidature. Veuillez réessayer.')
      }
    }
  }
}
</script>

<style scoped>
.public-page {
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
  border-radius: 0;
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

.filters-section {
  background: #ffffff;
  padding: 20px;
  border-radius: 12px;
  margin: 0 20px 30px 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
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
  min-width: 150px;
}

.job-offers-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  padding: 0 20px;
}

@media (max-width: 1400px) {
  .job-offers-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 1024px) {
  .job-offers-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .job-offers-grid { grid-template-columns: 1fr; }
}

.job-offer-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-left: 4px solid #059669;
  border: 1px solid #e2ece8;
}

.job-offer-card.inactive {
  opacity: 0.7;
  border-left-color: #94d3bd;
  background: #f7fbf9;
}

.job-offer-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
}

.offer-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.offer-title {
  margin: 0 0 5px 0;
  color: #065f46;
  font-size: 20px;
}

.offer-department {
  margin: 0 0 10px 0;
  color: #0f766e;
  font-weight: 600;
  font-size: 16px;
}

.offer-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-item {
  font-size: 14px;
  color: #065f46;
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
  background: #059669;
  color: #ffffff;
}

.status-expired {
  background: rgba(5, 150, 105, 0.2);
  color: #065f46;
}

.offer-description {
  margin-bottom: 20px;
}

.offer-description p {
  color: #065f46;
  line-height: 1.6;
  margin: 0;
}

.offer-requirements h4 {
  margin: 0 0 10px 0;
  color: #065f46;
  font-size: 16px;
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
  color: #1e3a8a;
  font-size: 14px;
  font-weight: 600;
}

.skill-tag {
  background: #059669;
  color: #ffffff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.diploma-tag {
  background: #10b981;
  color: #ffffff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid #10b981;
}

.empty-message {
  color: #065f46;
  font-style: italic;
  font-size: 14px;
}

.offer-details {
  margin-bottom: 20px;
  padding: 15px;
  background: rgba(5, 150, 105, 0.08);
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
  color: #065f46;
}

.detail-value {
  color: #0f766e;
}

.offer-actions {
  display: flex;
  justify-content: center;
}

.btn-apply {
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 25px;
  background: #059669;
  border: 2px solid #059669;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-apply:hover:not(:disabled) {
  transform: translateY(-2px);
  background: #047857;
  color: #ffffff;
  box-shadow: 0 5px 15px rgba(16, 185, 129, 0.35);
}

.btn-apply:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.error-icon, .empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.error-state h3 {
  color: #065f46;
  margin-bottom: 10px;
}

.empty-state h3 {
  color: #065f46;
  margin-bottom: 10px;
}

.error-state p, .empty-state p {
  color: #0f766e;
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
  background: #ffffff;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #e2ece8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #065f46;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #065f46;
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

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
  color: #065f46;
}

.form-input {
  width: 100%;
  padding: 10px;
  border: 2px solid #e2ece8;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  color: #065f46;
}

.form-input:focus {
  outline: none;
  border-color: #059669;
  box-shadow: 0 0 0 2px rgba(5, 150, 105, 0.2);
}

.modal-actions {
  padding: 20px;
  border-top: 1px solid #e2ece8;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #059669;
  color: #ffffff;
}

.btn-primary:hover {
  background: #047857;
  color: #ffffff;
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

.dynamic-fields-section {
  margin: 20px 0;
  padding: 20px;
  background: rgba(5, 150, 105, 0.08);
  border-radius: 8px;
  border-left: 4px solid #059669;
}

.dynamic-fields-section h4 {
  margin: 0 0 15px 0;
  color: #1e3a8a;
  font-size: 16px;
}

.expected-value {
  color: #1e3a8a;
  font-weight: normal;
  font-size: 12px;
  font-style: italic;
}

.required-asterisk {
  color: #1e3a8a;
  margin-left: 3px;
}

.checkbox-group, .radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 8px;
}

.checkbox-label, .radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: normal;
}

.checkbox-label input, .radio-label input {
  margin: 0;
}

.checkbox-text {
  font-size: 14px;
}

.auth-modal {
  max-width: 400px;
}

.auth-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

.btn-auth {
  width: 100%;
  padding: 12px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .job-offers-grid {
    grid-template-columns: 1fr;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .offer-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .offer-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .detail-row {
    flex-direction: column;
    gap: 5px;
  }
  
  .modal {
    width: 95%;
    max-height: 90vh;
  }
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.filters.show {
  display: flex;
}

.filters.hide {
  display: none;
}

.toggle-filters-btn {
  background: #059669;
  color: #ffffff;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.toggle-filters-btn:hover {
  background: #047857;
  color: #ffffff;
}
</style>
