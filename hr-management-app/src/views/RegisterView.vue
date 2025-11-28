<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>Inscription</h1>
        <p>Créez votre compte administrateur</p>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="email" class="form-label">Email</label>
          <input
            type="email"
            id="email"
            v-model="form.email"
            class="form-input"
            placeholder="admin@example.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="employe" class="form-label">Sélectionner un employé</label>
          <select
            id="employe"
            v-model="form.employeId"
            class="form-input"
            required
          >
            <option value="">-- Choisir un employé --</option>
            <option 
              v-for="employe in employes" 
              :key="employe.id" 
              :value="employe.id"
            >
              {{ employe.prenom }} {{ employe.nom }}
            </option>
          </select>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="password" class="form-label">Mot de passe</label>
            <input
              type="password"
              id="password"
              v-model="form.password"
              class="form-input"
              placeholder="••••••••"
              required
            />
          </div>

          <div class="form-group">
            <label for="confirmPassword" class="form-label">Confirmer le mot de passe</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="form.confirmPassword"
              class="form-input"
              placeholder="••••••••"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="form.acceptTerms" required />
            <span class="checkmark"></span>
            J'accepte les <a href="#" class="link">conditions d'utilisation</a> et la <a href="#" class="link">politique de confidentialité</a>
          </label>
        </div>

        <button type="submit" class="btn btn-primary btn-full" :disabled="isLoading || !isFormValid">
          <span v-if="isLoading">Inscription en cours...</span>
          <span v-else>S'inscrire</span>
        </button>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
      </form>

      <div class="register-footer">
        <p>Déjà un compte ? 
          <router-link to="/login" class="link">Se connecter</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RegisterView',
  data() {
    return {
      form: {
        email: '',
        employeId: '',
        password: '',
        confirmPassword: '',
        acceptTerms: false
      },
      employes: [],
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    }
  },
  computed: {
    isFormValid() {
      return this.form.email && 
             this.form.employeId && 
             this.form.password && 
             this.form.confirmPassword && 
             this.form.password === this.form.confirmPassword &&
             this.form.acceptTerms
    }
  },
  methods: {
    async handleRegister() {
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        // Vérification des mots de passe
        if (this.form.password !== this.form.confirmPassword) {
          this.errorMessage = 'Les mots de passe ne correspondent pas'
          return
        }

        if (this.form.password.length < 6) {
          this.errorMessage = 'Le mot de passe doit contenir au moins 6 caractères'
          return
        }

        const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/register`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email: this.form.email,
            motdepasse: this.form.password,
            employeId: parseInt(this.form.employeId)
          })
        })

        const data = await response.json()

        if (data.success) {
          this.successMessage = data.message + ' Redirection vers la connexion...'
          
          // Redirection vers la page de connexion
          setTimeout(() => {
            this.$router.push('/login')
          }, 2000)
        } else {
          this.errorMessage = data.message
        }
        
      } catch (error) {
        this.errorMessage = 'Erreur de connexion. Vérifiez que le backend est démarré.'
      } finally {
        this.isLoading = false
      }
    },

    async loadEmployes() {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/employes`)
        if (response.ok) {
          this.employes = await response.json()
        } else {
          console.error('Erreur lors du chargement des employés')
        }
      } catch (error) {
        console.error('Erreur de connexion:', error)
      }
    }
  },

  async mounted() {
    await this.loadEmployes()
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background: white;
  padding: 20px;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
}

.register-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 500px;
  border: 1px solid #e0e0e0;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.register-header p {
  color: #666;
}

.register-form {
  margin-bottom: 30px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

.form-group {
  margin-bottom: 20px;
}

.btn-full {
  width: 100%;
  padding: 12px;
  font-size: 16px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  color: #555;
  line-height: 1.4;
}

.checkbox-label input[type="checkbox"] {
  margin: 0;
  margin-top: 2px;
}

.error-message {
  background: #fee;
  color: #c33;
  padding: 10px;
  border-radius: 4px;
  margin-top: 15px;
  text-align: center;
  border: 1px solid #fcc;
}

.success-message {
  background: #efe;
  color: #363;
  padding: 10px;
  border-radius: 4px;
  margin-top: 15px;
  text-align: center;
  border: 1px solid #cfc;
}

.register-footer {
  text-align: center;
  color: #666;
}

.link {
  color: #3498db;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-container {
    padding: 10px;
  }
  
  .register-card {
    padding: 30px 20px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
