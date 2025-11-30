<template>
  <div class="emp-sidebar">
    <div class="emp-sidebar-header">
      <div class="emp-logo">EMP</div>
      <div class="emp-title">Espace Employé</div>
    </div>
    <nav class="emp-nav">
      <router-link to="/espace-employe/profil" class="emp-link" active-class="active">Profil</router-link>
      <router-link to="/espace-employe/profil" class="emp-link" active-class="active">Modifier profil</router-link>
      <router-link to="/espace-employe/solde-conges" class="emp-link" active-class="active">Solde congé</router-link>
      <router-link to="/espace-employe/demande-conge" class="emp-link" active-class="active">Demande de congé</router-link>
      <router-link to="/espace-employe/bulletins" class="emp-link" active-class="active">Bulletin de paie</router-link>
      <router-link to="/espace-employe/messages" class="emp-link" active-class="active">Messagerie RH</router-link>
    </nav>
    <button class="emp-logout" @click="logout">Se déconnecter</button>
  </div>
</template>

<script>
export default {
  name: 'EmployeeSidebar',
  methods: {
    logout() {
      localStorage.removeItem('selfEmployee')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.emp-sidebar {
  width: 220px;
  min-height: 100vh;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  padding: 16px;
}
.emp-sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}
.emp-logo {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: #059669;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}
.emp-title {
  font-weight: 600;
  color: #111827;
}
.emp-nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.emp-link {
  padding: 8px 10px;
  border-radius: 8px;
  text-decoration: none;
  color: #374151;
  font-size: 14px;
}
.emp-link:hover {
  background: #f3f4f6;
}
.emp-link.active {
  background: #dcfce7;
  color: #166534;
  font-weight: 600;
}
.emp-logout {
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
.emp-logout:hover {
  background: #fee2e2;
}
</style>
