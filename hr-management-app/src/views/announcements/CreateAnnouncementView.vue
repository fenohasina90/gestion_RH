<template>
  <div class="create-announcement-view">
    <div class="page-header">
      <h1 class="page-title">Créer une Nouvelle Annonce</h1>
      <router-link to="/announcements" class="btn btn-secondary">
        ← Retour à la liste
      </router-link>
    </div>

    <!-- Loading state -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Chargement des profils...</p>
    </div>

    <!-- Profiles grid -->
    <div v-else class="profiles-container">
      <div class="profiles-grid">
        <div 
          v-for="profil in profils" 
          :key="profil.id" 
          class="profile-card"
        >
          <div class="profile-header">
            <h3 class="profile-name">{{ profil.nom }}</h3>
<!--            <span class="profile-id">#{{ profil.description }}</span>-->
          </div>
          
          <div class="profile-info">
            <div class="info-section">
              <h4>Critères</h4>
              <div class="criteria-list">
                <span 
                  v-for="critere in profil.criteres" 
                  :key="critere.id"
                  class="criteria-tag"
                >
                  {{ critere.nom }}
                </span>
                <span v-if="!profil.criteres || profil.criteres.length === 0" class="no-data">
                  Aucun critère
                </span>
              </div>
            </div>
            
            <div class="info-section">
              <h4>Diplômes</h4>
              <div class="diplomas-list">
                <span 
                  v-for="diplome in profil.diplomes" 
                  :key="diplome.id"
                  class="diploma-tag"
                >
                  {{ diplome.nom }}
                </span>
                <span v-if="!profil.diplomes || profil.diplomes.length === 0" class="no-data">
                  Aucun diplôme
                </span>
              </div>
            </div>
          </div>
          
          <div class="profile-actions">
            <button 
              @click="openPublishModal(profil)" 
              class="btn btn-primary publish-btn"
            >
              📢 Publier
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Publish Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Publier une annonce pour {{ selectedProfil?.nom }}</h2>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        
        <form @submit.prevent="publishAnnouncement" class="modal-form">
          <div class="form-group">
            <label for="typeContrat" class="form-label">Type de contrat *</label>
            <select 
              id="typeContrat" 
              v-model="announcementForm.typeContrat" 
              class="form-input" 
              required
              @change="onContractTypeChange"
            >
              <option value="">Sélectionner un type de contrat</option>
              <option 
                v-for="type in typesAnnonce" 
                :key="type.id" 
                :value="type.id"
              >
                {{ type.libelle }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label for="departement" class="form-label">Département *</label>
            <select 
              id="departement" 
              v-model="announcementForm.departement" 
              class="form-input" 
              required
            >
              <option value="">Sélectionner un département</option>
              <option 
                v-for="dept in departements" 
                :key="dept.id" 
                :value="dept.id"
              >
                {{ dept.nom }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="province" class="form-label">Province *</label>
            <select 
              id="province" 
              v-model="announcementForm.province" 
              class="form-input" 
              required
            >
              <option value="">Sélectionner une province</option>
              <option 
                v-for="prov in provinces" 
                :key="prov.id" 
                :value="prov.id"
              >
                {{ prov.nom }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label for="dateDebut" class="form-label">Date de début *</label>
            <input 
              type="date" 
              id="dateDebut" 
              v-model="announcementForm.dateDebut" 
              class="form-input" 
              required
            />
          </div>
          
          <div v-if="showDateFin" class="form-group">
            <label for="dateFin" class="form-label">Date de fin *</label>
            <input 
              type="date" 
              id="dateFin" 
              v-model="announcementForm.dateFin" 
              class="form-input" 
              required
            />
          </div>
          
          <div class="form-group">
            <label for="description" class="form-label">Description</label>
            <textarea 
              id="description" 
              v-model="announcementForm.description" 
              class="form-input" 
              rows="4"
              placeholder="Description de l'annonce..."
            ></textarea>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="btn btn-secondary">
              Annuler
            </button>
            <button type="submit" class="btn btn-primary" :disabled="isPublishing">
              <span v-if="isPublishing">Publication en cours...</span>
              <span v-else>Publier l'annonce</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Messages -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CreateAnnouncementView',
  data() {
    return {
      profils: [],
      typesAnnonce: [],
      departements: [],
      isLoading: true,
      showModal: false,
      selectedProfil: null,
      isPublishing: false,
      errorMessage: '',
      successMessage: '',
      announcementForm: {
        typeContrat: '',
        departement: '',
        province: '',
        dateDebut: '',
        dateFin: '',
        description: ''
      }
    }
  },
  computed: {
    showDateFin() {
      // Afficher le champ date de fin seulement si le type de contrat est CDD
      const selectedType = this.typesAnnonce.find(type => type.id === this.announcementForm.typeContrat)
      return selectedType && selectedType.libelle.toLowerCase().includes('cdd')
    }
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.isLoading = true
        
        // Charger les profils avec leurs critères et diplômes
        const [profilsResponse, typesResponse, departementsResponse, provincesResponse] = await Promise.all([
          this.loadProfils(),
          this.loadTypesAnnonce(),
          this.loadDepartements(),
          this.loadProvinces()
        ])
        
        this.profils = profilsResponse
        this.typesAnnonce = typesResponse
        this.departements = departementsResponse
        this.provinces = provincesResponse
        
      } catch (error) {
        console.error('Erreur lors du chargement des données:', error)
        this.errorMessage = 'Erreur lors du chargement des données'
      } finally {
        this.isLoading = false
      }
    },
    
    async loadProfils() {
      try {
        const response = await axios.get('/api/profils')
        let profils = response.data
        
        // Vérifier que profils est un tableau et non du HTML
        if (!Array.isArray(profils)) {
          console.error('La réponse des profils n\'est pas un tableau:', profils)
          // Si c'est du HTML, c'est que l'API backend n'est pas accessible
          if (typeof profils === 'string' && profils.includes('<!DOCTYPE html>')) {
            this.errorMessage = 'Erreur: Le serveur backend n\'est pas accessible. Veuillez vérifier que le serveur Spring Boot est démarré.'
            return []
          }
          return []
        }
        
        // Charger les critères et diplômes pour chaque profil
        for (let profil of profils) {
          // Vérifier que profil est un objet et a un id
          if (!profil || typeof profil !== 'object' || !profil.id) {
            console.error('Profil invalide:', profil)
            continue
          }
          
          try {
            const [criteresResponse, diplomesResponse] = await Promise.all([
              axios.get(`/api/profils/${profil.id}/criteres`).catch(() => ({ data: [] })),
              axios.get(`/api/profils/${profil.id}/diplomes`).catch(() => ({ data: [] }))
            ])
            
            // Extraire les critères depuis les objets Critereprofil
            const critereprofils = Array.isArray(criteresResponse.data) ? criteresResponse.data : []
            profil.criteres = critereprofils.map(cp => cp.idcritere || cp.critere || { nom: 'Critère inconnu' })
            
            // Extraire les diplômes depuis les objets Profildiplome
            const profildiplomes = Array.isArray(diplomesResponse.data) ? diplomesResponse.data : []
            profil.diplomes = profildiplomes.map(pd => pd.iddiplome || pd.diplome || { nom: 'Diplôme inconnu' })
          } catch (error) {
            console.error(`Erreur lors du chargement des données pour le profil ${profil.id}:`, error)
            profil.criteres = []
            profil.diplomes = []
          }
        }
        
        return profils
      } catch (error) {
        console.error('Erreur lors du chargement des profils:', error)
        return []
      }
    },
    
    async loadTypesAnnonce() {
      try {
        const response = await axios.get('/api/typeannonces')
        return response.data || []
      } catch (error) {
        console.error('Erreur lors du chargement des types d\'annonce:', error)
        return []
      }
    },
    
    async loadDepartements() {
      try {
        const response = await axios.get('/api/departements')
        return response.data || []
      } catch (error) {
        console.error('Erreur lors du chargement des départements:', error)
        return []
      }
    },
    
    async loadProvinces() {
      try {
        const response = await axios.get('/api/provinces')
        return response.data || []
      } catch (error) {
        console.error('Erreur lors du chargement des provinces:', error)
        return []
      }
    },
    
    openPublishModal(profil) {
      this.selectedProfil = profil
      this.showModal = true
      
      // Réinitialiser le formulaire
      this.announcementForm = {
        typeContrat: '',
        departement: '',
        province: '',
        dateDebut: '',
        dateFin: '',
        description: ''
      }
      
      // Définir la date de début par défaut à aujourd'hui
      const today = new Date().toISOString().split('T')[0]
      this.announcementForm.dateDebut = today
    },
    
    closeModal() {
      this.showModal = false
      this.selectedProfil = null
      this.errorMessage = ''
    },
    
    onContractTypeChange() {
      // Réinitialiser la date de fin quand le type de contrat change
      if (!this.showDateFin) {
        this.announcementForm.dateFin = ''
      }
    },
    
    async publishAnnouncement() {
      try {
        this.isPublishing = true
        this.errorMessage = ''
        
        // Validation
        if (!this.announcementForm.typeContrat || !this.announcementForm.departement || 
            !this.announcementForm.province || !this.announcementForm.dateDebut) {
          this.errorMessage = 'Veuillez remplir tous les champs obligatoires'
          return
        }
        
        if (this.showDateFin && !this.announcementForm.dateFin) {
          this.errorMessage = 'La date de fin est obligatoire pour ce type de contrat'
          return
        }
        
        // Vérifier que la date de fin est après la date de début
        if (this.announcementForm.dateFin && 
            new Date(this.announcementForm.dateFin) <= new Date(this.announcementForm.dateDebut)) {
          this.errorMessage = 'La date de fin doit être postérieure à la date de début'
          return
        }
        
        // Préparer les données pour l'API
        const annonceData = {
          nomposte: this.selectedProfil.nom,
          description: this.announcementForm.description,
          datedebut: this.announcementForm.dateDebut,
          datefin: this.showDateFin ? this.announcementForm.dateFin : null,
          idprofil: { id: this.selectedProfil.id },
          iddepartement: { id: this.announcementForm.departement },
          idtypeannonce: { id: this.announcementForm.typeContrat },
          idprovince: { id: this.announcementForm.province }
        }
        
        console.log('Données envoyées:', annonceData)
        
        // Créer l'annonce
        await axios.post('/api/annonces', annonceData)
        
        this.successMessage = `Annonce publiée avec succès pour le profil "${this.selectedProfil.nom}" !`
        this.closeModal()
        
        // Effacer le message de succès après 5 secondes
        setTimeout(() => {
          this.successMessage = ''
        }, 5000)
        
      } catch (error) {
        console.error('Erreur lors de la publication de l\'annonce:', error)
        this.errorMessage = 'Erreur lors de la publication de l\'annonce. Veuillez réessayer.'
      } finally {
        this.isPublishing = false
      }
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

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #28a745;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Profiles Grid */
.profiles-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.profiles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
  max-width: 1400px;
  margin: 0 auto;
}

@media (min-width: 768px) {
  .profiles-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1200px) {
  .profiles-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.profile-card {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.profile-card:hover {
  border-color: #28a745;
  box-shadow: 0 8px 25px rgba(40, 167, 69, 0.15);
  transform: translateY(-2px);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #dee2e6;
}

.profile-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.profile-id {
  background: #28a745;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.profile-info {
  flex-grow: 1;
  margin-bottom: 20px;
}

.info-section {
  margin-bottom: 15px;
}

.info-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  margin: 0 0 8px 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.criteria-list, .diplomas-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.criteria-tag {
  background: #d1f2d8;
  color: #155724;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.diploma-tag {
  background: #d4edda;
  color: #28a745;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.no-data {
  color: #6c757d;
  font-style: italic;
  font-size: 12px;
}

.profile-actions {
  margin-top: auto;
}

.publish-btn {
  width: 100%;
  padding: 12px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.publish-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #f8f9fa;
  color: #333;
}

.modal-form {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #28a745;
  box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.1);
}

.modal-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* Messages */
.error-message {
  background: #fee;
  color: #c33;
  padding: 15px;
  border-radius: 8px;
  margin: 20px 0;
  text-align: center;
  border: 1px solid #fcc;
}

.success-message {
  background: #efe;
  color: #363;
  padding: 15px;
  border-radius: 8px;
  margin: 20px 0;
  text-align: center;
  border: 1px solid #cfc;
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .profiles-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .modal-overlay {
    padding: 10px;
  }
  
  .modal-header {
    padding: 20px;
  }
  
  .modal-form {
    padding: 20px;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
</style>
