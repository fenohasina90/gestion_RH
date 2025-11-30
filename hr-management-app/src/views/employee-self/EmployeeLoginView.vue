<template>
  <div class="emp-login-page">
    <div class="emp-login-illustration">
      <div class="emp-login-overlay">
        <div class="emp-logo">
          <div class="emp-logo-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
          <span class="emp-logo-text">RH Pro</span>
        </div>
        <h1 class="emp-login-title">Espace Employé</h1>
        <p class="emp-login-subtitle">Consultez vos informations, vos congés et vos bulletins de paie en toute simplicité.</p>
        <ul class="emp-login-features">
          <li class="emp-feature-item">
            <span class="emp-feature-icon">✓</span>
            <span>Profil et coordonnées à jour</span>
          </li>
          <li class="emp-feature-item">
            <span class="emp-feature-icon">✓</span>
            <span>Consultation des bulletins de paie</span>
          </li>
          <li class="emp-feature-item">
            <span class="emp-feature-icon">✓</span>
            <span>Suivi de vos congés et demandes</span>
          </li>
        </ul>
        <div class="emp-login-footer">
          <p>Une plateforme sécurisée pour gérer votre vie professionnelle</p>
        </div>
      </div>
    </div>
    <div class="emp-login-panel">
      <div class="emp-login-card">
        <div class="emp-login-header">
          <h2 class="emp-login-card-title">Connexion Employé</h2>
          <p class="emp-login-card-subtitle">Accédez à votre espace personnel</p>
        </div>
        <form @submit.prevent="login" class="emp-login-form">
          <div class="emp-form-group">
            <label class="emp-label">Matricule</label>
            <div class="emp-input-wrapper">
              <input v-model="matricule" type="text" class="emp-input" placeholder="Votre numéro de matricule" required />
              <div class="emp-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                  <circle cx="8.5" cy="7" r="4"></circle>
                  <line x1="20" y1="8" x2="20" y2="14"></line>
                  <line x1="23" y1="11" x2="17" y2="11"></line>
                </svg>
              </div>
            </div>
          </div>
          <div class="emp-form-group">
            <label class="emp-label">Mot de passe</label>
            <div class="emp-input-wrapper">
              <input :type="showPassword ? 'text' : 'password'" v-model="motdepasse" class="emp-input" placeholder="Votre mot de passe" required />
              <div class="emp-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <circle cx="12" cy="16" r="1"></circle>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
              </div>
              <button type="button" class="emp-toggle-password" @click="showPassword = !showPassword">
                <span>{{ showPassword ? 'Masquer' : 'Afficher' }}</span>
              </button>
            </div>
            <p class="emp-input-hint">Mot de passe par défaut : emp123</p>
          </div>
          <div v-if="message" class="emp-message" :class="success ? 'emp-message-success' : 'emp-message-error'">
            <span class="emp-message-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
              </svg>
            </span>
            {{ message }}
          </div>
          <button type="submit" class="emp-login-button" :disabled="loading">
            <span v-if="!loading" class="emp-button-content">
              <span>Se connecter</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"></path>
              </svg>
            </span>
            <span v-else class="emp-login-spinner">
              <span>Connexion en cours...</span>
            </span>
          </button>
          <div class="emp-login-links">
            <router-link to="/" class="emp-link">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 12H5M12 19l-7-7 7-7"></path>
              </svg>
              Retour au site principal
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmployeeLoginView',
  data() {
    return {
      matricule: '',
      motdepasse: 'emp123',
      message: '',
      success: false,
      loading: false,
      showPassword: false
    }
  },
  methods: {
    async login() {
      this.message = ''
      this.success = false
      this.loading = true
      try {
        const res = await fetch('http://localhost:8080/api/employe/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ matricule: this.matricule, motdepasse: this.motdepasse })
        })
        const data = await res.json()
        if (data.success) {
          this.success = true
          this.message = data.message
          localStorage.setItem('selfEmployee', JSON.stringify(data.employe))
          this.$router.push('/espace-employe/profil')
        } else {
          this.message = data.message || 'Connexion échouée'
        }
      } catch (e) {
        console.error(e)
        this.message = "Erreur de connexion au serveur"
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.emp-login-page {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(0, 1fr);
  min-height: 100vh;
  background: linear-gradient(135deg, #0f766e 0%, #065f46 50%, #022c22 100%);
  color: #f9fafb;
}

.emp-login-illustration {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
}

.emp-login-overlay {
  max-width: 480px;
  text-align: center;
}

.emp-logo {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 40px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.emp-logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emp-logo-text {
  font-weight: 700;
  font-size: 18px;
  letter-spacing: 0.05em;
}

.emp-login-title {
  font-size: 36px;
  font-weight: 800;
  letter-spacing: .03em;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #fff, #d1fae5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.emp-login-subtitle {
  font-size: 16px;
  line-height: 1.6;
  color: #e5e7eb;
  margin-bottom: 32px;
}

.emp-login-features {
  margin: 32px 0;
  text-align: left;
}

.emp-feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  font-size: 14px;
  color: #d1fae5;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.emp-feature-item:last-child {
  border-bottom: none;
}

.emp-feature-icon {
  width: 20px;
  height: 20px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.emp-login-footer {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 12px;
  color: #9ca3af;
}

.emp-login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 32px;
  background: #f8fafc;
}

.emp-login-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 20px;
  padding: 40px 32px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  border: 1px solid #e2e8f0;
}

.emp-login-header {
  text-align: center;
  margin-bottom: 32px;
}

.emp-login-card-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.emp-login-card-subtitle {
  font-size: 14px;
  color: #6b7280;
}

.emp-login-form {
  space-y: 24px;
}

.emp-form-group {
  margin-bottom: 24px;
}

.emp-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.emp-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.emp-input {
  width: 100%;
  border-radius: 12px;
  border: 2px solid #e5e7eb;
  padding: 12px 44px 12px 44px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: #fafafa;
}

.emp-input:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.emp-input-icon {
  position: absolute;
  left: 14px;
  color: #9ca3af;
}

.emp-toggle-password {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: #6b7280;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.emp-toggle-password:hover {
  background: #f3f4f6;
  color: #374151;
}

.emp-input-hint {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 6px;
}

.emp-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  margin: 20px 0;
}

.emp-message-success {
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.emp-message-error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.emp-message-icon {
  display: flex;
}

.emp-login-button {
  width: 100%;
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
  border-radius: 12px;
  padding: 14px 20px;
  font-size: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  margin: 24px 0;
}

.emp-login-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.4);
}

.emp-login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.emp-button-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.emp-login-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.emp-login-spinner::after {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #fff;
  animation: emp-spin 0.8s linear infinite;
}

.emp-login-links {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.emp-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
  text-decoration: none;
  transition: color 0.2s ease;
}

.emp-link:hover {
  color: #374151;
}

@keyframes emp-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 900px) {
  .emp-login-page {
    grid-template-columns: 1fr;
  }
  .emp-login-illustration {
    display: none;
  }
  .emp-login-card {
    padding: 32px 24px;
    margin: 20px;
  }
}
</style>