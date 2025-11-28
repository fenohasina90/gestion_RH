<template>
  <div class="apply-job-container">
    <!-- Header avec bouton logout -->
    <div class="header-section" v-if="candidateAuthStore.isLoggedIn">
      <div class="user-info">
        <span>Connecté en tant que: {{ candidateAuthStore.candidate?.email }}</span>
        <button @click="handleLogout" class="logout-btn">
          <i class="fas fa-sign-out-alt"></i> Déconnexion
        </button>
      </div>
    </div>

    <div class="apply-job-content">
      <!-- Section gauche: Formulaire de candidature -->
      <div class="application-section">
        <div class="auth-section" v-if="!candidateAuthStore.isLoggedIn">
          <h2>Authentification requise</h2>
          <p>Vous devez vous connecter pour postuler à cette offre.</p>
          
          <div class="auth-toggle">
            <button 
              @click="authMode = 'login'" 
              :class="{ active: authMode === 'login' }"
            >
              Connexion
            </button>
            <button 
              @click="authMode = 'register'" 
              :class="{ active: authMode === 'register' }"
            >
              Inscription
            </button>
          </div>
        </div>

        <div class="form-header">
          <h2>Postuler pour ce poste</h2>
          <p class="form-subtitle">Remplissez le formulaire ci-dessous pour soumettre votre candidature</p>
        </div>

        <form @submit.prevent="submitApplication" class="application-form">
          <!-- Informations personnelles -->
          <div class="form-section">
            <h3>Informations personnelles</h3>
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
            <div class="form-group">
              <label>Province *</label>
              <select v-model="applicationForm.province" class="form-input" required>
                <option value="">Sélectionner une province</option>
                <option v-for="prov in provinces" :key="prov.id" :value="prov.id">{{ prov.nom }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Prétention salariale</label>
              <input 
                type="number" 
                v-model.number="applicationForm.salaire" 
                class="form-input" 
                min="0" 
                step="0.01" 
                placeholder="Ex: 1200.00"
              >
            </div>
          </div>
          
          <!-- Champs dynamiques basés sur les critères du profil -->
          <div v-if="dynamicFields.length > 0" class="form-section dynamic-fields-section">
            <h3>Exigences du poste</h3>
            <div v-for="field in dynamicFields" :key="field.id" class="form-group">
              <label>
                {{ field.idcritere ? field.idcritere.nom : 'Critère' }}
                <span v-if="getExpectedValue(field)" class="expected-value">({{ getExpectedValue(field) }})</span>
                <span v-if="field.estobligatoire" class="required-asterisk">*</span>
              </label>
              
              <!-- Cas spécial: Diplome requis -> désactiver l'entrée et proposer la liste de diplômes du profil -->
              <template v-if="field.idcritere && field.idcritere.nom && field.idcritere.nom.toLowerCase() === 'diplome requis'">
                <input type="text" class="form-input disabled-input" value="Géré via la liste de diplômes ci-dessous" disabled />
                <div class="form-group" style="margin-top:10px;">
                  <label>Diplôme requis</label>
                  <select v-model="applicationForm.diplomeId" class="form-input" :required="field.estobligatoire">
                    <option value="">Sélectionner un diplôme</option>
                    <option v-for="d in allDiplomes" :key="d.id" :value="d.id">
                      {{ d.nom }}{{ isExpectedDiplome(d.id) ? ' (Attendu)' : '' }}
                    </option>
                  </select>
                  <small v-if="expectedDiplomeNames.length" class="expected-value">Attendu: {{ expectedDiplomeNames.join(', ') }}</small>
                </div>
              </template>
              
              <!-- Sinon afficher les champs dynamiques par type -->
              <template v-else>
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
                
                <!-- Select générique -->
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
              </template>
            </div>
          </div>
          
          <!-- CV et lettre de motivation -->
          <div class="form-section">
            <h3>CV et motivation</h3>
            <div class="form-group">
              <label>CV / Lettre de motivation</label>
              <textarea v-model="applicationForm.cv" class="form-input" rows="6" placeholder="Décrivez votre expérience, vos compétences et votre motivation..."></textarea>
            </div>
          </div>

          <!-- Boutons d'action -->
          <div class="form-actions">
            <button type="button" @click="goBack" class="btn btn-secondary">
              Retour aux offres
            </button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? 'Envoi en cours...' : 'Envoyer ma candidature' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Détails de l'offre à droite -->
      <div class="job-details-section">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Chargement de l'offre...</p>
        </div>

        <div v-else-if="error" class="error-state">
          <div class="error-icon">⚠️</div>
          <h3>Erreur</h3>
          <p>{{ error }}</p>
        </div>

        <div v-else-if="jobOffer" class="job-details">
          <div class="job-header">
            <h2>{{ jobOffer.nomposte || (jobOffer.idprofil ? jobOffer.idprofil.nom : 'Poste non défini') }}</h2>
            <div class="job-meta">
              <div class="meta-item">
                <span class="meta-label">📍 Département:</span>
                <span class="meta-value">{{ jobOffer.iddepartement ? jobOffer.iddepartement.nom : 'Non défini' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">💼 Type:</span>
                <span class="meta-value">{{ jobOffer.idtypeannonce ? jobOffer.idtypeannonce.libelle.toUpperCase() : 'TYPE NON DÉFINI' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">📅 Période:</span>
                <span class="meta-value">Du {{ formatDate(jobOffer.datedebut) }} au {{ formatDate(jobOffer.datefin) }}</span>
              </div>
            </div>
          </div>

          <div class="job-description">
            <h3>Description du poste</h3>
            <p>{{ jobOffer.description || 'Aucune description disponible' }}</p>
          </div>

          <div v-if="jobOffer.idprofil" class="job-requirements">
            <h3>Compétences et exigences</h3>
            
            <div v-if="jobOffer.criteres && jobOffer.criteres.length > 0" class="criteria-section">
              <h4>Critères requis:</h4>
              <div class="criteria-list">
                <div 
                  v-for="critere in jobOffer.criteres" 
                  :key="critere.id" 
                  class="criteria-item"
                  :class="{ 'required': critere.estobligatoire }"
                >
                  <span class="criteria-name">{{ getCriteriaDisplayText(critere) }}</span>
                  <span v-if="critere.estobligatoire" class="required-badge">Obligatoire</span>
                </div>
              </div>
            </div>
            
            <div v-if="jobOffer.diplomes && jobOffer.diplomes.length > 0" class="diplomas-section">
              <h4>Diplômes requis:</h4>
              <div class="diplomas-list">
                <span 
                  v-for="diplome in jobOffer.diplomes" 
                  :key="diplome.id" 
                  class="diploma-tag"
                >
                  {{ diplome.iddiplome ? diplome.iddiplome.nom : 'Diplôme non défini' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal d'authentification -->
    <div v-if="showAuthModal" class="modal-overlay" @click="showAuthModal = false">
      <div class="modal auth-modal" @click.stop>
        <div class="modal-header">
          <h3>Connexion candidat requise</h3>
          <button @click="showAuthModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-content">
          <div v-if="authMode === 'login'" class="auth-form">
            <h4>Se connecter</h4>
            <form @submit.prevent="loginCandidate">
              <div class="form-group">
                <label>Email</label>
                <input type="email" v-model="authForm.email" required class="form-input" value="rakoto.hery@gmail.com">
              </div>
              <div class="form-group">
                <label>Mot de passe</label>
                <input type="password" v-model="authForm.motdepasse" required class="form-input">
              </div>
              <div class="auth-actions">
                <button type="submit" class="btn btn-primary">Se connecter</button>
                <button type="button" @click="authMode = 'register'" class="btn btn-link">
                  Pas de compte ? S'inscrire
                </button>
              </div>
            </form>
          </div>

          <div v-else class="auth-form">
            <h4>Créer un compte candidat</h4>
            <form @submit.prevent="registerCandidate">
              <div class="form-group">
                <label>Email</label>
                <input type="email" v-model="authForm.email" required class="form-input">
              </div>
              <div class="form-group">
                <label>Mot de passe</label>
                <input type="password" v-model="authForm.motdepasse" required class="form-input">
              </div>
              <div class="form-group">
                <label>Confirmer le mot de passe</label>
                <input type="password" v-model="authForm.confirmPassword" required class="form-input">
              </div>
              <div class="auth-actions">
                <button type="submit" class="btn btn-primary">S'inscrire</button>
                <button type="button" @click="authMode = 'login'" class="btn btn-link">
                  Déjà un compte ? Se connecter
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useCandidateAuthStore } from '../../stores/candidateAuth'

export default {
  name: 'ApplyJobView',
  setup() {
    const candidateAuthStore = useCandidateAuthStore()
    return {
      candidateAuthStore
    }
  },
  computed: {
    expectedDiplomeIds() {
      if (!this.jobOffer || !this.jobOffer.diplomes) return []
      return this.jobOffer.diplomes
        .map(dp => (dp && dp.iddiplome ? dp.iddiplome.id : null))
        .filter(id => id !== null && id !== undefined)
    },
    expectedDiplomeNames() {
      if (!this.jobOffer || !this.jobOffer.diplomes) return []
      return this.jobOffer.diplomes
        .map(dp => (dp && dp.iddiplome ? dp.iddiplome.nom : null))
        .filter(n => !!n)
    }
  },
  data() {
    return {
      jobOffer: null,
      loading: true,
      error: null,
      submitting: false,
      showAuthModal: false,
      authMode: 'login',
      applicationForm: {
        nom: '',
        prenom: '',
        datenaissance: '',
        adresse: '',
        diplomeId: '',
        province: '',
        cv: '',
        salaire: null
      },
      authForm: {
        email: 'rakoto.hery@gmail.com',
        motdepasse: 'pass123',
        confirmPassword: ''
      },
      dynamicFields: [],
      allDiplomes: [],
      provinces: []
    }
  },
  async mounted() {
    if (!this.candidateAuthStore.isLoggedIn) {
      this.showAuthModal = true
    }
    await this.loadJobOffer()
    await this.loadProvinces()
    await this.loadDiplomes()
  },
  methods: {
    isExpectedDiplome(id) {
      return this.expectedDiplomeIds.includes(id)
    },
    async loadDiplomes() {
      try {
        const resp = await axios.get('/api/diplomes')
        this.allDiplomes = Array.isArray(resp.data) ? resp.data : []
      } catch (e) {
        console.warn('Impossible de charger les diplomes', e)
        this.allDiplomes = []
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
    async loadJobOffer() {
      try {
        this.loading = true
        this.error = null
        
        const offerId = this.$route.params.id
        const response = await axios.get(`/api/annonces/${offerId}`)
        this.jobOffer = response.data
        
        await this.loadJobDetails()
        
      } catch (error) {
        console.error('Erreur lors du chargement de l\'offre:', error)
        this.error = 'Impossible de charger les détails de l\'offre.'
      } finally {
        this.loading = false
      }
    },
    
    async loadJobDetails() {
      if (this.jobOffer.idprofil && this.jobOffer.idprofil.id) {
        try {
          const criteresResponse = await axios.get(`/api/profils/${this.jobOffer.idprofil.id}/criteres`)
          this.jobOffer.criteres = criteresResponse.data
          this.dynamicFields = criteresResponse.data.map(critere => ({
            ...critere,
            value: null,
            fieldName: `critere_${critere.id}`
          }))
          
          const diplomesResponse = await axios.get(`/api/profils/${this.jobOffer.idprofil.id}/diplomes`)
          this.jobOffer.diplomes = diplomesResponse.data
          // Pré-sélectionner le diplôme attendu s'il n'y en a qu'un
          if (!this.applicationForm.diplomeId && this.expectedDiplomeIds.length === 1) {
            this.applicationForm.diplomeId = this.expectedDiplomeIds[0]
          }
          
        } catch (error) {
          console.warn('Erreur lors du chargement des détails:', error)
          this.jobOffer.criteres = []
          this.jobOffer.diplomes = []
          this.dynamicFields = []
        }
      }
    },
    
    async loginCandidate() {
      const result = await this.candidateAuthStore.login(this.authForm.email, this.authForm.motdepasse)
      if (result.success) {
        this.showAuthModal = false
        this.resetAuthForm()
      } else {
        alert(result.message || 'Erreur de connexion')
      }
    },
    
    async registerCandidate() {
      if (this.authForm.motdepasse !== this.authForm.confirmPassword) {
        alert('Les mots de passe ne correspondent pas')
        return
      }
      
      const result = await this.candidateAuthStore.register(this.authForm.email, this.authForm.motdepasse)
      if (result.success) {
        this.showAuthModal = false
        this.resetAuthForm()
      } else {
        alert(result.message || 'Erreur d\'inscription')
      }
    },
    
    resetAuthForm() {
      this.authForm = {
        email: '',
        motdepasse: '',
        confirmPassword: ''
      }
    },
    
    async handleLogout() {
      try {
        await this.candidateAuthStore.logout()
        this.$router.push('/jobs')
      } catch (error) {
        console.error('Erreur lors de la déconnexion:', error)
      }
    },

    async submitApplication() {
      if (!this.candidateAuthStore.isLoggedIn) {
        this.showAuthModal = true
        return
      }

      try {
        this.submitting = true
        
        // Debug: Vérifier les données avant soumission
        console.log('=== DEBUG SOUMISSION ===')
        console.log('jobOffer:', this.jobOffer)
        console.log('candidateAuthStore.candidate:', this.candidateAuthStore.candidate)
        console.log('applicationForm:', this.applicationForm)
        console.log('dynamicFields:', this.dynamicFields)
        
        // Vérifications de sécurité
        if (!this.jobOffer || !this.jobOffer.id) {
          throw new Error('Aucune offre d\'emploi sélectionnée')
        }
        
        if (!this.candidateAuthStore.candidate || !this.candidateAuthStore.candidate.id) {
          throw new Error('Candidat non authentifié')
        }
        
        // Préparer les données de candidature avec critères
        const candidatureData = {
          nom: this.applicationForm.nom,
          prenom: this.applicationForm.prenom,
          datenaissance: this.applicationForm.datenaissance,
          adresse: this.applicationForm.adresse,
          cv: this.applicationForm.cv,
          salaire: this.applicationForm.salaire,
          idannonce: this.jobOffer.id,
          idcomptecandidat: this.candidateAuthStore.candidate.id,
          idprovince: this.applicationForm.province || null,
          iddiplome: this.applicationForm.diplomeId || null
        }

        // Validation spécifique: si un critère "Diplome requis" est obligatoire, s'assurer qu'un diplôme est sélectionné
        const hasDiplomeRequisObligatoire = (this.dynamicFields || []).some(f => (
          f.idcritere && f.idcritere.nom && f.idcritere.nom.toLowerCase() === 'diplome requis' && f.estobligatoire
        ))
        if (hasDiplomeRequisObligatoire && !this.applicationForm.diplomeId) {
          alert('Veuillez sélectionner un diplôme pour le critère Diplôme requis.')
          this.submitting = false
          return
        }

        // Ajouter les valeurs des critères
        console.log('=== TRAITEMENT CRITÈRES ===')
        if (this.dynamicFields && this.dynamicFields.length > 0) {
          this.dynamicFields.forEach((field, index) => {
            console.log(`Critère ${index}:`, field)
            // Cas spécial: Diplome requis -> utiliser la sélection diplomeId
            const isDiplomeRequis = field.idcritere && field.idcritere.nom && field.idcritere.nom.toLowerCase() === 'diplome requis'
            const valueToSend = isDiplomeRequis ? this.applicationForm.diplomeId : field.value

            if (valueToSend !== null && valueToSend !== undefined && valueToSend !== '') {
              if (field.idcritere && field.idcritere.id) {
                candidatureData[`critere_${field.idcritere.id}`] = valueToSend
                console.log(`✓ Critère ajouté: critere_${field.idcritere.id} = ${valueToSend}`)
              } else {
                console.warn(`⚠️ Critère ${index} sans idcritere ou id:`, field)
              }
            } else {
              console.log(`- Critère ${index} ignoré (valeur vide)`)
            }
          })
        } else {
          console.log('Aucun critère dynamique trouvé')
        }
        
        console.log('Données finales à envoyer:', candidatureData)

        const response = await axios.post('/api/candidats', candidatureData)
        
        if (response.status === 200 || response.status === 201) {
          alert('Candidature soumise avec succès!')
          // Rediriger vers la page des offres
          this.$router.push('/jobs')
        }
      } catch (error) {
        console.error('Erreur lors de la soumission:', error)
        alert('Erreur lors de la soumission de la candidature')
      } finally {
        this.submitting = false
      }
    },
    
    goBack() {
      this.$router.push('/jobs')
    },
    
    formatDate(dateString) {
      if (!dateString) return 'Non définie'
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR')
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
    
    getCriteriaDisplayText(critere) {
      if (!critere || !critere.idcritere) return 'Critère non défini'
      
      const critereName = critere.idcritere.nom
      let value = ''
      
      if (critere.valeurdouble !== null) {
        value = critere.valeurdouble
      } else if (critere.valeurvarchar !== null) {
        value = critere.valeurvarchar
      } else if (critere.valeurbool !== null) {
        value = critere.valeurbool ? 'Oui' : 'Non'
      }
      
      if (critereName.toLowerCase().includes('expérience')) {
        return `${critereName}: ${value} an${value > 1 ? 's' : ''}`
      } else if (value) {
        return `${critereName}: ${value}`
      } else {
        return critereName
      }
    }
  }
}
</script>

<style scoped>
.apply-job-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f9f7 0%, #eefaf6 100%);
  padding: 20px;
}

.header-section {
  max-width: 1200px;
  margin: 0 auto 20px auto;
  background: #ffffff;
  border-radius: 10px;
  padding: 15px 25px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
}

.user-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info span {
  color: #065f46;
  font-weight: 600;
}

.logout-btn {
  background: #065f46;
  color: #ffffff;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background: #047857;
}

.apply-job-content {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  gap: 0;
  background: #ffffff;
  border-radius: 15px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
  overflow: hidden;
  min-height: 80vh;
  width: 100%;
}

.application-section {
  flex: 1;
  padding: 40px;
  overflow-y: auto;
  border-right: 1px solid #e2ece8;
}

.job-details-section {
  flex: 1;
  background: #f7fbf9;
  padding: 40px;
  overflow-y: auto;
}

.form-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #059669;
}

.form-header h2 {
  margin: 0 0 10px 0;
  color: #065f46;
  font-size: 28px;
}

.form-subtitle {
  margin: 0;
  color: #0f766e;
  font-size: 16px;
}

.form-section {
  margin-bottom: 40px;
  padding: 25px;
  background: #f7fbf9;
  border-radius: 8px;
  border-left: 4px solid #059669;
}

.form-section h3 {
  margin: 0 0 20px 0;
  color: #065f46;
  font-size: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #065f46;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e2ece8;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  color: #065f46;
}

.form-input:focus {
  outline: none;
  border-color: #059669;
  box-shadow: 0 0 0 2px rgba(5, 150, 105, 0.2);
}

.expected-value {
  color: #0f766e;
  font-weight: normal;
  font-size: 12px;
  font-style: italic;
}

.required-asterisk {
  color: #be123c;
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

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  padding-top: 30px;
  border-top: 1px solid #e2ece8;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #059669;
  color: #ffffff;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  background: #047857;
  box-shadow: 0 5px 15px rgba(16, 185, 129, 0.35);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
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

.job-details {
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
}

.job-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e2ece8;
}

.job-header h2 {
  margin: 0 0 20px 0;
  color: #065f46;
  font-size: 24px;
}

.job-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.meta-label {
  font-weight: 600;
  color: #065f46;
  min-width: 120px;
}

.meta-value {
  color: #0f766e;
}

.job-description {
  margin-bottom: 30px;
}

.job-description h3 {
  margin: 0 0 15px 0;
  color: #065f46;
  font-size: 20px;
}

.job-description p {
  color: #065f46;
  line-height: 1.6;
  margin: 0;
}

.job-requirements h3 {
  margin: 0 0 20px 0;
  color: #065f46;
  font-size: 20px;
}

.criteria-section, .diplomas-section {
  margin-bottom: 25px;
}

.criteria-section h4, .diplomas-section h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
}

.criteria-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.criteria-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #f7fbf9;
  border-radius: 6px;
  border-left: 3px solid #94d3bd;
}

.criteria-item.required {
  border-left-color: #b91c1c;
  background: #fef2f2;
}

.criteria-name {
  font-weight: 600;
  color: #065f46;
}

.required-badge {
  background: #b91c1c;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.diplomas-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.diploma-tag {
  background: #10b981;
  color: #ffffff;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #10b981;
}

.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(5, 150, 105, 0.25);
  border-top: 4px solid #059669;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon {
  font-size: 48px;
  margin-bottom: 15px;
  opacity: 0.5;
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
  max-width: 400px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 15px rgba(0,0,0,0.06);
  border: 1px solid #e2ece8;
}

.auth-modal {
  max-width: 450px;
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

.auth-form h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

.auth-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

.btn-link {
  background: none;
  border: none;
  color: #059669;
  cursor: pointer;
  text-decoration: underline;
  font-size: 14px;
  padding: 5px;
}

.btn-link:hover {
  color: #047857;
}

@media (max-width: 1024px) {
  .apply-container {
    grid-template-columns: 1fr;
  }
  
  .application-form-section {
    border-right: none;
    border-bottom: 1px solid #e9ecef;
  }
}

@media (max-width: 768px) {
  .application-form-section,
  .job-details-section {
    padding: 20px;
  }
  
  .form-header h2 {
    font-size: 24px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
