<template>
  <div class="max-w-3xl mx-auto py-16 px-4 text-center">
    <h1 class="text-2xl font-semibold mb-4">Mes congés</h1>
    <p class="text-gray-600 mb-6">
      L'affichage des congés a été séparé en deux écrans : solde et demandes.
    </p>
    <div class="space-x-4">
      <router-link
        to="/espace-employe/solde-conges"
        class="inline-block bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 text-sm"
      >
        Voir mon solde de congés
      </router-link>
      <router-link
        to="/espace-employe/demande-conge"
        class="inline-block bg-white border border-green-600 text-green-700 px-4 py-2 rounded hover:bg-green-50 text-sm"
      >
        Gérer mes demandes
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmployeeLeavesView'
}
</script>

<style scoped>
</style>
