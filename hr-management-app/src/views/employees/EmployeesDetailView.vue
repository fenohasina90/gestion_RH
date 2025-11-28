<template>
  <div class="employee-detail-container" v-if="contract">
    <div class="page-header">
      <div class="header-left">
        <h1>Détails de l'employé</h1>
        <p>Informations personnelles, contrat et statut</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-secondary" @click="$router.push('/employees')">← Retour à la liste</button>
      </div>
    </div>
    <!-- Tabs -->
    <div class="section-tabs">
      <button 
        @click="activeSection = 'personal'" 
        :class="['tab-button', { active: activeSection === 'personal' }]">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
        Informations personnelles
      </button>
      <button 
        @click="activeSection = 'contract'" 
        :class="['tab-button', { active: activeSection === 'contract' }]">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
          <polyline points="14,2 14,8 20,8"></polyline>
        </svg>
        Contrat
      </button>
      <button 
        @click="activeSection = 'status'" 
        :class="['tab-button', { active: activeSection === 'status' }]">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="22,12 18,12 15,21 9,3 6,12 2,12"></polyline>
        </svg>
        Statut
      </button>
    </div>

    <!-- Content -->
    <section class="card" v-if="activeSection === 'personal'">
      <h2 class="section-title">Informations personnelles</h2>
      <div class="identity-row">
        <div class="photo-box">
          <div class="photo-wrapper">
            <img v-if="contract.idemploye?.photo" :src="getPhotoUrl(contract.idemploye.photo)" alt="Photo employé">
            <div v-else class="initials">{{ getInitials(contract.idemploye?.nom, contract.idemploye?.prenom) }}</div>
          </div>
        </div>
        <div class="identity-info">
          <div class="name">{{ contract.idemploye?.prenom }} {{ contract.idemploye?.nom }}</div>
          <div class="dept">{{ contract.idemploye?.iddept?.nom || 'Département non assigné' }}</div>
        </div>
      </div>
      <div class="info-table">
        <div class="table-row"><div class="table-label">Adresse</div><div class="table-value">{{ contract.idemploye?.adresse || 'Non renseignée' }}</div></div>
        <div class="table-row"><div class="table-label">Email</div><div class="table-value">{{ contract.idemploye?.email || 'Non renseigné' }}</div></div>
        <div class="table-row"><div class="table-label">Téléphone</div><div class="table-value">{{ contract.idemploye?.telephone || 'Non renseigné' }}</div></div>
        <div class="table-row"><div class="table-label">Date de naissance</div><div class="table-value">{{ formatDate(contract.idemploye?.datenaissance) }}</div></div>
        <div class="table-row"><div class="table-label">Lieu de naissance</div><div class="table-value">{{ contract.idemploye?.lieunaissance || 'Non renseigné' }}</div></div>
        <div class="table-row"><div class="table-label">Nationalité</div><div class="table-value">{{ contract.idemploye?.nationalite || 'Non renseignée' }}</div></div>
        <div class="table-row"><div class="table-label">Situation familiale</div><div class="table-value">{{ contract.idemploye?.situationfamiliale || 'Non renseignée' }}</div></div>
        <div class="table-row"><div class="table-label">Nombre d'enfants</div><div class="table-value">{{ contract.idemploye?.nombreenfants ?? '0' }}</div></div>
        <div class="table-row"><div class="table-label">Numéro CNAPS</div><div class="table-value">{{ contract.idemploye?.numerocnaps || 'Non renseigné' }}</div></div>
        <div class="table-row"><div class="table-label">Numéro OSTIE</div><div class="table-value">{{ contract.idemploye?.numeroostie || 'Non renseigné' }}</div></div>
        <div class="table-row"><div class="table-label">Matricule</div><div class="table-value">{{ contract.idemploye?.matricule || 'Non renseigné' }}</div></div>
      </div>
    </section>

    <section class="card" v-if="activeSection === 'contract'">
      <div class="section-title-row">
        <h2 class="section-title">Contrat</h2>
        <span v-if="hasPreviousData" class="renew-badge">Renouvelé le {{ renewalDate }}</span>
      </div>
      <div class="section-actions">
        <button class="btn btn-primary" @click="toggleRenewForm">
          Renouveler
        </button>
      </div>
      <div class="info-table">
        <div class="table-row"><div class="table-label">Poste</div><div class="table-value">{{ contract.poste || 'Non défini' }}</div></div>
        <div class="table-row"><div class="table-label">Type de contrat</div><div class="table-value"><span class="contract-type" :class="getContractTypeClass(contract.typecontrat)">{{ contract.typecontrat || 'Non défini' }}</span></div></div>
        <div class="table-row"><div class="table-label">Salaire</div><div class="table-value salary">{{ formatSalaire(contract.salaire) }}</div></div>
        <div class="table-row"><div class="table-label">Date de début</div><div class="table-value">{{ formatDate(contract.datedebut) }}</div></div>
        <div class="table-row"><div class="table-label">Durée (mois)</div><div class="table-value">{{ contract.nombremois || 'Indéterminée' }}</div></div>
        <div class="table-row"><div class="table-label">Période d’essai (mois)</div><div class="table-value">{{ contract.periodessai != null ? contract.periodessai : 'Non définie' }}</div></div>
        <div class="table-row"><div class="table-label">Date de fin</div><div class="table-value">{{ getContractEndDate(contract) }}</div></div>
      </div>

      <h2 class="section-title" style="margin-top:20px;">Suivi du contrat de travail</h2>
      <div class="history-list" v-if="histories && histories.length">
        <div class="history-item" v-for="h in histories" :key="h.id">
          <div class="history-date">{{ new Date(h.datechangement).toLocaleString('fr-FR') }}</div>
          <div class="history-status">
            <span class="status-badge" :class="{'status-active': (h.idstatut?.nom||'').toLowerCase()==='actif', 'status-expired': (h.idstatut?.nom||'').toLowerCase()==='expiré'}">
              {{ h.idstatut?.nom || 'Statut inconnu' }}
            </span>
          </div>
          <div class="history-comment">{{ h.commentaire || '—' }}</div>
        </div>
      </div>
      <div v-else class="history-empty">Aucun événement de suivi pour ce contrat.</div>

      <!-- Le formulaire de renouvellement passe en modal -->
    </section>

    <section class="card" v-if="activeSection === 'status'">
      <h2 class="section-title">Statut</h2>
      <div class="info-table">
        <div class="table-row"><div class="table-label">Statut actuel</div><div class="table-value"><span class="status-badge" :class="getContractStatusClass(contract)">{{ getContractStatus(contract) }}</span></div></div>
        <div class="table-row full-width"><div class="table-label">Description</div><div class="table-value description">{{ getContractStatusDescription(contract) }}</div></div>
      </div>
    </section>
  </div>
  <div v-else class="loading"><div class="loading-spinner"></div><p>Chargement...</p>  </div>
  
  <!-- Modal Renouvellement -->
  <div v-if="showRenewForm" class="modal-overlay" @click.self="toggleRenewForm">
    <div class="modal-window">
      <div class="modal-header">
        <h3>Renouvellement du contrat</h3>
        <button class="icon-btn" @click="toggleRenewForm" aria-label="Fermer">✕</button>
      </div>
      <div class="modal-body">
        <div class="renew-grid">
          <div class="form-group">
            <label>Poste</label>
            <input v-model="renewForm.poste" type="text" />
          </div>
          <div class="form-group">
            <label>Salaire (brut)</label>
            <input v-model.number="renewForm.salaire" type="number" min="0" step="0.01" />
          </div>
          <div class="form-group">
            <label>Type de contrat</label>
            <select v-model="renewForm.typecontrat">
              <option value="">-- Sélectionner --</option>
              <option v-for="t in typecontrats" :key="t.id" :value="t.libelle">{{ t.libelle }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>Statut du contrat</label>
            <select v-model="renewForm.statutId">
              <option value="">-- Sélectionner --</option>
              <option v-for="s in statuts" :key="s.id" :value="s.id">{{ s.nom }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>Période d’essai (mois)</label>
            <input v-model.number="renewForm.periodessai" type="number" min="0" />
          </div>
          <div class="form-group" v-if="renewShouldShowDuration">
            <label>Durée (mois)</label>
            <input v-model.number="renewForm.nombremois" type="number" min="1" />
          </div>
          <div class="form-group">
            <label>Date de début</label>
            <input v-model="renewForm.datedebut" type="date" />
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="toggleRenewForm">Annuler</button>
        <button class="btn btn-primary" @click="submitRenew" :disabled="renewSubmitting">{{ renewSubmitting ? 'Enregistrement...' : 'Valider le renouvellement' }}</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'EmployeesDetailView',
  data() {
    return {
      contract: null,
      activeSection: 'personal',
      histories: [],
      previousContracts: [],
      // Renouvellement
      showRenewForm: false,
      renewSubmitting: false,
      renewForm: {
        poste: '',
        salaire: null,
        typecontrat: '',
        statutId: '',
        periodessai: null,
        nombremois: null,
        datedebut: ''
      },
    async loadEmployeeContracts() {
      try {
        if (!this.contract?.idemploye?.id) return
        const empId = this.contract.idemploye.id
        const res = await axios.get(`/api/employes/${empId}/contrats`)
        this.previousContracts = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement contrats précédents', e)
        this.previousContracts = []
      }
    },
      typecontrats: [],
      statuts: [],
    }
  },
  async mounted() {
    await this.loadContract()
    await this.loadHistories()
    await Promise.all([this.loadTypecontrats(), this.loadStatuts()])
    this.initRenewDefaults()
    await this.loadEmployeeContracts()
  },
  methods: {
    async loadContract() {
      try {
        const id = this.$route.params.id
        const res = await axios.get(`/api/contrats/${id}`)
        this.contract = res.data
      } catch (e) {
        console.error('Erreur chargement contrat', e)
      }
    },
    async loadHistories() {
      try {
        const id = this.$route.params.id
        const res = await axios.get(`/api/contrats/${id}/historiques`)
        this.histories = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement historique du contrat', e)
        this.histories = []
      }
    },
    async loadTypecontrats() {
      try {
        const res = await axios.get('/api/typecontrats')
        this.typecontrats = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement types de contrat', e)
      }
    },
    async loadStatuts() {
      try {
        const res = await axios.get('/api/statutcontrats')
        this.statuts = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement statuts contrat', e)
      }
    },
    initRenewDefaults() {
      if (!this.contract) return
      // Préremplir le formulaire avec les valeurs actuelles
      this.renewForm.poste = this.contract.poste || ''
      this.renewForm.salaire = this.contract.salaire || null
      this.renewForm.typecontrat = this.contract.typecontrat || ''
      this.renewForm.periodessai = this.contract.periodessai || null
      this.renewForm.nombremois = this.contract.nombremois || null
      const actif = this.statuts.find(s => String(s.nom).toLowerCase() === 'actif')
      this.renewForm.statutId = actif ? actif.id : (this.statuts[0]?.id || '')
      // Date de début = date de fin du contrat courant si existante, sinon aujourd'hui
      const isoEnd = this.getContractEndDateISO(this.contract)
      const today = new Date().toISOString().slice(0, 10)
      this.renewForm.datedebut = isoEnd || today
    },
    getInitials(nom, prenom) {
      const n = nom ? nom.charAt(0).toUpperCase() : ''
      const p = prenom ? prenom.charAt(0).toUpperCase() : ''
      return n + p || '??'
    },
    getPhotoUrl(path) {
      if (!path) return null
      if (/^https?:\/\//i.test(path)) return path
      const normalized = path.startsWith('/') ? path : `/${path}`
      const base = import.meta.env.VITE_API_BASE_URL || ''
      return `${base}${normalized}`
    },
    formatSalaire(s) {
      if (!s) return 'Non renseigné'
      return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(s)
    },
    formatDate(d) {
      if (!d) return 'Non renseignée'
      return new Date(d).toLocaleDateString('fr-FR')
    },
    getContractTypeClass(type) {
      if (!type) return ''
      switch (type.toLowerCase()) {
        case 'cdi': return 'type-cdi'
        case 'cdd': return 'type-cdd'
        case 'stage': return 'type-stage'
        case 'freelance': return 'type-freelance'
        default: return 'type-other'
      }
    },
    getContractEndDate(employee) {
      if (!employee.datedebut || !employee.nombremois) return 'Non définie'
      const start = new Date(employee.datedebut)
      const end = new Date(start)
      end.setMonth(end.getMonth() + parseInt(employee.nombremois))
      return this.formatDate(end)
    },
    getContractEndDateISO(employee) {
      if (!employee?.datedebut || !employee?.nombremois) return ''
      const start = new Date(employee.datedebut)
      const end = new Date(start)
      end.setMonth(end.getMonth() + parseInt(employee.nombremois))
      return end.toISOString().slice(0, 10)
    },
    toggleRenewForm() {
      this.showRenewForm = !this.showRenewForm
      if (this.showRenewForm) this.initRenewDefaults()
    },
    async submitRenew() {
      if (!this.contract?.idemploye?.id) return
      this.renewSubmitting = true
      try {
        const body = {
          poste: this.renewForm.poste || null,
          salaire: this.renewForm.salaire != null ? Number(this.renewForm.salaire) : null,
          typecontratLibelle: this.renewForm.typecontrat || null,
          datedebut: this.renewForm.datedebut || null,
          nombremois: (this.renewShouldShowDuration && this.renewForm.nombremois) ? Number(this.renewForm.nombremois) : null,
          periodessai: this.renewForm.periodessai != null ? Number(this.renewForm.periodessai) : null,
          statutId: this.renewForm.statutId ? Number(this.renewForm.statutId) : null
        }
        const res = await axios.post(`/api/employes/${this.contract.idemploye.id}/contrat`, body)
        const newContrat = res?.data
        if (newContrat?.id) {
          this.$router.push(`/employees/contract/${newContrat.id}`)
        } else {
          // Recharge la page courante comme fallback
          await this.loadContract(); await this.loadHistories(); this.showRenewForm = false
        }
      } catch (e) {
        console.error('Erreur renouvellement contrat', e)
      } finally {
        this.renewSubmitting = false
      }
    },
    getContractStatus(employee) {
      if (!employee?.datedebut) return 'Statut inconnu'
      const today = new Date()
      const startDate = new Date(employee.datedebut)
      if (startDate > today) return 'À venir'
      if (employee.nombremois) {
        const endDate = new Date(startDate)
        endDate.setMonth(endDate.getMonth() + parseInt(employee.nombremois))
        return (today > endDate) ? 'Expiré' : 'Actif'
      }
      return 'Actif'
    },
    getContractStatusClass(employee) {
      const status = this.getContractStatus(employee)
      switch (status) {
        case 'Actif': return 'status-active'
        case 'À venir': return 'status-upcoming'
        case 'Expiré': return 'status-expired'
        default: return 'status-unknown'
      }
    },
    getContractStatusDescription(employee) {
      const status = this.getContractStatus(employee)
      switch (status) {
        case 'Actif':
          if (employee.nombremois) {
            const startDate = new Date(employee.datedebut)
            const endDate = new Date(startDate)
            endDate.setMonth(endDate.getMonth() + parseInt(employee.nombremois))
            const daysLeft = Math.ceil((endDate - new Date()) / (1000 * 60 * 60 * 24))
            return `Contrat actif. ${daysLeft > 0 ? `${daysLeft} jours restants.` : 'Expire bientôt.'}`
          }
          return 'Contrat à durée indéterminée en cours.'
        case 'À venir':
          const daysUntilStart = Math.ceil((new Date(employee.datedebut) - new Date()) / (1000 * 60 * 60 * 24))
          return `Le contrat commencera dans ${daysUntilStart} jours.`
        case 'Expiré':
          return 'Ce contrat a expiré et doit être renouvelé ou clôturé.'
        default:
          return 'Statut du contrat non déterminé.'
      }
    }
  },
  computed: {
    renewShouldShowDuration() {
      const t = (this.renewForm.typecontrat || '').toLowerCase()
      return t && t !== 'cdi'
    },
    hasPreviousData() {
      // previousContracts est trié DESC par datedebut, l'index 0 est le contrat courant
      return Array.isArray(this.previousContracts) && this.previousContracts.length > 1
    },
    renewalDate() {
      // Utiliser la date de fin du contrat précédent (index 1), sinon la date de début du contrat courant
      if (!Array.isArray(this.previousContracts) || this.previousContracts.length === 0) return ''
      const prev = this.previousContracts[1]
      if (prev?.datefin) return this.formatDate(prev.datefin)
      return this.formatDate(this.contract?.datedebut)
    }
  }
}
</script>

<style scoped>
.employee-detail-container { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.header-actions .btn { padding: 10px 14px; border-radius: 8px; }

.section-tabs { display: flex; gap: 8px; margin-bottom: 16px; }
.tab-button { display: inline-flex; align-items: center; gap: 8px; padding: 10px 14px; border-radius: 10px; background: #f3f4f6; color: #374151; border: none; cursor: pointer; font-weight: 600; transition: all 0.2s ease; }
.tab-button:hover { background: #e5e7eb; }
.tab-button.active { background: #dcfce7; color: #166534; box-shadow: 0 0 0 2px rgba(22,101,52,0.15) inset; }

.detail-grid { display: grid; grid-template-columns: 1.3fr 1fr; gap: 24px; }
.card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.section-title { margin: 0 0 16px 0; font-size: 18px; font-weight: 600; color: #1f2937; }
.section-title-row { display:flex; align-items:center; justify-content: space-between; gap: 12px; }
.renew-badge { background: #eef2ff; color: #3730a3; padding: 6px 10px; border-radius: 999px; font-weight: 600; font-size: 0.9rem; }

.identity-row { display: flex; align-items: center; gap: 16px; margin-bottom: 12px; }
.photo-box { width: 180px; }
.photo-wrapper { width: 160px; height: 160px; border-radius: 16px; overflow: hidden; background: #f3f4f6; display: flex; align-items: center; justify-content: center; }
.photo-wrapper img { width: 100%; height: 100%; object-fit: cover; }
.initials { font-size: 48px; font-weight: 700; color: #374151; }
.identity-info .name { font-size: 20px; font-weight: 700; }
.identity-info .dept { color: #6b7280; }

.info-table { display: grid; grid-template-columns: 220px 1fr; row-gap: 10px; }
.table-row { display: contents; }
.table-label { color: #6b7280; font-weight: 600; }
.table-value { color: #1f2937; }
.table-row.full-width { grid-column: 1 / -1; display: grid; grid-template-columns: 220px 1fr; }
.description { color: #374151; }

.contract-type { padding: 4px 12px; border-radius: 20px; font-size: 0.9rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
.type-cdi { background: #d4edda; color: #155724; }
.type-cdd { background: #fff3cd; color: #856404; }
.type-stage { background: #d1ecf1; color: #0c5460; }
.type-freelance { background: #f8d7da; color: #721c24; }
.type-other { background: #e2e3e5; color: #383d41; }

.status-badge { padding: 8px 16px; border-radius: 20px; font-weight: 600; }
.status-active { background: #dcfce7; color: #166534; }
.status-upcoming { background: #d1ecf1; color: #0c5460; }
.status-expired { background: #f8d7da; color: #721c24; }
.status-unknown { background: #e2e3e5; color: #383d41; }

.section-actions { display:flex; justify-content:flex-end; margin-bottom: 8px; }
.renew-form { margin-top: 16px; padding-top: 8px; border-top: 1px dashed #e5e7eb; }
.renew-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; margin-top: 8px; }
.renew-actions { display:flex; justify-content:flex-end; gap: 10px; margin-top: 12px; }

/* Modal */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 50; }
.modal-window { width: 100%; max-width: 820px; background: #fff; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); display: flex; flex-direction: column; overflow: hidden; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 14px 16px; border-bottom: 1px solid #e5e7eb; }
.modal-body { padding: 16px; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 12px 16px; border-top: 1px solid #e5e7eb; }
.icon-btn { background: transparent; border: none; font-size: 18px; cursor: pointer; color: #6b7280; }

.loading { text-align: center; padding: 60px 20px; color: #666; }
.loading-spinner { width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #007bff; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 20px; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>
