<template>
  <nav class="navbar">
    <div class="navbar-left">
      <button class="menu-toggle" @click="$emit('toggle-sidebar')">
        ☰
      </button>
      <h1 class="page-title">{{ pageTitle }}</h1>
    </div>
    
    <div class="navbar-right">
      <div class="user-info" v-if="authStore.isLoggedIn">
        <span class="welcome-text">Bienvenue, {{ userName }}</span>
        <button class="btn btn-secondary" @click="logout">
          Déconnexion
        </button>
      </div>
      <div v-else>
        <router-link to="/login" class="btn btn-primary">
          Se connecter
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script>
import { useAuthStore } from '../stores/auth'

export default {
  name: 'Navbar',
  emits: ['toggle-sidebar'],
  data() {
    return {
      userName: 'Administrateur'
    }
  },
  setup() {
    const authStore = useAuthStore()
    return {
      authStore
    }
  },
  computed: {
    pageTitle() {
      const routeTitles = {
        '/': 'Tableau de Bord',
        '/login': 'Connexion',
        '/register': 'Inscription',
        '/profiles': 'Gestion des Profils',
        '/profiles/create': 'Créer un Profil',
        '/announcements': 'Gestion des Annonces',
        '/announcements/create': 'Créer une Annonce',
        '/applications': 'Candidatures',
        '/cv/submit': 'Soumettre un CV',
        '/qcm/create': 'Créer un Test QCM',
        '/qcm/take': 'Passer un Test',
        '/qcm/results': 'Résultats des Tests',
        '/interviews/calendar': 'Calendrier des Entretiens',
        '/interviews/results': 'Résultats des Entretiens',
        '/contracts': 'Contrats d\'Essai',
        '/contracts/create': 'Créer un Contrat'
      }
      return routeTitles[this.$route.path] || 'Gestion RH'
    }
  },
  methods: {
    async logout() {
      try {
        await this.authStore.logout()
        this.$router.push('/login')
      } catch (error) {
        console.error('Erreur lors de la déconnexion:', error)
        // Forcer la déconnexion même en cas d'erreur
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.navbar-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome-text {
  color: #555;
  font-weight: 500;
}

.page-title {
  font-size: 20px;
  color: #2c3e50;
  margin: 0;
}

@media (max-width: 768px) {
  .welcome-text {
    display: none;
  }
  
  .page-title {
    font-size: 16px;
  }
}
</style>
