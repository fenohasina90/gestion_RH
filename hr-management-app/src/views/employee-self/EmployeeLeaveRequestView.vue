<template>
  <div class="emp-layout">
    <EmployeeSidebar />
    <div class="emp-content">
      <div class="emp-container">
        <div class="emp-header">
          <h1 class="emp-title">Mes demandes de congé</h1>
          <p class="emp-subtitle">Gérez vos demandes de congé et suivez leur statut</p>
        </div>

        <div class="emp-requests-grid">
          <div class="emp-requests-list">
            <div class="emp-section-card">
              <div class="emp-section-header">
                <h2 class="emp-section-title">Historique des demandes</h2>
                <div class="emp-section-actions">
                  <button @click="loadDemandes" class="emp-icon-button" title="Actualiser">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M23 4v6h-6"></path>
                      <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path>
                    </svg>
                  </button>
                </div>
              </div>

              <div v-if="loadingDemandes" class="emp-loading">
                <div class="emp-spinner"></div>
                <span>Chargement des demandes...</span>
              </div>

              <div v-else-if="demandes.length" class="emp-requests-table">
                <div v-for="d in demandes" :key="d.id" class="emp-request-item">
                  <div class="emp-request-main">
                    <div class="emp-request-type">{{ d.typeconge }}</div>
                    <div class="emp-request-dates">
                      Du {{ formatDate(d.datedebut) }} au {{ formatDate(d.datefin) }}
                    </div>
                    <div class="emp-request-duration">
                      {{ d.nombrejoursouvres }} jour(s) ouvré(s)
                    </div>
                  </div>
                  <div class="emp-request-status">
                    <span class="emp-status" :class="getStatusClass(d.statut)">{{ d.statut }}</span>
                  </div>
                </div>
              </div>
              
              <div v-else class="emp-empty-state">
                <div class="emp-empty-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                  </svg>
                </div>
                <h3 class="emp-empty-title">Aucune demande</h3>
                <p class="emp-empty-description">Vous n'avez pas encore de demande de congé.</p>
              </div>
            </div>
          </div>

          <div class="emp-new-request">
            <div class="emp-section-card">
              <div class="emp-section-header">
                <h2 class="emp-section-title">Nouvelle demande</h2>
              </div>

              <form @submit.prevent="creerDemande" class="emp-request-form">
                <div class="emp-form-group">
                  <label class="emp-label">Type de congé</label>
                  <select v-model.number="form.idtypeconge" class="emp-select" required>
                    <option value="1">Congé payé</option>
                    <option value="2">Congé sans solde</option>
                    <option value="3">Congé maladie</option>
                    <option value="4">Congé maternité</option>
                    <option value="5">Congé paternité</option>
                    <option value="6">Congé mariage</option>
                    <option value="7">Congé décès</option>
                    <option value="8">Congé formation</option>
                    <option value="9">Congé sabbatique</option>
                  </select>
                </div>

                <div class="emp-form-row">
                  <div class="emp-form-group">
                    <label class="emp-label">Date de début</label>
                    <input v-model="form.datedebut" type="date" class="emp-input" required />
                  </div>
                  <div class="emp-form-group">
                    <label class="emp-label">Date de fin</label>
                    <input v-model="form.datefin" type="date" class="emp-input" required />
                  </div>
                </div>

                <div class="emp-form-group">
                  <label class="emp-label">Motif</label>
                  <textarea v-model="form.motif" class="emp-textarea" placeholder="Décrivez le motif de votre demande..." rows="4"></textarea>
                </div>

                <div v-if="message" class="emp-message" :class="success ? 'emp-message-success' : 'emp-message-error'">
                  <span class="emp-message-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10"></circle>
                      <path d="m9 12 2 2 4-4"></path>
                    </svg>
                  </span>
                  {{ message }}
                </div>

                <div class="emp-form-actions">
                  <button type="submit" class="emp-button emp-button-primary" :disabled="loadingDemandes">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="22" y1="2" x2="11" y2="13"></line>
                      <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                    </svg>
                    Envoyer la demande
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EmployeeSidebar from '../../components/EmployeeSidebar.vue'

export default {
  name: 'EmployeeLeaveRequestView',
  components: { EmployeeSidebar },
  data() {
    return {
      demandes: [],
      loadingDemandes: false,
      message: '',
      success: false,
      form: {
        idtypeconge: 1,
        datedebut: '',
        datefin: '',
        motif: ''
      }
    }
  },
  created() {
    const stored = localStorage.getItem('selfEmployee')
    if (!stored) {
      this.$router.push('/espace-employe/login')
      return
    }
    this.loadDemandes()
  },
  methods: {
    async loadDemandes() {
      const stored = localStorage.getItem('selfEmployee')
      if (!stored) {
        this.$router.push('/espace-employe/login')
        return
      }
      const emp = JSON.parse(stored)
      this.loadingDemandes = true
      try {
        const res = await fetch(`http://localhost:8080/api/employe/self/demandes-conge?idemploye=${emp.id}`)
        if (!res.ok) throw new Error('Erreur serveur')
        this.demandes = await res.json()
      } catch (e) {
        console.error(e)
        this.demandes = []
      } finally {
        this.loadingDemandes = false
      }
    },
    async creerDemande() {
      this.message = ''
      this.success = false
      const stored = localStorage.getItem('selfEmployee')
      if (!stored) {
        this.$router.push('/espace-employe/login')
        return
      }
      const emp = JSON.parse(stored)
      try {
        const res = await fetch('http://localhost:8080/api/employe/self/demandes-conge', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            idemploye: emp.id,
            idtypeconge: this.form.idtypeconge,
            datedebut: this.form.datedebut,
            datefin: this.form.datefin,
            motif: this.form.motif
          })
        })
        const data = await res.json()
        this.success = !!data.success
        this.message = data.message || ''
        if (data.success) {
          this.form = {
            idtypeconge: 1,
            datedebut: '',
            datefin: '',
            motif: ''
          }
          this.loadDemandes()
        }
      } catch (e) {
        console.error(e)
        this.message = "Erreur lors de l'envoi de la demande"
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR')
    },
    getStatusClass(statut) {
      const statusMap = {
        'En attente': 'emp-status-pending',
        'Approuvé': 'emp-status-approved',
        'Refusé': 'emp-status-rejected',
        'En traitement': 'emp-status-processing'
      }
      return statusMap[statut] || 'emp-status-default'
    }
  }
}
</script>

<style scoped>
.emp-layout {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
}

.emp-content {
  flex: 1;
  padding: 0;
}

.emp-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

.emp-header {
  margin-bottom: 32px;
}

.emp-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.emp-subtitle {
  font-size: 16px;
  color: #6b7280;
}

.emp-requests-grid {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 32px;
}

.emp-section-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
  height: fit-content;
}

.emp-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.emp-section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.emp-section-actions {
  display: flex;
  gap: 8px;
}

.emp-icon-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emp-icon-button:hover {
  background: #e5e7eb;
}

.emp-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px 20px;
  color: #6b7280;
}

.emp-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top: 3px solid #10b981;
  border-radius: 50%;
  animation: emp-spin 1s linear infinite;
}

.emp-requests-table {
  space-y: 12px;
}

.emp-request-item {
  display: flex;
  justify-content: space-between;
  align-items: start;
  padding: 16px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.emp-request-item:hover {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.emp-request-main {
  flex: 1;
}

.emp-request-type {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.emp-request-dates {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.emp-request-duration {
  font-size: 12px;
  color: #9ca3af;
}

.emp-request-status {
  margin-left: 16px;
}

.emp-status {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.emp-status-pending {
  background: #fef3c7;
  color: #92400e;
}

.emp-status-approved {
  background: #d1fae5;
  color: #065f46;
}

.emp-status-rejected {
  background: #fee2e2;
  color: #991b1b;
}

.emp-status-processing {
  background: #dbeafe;
  color: #1e40af;
}

.emp-status-default {
  background: #f3f4f6;
  color: #374151;
}

.emp-empty-state {
  text-align: center;
  padding: 40px 20px;
}

.emp-empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.emp-empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.emp-empty-description {
  font-size: 14px;
  color: #6b7280;
  max-width: 200px;
  margin: 0 auto;
}

.emp-request-form {
  space-y: 20px;
}

.emp-form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.emp-form-group {
  space-y: 8px;
}

.emp-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.emp-input, .emp-select, .emp-textarea {
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: #fafafa;
  font-family: inherit;
}

.emp-input:focus, .emp-select:focus, .emp-textarea:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.emp-textarea {
  resize: vertical;
  min-height: 80px;
}

.emp-form-actions {
  text-align: right;
  padding-top: 8px;
}

.emp-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.emp-button-primary {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.emp-button-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.4);
}

.emp-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.emp-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.emp-message-success {
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.emp-message-error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.emp-message-icon {
  display: flex;
}

@keyframes emp-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .emp-requests-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .emp-new-request {
    order: -1;
  }
}

@media (max-width: 768px) {
  .emp-container {
    padding: 24px 16px;
  }
  
  .emp-section-card {
    padding: 20px;
  }
  
  .emp-form-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .emp-request-item {
    flex-direction: column;
    gap: 12px;
  }
  
  .emp-request-status {
    margin-left: 0;
    align-self: flex-start;
  }
}
</style>