<template>
  <div class="layout">
    <ClientSidebar class="sidebar" />
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script>
import ClientSidebar from '../components/ClientSidebar.vue'

export default {
  name: 'PublicClientLayout',
  components: { ClientSidebar }
}
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; background: linear-gradient(180deg, #f5f9f7 0%, #eefaf6 100%); }
.sidebar { flex: 0 0 260px; }
.content { flex: 1; min-width: 0; padding: 24px; }
</style>
