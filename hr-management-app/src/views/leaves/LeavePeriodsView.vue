<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Périodes de congés</h1>
        <p>Liste filtrée par employé, type et année</p>
      </div>
      <div class="header-actions">
        <button class="btn" @click="reload">Rafraîchir</button>
      </div>
    </div>

    <div class="filters">
      <input v-model.number="filters.empId" class="input" placeholder="ID Employé" @input="debouncedFetch" />
      <input v-model.number="filters.typeId" class="input" placeholder="ID Type congé" @input="debouncedFetch" />
      <input v-model.number="filters.annee" class="input" placeholder="Année" @input="debouncedFetch" />
    </div>

    <div class="card">
      <div class="table-responsive">
        <table class="table">
          <thead>
          <tr>
            <th>Employé</th>
            <th>Type</th>
            <th>Début</th>
            <th>Fin</th>
            <th>Jours pris</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="isLoading"><td colspan="5">Chargement...</td></tr>
          <tr v-else-if="!items.length"><td colspan="5">Aucun résultat</td></tr>
          <tr v-else v-for="p in items" :key="p.id">
            <td>{{ [p.employeNom, p.employePrenom].filter(Boolean).join(' ') || '—' }}</td>
            <td>{{ p.typeLibelle || '—' }}</td>
            <td>{{ fmtDate(p.datedebut) }}</td>
            <td>{{ fmtDate(p.datefin) }}</td>
            <td class="right">{{ fmtNum(p.nombrejourspris) }}</td>
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
  name: 'LeavePeriodsView',
  data() {
    const q = this.$route.query
    return {
      items: [],
      isLoading: false,
      filters: {
        empId: q.empId ? Number(q.empId) : undefined,
        typeId: q.typeId ? Number(q.typeId) : undefined,
        annee: q.annee ? Number(q.annee) : undefined
      },
      debounceTimer: null
    }
  },
  async mounted() {
    await this.fetchData()
  },
  methods: {
    fmtDate(v) { return v ? new Date(v).toLocaleDateString('fr-FR') : '—' },
    fmtNum(v) { return (v == null) ? '—' : Number(v).toFixed(2) },
    async fetchData() {
      try {
        this.isLoading = true
        const params = {
          empId: this.filters.empId || undefined,
          typeId: this.filters.typeId || undefined,
          annee: this.filters.annee || undefined
        }
        const res = await axios.get('/api/conges/periodes', { params })
        this.items = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement périodes', e)
        this.items = []
      } finally {
        this.isLoading = false
      }
    },
    debouncedFetch() {
      clearTimeout(this.debounceTimer)
      this.debounceTimer = setTimeout(() => this.fetchData(), 400)
    },
    reload() { this.fetchData() }
  }
}
</script>

<style scoped>
.page { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.filters { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 12px; }
.input { min-width: 160px; height: 38px; padding: 0 10px; border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; }
.card { background: #fff; border-radius: 12px; padding: 0; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.table-responsive { width: 100%; overflow: auto; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 12px 14px; border-bottom: 1px solid #eef2f7; text-align: left; }
.table thead th { background: #f9fafb; color: #374151; font-weight: 600; }
.right { text-align: right; }
</style>
