<template>
  <div class="critere-add-container">
    <div class="form-card">
      <h2 class="form-title">Ajouter un Critère</h2>
      
      <form @submit.prevent="submitForm" class="critere-form">
        <div class="form-group">
          <label for="nom" class="form-label">Nom du critère</label>
          <input
            type="text"
            id="nom"
            v-model="critere.nom"
            class="form-input"
            placeholder="Entrez le nom du critère"
            required
          />
        </div>

        <div class="form-group">
          <label for="typechamp" class="form-label">Type de champ</label>
          <select id="typechamp" v-model.number="critere.typechampId" class="form-input" required>
            <option value="" disabled>-- Sélectionner un type --</option>
            <option v-for="t in typechamps" :key="t.id" :value="t.id">
              {{ t.libelle }}
            </option>
          </select>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Ajout en cours...' : 'Ajouter le critère' }}
          </button>
          <button type="button" @click="goBack" class="btn btn-secondary">
            Annuler
          </button>
        </div>
      </form>

      <div v-if="message" class="message" :class="messageType">
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CritereAdd',
  data() {
    return {
      critere: {
        nom: '',
        typechampId: ''
      },
      typechamps: [],
      loading: false,
      message: '',
      messageType: ''
    }
  },
  async mounted() {
    await this.loadTypechamps()
  },
  methods: {
    async loadTypechamps() {
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const res = await fetch(`${baseUrl}/api/typechamps`)
        if (!res.ok) throw new Error('Erreur API typechamps')
        this.typechamps = await res.json()
      } catch (e) {
        console.error('Chargement typechamps:', e)
        this.typechamps = []
        this.showMessage('Impossible de charger les types de champ', 'error')
      }
    },
    async submitForm() {
      if (!this.critere.nom.trim()) {
        this.showMessage('Veuillez saisir un nom pour le critère', 'error');
        return;
      }
      if (!this.critere.typechampId) {
        this.showMessage('Veuillez sélectionner un type de champ', 'error');
        return;
      }

      this.loading = true;
      this.message = '';

      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/criteres`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            nom: this.critere.nom,
            idtypechamp: { id: this.critere.typechampId }
          })
        });

        if (response.ok) {
          this.showMessage('Critère ajouté avec succès !', 'success');
          setTimeout(() => {
            this.$router.push('/criteres');
          }, 1500);
        } else {
          throw new Error('Erreur lors de l\'ajout du critère');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors de l\'ajout du critère', 'error');
      } finally {
        this.loading = false;
      }
    },

    showMessage(text, type) {
      this.message = text;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 5000);
    },

    goBack() {
      this.$router.push('/criteres');
    }
  }
}
</script>

<style scoped>
.critere-add-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.form-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 500px;
}

.form-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
}

.critere-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
  font-size: 14px;
}

.form-input {
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  flex: 1;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f8f9fa;
  color: #6c757d;
  border: 2px solid #e9ecef;
}

.btn-secondary:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.message {
  margin-top: 20px;
  padding: 12px 16px;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

@media (max-width: 768px) {
  .form-card {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .form-title {
    font-size: 24px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
