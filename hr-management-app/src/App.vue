<template>
  <div class="app-container">
    <!-- Layout avec sidebar pour les pages authentifiées -->
    <template v-if="!isAuthPage">
      <Sidebar 
        :isCollapsed="sidebarCollapsed" 
        @toggle="toggleSidebar"
      />
      <div class="main-content" :class="{ expanded: sidebarCollapsed }">
        <Navbar @toggle-sidebar="toggleSidebar" />
        <div class="content">
          <router-view />
        </div>
      </div>
    </template>
    
    <!-- Layout sans sidebar pour les pages d'authentification -->
    <template v-else>
      <router-view />
    </template>
  </div>
</template>

<script>
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'

export default {
  name: 'App',
  components: {
    Sidebar,
    Navbar
  },
  data() {
    return {
      sidebarCollapsed: false
    }
  },
  computed: {
    isAuthPage() {
      // Vérifier si on est sur une page d'authentification ou publique
      const p = this.$route.path
      const publicExact = ['/', '/login', '/register', '/jobs', '/mes-qcm']
      // Pages QCM admin qui DOIVENT garder la sidebar
      const qcmKeepSidebar = ['/qcm/create', '/qcm/results', '/resultat-qcm']
      if (qcmKeepSidebar.includes(p)) return false

      // Les pages de l'espace employé et manager n'utilisent pas la sidebar RH globale
      if (p.startsWith('/espace-employe') || p.startsWith('/espace-manager')) return true

      return publicExact.includes(p) ||
             p.startsWith('/jobs/') ||
             // les pages QCM publiques candidats: /qcm/:id, /qcm/:id/results
             (p.startsWith('/qcm/') && !qcmKeepSidebar.includes(p))
    }
  },
  methods: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    }
  },
  mounted() {
    // Gérer la responsivité
    const handleResize = () => {
      if (window.innerWidth <= 768) {
        this.sidebarCollapsed = true
      }
    }
    
    window.addEventListener('resize', handleResize)
    handleResize()
  }
}
</script>
