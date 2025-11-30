<template>
  <div class="mgr-layout">
    <ManagerSidebar />
    <div class="mgr-content">
      <div class="mgr-container">
        <div class="mgr-header">
          <h1 class="mgr-title">Demandes de congé de mon équipe</h1>
          <p class="mgr-subtitle">Validez ou refusez les demandes de congé des employés de votre département</p>
        </div>

        <div class="mgr-filters">
          <div class="mgr-filter-group">
            <label class="mgr-filter-label">Statut</label>
            <select v-model="filters.statut" class="mgr-filter-input">
              <option value="">Tous</option>
              <option v-for="s in statuts" :key="s" :value="s">{{ s }}</option>
            </select>
          </div>
          <div class="mgr-filter-group">
            <label class="mgr-filter-label">Année</label>
            <input v-model.number="filters.annee" type="number" class="mgr-filter-input" min="2000" max="2100" />
          </div>
          <div class="mgr-filter-group">
            <label class="mgr-filter-label">Type de congé (id)</label>
            <input v-model.number="filters.idtypeconge" type="number" class="mgr-filter-input" min="1" />
          </div>
          <div class="mgr-filter-actions">
            <button class="mgr-btn-outline" @click="resetFilters">Réinitialiser</button>
            <button class="mgr-btn" @click="loadDemandes">Actualiser</button>
          </div>
        </div>

        <div v-if="loading" class="mgr-loading">
          <div class="mgr-spinner"></div>
          <span>Chargement des demandes...</span>
        </div>

        <div v-else-if="demandes.length === 0" class="mgr-empty">
          <p>Aucune demande trouvée.</p>
        </div>

        <div v-else class="mgr-table">
          <table>
            <thead>
              <tr>
                <th>Employé</th>
                <th>Type</th>
                <th>Période</th>
                <th>Jours</th>
                <th>Statut</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="d in demandes" :key="d.id">
                <td>
                  <div class="mgr-emp-name">{{ d.prenom }} {{ d.nom }}</div>
                  <div class="mgr-emp-meta">{{ d.matricule }}</div>
                </td>
                <td>{{ d.typeconge }}</td>
                <td>Du {{ formatDate(d.datedebut) }} au {{ formatDate(d.datefin) }}</td>
                <td>{{ d.nombrejoursouvres }}</td>
                <td>{{ d.statut }}</td>
                <td>
                  <button
                    class="mgr-action-btn mgr-approve"
                    @click="valider(d)"
                    :disabled="acting || d.statut !== 'En attente manager'"
                  >
                    Valider
                  </button>
                  <button
                    class="mgr-action-btn mgr-reject"
                    @click="refuser(d)"
                    :disabled="acting || d.statut !== 'En attente manager'"
                  >
                    Refuser
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="message" class="mgr-global-message" :class="success ? 'mgr-global-success' : 'mgr-global-error'">
          {{ message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ManagerSidebar from '../../components/ManagerSidebar.vue'

export default {
  name: 'ManagerLeaveRequestsView',
  components: { ManagerSidebar },
  data() {
    return {
      demandes: [],
      loading: false,
      acting: false,
      message: '',
      success: false,
      filters: {
        statut: '',
        annee: new Date().getFullYear(),
        idtypeconge: null
      },
      statuts: [
        'En attente manager',
        'Validé manager',
        'Refusé manager',
        'En attente RH',
        'Validé RH',
        'Refusé RH'
      ]
    }
  },
  created() {
    const stored = localStorage.getItem('managerUser')
    if (!stored) {
      this.$router.push('/espace-manager/login')
      return
    }
    this.loadDemandes()
  },
  methods: {
    async loadDemandes() {
      const stored = localStorage.getItem('managerUser')
      if (!stored) {
        this.$router.push('/espace-manager/login')
        return
      }
      const mgr = JSON.parse(stored)
      this.loading = true
      this.message = ''
      this.success = false
      try {
        const params = new URLSearchParams()
        params.append('idmanager', mgr.id)
        if (this.filters.statut) params.append('statut', this.filters.statut)
        if (this.filters.annee) params.append('annee', this.filters.annee)
        if (this.filters.idtypeconge) params.append('idtypeconge', this.filters.idtypeconge)

        const res = await fetch(`http://localhost:8080/api/manager/demandes-conge?${params.toString()}`)
        if (!res.ok) throw new Error('Erreur serveur')
        this.demandes = await res.json()
      } catch (e) {
        console.error(e)
        this.demandes = []
        this.message = 'Erreur lors du chargement des demandes'
        this.success = false
      } finally {
        this.loading = false
      }
    },
    resetFilters() {
      this.filters.statut = ''
      this.filters.annee = new Date().getFullYear()
      this.filters.idtypeconge = null
      this.loadDemandes()
    },
    formatDate(d) {
      if (!d) return ''
      const date = new Date(d)
      return date.toLocaleDateString('fr-FR')
    },
    async valider(demande) {
      await this._actionDemande(demande, 'valider')
    },
    async refuser(demande) {
      await this._actionDemande(demande, 'refuser')
    },
    async _actionDemande(demande, action) {
      const stored = localStorage.getItem('managerUser')
      if (!stored) {
        this.$router.push('/espace-manager/login')
        return
      }
      const mgr = JSON.parse(stored)
      this.acting = true
      this.message = ''
      this.success = false
      try {
        const res = await fetch(`http://localhost:8080/api/manager/demandes-conge/${demande.id}/${action}?idmanager=${mgr.id}`, {
          method: 'POST'
        })
        const data = await res.json()
        this.success = !!data.success
        this.message = data.message || ''
        if (data.success) {
          await this.loadDemandes()
        }
      } catch (e) {
        console.error(e)
        this.message = `Erreur lors de l'action ${action}`
        this.success = false
      } finally {
        this.acting = false
      }
    }
  }
}
</script>

<style scoped>
.mgr-layout {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
}
.mgr-content {
  flex: 1;
  padding: 24px;
}
.mgr-container {
  max-width: 1100px;
  margin: 0 auto;
}
.mgr-header {
  margin-bottom: 20px;
}
.mgr-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}
.mgr-subtitle {
  font-size: 14px;
  color: #6b7280;
}
.mgr-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
  padding: 12px 16px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
}
.mgr-filter-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.mgr-filter-label {
  font-size: 12px;
  color: #4b5563;
}
.mgr-filter-input {
  border-radius: 8px;
  border: 1px solid #d1d5db;
  padding: 6px 8px;
  font-size: 13px;
}
.mgr-filter-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}
.mgr-btn, .mgr-btn-outline {
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  border: none;
}
.mgr-btn {
  background: #0f766e;
  color: white;
}
.mgr-btn-outline {
  background: white;
  color: #111827;
  border: 1px solid #d1d5db;
}
.mgr-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
}
.mgr-spinner {
  width: 18px;
  height: 18px;
  border-radius: 999px;
  border: 2px solid #e5e7eb;
  border-top-color: #0f766e;
  animation: spin 1s linear infinite;
}
.mgr-empty {
  padding: 24px;
  text-align: center;
  color: #6b7280;
}
.mgr-table {
  margin-top: 8px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}
.mgr-table table {
  width: 100%;
  border-collapse: collapse;
}
.mgr-table th, .mgr-table td {
  padding: 10px 12px;
  font-size: 13px;
  border-bottom: 1px solid #e5e7eb;
}
.mgr-table th {
  background: #f9fafb;
  text-align: left;
  font-weight: 600;
  color: #4b5563;
}
.mgr-emp-name {
  font-weight: 600;
  color: #111827;
}
.mgr-emp-meta {
  font-size: 12px;
  color: #6b7280;
}
.mgr-action-btn {
  padding: 6px 8px;
  font-size: 12px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  margin-right: 4px;
}
.mgr-approve {
  background: #16a34a;
  color: white;
}
.mgr-reject {
  background: #ef4444;
  color: white;
}
.mgr-action-btn:disabled {
  opacity: 0.5;
  cursor: default;
}
.mgr-global-message {
  margin-top: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 13px;
}
.mgr-global-success {
  background: #ecfdf5;
  color: #166534;
  border: 1px solid #bbf7d0;
}
.mgr-global-error {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .mgr-layout {
    flex-direction: column;
  }
  .mgr-content {
    padding: 16px;
  }
  .mgr-filters {
    flex-direction: column;
    align-items: stretch;
  }
  .mgr-filter-actions {
    margin-left: 0;
    justify-content: flex-end;
  }
}
</style>
