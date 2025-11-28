<template>
  <div class="contracts-view">
    <div class="page-header">
      <h1 class="page-title">Contrats d'Essai</h1>
      <div class="page-actions">
        <router-link to="/contracts/create" class="btn btn-primary">
          ➕ Nouveau contrat
        </router-link>
        <button @click="exportContracts" class="btn btn-secondary">
          📄 Exporter PDF
        </button>
      </div>
    </div>

    <!-- Filtres et recherche -->
    <div class="filters-section">
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          @input="debouncedApplyFilters"
          placeholder="Rechercher par nom, poste ou département..."
          class="form-input search-input"
        />
      </div>
      
      <div class="filters">
        <select v-model="selectedStatus" @change="applyFilters" class="form-input filter-select">
          <option value="">Tous les statuts</option>
          <option value="non_commence">Non commencé</option>
          <option value="en_cours">En cours</option>
          <option value="termine">Terminé</option>
        </select>
        
        <select v-model="selectedDepartmentId" @change="applyFilters" class="form-input filter-select">
          <option value="">Tous les départements</option>
          <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.nom }}</option>
        </select>
        
        <select v-model="selectedContractType" @change="applyFilters" class="form-input filter-select">
          <option value="">Tous les types</option>
          <option v-for="t in contractTypes" :key="t" :value="t">{{ t }}</option>
        </select>
        
        <select v-model="selectedPeriod" class="form-input filter-select">
          <option value="">Toutes les périodes</option>
          <option value="current">En cours</option>
          <option value="ending-soon">Se termine bientôt</option>
          <option value="this-month">Ce mois</option>
          <option value="last-month">Le mois dernier</option>
        </select>
      </div>
    </div>

    <!-- Statistiques -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📄</div>
          <div class="stat-content">
            <h3>{{ totalContracts }}</h3>
            <p>Contrats totaux</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <h3>{{ activeContracts }}</h3>
            <p>Contrats actifs</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">⏰</div>
          <div class="stat-content">
            <h3>{{ endingSoonContracts }}</h3>
            <p>Se terminent bientôt</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <h3>{{ successRate }}%</h3>
            <p>Taux de confirmation</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Liste des contrats -->
    <div class="contracts-section">
      <div class="contracts-list">
        <div 
          v-for="contract in filteredContracts" 
          :key="contract.id"
          class="contract-card"
          :class="`status-${contract.status}`"
        >
          <div class="contract-header">
            <div class="employee-info">
              <div class="employee-avatar">
                {{ contract.employeeName.split(' ').map(n => n[0]).join('') }}
              </div>
              <div class="employee-details">
                <h3>{{ contract.employeeName }}</h3>
                <p class="position">{{ contract.position }}</p>
                <p class="department">{{ contract.department }}</p>
              </div>
            </div>
            
            <div class="contract-summary">
              <div class="status-badge" :class="`status-${contract.status}`">
                {{ getStatusLabel(contract.status) }}
              </div>
              <div class="contract-type">{{ contract.contractType }}</div>
            </div>
          </div>

          <div class="contract-details">
            <div class="details-grid">
              <div class="detail-item">
                <span class="detail-label">Date de début:</span>
                <span class="detail-value">{{ formatDate(contract.startDate) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Fin période d'essai:</span>
                <span class="detail-value">{{ formatDate(contract.trialEndDate) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Salaire:</span>
                <span class="detail-value">{{ formatCurrency(contract.salary) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Manager:</span>
                <span class="detail-value">{{ contract.manager }}</span>
              </div>
            </div>

            <div class="progress-section" v-if="contract.status === 'active'">
              <div class="progress-info">
                <span class="progress-label">Progression de la période d'essai:</span>
                <span class="progress-text">{{ getTrialProgress(contract) }}%</span>
              </div>
              <div class="progress-bar">
                <div 
                  class="progress-fill" 
                  :style="{ width: getTrialProgress(contract) + '%' }"
                  :class="getProgressClass(contract)"
                ></div>
              </div>
              <div class="time-remaining">
                {{ getTimeRemaining(contract) }}
              </div>
            </div>

            <div class="contract-timeline" v-if="contract.timeline">
              <h4>Historique:</h4>
              <div class="timeline-list">
                <div 
                  v-for="event in contract.timeline" 
                  :key="event.id"
                  class="timeline-item"
                >
                  <div class="timeline-date">{{ formatDate(event.date) }}</div>
                  <div class="timeline-event">{{ event.description }}</div>
                </div>
              </div>
            </div>
          </div>

          <div class="contract-actions">
            <button @click="viewContract(contract)" class="btn btn-secondary btn-sm">
              👁️ Voir détails
            </button>
            <button 
              @click="downloadContract(contract)" 
              class="btn btn-primary btn-sm"
              v-if="contract.status !== 'draft'"
            >
              📄 Télécharger
            </button>
            <button 
              @click="editContract(contract)" 
              class="btn btn-secondary btn-sm"
              v-if="contract.status === 'draft'"
            >
              ✏️ Modifier
            </button>
            <button 
              @click="sendForSignature(contract)" 
              class="btn btn-success btn-sm"
              v-if="contract.status === 'draft'"
            >
              ✉️ Envoyer pour signature
            </button>
            <button 
              @click="confirmEmployee(contract)" 
              class="btn btn-success btn-sm"
              v-if="contract.status === 'active' && isTrialEndingSoon(contract)"
            >
              ✅ Confirmer l'employé
            </button>
            <button 
              @click="terminateContract(contract)" 
              class="btn btn-danger btn-sm"
              v-if="contract.status === 'active'"
            >
              ❌ Résilier
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="filteredContracts.length === 0" class="empty-state">
      <div class="empty-icon">📄</div>
      <h3>Aucun contrat trouvé</h3>
      <p>{{ searchQuery ? 'Aucun contrat ne correspond à votre recherche.' : 'Aucun contrat n\'a encore été créé.' }}</p>
      <router-link to="/contracts/create" class="btn btn-primary">
        ➕ Créer le premier contrat
      </router-link>
    </div>

    <!-- Modal de détails -->
    <div v-if="showDetailsModal" class="modal-overlay" @click="showDetailsModal = false">
      <div class="modal details-modal" @click.stop>
        <div class="modal-header">
          <h3>Détails du contrat: {{ selectedContract?.employeeName }}</h3>
          <button @click="showDetailsModal = false" class="modal-close">×</button>
        </div>
        
        <div class="modal-content" v-if="selectedContract">
          <div class="contract-info">
            <div class="info-section">
              <h4>Informations de l'employé</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">Nom:</span>
                  <span>{{ selectedContract.employeeName }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Email:</span>
                  <span>{{ selectedContract.email }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Téléphone:</span>
                  <span>{{ selectedContract.phone }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Adresse:</span>
                  <span>{{ selectedContract.address }}</span>
                </div>
              </div>
            </div>

            <div class="info-section">
              <h4>Détails du contrat</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">Poste:</span>
                  <span>{{ selectedContract.position }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Département:</span>
                  <span>{{ selectedContract.department }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Manager:</span>
                  <span>{{ selectedContract.manager }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Type:</span>
                  <span>{{ selectedContract.contractType }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Lieu de travail:</span>
                  <span>{{ selectedContract.workLocation }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Temps de travail:</span>
                  <span>{{ selectedContract.workingTime }}</span>
                </div>
              </div>
            </div>

            <div class="info-section">
              <h4>Rémunération et avantages</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">Salaire de base:</span>
                  <span>{{ formatCurrency(selectedContract.salary) }}</span>
                </div>
                <div class="info-item" v-if="selectedContract.bonus">
                  <span class="info-label">Prime variable:</span>
                  <span>{{ formatCurrency(selectedContract.bonus) }}</span>
                </div>
              </div>
              <div v-if="selectedContract.benefits && selectedContract.benefits.length > 0" class="benefits-section">
                <h5>Avantages:</h5>
                <ul>
                  <li v-for="benefit in selectedContract.benefits" :key="benefit">{{ benefit }}</li>
                </ul>
              </div>
            </div>

            <div class="info-section">
              <h4>Dates importantes</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">Date de début:</span>
                  <span>{{ formatDate(selectedContract.startDate) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Fin période d'essai:</span>
                  <span>{{ formatDate(selectedContract.trialEndDate) }}</span>
                </div>
                <div class="info-item" v-if="selectedContract.endDate">
                  <span class="info-label">Date de fin:</span>
                  <span>{{ formatDate(selectedContract.endDate) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Statut:</span>
                  <span class="status-badge" :class="`status-${selectedContract.status}`">
                    {{ getStatusLabel(selectedContract.status) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="showDetailsModal = false" class="btn btn-secondary">
            Fermer
          </button>
          <button @click="downloadContract(selectedContract)" class="btn btn-primary">
            📄 Télécharger contrat
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContractsView',
  data() {
    return {
      searchQuery: '',
      selectedStatus: '',
      selectedDepartmentId: '',
      selectedContractType: '',
      selectedPeriod: '',
      showDetailsModal: false,
      selectedContract: null,
      contracts: [],
      loading: false,
      error: null,
      departments: [],
      contractTypes: [],
      debounceHandle: null
    }
  },
  async mounted() {
    await Promise.all([
      this.loadDepartments(),
      this.loadContractTypes()
    ])
    await this.applyFilters()
  },
  computed: {
    filteredContracts() {
      // Données déjà filtrées par le backend
      return [...this.contracts].sort((a, b) => new Date(b.startDate) - new Date(a.startDate))
    },
    totalContracts() {
      return this.contracts.length
    },
    activeContracts() {
      return this.contracts.filter(c => c.status === 'active').length
    },
    endingSoonContracts() {
      const now = new Date()
      return this.contracts.filter(contract => {
        if (contract.status !== 'active') return false
        const trialEndDate = new Date(contract.trialEndDate)
        const daysUntilEnd = Math.ceil((trialEndDate - now) / (1000 * 60 * 60 * 24))
        return daysUntilEnd <= 30 && daysUntilEnd > 0
      }).length
    },
    successRate() {
      const completedContracts = this.contracts.filter(c => c.status === 'completed').length
      const totalFinishedTrials = this.contracts.filter(c => 
        c.status === 'completed' || c.status === 'cancelled'
      ).length
      
      if (totalFinishedTrials === 0) return 0
      return Math.round((completedContracts / totalFinishedTrials) * 100)
    }
  },
  methods: {
    async loadDepartments() {
      try {
        const res = await fetch('/api/departements')
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        this.departments = await res.json()
      } catch (e) {
        console.error('Erreur chargement départements:', e)
      }
    },
    async loadContractTypes() {
      try {
        const res = await fetch('/api/contrats/types')
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        this.contractTypes = await res.json()
      } catch (e) {
        console.error('Erreur chargement types de contrat:', e)
      }
    },
    async applyFilters() {
      try {
        this.loading = true
        this.error = null
        const params = new URLSearchParams()
        if (this.searchQuery) params.append('search', this.searchQuery)
        if (this.selectedStatus) params.append('status', this.selectedStatus)
        if (this.selectedDepartmentId) params.append('departementId', this.selectedDepartmentId)
        if (this.selectedContractType) params.append('typeContrat', this.selectedContractType)
        const url = params.toString() ? `/api/contrats/filter?${params.toString()}` : '/api/contrats'
        const res = await fetch(url)
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        const now = new Date()
        this.contracts = (data || []).map(c => {
          const employeeName = c.candidat ? `${c.candidat.prenom || ''} ${c.candidat.nom || ''}`.trim() : (c.idemploye ? `${c.idemploye.prenom || ''} ${c.idemploye.nom || ''}`.trim() : 'Employé')
          const startDate = c.startDate || c.datedebut
          const endDate = c.endDate || (c.datedebut && c.nombremois ? new Date(c.datedebut) : null)
          const trialEndDate = c.endDate || (c.datedebut && c.nombremois ? new Date(new Date(c.datedebut).setMonth(new Date(c.datedebut).getMonth() + (c.nombremois || 0))).toISOString().split('T')[0] : c.endDate)
          // status affiché (dérivé simple)
          let status = 'active'
          if (c.endDate) {
            const end = new Date(c.endDate)
            status = end < now ? 'completed' : 'active'
          }
          return {
            id: c.id,
            employeeName,
            email: c.candidat?.idcomptecandidat?.email || c.candidat?.email || c.idemploye?.email || '',
            phone: c.candidat?.telephone || c.idemploye?.telephone || '',
            address: c.candidat?.adresse || c.idemploye?.adresse || '',
            position: c.poste || '',
            department: c.idemploye?.iddept?.nom || '',
            manager: c.manager || '',
            contractType: c.typecontrat || 'Contrat d\'essai',
            workLocation: c.lieu || '',
            workingTime: c.temps || 'Temps plein',
            salary: c.salaire || 0,
            bonus: c.bonus || null,
            benefits: c.benefits || [],
            startDate,
            trialEndDate: c.endDate || trialEndDate,
            endDate: c.endDate || null,
            status,
            timeline: []
          }
        })
      } catch (e) {
        console.error('Erreur filtrage contrats:', e)
        this.error = 'Impossible de charger les contrats.'
      } finally {
        this.loading = false
      }
    },
    debouncedApplyFilters() {
      if (this.debounceHandle) clearTimeout(this.debounceHandle)
      this.debounceHandle = setTimeout(() => this.applyFilters(), 400)
    },
    async loadContracts() {
      try {
        this.loading = true
        this.error = null
        const res = await fetch('/api/contrats')
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        const now = new Date()
        this.contracts = (data || []).map(c => {
          const employeeName = c.candidat ? `${c.candidat.prenom || ''} ${c.candidat.nom || ''}`.trim() : (c.idemploye ? `${c.idemploye.prenom || ''} ${c.idemploye.nom || ''}`.trim() : 'Employé')
          const startDate = c.startDate || c.datedebut
          const trialEndDate = c.endDate || (c.datedebut && c.nombremois ? new Date(c.datedebut).toISOString().split('T')[0] : c.endDate)
          // Derive simple status from dates (fallback)
          let status = 'active'
          if (c.endDate) {
            const end = new Date(c.endDate)
            status = end < now ? 'completed' : 'active'
          }
          return {
            id: c.id,
            employeeName,
            email: c.candidat?.idcomptecandidat?.email || c.candidat?.email || c.idemploye?.email || '',
            phone: c.candidat?.telephone || c.idemploye?.telephone || '',
            address: c.candidat?.adresse || c.idemploye?.adresse || '',
            position: c.poste || '',
            department: c.idemploye?.departement?.nom || '',
            manager: c.manager || '',
            contractType: c.typecontrat || 'Contrat d\'essai',
            workLocation: c.lieu || '',
            workingTime: c.temps || 'Temps plein',
            salary: c.salaire || 0,
            bonus: c.bonus || null,
            benefits: c.benefits || [],
            startDate,
            trialEndDate: c.endDate || trialEndDate,
            endDate: c.endDate || null,
            status,
            timeline: []
          }
        })
      } catch (e) {
        console.error('Erreur chargement contrats:', e)
        this.error = 'Impossible de charger les contrats.'
      } finally {
        this.loading = false
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR')
    },
    formatCurrency(amount) {
      if (!amount) return '0 €'
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 0
      }).format(amount)
    },
    getStatusLabel(status) {
      const labels = {
        draft: '📝 Brouillon',
        pending: '⏳ En attente',
        signed: '✍️ Signé',
        active: '✅ Actif',
        completed: '🎉 Terminé',
        cancelled: '❌ Annulé'
      }
      return labels[status] || status
    },
    getTrialProgress(contract) {
      const now = new Date()
      const startDate = new Date(contract.startDate)
      const endDate = new Date(contract.trialEndDate)
      
      const totalDays = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24))
      const elapsedDays = Math.ceil((now - startDate) / (1000 * 60 * 60 * 24))
      
      return Math.min(Math.max(Math.round((elapsedDays / totalDays) * 100), 0), 100)
    },
    getProgressClass(contract) {
      const progress = this.getTrialProgress(contract)
      if (progress >= 80) return 'ending-soon'
      if (progress >= 60) return 'warning'
      return 'normal'
    },
    getTimeRemaining(contract) {
      const now = new Date()
      const endDate = new Date(contract.trialEndDate)
      const daysRemaining = Math.ceil((endDate - now) / (1000 * 60 * 60 * 24))
      
      if (daysRemaining < 0) return 'Période d\'essai terminée'
      if (daysRemaining === 0) return 'Se termine aujourd\'hui'
      if (daysRemaining === 1) return 'Se termine demain'
      return `${daysRemaining} jours restants`
    },
    isTrialEndingSoon(contract) {
      const now = new Date()
      const endDate = new Date(contract.trialEndDate)
      const daysRemaining = Math.ceil((endDate - now) / (1000 * 60 * 60 * 24))
      return daysRemaining <= 30 && daysRemaining > 0
    },
    viewContract(contract) {
      // Ouvrir la page de détails du contrat avec export PDF
      this.$router.push(`/contracts/view/${contract.id}`)
    },
    editContract(contract) {
      this.$router.push(`/contracts/edit/${contract.id}`)
    },
    downloadContract(contract) {
      // Simulation de téléchargement
      alert(`Téléchargement du contrat de ${contract.employeeName}...`)
    },
    sendForSignature(contract) {
      // Simulation d'envoi
      contract.status = 'pending'
      contract.timeline.push({
        id: Date.now(),
        date: new Date().toISOString().split('T')[0],
        description: 'Envoyé pour signature'
      })
      alert(`Contrat envoyé à ${contract.employeeName} pour signature`)
    },
    confirmEmployee(contract) {
      if (confirm(`Confirmer l'employé ${contract.employeeName} après sa période d'essai ?`)) {
        contract.status = 'completed'
        contract.timeline.push({
          id: Date.now(),
          date: new Date().toISOString().split('T')[0],
          description: 'Période d\'essai terminée avec succès'
        })
        alert(`${contract.employeeName} a été confirmé(e) avec succès !`)
      }
    },
    terminateContract(contract) {
      if (confirm(`Êtes-vous sûr de vouloir résilier le contrat de ${contract.employeeName} ?`)) {
        contract.status = 'cancelled'
        contract.timeline.push({
          id: Date.now(),
          date: new Date().toISOString().split('T')[0],
          description: 'Contrat résilié'
        })
        alert(`Contrat de ${contract.employeeName} résilié`)
      }
    },
    exportContracts() {
      // Simulation d'export
      alert('Export PDF des contrats en cours...')
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-actions {
  display: flex;
  gap: 10px;
}

.filters-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  max-width: 400px;
}

.filters {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-select {
  min-width: 180px;
}

.stats-section {
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  font-size: 32px;
  opacity: 0.8;
}

.stat-content h3 {
  margin: 0 0 5px 0;
  font-size: 28px;
  color: #2c3e50;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.contracts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contract-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  border-left: 4px solid #059669;
}

.contract-card.status-draft {
  border-left-color: #95a5a6;
}

.contract-card.status-pending {
  border-left-color: #f39c12;
}

.contract-card.status-signed {
  border-left-color: #059669;
}

.contract-card.status-active {
  border-left-color: #27ae60;
}

.contract-card.status-completed {
  border-left-color: #8e44ad;
}

.contract-card.status-cancelled {
  border-left-color: #e74c3c;
}

.contract-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.employee-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.employee-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #059669, #047857);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.employee-details h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 18px;
}

.employee-details p {
  margin: 0 0 3px 0;
  color: #666;
  font-size: 14px;
}

.position {
  color: #059669 !important;
  font-weight: 600 !important;
}

.contract-summary {
  text-align: center;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
  display: inline-block;
}

.status-badge.status-draft {
  background: #ecf0f1;
  color: #7f8c8d;
}

.status-badge.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.status-signed {
  background: #d1ecf1;
  color: #0c5460;
}

.status-badge.status-active {
  background: #d4edda;
  color: #155724;
}

.status-badge.status-completed {
  background: #e2d9f3;
  color: #6f42c1;
}

.status-badge.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.contract-type {
  font-size: 14px;
  color: #666;
  font-weight: 600;
}

.contract-details {
  margin-bottom: 20px;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-label {
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.detail-value {
  color: #333;
}

.progress-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.progress-label {
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.progress-text {
  font-weight: bold;
  color: #2c3e50;
}

.progress-bar {
  height: 12px;
  background: #ecf0f1;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.progress-fill.normal {
  background: linear-gradient(90deg, #27ae60, #2ecc71);
}

.progress-fill.warning {
  background: linear-gradient(90deg, #f39c12, #e67e22);
}

.progress-fill.ending-soon {
  background: linear-gradient(90deg, #e74c3c, #c0392b);
}

.time-remaining {
  text-align: center;
  font-size: 14px;
  color: #666;
  font-weight: 600;
}

.contract-timeline h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
}

.timeline-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.timeline-item {
  display: flex;
  gap: 15px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 14px;
}

.timeline-date {
  font-weight: 600;
  color: #666;
  min-width: 100px;
}

.timeline-event {
  color: #555;
}

.contract-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-state p {
  color: #666;
  margin-bottom: 20px;
}

/* Modal styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.details-modal {
  max-width: 800px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 0;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  padding: 20px;
}

.contract-info {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.info-section h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-label {
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.benefits-section {
  margin-top: 15px;
}

.benefits-section h5 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 14px;
}

.benefits-section ul {
  margin: 0;
  padding-left: 20px;
}

.benefits-section li {
  margin-bottom: 5px;
  color: #555;
}

.modal-actions {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .page-actions {
    justify-content: center;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .contract-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .details-grid {
    grid-template-columns: 1fr;
  }
  
  .progress-info {
    flex-direction: column;
    gap: 5px;
    text-align: center;
  }
  
  .timeline-item {
    flex-direction: column;
    gap: 5px;
  }
  
  .timeline-date {
    min-width: auto;
  }
  
  .contract-actions {
    justify-content: center;
  }
  
  .details-modal {
    width: 95%;
    max-height: 90vh;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
