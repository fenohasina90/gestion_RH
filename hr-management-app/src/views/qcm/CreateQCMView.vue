<template>
  <div class="create-qcm-view">
    <div class="page-header">
      <h1 class="page-title">Créer un Test QCM</h1>
      <router-link to="/qcm/results" class="btn btn-secondary">
        ← Voir les résultats
      </router-link>
    </div>

    <div class="form-container">
      <form @submit.prevent="handleSubmit" class="qcm-form">
        <!-- Informations générales du test (seulement champs DB: nom, idprofil) -->
        <div class="form-section">
          <h2 class="section-title">Informations du Test</h2>
          
          <div class="form-row">
            <div class="form-group">
              <label for="title" class="form-label">Nom du test *</label>
              <input
                type="text"
                id="title"
                v-model="form.nom"
                class="form-input"
                placeholder="Ex: Test de compétences JavaScript"
                required
              />
            </div>

            <div class="form-group">
              <label for="profil" class="form-label">Profil concerné *</label>
              <select id="profil" v-model="form.profilId" class="form-input" required>
                <option value="">Sélectionner un profil...</option>
                <option v-for="p in profils" :key="p.id" :value="p.id">{{ p.nom }}</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Questions -->
        <div class="form-section">
          <div class="questions-header">
            <h2 class="section-title">Questions ({{ form.questions.length }})</h2>
            <button type="button" @click="addQuestion" class="btn btn-primary">
              ➕ Ajouter une question
            </button>
          </div>

          <div v-if="form.questions.length === 0" class="no-questions">
            <div class="no-questions-icon">❓</div>
            <p>Aucune question ajoutée. Cliquez sur "Ajouter une question" pour commencer.</p>
          </div>

          <div v-else class="questions-list">
            <div 
              v-for="(question, index) in form.questions" 
              :key="index"
              class="question-card"
            >
              <div class="question-header">
                <h3>Question {{ index + 1 }}</h3>
                <button 
                  type="button" 
                  @click="removeQuestion(index)"
                  class="btn btn-danger btn-sm"
                >
                  🗑️ Supprimer
                </button>
              </div>

              <div class="question-form">
                <div class="form-group">
                  <label :for="`question-${index}`" class="form-label">Question *</label>
                  <textarea
                    :id="`question-${index}`"
                    v-model="question.text"
                    class="form-input"
                    rows="3"
                    placeholder="Quelle est la différence entre let et var en JavaScript ?"
                    required
                  ></textarea>
                </div>

                <div class="form-group">
                  <label class="form-label">Choix (au moins 2)</label>
                  <div class="answers-list">
                    <div 
                      v-for="(answer, answerIndex) in question.answers" 
                      :key="answerIndex"
                      class="answer-item"
                    >
                      <div class="answer-input-group">
                        <input
                          type="text"
                          v-model="answer.text"
                          class="form-input answer-input"
                          placeholder="Texte du choix"
                          required
                        />
                        <label class="answer-correct">
                          <input type="checkbox" v-model="answer.isCorrect" />
                          <span>Correcte</span>
                        </label>
                        <button 
                          type="button" 
                          @click="removeAnswer(index, answerIndex)"
                          class="btn btn-danger btn-sm"
                          :disabled="question.answers.length <= 2"
                        >
                          ×
                        </button>
                      </div>
                    </div>
                  </div>
                  <button 
                    type="button" 
                    @click="addAnswer(index)"
                    class="btn btn-secondary btn-sm"
                    :disabled="question.answers.length >= 6"
                  >
                    ➕ Ajouter un choix
                  </button>
                </div>

                <div class="form-group">
                  <label :for="`points-${index}`" class="form-label">Points (entier) *</label>
                  <input
                    type="number"
                    :id="`points-${index}`"
                    v-model="question.points"
                    class="form-input"
                    placeholder="1"
                    min="1"
                    max="10"
                    required
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="isLoading || form.questions.length === 0">
            <span v-if="isLoading">Création en cours...</span>
            <span v-else">✅ Créer le test</span>
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
  name: 'CreateQCMView',
  data() {
    return {
      form: {
        nom: '',
        profilId: '',
        questions: []
      },
      isLoading: false,
      errorMessage: '',
      successMessage: '',
      profils: []
    }
  },
  methods: {
    addQuestion() {
      this.form.questions.push({
        text: '',
        answers: [
          { text: '', isCorrect: false },
          { text: '', isCorrect: false }
        ],
        points: 1
      })
    },
    removeQuestion(index) {
      this.form.questions.splice(index, 1)
    },
    addAnswer(questionIndex) {
      if (this.form.questions[questionIndex].answers.length < 6) {
        this.form.questions[questionIndex].answers.push({
          text: '',
          isCorrect: false
        })
      }
    },
    removeAnswer(questionIndex, answerIndex) {
      if (this.form.questions[questionIndex].answers.length > 2) {
        this.form.questions[questionIndex].answers.splice(answerIndex, 1)
      }
    },
    validateForm() {
      // Validation minimale adaptée aux champs DB
      if (!this.form.nom || !this.form.profilId) {
        return 'Veuillez renseigner le nom du test et le profil'
      }

      if (this.form.questions.length === 0) {
        return 'Veuillez ajouter au moins une question'
      }

      // Validation des questions
      for (let i = 0; i < this.form.questions.length; i++) {
        const question = this.form.questions[i]
        
        if (!question.text.trim()) {
          return `La question ${i + 1} ne peut pas être vide`
        }

        if (!question.points || question.points < 1) {
          return `La question ${i + 1} doit avoir au moins 1 point`
        }

        // Vérifier que toutes les réponses sont remplies
        for (let j = 0; j < question.answers.length; j++) {
          if (!question.answers[j].text.trim()) {
            return `Toutes les réponses de la question ${i + 1} doivent être remplies`
          }
        }

        // Au moins une bonne réponse
        const hasCorrectAnswer = question.answers.some(answer => answer.isCorrect)
        if (!hasCorrectAnswer) {
          return `Veuillez sélectionner au moins une bonne réponse pour la question ${i + 1}`
        }
      }

      return null
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

        // Construire le payload conforme aux champs DB
        const payload = {
          nom: this.form.nom,
          profilId: parseInt(this.form.profilId),
          questions: this.form.questions.map((q, idx) => ({
            numero: idx + 1,
            question: q.text,
            points: parseInt(q.points),
            choix: q.answers.map(a => ({ texte: a.text, estcorrect: !!a.isCorrect }))
          }))
        }

        const res = await fetch('/api/qcm/admin/create', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        })
        if (!res.ok) {
          const msg = await res.text()
          throw new Error(msg)
        }
        const data = await res.json()
        this.successMessage = 'Test QCM créé avec succès !'
        this.$router.push('/resultat-qcm')

      } catch (error) {
        this.errorMessage = 'Erreur lors de la création du test. Veuillez réessayer.'
      } finally {
        this.isLoading = false
      }
    }
  },
  mounted() {
    // Charger les profils pour la liste
    fetch('/api/profils')
      .then(r => r.json())
      .then(data => { this.profils = data || [] })
      .catch(() => { this.profils = [] })
    // Ajouter une question par défaut
    this.addQuestion()
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
  background: #059669;
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

.questions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.no-questions {
  text-align: center;
  padding: 40px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 2px dashed #ddd;
}

.no-questions-icon {
  font-size: 48px;
  margin-bottom: 15px;
  opacity: 0.5;
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.question-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e9ecef;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.question-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
}

.radio-group {
  display: flex;
  gap: 20px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #555;
}

.radio-label input[type="radio"] {
  margin: 0;
}

.answers-list {
  margin-bottom: 15px;
}

.answer-item {
  margin-bottom: 10px;
}

.answer-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.answer-input {
  flex: 1;
}

.answer-correct {
  display: flex;
  align-items: center;
  gap: 5px;
  white-space: nowrap;
  color: #555;
  cursor: pointer;
}

.answer-correct input {
  margin: 0;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
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
  background: #d1f7c4;
  color: #047857;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
  text-align: center;
  border: 1px solid #a7f3d0;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .questions-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .radio-group {
    flex-direction: column;
    gap: 10px;
  }
  
  .answer-input-group {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
