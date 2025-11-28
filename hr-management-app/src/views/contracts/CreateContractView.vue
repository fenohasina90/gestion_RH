<template>
  <div class="create-contract-view">
    <div class="page-header">
      <h1 class="page-title">Créer un Contrat d'Essai</h1>
      <router-link to="/contracts" class="btn btn-secondary">
        ← Retour à la liste
      </router-link>
    </div>

    <div class="form-container">
      <form @submit.prevent="handleSubmit" class="contract-form">
        <!-- Informations du candidat -->
        <div class="form-section">
          <h2 class="section-title">Informations du Candidat</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="candidateName" class="form-label">Nom complet *</label>
              <input
                type="text"
                id="candidateName"
                v-model="form.candidateName"
                class="form-input"
                placeholder="Jean Dupont"
                required
              />
            </div>

            <div class="form-group">
              <label for="candidateEmail" class="form-label">Email *</label>
              <input
                type="email"
                id="candidateEmail"
                v-model="form.candidateEmail"
                class="form-input"
                placeholder="jean.dupont@email.com"
                required
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="candidatePhone" class="form-label">Téléphone</label>
              <input
                type="tel"
                id="candidatePhone"
                v-model="form.candidatePhone"
                class="form-input"
                placeholder="+33 1 23 45 67 89"
              />
            </div>

            <div class="form-group">
              <label for="candidateAddress" class="form-label">Adresse</label>
              <input
                type="text"
                id="candidateAddress"
                v-model="form.candidateAddress"
                class="form-input"
                placeholder="123 Rue de la Paix, 75001 Paris"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="birthDate" class="form-label">Date de naissance</label>
              <input
                type="date"
                id="birthDate"
                v-model="form.birthDate"
                class="form-input"
              />
            </div>

            <div class="form-group">
              <label for="socialSecurityNumber" class="form-label">Numéro de sécurité sociale</label>
              <input
                type="text"
                id="socialSecurityNumber"
                v-model="form.socialSecurityNumber"
                class="form-input"
                placeholder="1 23 45 67 890 123 45"
              />
            </div>
          </div>
        </div>

        <!-- Informations du poste -->
        <div class="form-section">
          <h2 class="section-title">Informations du Poste</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="position" class="form-label">Intitulé du poste *</label>
              <input
                type="text"
                id="position"
                v-model="form.position"
                class="form-input"
                placeholder="Développeur Full Stack Senior"
                required
              />
            </div>

            <div class="form-group">
              <label for="department" class="form-label">Département *</label>
              <select
                id="department"
                v-model="form.department"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option value="IT">Informatique</option>
                <option value="HR">Ressources Humaines</option>
                <option value="Marketing">Marketing</option>
                <option value="Sales">Commercial</option>
                <option value="Finance">Finance</option>
                <option value="Operations">Opérations</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="manager" class="form-label">Manager direct *</label>
              <select
                id="manager"
                v-model="form.manager"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option v-for="manager in managers" :key="manager.id" :value="manager.name">
                  {{ manager.name }} - {{ manager.position }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="workLocation" class="form-label">Lieu de travail *</label>
              <select
                id="workLocation"
                v-model="form.workLocation"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option value="Paris">Paris</option>
                <option value="Lyon">Lyon</option>
                <option value="Marseille">Marseille</option>
                <option value="Télétravail">Télétravail</option>
                <option value="Hybride">Hybride</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label for="jobDescription" class="form-label">Description du poste</label>
            <textarea
              id="jobDescription"
              v-model="form.jobDescription"
              class="form-input"
              rows="4"
              placeholder="Décrivez les principales missions et responsabilités..."
            ></textarea>
          </div>
        </div>

        <!-- Conditions contractuelles -->
        <div class="form-section">
          <h2 class="section-title">Conditions Contractuelles</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="contractType" class="form-label">Type de contrat *</label>
              <select
                id="contractType"
                v-model="form.contractType"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option value="CDI">CDI (Contrat à Durée Indéterminée)</option>
                <option value="CDD">CDD (Contrat à Durée Déterminée)</option>
                <option value="Stage">Stage</option>
                <option value="Freelance">Freelance</option>
              </select>
            </div>

            <div class="form-group">
              <label for="trialPeriod" class="form-label">Période d'essai (mois) *</label>
              <select
                id="trialPeriod"
                v-model="form.trialPeriod"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option value="1">1 mois</option>
                <option value="2">2 mois</option>
                <option value="3">3 mois</option>
                <option value="4">4 mois</option>
                <option value="6">6 mois</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="startDate" class="form-label">Date de début *</label>
              <input
                type="date"
                id="startDate"
                v-model="form.startDate"
                class="form-input"
                required
              />
            </div>

            <div class="form-group" v-if="form.contractType === 'CDD'">
              <label for="endDate" class="form-label">Date de fin</label>
              <input
                type="date"
                id="endDate"
                v-model="form.endDate"
                class="form-input"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="workingTime" class="form-label">Temps de travail *</label>
              <select
                id="workingTime"
                v-model="form.workingTime"
                class="form-input"
                required
              >
                <option value="">Sélectionner...</option>
                <option value="full-time">Temps plein (35h/semaine)</option>
                <option value="part-time-80">Temps partiel 80% (28h/semaine)</option>
                <option value="part-time-60">Temps partiel 60% (21h/semaine)</option>
                <option value="part-time-50">Temps partiel 50% (17.5h/semaine)</option>
              </select>
            </div>

            <div class="form-group">
              <label for="workSchedule" class="form-label">Horaires de travail</label>
              <input
                type="text"
                id="workSchedule"
                v-model="form.workSchedule"
                class="form-input"
                placeholder="9h00 - 17h30"
              />
            </div>
          </div>
        </div>

        <!-- Rémunération -->
        <div class="form-section">
          <h2 class="section-title">Rémunération</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="baseSalary" class="form-label">Salaire de base (€/an) *</label>
              <input
                type="number"
                id="baseSalary"
                v-model="form.baseSalary"
                class="form-input"
                placeholder="45000"
                min="0"
                required
              />
            </div>

            <div class="form-group">
              <label for="bonus" class="form-label">Prime variable (€/an)</label>
              <input
                type="number"
                id="bonus"
                v-model="form.bonus"
                class="form-input"
                placeholder="5000"
                min="0"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="benefits" class="form-label">Avantages</label>
              <div class="checkbox-group">
                <label class="checkbox-label">
                  <input type="checkbox" v-model="form.benefits.healthInsurance" />
                  <span class="checkmark"></span>
                  Mutuelle santé
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" v-model="form.benefits.mealVouchers" />
                  <span class="checkmark"></span>
                  Tickets restaurant
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" v-model="form.benefits.transportAllowance" />
                  <span class="checkmark"></span>
                  Indemnité transport
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" v-model="form.benefits.companyPhone" />
                  <span class="checkmark"></span>
                  Téléphone de fonction
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" v-model="form.benefits.companyLaptop" />
                  <span class="checkmark"></span>
                  Ordinateur portable
                </label>
                <label class="checkbox-label">
                  <input type="checkbox" v-model="form.benefits.parkingSpace" />
                  <span class="checkmark"></span>
                  Place de parking
                </label>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="additionalBenefits" class="form-label">Autres avantages</label>
            <textarea
              id="additionalBenefits"
              v-model="form.additionalBenefits"
              class="form-input"
              rows="3"
              placeholder="Décrivez les autres avantages (formation, congés supplémentaires, etc.)"
            ></textarea>
          </div>
        </div>

        <!-- Clauses particulières -->
        <div class="form-section">
          <h2 class="section-title">Clauses Particulières</h2>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.clauses.confidentiality" />
              <span class="checkmark"></span>
              Clause de confidentialité
            </label>
          </div>

          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.clauses.nonCompete" />
              <span class="checkmark"></span>
              Clause de non-concurrence
            </label>
          </div>

          <div class="form-group" v-if="form.clauses.nonCompete">
            <label for="nonCompeteDuration" class="form-label">Durée de non-concurrence (mois)</label>
            <select
              id="nonCompeteDuration"
              v-model="form.nonCompeteDuration"
              class="form-input"
            >
              <option value="">Sélectionner...</option>
              <option value="6">6 mois</option>
              <option value="12">12 mois</option>
              <option value="18">18 mois</option>
              <option value="24">24 mois</option>
            </select>
          </div>

          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.clauses.intellectualProperty" />
              <span class="checkmark"></span>
              Clause de propriété intellectuelle
            </label>
          </div>

          <div class="form-group">
            <label for="additionalClauses" class="form-label">Clauses supplémentaires</label>
            <textarea
              id="additionalClauses"
              v-model="form.additionalClauses"
              class="form-input"
              rows="4"
              placeholder="Ajoutez toute clause particulière ou condition spécifique..."
            ></textarea>
          </div>
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <button type="button" @click="saveDraft" class="btn btn-secondary" :disabled="isLoading">
            💾 Sauvegarder en brouillon
          </button>
          <button type="button" @click="previewContract" class="btn btn-primary" :disabled="isLoading">
            👁️ Aperçu du contrat
          </button>
          <button type="submit" class="btn btn-success" :disabled="isLoading">
            <span v-if="isLoading">Génération en cours...</span>
            <span v-else">✅ Générer le contrat</span>
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

    <!-- Modal d'aperçu -->
    <div v-if="showPreviewModal" class="modal-overlay" @click="showPreviewModal = false">
      <div class="modal preview-modal" @click.stop>
        <div class="modal-header">
          <h3>Aperçu du Contrat d'Essai</h3>
          <button @click="showPreviewModal = false" class="modal-close">×</button>
        </div>
        
        <div class="modal-content">
          <div class="contract-preview">
            <div class="contract-header">
              <h2>CONTRAT DE TRAVAIL À DURÉE INDÉTERMINÉE</h2>
              <h3>AVEC PÉRIODE D'ESSAI</h3>
            </div>

            <div class="contract-section">
              <h4>ENTRE LES SOUSSIGNÉS :</h4>
              <p><strong>L'EMPLOYEUR :</strong></p>
              <p>Société TechCorp SAS<br>
              Siège social : 123 Avenue des Champs-Élysées, 75008 Paris<br>
              SIRET : 123 456 789 00012<br>
              Représentée par M. Jean MARTIN, Directeur Général</p>

              <p><strong>LE SALARIÉ :</strong></p>
              <p>{{ form.candidateName || '[Nom du candidat]' }}<br>
              {{ form.candidateAddress || '[Adresse]' }}<br>
              Né(e) le {{ formatDate(form.birthDate) || '[Date de naissance]' }}<br>
              N° Sécurité Sociale : {{ form.socialSecurityNumber || '[Numéro SS]' }}</p>
            </div>

            <div class="contract-section">
              <h4>ARTICLE 1 - OBJET DU CONTRAT</h4>
              <p>Le présent contrat a pour objet l'engagement de {{ form.candidateName || '[Nom]' }} 
              en qualité de <strong>{{ form.position || '[Poste]' }}</strong> 
              au sein du département {{ form.department || '[Département]' }}.</p>
            </div>

            <div class="contract-section">
              <h4>ARTICLE 2 - DURÉE ET PÉRIODE D'ESSAI</h4>
              <p>Le présent contrat est conclu pour une durée indéterminée à compter du 
              {{ formatDate(form.startDate) || '[Date de début]' }}.</p>
              <p>Il est assorti d'une période d'essai de <strong>{{ form.trialPeriod || '[X]' }} mois</strong>, 
              renouvelable une fois dans la limite légale.</p>
            </div>

            <div class="contract-section">
              <h4>ARTICLE 3 - FONCTIONS</h4>
              <p>{{ form.jobDescription || 'Le salarié exercera les fonctions définies dans la fiche de poste annexée au présent contrat.' }}</p>
              <p>Le salarié sera placé sous l'autorité hiérarchique de {{ form.manager || '[Manager]' }}.</p>
            </div>

            <div class="contract-section">
              <h4>ARTICLE 4 - LIEU DE TRAVAIL</h4>
              <p>Le salarié exercera ses fonctions à {{ form.workLocation || '[Lieu]' }}.</p>
            </div>

            <div class="contract-section">
              <h4>ARTICLE 5 - HORAIRES DE TRAVAIL</h4>
              <p>Le salarié travaillera selon un régime de {{ getWorkingTimeLabel(form.workingTime) }}.</p>
              <p v-if="form.workSchedule">Horaires : {{ form.workSchedule }}</p>
            </div>

            <div class="contract-section">
              <h4>ARTICLE 6 - RÉMUNÉRATION</h4>
              <p>La rémunération brute annuelle est fixée à <strong>{{ formatCurrency(form.baseSalary) }}</strong>, 
              payable en 12 mensualités.</p>
              <p v-if="form.bonus">Une prime variable de {{ formatCurrency(form.bonus) }} pourra être versée 
              selon les conditions définies par l'entreprise.</p>
              
              <div v-if="hasSelectedBenefits" class="benefits-list">
                <p><strong>Avantages :</strong></p>
                <ul>
                  <li v-if="form.benefits.healthInsurance">Mutuelle santé prise en charge à 100%</li>
                  <li v-if="form.benefits.mealVouchers">Tickets restaurant d'une valeur de 9€</li>
                  <li v-if="form.benefits.transportAllowance">Prise en charge des frais de transport à 50%</li>
                  <li v-if="form.benefits.companyPhone">Téléphone de fonction</li>
                  <li v-if="form.benefits.companyLaptop">Ordinateur portable professionnel</li>
                  <li v-if="form.benefits.parkingSpace">Place de parking</li>
                </ul>
              </div>
            </div>

            <div class="contract-section" v-if="hasSelectedClauses">
              <h4>ARTICLE 7 - CLAUSES PARTICULIÈRES</h4>
              <p v-if="form.clauses.confidentiality">Le salarié s'engage à respecter la confidentialité des informations de l'entreprise.</p>
              <p v-if="form.clauses.nonCompete">Une clause de non-concurrence de {{ form.nonCompeteDuration }} mois s'applique après la fin du contrat.</p>
              <p v-if="form.clauses.intellectualProperty">Toute création intellectuelle réalisée dans le cadre des fonctions appartient à l'entreprise.</p>
            </div>

            <div class="contract-footer">
              <p>Fait en deux exemplaires à Paris, le {{ formatDate(new Date()) }}</p>
              <div class="signatures">
                <div class="signature-block">
                  <p>L'EMPLOYEUR</p>
                  <p>Jean MARTIN<br>Directeur Général</p>
                </div>
                <div class="signature-block">
                  <p>LE SALARIÉ</p>
                  <p>{{ form.candidateName || '[Nom du candidat]' }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="showPreviewModal = false" class="btn btn-secondary">
            Fermer
          </button>
          <button @click="generatePDF" class="btn btn-primary">
            📄 Télécharger PDF
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CreateContractView',
  data() {
    return {
      form: {
        candidateName: '',
        candidateEmail: '',
        candidatePhone: '',
        candidateAddress: '',
        birthDate: '',
        socialSecurityNumber: '',
        position: '',
        department: '',
        manager: '',
        workLocation: '',
        jobDescription: '',
        contractType: 'CDI',
        trialPeriod: '',
        startDate: '',
        endDate: '',
        workingTime: '',
        workSchedule: '',
        baseSalary: '',
        bonus: '',
        benefits: {
          healthInsurance: false,
          mealVouchers: false,
          transportAllowance: false,
          companyPhone: false,
          companyLaptop: false,
          parkingSpace: false
        },
        additionalBenefits: '',
        clauses: {
          confidentiality: false,
          nonCompete: false,
          intellectualProperty: false
        },
        nonCompeteDuration: '',
        additionalClauses: ''
      },
      managers: [
        { id: 1, name: 'Marie Dupont', position: 'Directrice IT' },
        { id: 2, name: 'Jean Martin', position: 'Directeur Général' },
        { id: 3, name: 'Sophie Bernard', position: 'Responsable Marketing' },
        { id: 4, name: 'Pierre Durand', position: 'Directeur Commercial' }
      ],
      isLoading: false,
      errorMessage: '',
      successMessage: '',
      showPreviewModal: false
    }
  },
  computed: {
    hasSelectedBenefits() {
      return Object.values(this.form.benefits).some(benefit => benefit)
    },
    hasSelectedClauses() {
      return Object.values(this.form.clauses).some(clause => clause)
    }
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR')
    },
    formatCurrency(amount) {
      if (!amount) return '0 €'
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 0
      }).format(amount)
    },
    getWorkingTimeLabel(workingTime) {
      const labels = {
        'full-time': 'temps plein (35h/semaine)',
        'part-time-80': 'temps partiel 80% (28h/semaine)',
        'part-time-60': 'temps partiel 60% (21h/semaine)',
        'part-time-50': 'temps partiel 50% (17.5h/semaine)'
      }
      return labels[workingTime] || workingTime
    },
    validateForm() {
      if (!this.form.candidateName || !this.form.candidateEmail || !this.form.position || 
          !this.form.department || !this.form.manager || !this.form.workLocation ||
          !this.form.contractType || !this.form.trialPeriod || !this.form.startDate ||
          !this.form.workingTime || !this.form.baseSalary) {
        return 'Veuillez remplir tous les champs obligatoires (*)'
      }

      if (this.form.contractType === 'CDD' && !this.form.endDate) {
        return 'La date de fin est obligatoire pour un CDD'
      }

      if (this.form.clauses.nonCompete && !this.form.nonCompeteDuration) {
        return 'Veuillez spécifier la durée de non-concurrence'
      }

      return null
    },
    async saveDraft() {
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        // Simulation de sauvegarde
        await new Promise(resolve => setTimeout(resolve, 1000))

        this.successMessage = 'Brouillon sauvegardé avec succès !'
        
        setTimeout(() => {
          this.successMessage = ''
        }, 3000)

      } catch (error) {
        this.errorMessage = 'Erreur lors de la sauvegarde. Veuillez réessayer.'
      } finally {
        this.isLoading = false
      }
    },
    previewContract() {
      const validationError = this.validateForm()
      if (validationError) {
        this.errorMessage = validationError
        return
      }
      
      this.showPreviewModal = true
    },
    async generatePDF() {
      // Simulation de génération PDF
      alert('Génération du PDF en cours...')
      this.showPreviewModal = false
    },
    async handleSubmit() {
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        // Validation
        const validationError = this.validateForm()
        if (validationError) {
          this.errorMessage = validationError
          return
        }

        // Simulation de génération
        await new Promise(resolve => setTimeout(resolve, 2000))

        this.successMessage = 'Contrat généré avec succès ! Redirection en cours...'
        
        // Redirection après 2 secondes
        setTimeout(() => {
          this.$router.push('/contracts')
        }, 2000)

      } catch (error) {
        this.errorMessage = 'Erreur lors de la génération du contrat. Veuillez réessayer.'
      } finally {
        this.isLoading = false
      }
    }
  },
  mounted() {
    // Pré-remplir avec les paramètres de l'URL si disponibles
    const urlParams = new URLSearchParams(window.location.search)
    if (urlParams.get('candidate')) {
      this.form.candidateName = urlParams.get('candidate')
    }
    if (urlParams.get('position')) {
      this.form.position = urlParams.get('position')
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

.checkbox-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 10px;
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

.preview-modal {
  max-width: 900px;
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

.contract-preview {
  font-family: 'Times New Roman', serif;
  line-height: 1.6;
  color: #333;
}

.contract-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #333;
}

.contract-header h2 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: bold;
}

.contract-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: normal;
}

.contract-section {
  margin-bottom: 25px;
}

.contract-section h4 {
  margin: 0 0 15px 0;
  font-size: 14px;
  font-weight: bold;
  text-decoration: underline;
}

.contract-section p {
  margin: 0 0 10px 0;
  font-size: 13px;
  text-align: justify;
}

.benefits-list ul {
  margin: 10px 0;
  padding-left: 20px;
}

.benefits-list li {
  margin-bottom: 5px;
  font-size: 13px;
}

.contract-footer {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #333;
}

.signatures {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
}

.signature-block {
  text-align: center;
  width: 200px;
}

.signature-block p {
  margin: 0 0 5px 0;
  font-size: 13px;
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
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .checkbox-group {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .preview-modal {
    width: 95%;
    max-height: 90vh;
  }
  
  .signatures {
    flex-direction: column;
    gap: 30px;
  }
  
  .contract-preview {
    font-size: 12px;
  }
  
  .contract-header h2 {
    font-size: 16px;
  }
  
  .contract-header h3 {
    font-size: 14px;
  }
  
  .contract-section h4 {
    font-size: 13px;
  }
  
  .contract-section p {
    font-size: 12px;
  }
}
</style>
