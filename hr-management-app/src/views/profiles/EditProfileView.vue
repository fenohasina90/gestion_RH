<template>
  <div class="edit-profile-view">
    <div class="page-header">
      <h1 class="page-title">Modifier le Profil</h1>
      <div class="header-actions">
        <router-link to="/profiles" class="btn btn-secondary">
          ← Retour à la liste
        </router-link>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Chargement du profil...</p>
    </div>

    <div v-else-if="!profile" class="error-state">
      <div class="error-icon">❌</div>
      <h3>Profil non trouvé</h3>
      <p>Le profil demandé n'existe pas ou a été supprimé.</p>
      <router-link to="/profiles" class="btn btn-primary">
        Retour à la liste
      </router-link>
    </div>

    <div v-else class="form-container">
      <form @submit.prevent="handleSubmit" class="profile-form">
        <!-- Informations personnelles -->
        <div class="form-section">
          <h2 class="section-title">Informations Personnelles</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="firstName" class="form-label">Prénom *</label>
              <input
                type="text"
                id="firstName"
                v-model="form.firstName"
                class="form-input"
                placeholder="Jean"
                required
              />
            </div>

            <div class="form-group">
              <label for="lastName" class="form-label">Nom *</label>
              <input
                type="text"
                id="lastName"
                v-model="form.lastName"
                class="form-input"
                placeholder="Dupont"
                required
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="email" class="form-label">Email *</label>
              <input
                type="email"
                id="email"
                v-model="form.email"
                class="form-input"
                placeholder="jean.dupont@email.com"
                required
              />
            </div>

            <div class="form-group">
              <label for="phone" class="form-label">Téléphone *</label>
              <input
                type="tel"
                id="phone"
                v-model="form.phone"
                class="form-input"
                placeholder="+33 1 23 45 67 89"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="address" class="form-label">Adresse</label>
            <textarea
              id="address"
              v-model="form.address"
              class="form-input"
              rows="3"
              placeholder="123 Rue de la Paix, 75001 Paris"
            ></textarea>
          </div>
        </div>

        <!-- Informations professionnelles -->
        <div class="form-section">
          <h2 class="section-title">Informations Professionnelles</h2>
          
          <div class="form-group">
            <label for="position" class="form-label">Poste recherché *</label>
            <input
              type="text"
              id="position"
              v-model="form.position"
              class="form-input"
              placeholder="Développeur Full Stack"
              required
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="experience" class="form-label">Années d'expérience *</label>
              <select
                id="experience"
                v-model="form.experience"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option value="0">Débutant (0 an)</option>
                <option value="1">1 an</option>
                <option value="2">2 ans</option>
                <option value="3">3 ans</option>
                <option value="4">4 ans</option>
                <option value="5">5 ans</option>
                <option value="6">6 ans</option>
                <option value="7">7 ans</option>
                <option value="8">8 ans</option>
                <option value="9">9 ans</option>
                <option value="10">10+ ans</option>
              </select>
            </div>

            <div class="form-group">
              <label for="salary" class="form-label">Salaire souhaité (€/an)</label>
              <input
                type="number"
                id="salary"
                v-model="form.salary"
                class="form-input"
                placeholder="45000"
                min="0"
              />
            </div>
          </div>
        </div>

        <!-- Formation -->
        <div class="form-section">
          <h2 class="section-title">Formation</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="education" class="form-label">Niveau d'études *</label>
              <select
                id="education"
                v-model="form.education"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option value="Baccalauréat">Baccalauréat</option>
                <option value="BTS/DUT">BTS/DUT</option>
                <option value="Licence">Licence</option>
                <option value="Master">Master</option>
                <option value="Doctorat">Doctorat</option>
                <option value="École d'ingénieur">École d'ingénieur</option>
                <option value="École de commerce">École de commerce</option>
              </select>
            </div>

            <div class="form-group">
              <label for="school" class="form-label">Établissement</label>
              <input
                type="text"
                id="school"
                v-model="form.school"
                class="form-input"
                placeholder="Université Paris-Sorbonne"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="specialization" class="form-label">Spécialisation</label>
            <input
              type="text"
              id="specialization"
              v-model="form.specialization"
              class="form-input"
              placeholder="Informatique, Génie logiciel"
            />
          </div>
        </div>

        <!-- Compétences -->
        <div class="form-section">
          <h2 class="section-title">Compétences</h2>
          
          <div class="form-group">
            <label for="skillInput" class="form-label">Ajouter des compétences</label>
            <div class="skill-input-container">
              <input
                type="text"
                id="skillInput"
                v-model="skillInput"
                class="form-input"
                placeholder="JavaScript, Vue.js, Node.js..."
                @keydown.enter.prevent="addSkill"
              />
              <button type="button" @click="addSkill" class="btn btn-secondary">
                Ajouter
              </button>
            </div>
          </div>

          <div v-if="form.skills.length > 0" class="skills-display">
            <div class="skills-tags">
              <span 
                v-for="(skill, index) in form.skills" 
                :key="index" 
                class="skill-tag"
              >
                {{ skill }}
                <button 
                  type="button" 
                  @click="removeSkill(index)"
                  class="skill-remove"
                >
                  ×
                </button>
              </span>
            </div>
          </div>
        </div>

        <!-- Description -->
        <div class="form-section">
          <h2 class="section-title">Description</h2>
          
          <div class="form-group">
            <label for="description" class="form-label">Présentation personnelle</label>
            <textarea
              id="description"
              v-model="form.description"
              class="form-input"
              rows="5"
              placeholder="Décrivez votre parcours, vos motivations et vos objectifs professionnels..."
            ></textarea>
          </div>
        </div>

        <!-- Statut -->
        <div class="form-section">
          <h2 class="section-title">Statut</h2>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.published" />
              <span class="checkmark"></span>
              Profil publié (visible dans les annonces)
            </label>
          </div>
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <router-link to="/profiles" class="btn btn-secondary">
            Annuler
          </router-link>
          <button type="submit" class="btn btn-primary" :disabled="isLoading">
            <span v-if="isLoading">Mise à jour en cours...</span>
            <span v-else>Mettre à jour</span>
          </button>
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EditProfileView',
  data() {
    return {
      loading: true,
      profile: null,
      form: {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
        position: '',
        experience: '',
        salary: '',
        education: '',
        school: '',
        specialization: '',
        skills: [],
        description: '',
        published: false
      },
      skillInput: '',
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    }
  },
  async mounted() {
    await this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        const profileId = this.$route.params.id
        
        // Simulation du chargement d'un profil
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // Données de test
        const mockProfiles = {
          '1': {
            id: 1,
            firstName: 'Jean',
            lastName: 'Dupont',
            position: 'Développeur Full Stack',
            experience: '5',
            education: 'Master en Informatique',
            email: 'jean.dupont@email.com',
            phone: '+33 1 23 45 67 89',
            address: '123 Rue de la République, 75001 Paris',
            school: 'Université Paris-Sorbonne',
            specialization: 'Informatique et Génie Logiciel',
            salary: '55000',
            skills: ['JavaScript', 'Vue.js', 'Node.js', 'MongoDB'],
            description: 'Développeur passionné avec 5 ans d\'expérience dans le développement web full-stack. Spécialisé dans les technologies JavaScript modernes.',
            published: false
          },
          '2': {
            id: 2,
            firstName: 'Marie',
            lastName: 'Martin',
            position: 'Designer UX/UI',
            experience: '3',
            education: 'Licence en Design',
            email: 'marie.martin@email.com',
            phone: '+33 1 98 76 54 32',
            address: '456 Avenue des Champs, 75008 Paris',
            school: 'École Supérieure de Design',
            specialization: 'Design d\'Interface et Expérience Utilisateur',
            salary: '42000',
            skills: ['Figma', 'Adobe XD', 'Photoshop', 'Prototypage'],
            description: 'Designer UX/UI créative avec une approche centrée utilisateur. Expérience dans la conception d\'interfaces web et mobile.',
            published: true
          }
        }
        
        this.profile = mockProfiles[profileId]
        
        if (this.profile) {
          // Copier les données du profil dans le formulaire
          Object.keys(this.form).forEach(key => {
            if (this.profile[key] !== undefined) {
              this.form[key] = this.profile[key]
            }
          })
        }
        
      } catch (error) {
        console.error('Erreur lors du chargement du profil:', error)
      } finally {
        this.loading = false
      }
    },
    addSkill() {
      if (this.skillInput.trim() && !this.form.skills.includes(this.skillInput.trim())) {
        this.form.skills.push(this.skillInput.trim())
        this.skillInput = ''
      }
    },
    removeSkill(index) {
      this.form.skills.splice(index, 1)
    },
    async handleSubmit() {
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        // Validation
        if (!this.form.firstName || !this.form.lastName || !this.form.email || 
            !this.form.phone || !this.form.position || !this.form.experience || 
            !this.form.education) {
          this.errorMessage = 'Veuillez remplir tous les champs obligatoires (*)'
          return
        }

        // Simulation de la mise à jour
        await new Promise(resolve => setTimeout(resolve, 1500))

        this.successMessage = 'Profil mis à jour avec succès !'
        
        // Redirection après 2 secondes
        setTimeout(() => {
          this.$router.push('/profiles')
        }, 2000)

      } catch (error) {
        this.errorMessage = 'Erreur lors de la mise à jour du profil. Veuillez réessayer.'
      } finally {
        this.isLoading = false
      }
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
  border-top: 4px solid #3498db;
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
  color: #2c3e50;
  margin-bottom: 10px;
}

.error-state p {
  color: #666;
  margin-bottom: 30px;
}

.form-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.form-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 30px;
}

.section-title {
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: #3498db;
  border-radius: 2px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .form-group {
  flex: 1;
}

.form-group {
  margin-bottom: 20px;
}

.skill-input-container {
  display: flex;
  gap: 10px;
}

.skill-input-container .form-input {
  flex: 1;
}

.skills-display {
  margin-top: 15px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  background: #ecf0f1;
  color: #2c3e50;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.skill-remove {
  background: none;
  border: none;
  color: #e74c3c;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.skill-remove:hover {
  background: #e74c3c;
  color: white;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #555;
}

.checkbox-label input[type="checkbox"] {
  margin: 0;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
}

.error-message {
  background: #fee;
  color: #c33;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
  text-align: center;
  border: 1px solid #fcc;
}

.success-message {
  background: #efe;
  color: #363;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
  text-align: center;
  border: 1px solid #cfc;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .skill-input-container {
    flex-direction: column;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
