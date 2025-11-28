<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Demandes de congé en attente</h1>
        <p>Filtrées par employé, type et année</p>
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
            <th>Date demande</th>
            <th>Début</th>
            <th>Fin</th>
            <th>Motif</th>
            <th>Statut</th>
            <th class="right">Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="isLoading"><td colspan="8">Chargement...</td></tr>
          <tr v-else-if="!items.length"><td colspan="8">Aucun résultat</td></tr>
          <tr v-else v-for="d in items" :key="d.id">
            <td>{{ [d.employeNom, d.employePrenom].filter(Boolean).join(' ') || '—' }}</td>
            <td>{{ d.typeLibelle || '—' }}</td>
            <td>{{ fmtDateTime(d.datedemande) }}</td>
            <td>{{ fmtDate(d.datedebut) }}</td>
            <td>{{ fmtDate(d.datefin) }}</td>
            <td>{{ d.motif || '—' }}</td>
            <td><span class="badge yellow">{{ d.statut || 'En attente' }}</span></td>
            <td class="right">
              <button class="btn btn-small" @click="approve(d)" title="Valider">Valider</button>
              <button class="btn btn-small danger" @click="reject(d)" title="Rejeter">Rejeter</button>
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
  name: 'LeavePendingView',
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
    fmtDateTime(v) { return v ? new Date(v).toLocaleString('fr-FR') : '—' },
    async fetchData() {
      try {
        this.isLoading = true
        const params = {
          empId: this.filters.empId || undefined,
          typeId: this.filters.typeId || undefined,
          annee: this.filters.annee || undefined
        }
        const res = await axios.get('/api/conges/demandes/pending', { params })
        this.items = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement demandes en attente', e)
        this.items = []
      } finally {
        this.isLoading = false
      }
    },
    async approve(d) {
      try {
        await axios.post(`/api/conges/demandes/${d.id}/approve`)
        await this.fetchData()
      } catch (e) {
        console.error('Erreur validation', e)
      }
    },
    async reject(d) {
      try {
        await axios.post(`/api/conges/demandes/${d.id}/reject`)
        await this.fetchData()
      } catch (e) {
        console.error('Erreur rejet', e)
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
.badge { padding: 4px 8px; border-radius: 999px; font-weight: 600; font-size: 12px; }
.badge.yellow { background: #fef3c7; color: #92400e; }
.right { text-align: right; }
</style>
