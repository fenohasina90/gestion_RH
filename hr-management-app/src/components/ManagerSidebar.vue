<template>
  <div class="mgr-sidebar">
    <div class="mgr-sidebar-header">
      <div class="mgr-logo">MGR</div>
      <div class="mgr-title">Espace Manager</div>
    </div>
    <nav class="mgr-nav">
      <router-link to="/espace-manager/dashboard" class="mgr-link" active-class="active">
        Tableau de bord
      </router-link>
      <router-link to="/espace-manager/demandes-conge" class="mgr-link" active-class="active">
        Demandes de congé
      </router-link>
    </nav>
    <button class="mgr-logout" @click="logout">Se déconnecter</button>
  </div>
</template>

<script>
export default {
  name: 'ManagerSidebar',
  methods: {
    logout() {
      localStorage.removeItem('managerUser')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.mgr-sidebar {
  width: 220px;
  min-height: 100vh;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  padding: 16px;
}
.mgr-sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}
.mgr-logo {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: #0f766e;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}
.mgr-title {
  font-weight: 600;
  color: #111827;
}
.mgr-nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.mgr-link {
  padding: 8px 10px;
  border-radius: 8px;
  text-decoration: none;
  color: #374151;
  font-size: 14px;
}
.mgr-link:hover {
  background: #f3f4f6;
}
.mgr-link.active {
  background: #ccfbf1;
  color: #0f766e;
  font-weight: 600;
}
.mgr-logout {
  margin-top: 24px;
  width: 100%;
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #b91c1c;
  font-size: 14px;
  cursor: pointer;
}
.mgr-logout:hover {
  background: #fee2e2;
}
</style>
