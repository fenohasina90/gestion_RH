<template>
  <div class="submit-cv-view">
    <div class="page-header">
      <h1 class="page-title">Soumettre votre Candidature</h1>
      <p class="page-subtitle">Postulez aux offres d'emploi disponibles</p>
    </div>

    <!-- Sélection de l'annonce -->
    <div class="announcement-selection">
      <h2>Choisir une offre d'emploi</h2>
      <div class="announcements-grid">
        <div 
          v-for="announcement in availableAnnouncements" 
          :key="announcement.id"
          class="announcement-card"
          :class="{ selected: selectedAnnouncement?.id === announcement.id }"
          @click="selectAnnouncement(announcement)"
        >
          <div class="announcement-header">
            <h3>{{ announcement.title }}</h3>
            <span class="company">{{ announcement.company }}</span>
          </div>
          <div class="announcement-details">
            <div class="detail">📍 {{ announcement.location }}</div>
            <div class="detail">💼 {{ announcement.type.toUpperCase() }}</div>
            <div class="detail">💰 {{ announcement.salary }}</div>
          </div>
          <div class="announcement-skills">
            <div class="skills-tags">
              <span 
                v-for="skill in announcement.requiredSkills.slice(0, 3)" 
                :key="skill" 
                class="skill-tag"
              >
                {{ skill }}
              </span>
              <span v-if="announcement.requiredSkills.length > 3" class="skill-tag more">
                +{{ announcement.requiredSkills.length - 3 }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Formulaire de candidature -->
    <div v-if="selectedAnnouncement" class="application-form-container">
      <h2>Votre candidature pour: {{ selectedAnnouncement.title }}</h2>
      
      <form @submit.prevent="handleSubmit" class="application-form">
        <!-- Informations personnelles -->
        <div class="form-section">
          <h3 class="section-title">Informations Personnelles</h3>
          
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
              rows="2"
              placeholder="123 Rue de la Paix, 75001 Paris"
            ></textarea>
          </div>
        </div>

        <!-- Expérience professionnelle -->
        <div class="form-section">
          <h3 class="section-title">Expérience Professionnelle</h3>
          
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
              <label for="expectedSalary" class="form-label">Salaire souhaité (€/an)</label>
              <input
                type="number"
                id="expectedSalary"
                v-model="form.expectedSalary"
                class="form-input"
                placeholder="45000"
                min="0"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="currentPosition" class="form-label">Poste actuel</label>
            <input
              type="text"
              id="currentPosition"
              v-model="form.currentPosition"
              class="form-input"
              placeholder="Développeur Junior chez TechCorp"
            />
          </div>
        </div>

        <!-- Formation -->
        <div class="form-section">
          <h3 class="section-title">Formation</h3>
          
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
        </div>

        <!-- Compétences -->
        <div class="form-section">
          <h3 class="section-title">Compétences</h3>
          
          <div class="form-group">
            <label for="skillInput" class="form-label">Ajouter vos compétences *</label>
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

        <!-- CV et lettre de motivation -->
        <div class="form-section">
          <h3 class="section-title">Documents</h3>
          
          <div class="form-group">
            <label for="cvFile" class="form-label">CV (PDF) *</label>
            <div class="file-upload">
              <input
                type="file"
                id="cvFile"
                @change="handleCVUpload"
                accept=".pdf"
                required
                class="file-input"
              />
              <label for="cvFile" class="file-label">
                <span v-if="!form.cvFile">📄 Choisir un fichier PDF</span>
                <span v-else>✅ {{ form.cvFile.name }}</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <label for="coverLetter" class="form-label">Lettre de motivation</label>
            <textarea
              id="coverLetter"
              v-model="form.coverLetter"
              class="form-input"
              rows="6"
              placeholder="Expliquez votre motivation pour ce poste et ce qui vous rend unique..."
            ></textarea>
          </div>
        </div>

        <!-- Disponibilité -->
        <div class="form-section">
          <h3 class="section-title">Disponibilité</h3>
          
          <div class="form-row">
            <div class="form-group">
              <label for="availability" class="form-label">Disponibilité</label>
              <select
                id="availability"
                v-model="form.availability"
                class="form-input"
              >
                <option value="">Sélectionner...</option>
                <option value="immediate">Immédiate</option>
                <option value="2weeks">Dans 2 semaines</option>
                <option value="1month">Dans 1 mois</option>
                <option value="3months">Dans 3 mois</option>
                <option value="negotiable">À négocier</option>
              </select>
            </div>

            <div class="form-group">
              <label for="workMode" class="form-label">Mode de travail préféré</label>
              <select
                id="workMode"
                v-model="form.workMode"
                class="form-input"
              >
                <option value="">Aucune préférence</option>
                <option value="onsite">Sur site</option>
                <option value="remote">Télétravail</option>
                <option value="hybrid">Hybride</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <button type="button" @click="clearForm" class="btn btn-secondary">
            Effacer
          </button>
          <button type="submit" class="btn btn-primary" :disabled="isLoading">
            <span v-if="isLoading">Envoi en cours...</span>
            <span v-else">📤 Envoyer ma candidature</span>
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

    <div v-else class="no-selection">
      <div class="no-selection-icon">👆</div>
      <h3>Sélectionnez une offre d'emploi</h3>
      <p>Choisissez l'offre qui vous intéresse pour commencer votre candidature.</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SubmitCVView',
  data() {
    return {
      selectedAnnouncement: null,
      availableAnnouncements: [
        {
          id: 1,
          title: 'Développeur Full Stack Senior',
          company: 'TechCorp',
          location: 'Paris',
          type: 'cdi',
          salary: '55 000 - 70 000 €',
          requiredSkills: ['JavaScript', 'Vue.js', 'Node.js', 'MongoDB', 'Docker']
        },
        {
          id: 2,
          title: 'Designer UX/UI',
          company: 'DesignStudio',
          location: 'Lyon',
          type: 'cdd',
          salary: '35 000 - 45 000 €',
          requiredSkills: ['Figma', 'Adobe XD', 'Prototypage', 'User Research']
        },
        {
          id: 3,
          title: 'Data Scientist',
          company: 'DataLab',
          location: 'Télétravail',
          type: 'cdi',
          salary: '60 000 - 80 000 €',
          requiredSkills: ['Python', 'R', 'Machine Learning', 'SQL', 'TensorFlow']
        }
      ],
      form: {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
        experience: '',
        expectedSalary: '',
        currentPosition: '',
        education: '',
        school: '',
        skills: [],
        cvFile: null,
        coverLetter: '',
        availability: '',
        workMode: ''
      },
      skillInput: '',
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    }
  },
  methods: {
    selectAnnouncement(announcement) {
      this.selectedAnnouncement = announcement
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
    handleCVUpload(event) {
      const file = event.target.files[0]
      if (file && file.type === 'application/pdf') {
        this.form.cvFile = file
      } else {
        alert('Veuillez sélectionner un fichier PDF')
        event.target.value = ''
      }
    },
    clearForm() {
      this.form = {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
        experience: '',
        expectedSalary: '',
        currentPosition: '',
        education: '',
        school: '',
        skills: [],
        cvFile: null,
        coverLetter: '',
        availability: '',
        workMode: ''
      }
      this.skillInput = ''
      // Reset file input
      const fileInput = document.getElementById('cvFile')
      if (fileInput) fileInput.value = ''
    },
    async handleSubmit() {
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        // Validation
        if (!this.form.firstName || !this.form.lastName || !this.form.email || 
            !this.form.phone || !this.form.experience || !this.form.education ||
            this.form.skills.length === 0 || !this.form.cvFile) {
          this.errorMessage = 'Veuillez remplir tous les champs obligatoires (*)'
          return
        }

        // Simulation de l'envoi
        await new Promise(resolve => setTimeout(resolve, 2000))

        this.successMessage = `Candidature envoyée avec succès pour le poste "${this.selectedAnnouncement.title}" chez ${this.selectedAnnouncement.company} !`
        
        // Reset form after success
        setTimeout(() => {
          this.clearForm()
          this.selectedAnnouncement = null
          this.successMessage = ''
        }, 3000)

      } catch (error) {
        this.errorMessage = 'Erreur lors de l\'envoi de la candidature. Veuillez réessayer.'
      } finally {
        this.isLoading = false
      }
    }
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

.announcement-selection {
  margin-bottom: 40px;
}

.announcement-selection h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.announcements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.announcement-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.announcement-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.announcement-card.selected {
  border-color: #3498db;
  background: #f8fbff;
}

.announcement-header h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 18px;
}

.company {
  color: #3498db;
  font-weight: 600;
}

.announcement-details {
  margin: 15px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.detail {
  font-size: 14px;
  color: #666;
}

.announcement-skills {
  margin-top: 15px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.skill-tag {
  background: #ecf0f1;
  color: #2c3e50;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.skill-tag.more {
  background: #3498db;
  color: white;
}

.application-form-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.application-form-container h2 {
  color: #2c3e50;
  margin-bottom: 30px;
  text-align: center;
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
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 18px;
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

.file-upload {
  position: relative;
}

.file-input {
  position: absolute;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.file-label {
  display: block;
  padding: 12px 20px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.file-label:hover {
  border-color: #3498db;
  background: #f0f8ff;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
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

.no-selection {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.no-selection-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.no-selection h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.no-selection p {
  color: #666;
}

@media (max-width: 768px) {
  .announcements-grid {
    grid-template-columns: 1fr;
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
  
  .announcement-details {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
