<template>
  <div class="mgr-login-container">
    <div class="mgr-login-card">
      <h1 class="mgr-login-title">Connexion Manager</h1>
      <p class="mgr-login-subtitle">Accédez à la gestion des demandes de congé de votre équipe</p>

      <form @submit.prevent="login" class="mgr-login-form">
        <div class="mgr-form-group">
          <label class="mgr-label">Matricule</label>
          <input v-model="matricule" type="text" class="mgr-input" required />
        </div>
        <div class="mgr-form-group">
          <label class="mgr-label">Mot de passe</label>
          <input v-model="motdepasse" type="password" class="mgr-input" required />
        </div>

        <div v-if="message" class="mgr-message" :class="success ? 'mgr-message-success' : 'mgr-message-error'">
          {{ message }}
        </div>

        <button type="submit" class="mgr-button" :disabled="loading">
          <span v-if="!loading">Se connecter</span>
          <span v-else>Connexion...</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ManagerLoginView',
  data() {
    return {
      matricule: '',
      motdepasse: '',
      loading: false,
      message: '',
      success: false
    }
  },
  created() {
    const stored = localStorage.getItem('managerUser')
    if (stored) {
      this.$router.push('/espace-manager/demandes-conge')
    }
  },
  methods: {
    async login() {
      this.loading = true
      this.message = ''
      this.success = false
      try {
        const res = await fetch('http://localhost:8080/api/manager/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            matricule: this.matricule,
            motdepasse: this.motdepasse
          })
        })
        const data = await res.json()
        this.success = !!data.success
        this.message = data.message || ''
        if (data.success && data.manager) {
          localStorage.setItem('managerUser', JSON.stringify(data.manager))
          this.$router.push('/espace-manager/demandes-conge')
        }
      } catch (e) {
        console.error(e)
        this.message = 'Erreur lors de la connexion'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.mgr-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}
.mgr-login-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 16px;
  padding: 32px 28px;
  box-shadow: 0 10px 30px rgba(15, 118, 110, 0.15);
  border: 1px solid #e5e7eb;
}
.mgr-login-title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 8px;
}
.mgr-login-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 20px;
}
.mgr-login-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.mgr-form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.mgr-label {
  font-size: 13px;
  color: #4b5563;
}
.mgr-input {
  border-radius: 8px;
  border: 1px solid #d1d5db;
  padding: 8px 10px;
  font-size: 14px;
}
.mgr-input:focus {
  outline: none;
  border-color: #0f766e;
  box-shadow: 0 0 0 3px rgba(15, 118, 110, 0.2);
}
.mgr-button {
  margin-top: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  background: linear-gradient(135deg, #0f766e, #14b8a6);
  color: white;
  font-size: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
}
.mgr-button:disabled {
  opacity: 0.7;
  cursor: default;
}
.mgr-message {
  margin-top: 4px;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 13px;
}
.mgr-message-success {
  background: #ecfdf5;
  color: #166534;
  border: 1px solid #bbf7d0;
}
.mgr-message-error {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
}
</style>
