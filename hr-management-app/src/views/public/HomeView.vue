<template>
  <div class="home-container">
    <!-- Header avec boutons Login/Signup -->
    <header class="home-header">
      <div class="header-content">
        <div class="logo">
          <h1>HR Manager</h1>
        </div>
        <div class="auth-buttons">
          <router-link to="/jobs" class="btn btn-outline">Voir les offres</router-link>
          <router-link to="/espace-employe/login" class="btn btn-success">Espace employé</router-link>
          <router-link to="/espace-manager/login" class="btn btn-outline">Espace manager</router-link>
          <template v-if="candidateAuthStore.isLoggedIn">
            <button v-if="candidateAuthStore.candidate" @click="$router.push('/mes-qcm')" class="btn btn-secondary">
              Mes QCM
            </button>
            <button @click="logout" class="btn btn-danger">Se déconnecter</button>
          </template>
          <template v-else>
            <router-link to="/dashboard" class="btn btn-primary">Se connecter</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- Contenu principal -->
    <main class="home-main" :style="homeMainStyle">
      <div class="hero-section">
        <div class="hero-content">
          <h2 class="hero-title">Trouvez votre emploi idéal</h2>
          <p class="hero-subtitle">
            Découvrez des opportunités de carrière exceptionnelles dans les meilleures entreprises. 
            Postulez facilement et suivez vos candidatures en temps réel.
          </p>
          <div class="hero-actions">
            <router-link to="/jobs" class="btn btn-large btn-primary">
              <i class="fas fa-search"></i>
              Parcourir les offres
            </router-link>
            <button @click="scrollToFeatures" class="btn btn-large btn-outline">
              <i class="fas fa-info-circle"></i>
              En savoir plus
            </button>
          </div>
        </div>
        <div class="hero-image">
          <div class="image-placeholder">
            <i class="fas fa-briefcase"></i>
          </div>
        </div>
      </div>

    </main>

    <!-- Footer -->
    <footer class="home-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>HR Manager</h4>
          <p>Votre plateforme de recrutement moderne</p>
        </div>
        <div class="footer-section">
          <h4>Liens rapides</h4>
          <ul>
            <li><router-link to="/jobs">Offres d'emploi</router-link></li>
            <li><router-link to="/dashboard">Espace admin</router-link></li>
            <li><router-link to="/espace-employe/login">Espace employé</router-link></li>
            <li><router-link to="/espace-manager/login">Espace manager</router-link></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>Contact</h4>
          <p>Email: contact@hrmanager.com</p>
          <p>Tél: +33 1 23 45 67 89</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 HR Manager. Tous droits réservés.</p>
      </div>
    </footer>

    <!-- Modal d'authentification -->
    <div v-if="showAuthModal" class="modal-overlay" @click="showAuthModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Authentification</h3>
          <button @click="showAuthModal = false" class="close-btn">&times;</button>
        </div>
        <div class="modal-body">
          <div class="auth-toggle">
            <button 
              @click="authMode = 'candidate'" 
              :class="{ active: authMode === 'candidate' }"
              class="toggle-btn"
            >
              Candidat
            </button>
            <button 
              @click="authMode = 'admin'" 
              :class="{ active: authMode === 'admin' }"
              class="toggle-btn"
            >
              Administrateur
            </button>
          </div>
          <div class="auth-actions">
            <router-link 
              :to="authMode === 'candidate' ? '/jobs' : '/dashboard'" 
              class="btn btn-primary btn-full"
              @click="showAuthModal = false"
            >
              Se connecter
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useCandidateAuthStore } from '../../stores/candidateAuth'

export default {
  name: 'HomeView',
  setup() {
    const candidateAuthStore = useCandidateAuthStore()
    return {
      candidateAuthStore
    }
  },
  data() {
    return {
      showAuthModal: false,
      authMode: 'candidate',
      bgUrl: new URL('../../assets/Offre-demploi-scaled.jpg', import.meta.url).href
    }
  },
  computed: {
    homeMainStyle() {
      return {
        backgroundImage: `linear-gradient(rgba(255,255,255,0.6), rgba(255,255,255,0.6)), url(${this.bgUrl})`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center',
        backgroundAttachment: 'fixed'
      }
    }
  },
  methods: {
    scrollToFeatures() {
      this.$refs.featuresSection.scrollIntoView({ behavior: 'smooth' })
    },
    async logout() {
      try {
        await this.candidateAuthStore.logout()
        this.$router.push('/')
      } catch (error) {
        console.error('Erreur lors de la déconnexion:', error)
      }
    }
  }
}
</script>

<style scoped>
.home-container {
  width: 100%;
  min-height: calc(100vh - 48px);
  margin: 0 auto;
  position: relative;
  /* Thème aligné sur la sidebar (palette émeraude) */
  background: linear-gradient(180deg, #f5f9f7 0%, #eefaf6 100%);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.home-header {
  background: #ffffff;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0,0,0,0.06);
  position: relative;
  z-index: 100;
  height: 80px;
}

.header-content {
  width: 100%;
  padding: 1rem 4rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo h1 {
  color: #065f46; /* emerald-800 */
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
}

.auth-buttons {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.home-main {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.hero-section {
  width: 100%;
  padding: 2rem 4rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  align-items: center;
  flex: 1;
  min-height: 0;
}

.hero-content {
  color: #064e3b;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  line-height: 1.2;
  color: #065f46;
}

.hero-subtitle {
  font-size: 1.2rem;
  margin-bottom: 2.5rem;
  line-height: 1.6;
  color: #0f766e;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.hero-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-placeholder {
  width: 300px;
  height: 300px;
  background: rgba(16, 185, 129, 0.08);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(5, 150, 105, 0.2);
}

.image-placeholder i {
  font-size: 6rem;
  color: #10b981;
}

.features-section {
  background: #ffffff;
  padding: 5rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  color: #065f46;
  margin-bottom: 3rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.feature-card {
  text-align: center;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.06);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 1.5rem;
}

.feature-icon i {
  font-size: 2rem;
  color: white;
}

.feature-card h4 {
  font-size: 1.5rem;
  color: #065f46;
  margin-bottom: 1rem;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

.cta-section {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  padding: 4rem 0;
  text-align: center;
}

.cta-content h3 {
  font-size: 2.5rem;
  color: white;
  margin-bottom: 1rem;
}

.cta-content p {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 2rem;
}

/* Boutons */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.btn-primary {
  background: #059669; /* emerald-600 */
  color: #ffffff;
  font-weight: 600;
}

.btn-primary:hover {
  background: #047857;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(16, 185, 129, 0.35);
}

.btn-outline {
  background: transparent;
  color: #059669;
  border: 2px solid #059669;
}

.btn-outline:hover {
  background: #059669;
  color: #ffffff;
}

.btn-success {
  background: #10b981;
  color: #ffffff;
  font-weight: 600;
}

.btn-success:hover {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(16, 185, 129, 0.35);
}

.btn-danger {
  background: #065f46;
  color: #ffffff;
  font-weight: 600;
  border: 2px solid #065f46;
}

.btn-danger:hover {
  background: #047857;
  color: #ffffff;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(16, 185, 129, 0.35);
}

.btn-large {
  padding: 1rem 2rem;
  font-size: 1.1rem;
}

.btn-full {
  width: 100%;
  justify-content: center;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 1.5rem;
}

.auth-toggle {
  display: flex;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  overflow: hidden;
}

.toggle-btn {
  flex: 1;
  padding: 0.75rem;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toggle-btn.active {
  background: #fbbf24;
  color: #1e3a8a;
}

.auth-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* Footer styles */
.home-footer {
  background: #065f46;
  color: #dcfce7;
  padding: 2rem 0 1rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
  padding: 0 4rem;
  margin-bottom: 1rem;
}

.footer-section h4 {
  margin: 0 0 1rem 0;
  color: #dcfce7;
  font-size: 1.1rem;
}

.footer-section p {
  margin: 0.5rem 0;
  color: rgba(236, 253, 245, 0.8);
  font-size: 0.9rem;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section li {
  margin: 0.5rem 0;
}

.footer-section a {
  color: rgba(236, 253, 245, 0.85);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.footer-section a:hover {
  color: #dcfce7;
}

.footer-bottom {
  border-top: 1px solid rgba(236, 253, 245, 0.25);
  padding: 1rem 4rem 0;
  text-align: center;
}

.footer-bottom p {
  margin: 0;
  color: rgba(236, 253, 245, 0.7);
  font-size: 0.8rem;
}

/* Ajout du style pour le body pour centrer parfaitement */
body {
  margin: 0;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #eefaf6;
}
</style>
