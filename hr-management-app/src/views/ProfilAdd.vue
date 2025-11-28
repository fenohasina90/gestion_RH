<template>
  <div class="profil-add-container">
    <div class="form-card">
      <h2 class="form-title">Ajouter un Profil</h2>
      
      <form @submit.prevent="submitForm" class="profil-form">
        <div class="form-group">
          <label for="nom" class="form-label">Nom du profil</label>
          <input
            type="text"
            id="nom"
            v-model="profil.nom"
            class="form-input"
            placeholder="Entrez le nom du profil"
            required
          />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Ajout en cours...' : 'Ajouter le profil' }}
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
  name: 'ProfilAdd',
  data() {
    return {
      profil: {
        nom: ''
      },
      loading: false,
      message: '',
      messageType: ''
    }
  },
  methods: {
    async submitForm() {
      if (!this.profil.nom.trim()) {
        this.showMessage('Veuillez saisir un nom pour le profil', 'error');
        return;
      }

      this.loading = true;
      this.message = '';

      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/profils`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.profil)
        });

        if (response.ok) {
          this.showMessage('Profil ajouté avec succès !', 'success');
          setTimeout(() => {
            this.$router.push('/profils');
          }, 1500);
        } else {
          throw new Error('Erreur lors de l\'ajout du profil');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors de l\'ajout du profil', 'error');
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
      this.$router.push('/profils');
    }
  }
}
</script>

<style scoped>
.profil-add-container {
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

.profil-form {
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
