<template>
  <aside class="client-sidebar" :class="{ collapsed: isCollapsed }">
    <div class="client-header">
      <div class="logo-section">
        <div class="logo-circle"> 
          <span class="logo-text">HR</span>
        </div>
        <div class="header-info">
          <h2>Espace Candidat</h2>
          <span class="status-indicator">
            <span class="status-dot"></span>
            Disponible
          </span>
        </div>
      </div>
    </div>

    <nav class="client-nav">
      <!-- Accueil -->
      <router-link to="/" class="client-nav-item simple" :class="{ active: isActiveExact('/') }">
        <div class="client-nav-content">
          <span class="client-nav-icon">🏠</span>
          <span class="client-nav-label">Accueil</span>
        </div>
        <span class="client-active-indicator"></span>
      </router-link>

      <!-- Offres -->
      <div class="client-nav-section">
        <button class="client-accordion" :class="{ open: isOpen('jobs'), active: isAnyActive(['/jobs']) }" @click="toggle('jobs')">
          <div class="client-nav-content">
            <span class="client-nav-icon">💼</span>
            <span class="client-nav-label">Offres</span>
          </div>
          <span class="client-chevron" :class="{ rotate: isOpen('jobs') }">▾</span>
        </button>
        <transition name="slide-fade">
          <div v-show="isOpen('jobs')" class="client-submenu">
            <router-link to="/jobs" class="client-nav-item sub" :class="{ active: isActiveExact('/jobs') }">Toutes les offres</router-link>
            <button class="client-nav-item sub linklike" :disabled="!applyLink" @click="applyLink && $router.push(applyLink)" :class="{ disabled: !applyLink, active: isActiveApply }">
              Postuler
            </button>
          </div>
        </transition>
      </div>

      <!-- Mes QCM -->
      <router-link to="/mes-qcm" class="client-nav-item simple" :class="{ active: isActiveExact('/mes-qcm') }">
        <div class="client-nav-content">
          <span class="client-nav-icon">📝</span>
          <span class="client-nav-label">Mes QCM</span>
        </div>
        <span class="client-active-indicator"></span>
      </router-link>

      <!-- Résultat de QCM -->
      <button class="client-nav-item simple linklike" :disabled="!resultsLink" @click="resultsLink && $router.push(resultsLink)" :class="{ active: isActiveResults, disabled: !resultsLink }">
        <div class="client-nav-content">
          <span class="client-nav-icon">📊</span>
          <span class="client-nav-label">Résultats QCM</span>
        </div>
        <span class="client-active-indicator"></span>
      </button>
    </nav>
  </aside>
</template>

<script>
export default {
  name: 'ClientSidebar',
  props: {
    isCollapsed: { type: Boolean, default: false }
  },
  data() {
    return {
      open: { jobs: true }
    }
  },
  computed: {
    applyLink() {
      // Use current route id if on an apply page or any job route with id in params
      const { path, params } = this.$route
      if (path.startsWith('/jobs/') && params.id) return `/jobs/${params.id}/apply`
      // fallback example (optionally could be null to keep disabled)
      return null
    },
    resultsLink() {
      const { path, params } = this.$route
      if (path.startsWith('/qcm/') && params.id) return `/qcm/${params.id}/results`
      return null
    },
    isActiveApply() {
      return this.$route.path.startsWith('/jobs/') && this.$route.path.endsWith('/apply')
    },
    isActiveResults() {
      return this.$route.path.startsWith('/qcm/') && this.$route.path.endsWith('/results')
    }
  },
  methods: {
    toggle(key) {
      Object.keys(this.open).forEach(k => {
        this.open[k] = (k === key) ? !this.open[k] : false
      })
    },
    isOpen(key) { return !!this.open[key] },
    isActive(prefix) { return this.$route.path === prefix || this.$route.path.startsWith(prefix) },
    isAnyActive(prefixes) { return prefixes.some(p => this.isActive(p)) },
    isActiveExact(path) { return this.$route.path === path }
  }
}
</script>

<style scoped>
.client-sidebar { width: 260px; height: 100vh; background: #ffffff; border-right: 1px solid #e5e7eb; display: flex; flex-direction: column; position: sticky; top: 0; flex: 0 0 260px; }
.client-header { padding: 16px; border-bottom: 1px solid #f3f4f6; }
.logo-section { display: flex; align-items: center; gap: 10px; }
.logo-circle { width: 36px; height: 36px; background: #059669; color: #fff; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-weight: 700; }
.header-info h2 { margin: 0 0 4px 0; font-size: 14px; color: #111827; font-weight: 600; }
.status-indicator { font-size: 12px; color: #6b7280; display: flex; align-items: center; gap: 6px; }
.status-dot { width: 6px; height: 6px; background: #10b981; border-radius: 50%; }
.client-nav { flex: 1; padding: 12px 0; overflow-y: auto; }
.client-nav-item { display: flex; align-items: center; justify-content: space-between; padding: 10px 16px; margin: 0 12px; border-radius: 8px; color: #374151; text-decoration: none; font-size: 14px; font-weight: 500; transition: .2s; width: calc(100% - 24px); }
.client-nav-item:hover { background: #f3f4f6; color: #111827; }
.client-nav-item.active { background: #dcfce7; color: #166534; font-weight: 600; }
.client-nav-content { display: flex; align-items: center; gap: 10px; }
.client-nav-icon { color: #6b7280; }
.client-nav-item.active .client-nav-icon { color: #059669; }
.client-active-indicator { width: 4px; height: 18px; background: #059669; border-radius: 2px; opacity: 0; transition: .2s; }
.client-nav-item.active .client-active-indicator { opacity: 1; }
.client-accordion { width: 100%; padding: 10px 16px; margin: 2px 12px; border: none; background: transparent; color: #374151; border-radius: 8px; cursor: pointer; display: flex; align-items: center; justify-content: space-between; }
.client-accordion:hover { background: #f3f4f6; color: #111827; }
.client-accordion.active, .client-accordion.open { background: #dcfce7; color: #166534; font-weight: 600; }
.client-chevron { color: #9ca3af; transition: transform .2s; }
.client-chevron.rotate { transform: rotate(180deg); }
.client-submenu { margin-top: 4px; margin-left: 12px; border-left: 2px solid #e5e7eb; }
.client-nav-item.sub { margin: 0; padding: 8px 16px 8px 20px; font-size: 13px; color: #6b7280; }
.client-nav-item.sub.active { background: #f0fdf4; color: #166534; border-left: 2px solid #059669; margin-left: -2px; }
.linklike { background: transparent; border: none; text-align: left; }
.linklike.disabled { opacity: .5; cursor: not-allowed; }
</style>
