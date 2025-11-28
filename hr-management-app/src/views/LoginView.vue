<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>Connexion</h1>
        <p>Connectez-vous à votre compte administrateur</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="email" class="form-label">Email</label>
          <input
            type="email"
            id="email"
            v-model="form.email"
            class="form-input"
            required
          />
        </div>

        <div class="form-group">
          <label for="password" class="form-label">Mot de passe</label>
          <input
            type="password"
            id="password"
            v-model="form.password"
            class="form-input"
            required
          />
        </div>

        <div class="form-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="form.rememberMe" />
            <span class="checkmark"></span>
            Se souvenir de moi
          </label>
        </div>

        <button type="submit" class="btn btn-primary btn-full" :disabled="isLoading">
          <span v-if="isLoading">Connexion en cours...</span>
          <span v-else>Se connecter</span>
        </button>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
      </form>

      <div class="login-footer">
        <p>Pas encore de compte ? 
          <router-link to="/register" class="link">S'inscrire</router-link>
        </p>
        <a href="#" class="link">Mot de passe oublié ?</a>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth'

export default {
  name: 'LoginView',
  data() {
    return {
      form: {
        email: 'soa.rabe@entreprise.mg',
        password: 'pass123',
        rememberMe: false
      },
      isLoading: false,
      errorMessage: '',
      successMessage: ''
    }
  },
  methods: {
    async handleLogin() {
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email: this.form.email,
            motdepasse: this.form.password
          })
        })

        const data = await response.json()

        if (data.success) {
          this.successMessage = data.message
          
          // Stocker les informations d'authentification
          const authStore = useAuthStore()
          authStore.setUser(data.user || { id: 1, email: this.form.email })
          authStore.setToken('authenticated') // Token simple pour cette démo
          
          // Redirection vers la page d'accueil
          setTimeout(() => {
            this.$router.push('/dashboard')
          }, 1000)
        } else {
          this.errorMessage = data.message
        }
      } catch (error) {
        this.errorMessage = 'Erreur de connexion. Vérifiez que le backend est démarré.'
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
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

.login-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
  border: 1px solid #e0e0e0;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.login-header p {
  color: #666;
}

.login-form {
  margin-bottom: 30px;
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
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #555;
}

.checkbox-label input[type="checkbox"] {
  margin: 0;
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

.login-footer {
  text-align: center;
  color: #666;
}

.login-footer p {
  margin-bottom: 10px;
}

.link {
  color: #3498db;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-container {
    padding: 10px;
  }
  
  .login-card {
    padding: 30px 20px;
  }
}
</style>
