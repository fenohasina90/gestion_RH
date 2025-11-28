<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Suivi des soldes de congés</h1>
        <p>Payés, maladie, exceptionnels</p>
      </div>
      <div class="header-actions">
        <button class="btn" @click="reload">Rafraîchir</button>
      </div>
    </div>

    <!-- Filtres -->
    <div class="filters">
      <input v-model.trim="filters.employe" class="input" placeholder="Employé (nom ou prénom)" @input="debouncedFetch" />
      <select v-model="filters.departementId" class="input" @change="fetchData">
        <option :value="''">Tous départements</option>
        <option v-for="d in departements" :key="d.id" :value="d.id">{{ d.nom }}</option>
      </select>
      <select v-model="filters.typeCongeId" class="input" @change="fetchData">
        <option :value="''">Tous types</option>
        <option v-for="t in types" :key="t.id" :value="t.id">{{ t.libelle }}</option>
      </select>
      <select v-model="filters.annee" class="input" @change="fetchData">
        <option :value="''">Toutes années</option>
        <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
      </select>
      <select v-model="sort.sortBy" class="input" @change="fetchData">
        <option value="nom">Trier par nom</option>
        <option value="solde">Trier par solde restant</option>
      </select>
      <select v-model="sort.order" class="input" @change="fetchData">
        <option value="asc">Asc</option>
        <option value="desc">Desc</option>
      </select>
    </div>

    <!-- Tableau -->
    <div class="card">
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th>Employé</th>
              <th>Département</th>
              <th>Type de congé</th>
              <th>Jours acquis</th>
              <th>Jours pris</th>
              <th>Solde restant</th>
              <th>Demandes en attente</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="isLoading"><td colspan="8">Chargement...</td></tr>
            <tr v-else-if="!items.length"><td colspan="8">Aucun résultat</td></tr>
            <tr v-else v-for="row in items" :key="rowKey(row)">
              <td>{{ fullName(row) }}</td>
              <td>{{ row.departement || '—' }}</td>
              <td>{{ row.type }}</td>
              <td class="right">{{ fmt(row.joursAcquis) }}</td>
              <td class="right">{{ fmt(row.joursPris) }}</td>
              <td class="right">
                <span class="badge" :class="row.joursRestants > 0 ? 'green' : 'red'">{{ fmt(row.joursRestants) }}</span>
              </td>
              <td class="right">
                <span class="badge link" :class="row.demandesEnAttente > 0 ? 'yellow' : ''" @click="viewPending(row)">{{ row.demandesEnAttente }}</span>
              </td>
              <td>
                <button class="btn btn-small" @click="viewDetails(row)">Détails</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'LeaveBalancesView',
  data() {
    const currentYear = new Date().getFullYear()
    return {
      items: [],
      isLoading: false,
      filters: {
        employe: '',
        departementId: '',
        typeCongeId: '',
        annee: ''
      },
      sort: { sortBy: 'nom', order: 'asc' },
      years: [currentYear - 1, currentYear, currentYear + 1],
      departements: [],
      types: [],
      debounceTimer: null
    }
  },
  async mounted() {
    await Promise.all([this.loadDepartements(), this.loadTypes()])
    await this.fetchData()
  },
  methods: {
    rowKey(r) { return `${r.employeId}-${r.typeId}-${r.annee}` },
    fullName(r) { return [r.nom, r.prenom].filter(Boolean).join(' ') },
    fmt(v) { return (v == null) ? '—' : Number(v).toFixed(2) },
    async loadDepartements() {
      try {
        // Assuming a backend endpoint exists; if not, list can be empty and filters still work
        const res = await axios.get('/api/departements')
        this.departements = Array.isArray(res.data) ? res.data : []
      } catch { this.departements = [] }
    },
    async loadTypes() {
      try {
        const res = await axios.get('/api/typeconges')
        this.types = Array.isArray(res.data) ? res.data : []
      } catch { this.types = [] }
    },
    async fetchData() {
      try {
        this.isLoading = true
        const params = {
          employe: this.filters.employe || undefined,
          departementId: this.filters.departementId || undefined,
          typeCongeId: this.filters.typeCongeId || undefined,
          annee: this.filters.annee || undefined,
          sortBy: this.sort.sortBy,
          order: this.sort.order
        }
        const res = await axios.get('/api/conges/solde', { params })
        this.items = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement soldes', e)
        this.items = []
      } finally {
        this.isLoading = false
      }
    },
    debouncedFetch() {
      clearTimeout(this.debounceTimer)
      this.debounceTimer = setTimeout(() => this.fetchData(), 400)
    },
    reload() { this.fetchData() },
    viewDetails(row) {
      this.$router.push({
        name: 'LeavePeriods',
        query: { empId: row.employeId, typeId: row.typeId, annee: row.annee }
      })
    },
    viewPending(row) {
      this.$router.push({
        name: 'LeavePending',
        query: { empId: row.employeId, typeId: row.typeId, annee: row.annee }
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.filters { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 12px; }
.input { min-width: 180px; height: 38px; padding: 0 10px; border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; }
.card { background: #fff; border-radius: 12px; padding: 0; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.table-responsive { width: 100%; overflow: auto; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 12px 14px; border-bottom: 1px solid #eef2f7; text-align: left; }
.table thead th { background: #f9fafb; color: #374151; font-weight: 600; }
.right { text-align: right; }
.badge { padding: 4px 8px; border-radius: 999px; font-weight: 600; font-size: 12px; }
.badge.green { background: #dcfce7; color: #166534; }
.badge.red { background: #fee2e2; color: #991b1b; }
.badge.yellow { background: #fef3c7; color: #92400e; }
.btn { padding: 8px 12px; border-radius: 8px; border: 1px solid #e5e7eb; background: #fff; cursor: pointer; }
.btn:hover { background: #f9fafb; }
.btn-small { padding: 6px 10px; font-size: 13px; }
</style>
