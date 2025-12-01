<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Paramètres de paie</h1>
      <p class="page-subtitle">Configurer les taux de cotisations, le barème IRSA, les coefficients d'heures supplémentaires et les éléments variables (primes, avances, absences).</p>
    </div>

    <div class="tabs">
      <button :class="['tab', activeTab==='cotisations' ? 'active' : '']" @click="activeTab='cotisations'">Cotisations & IRSA</button>
      <button :class="['tab', activeTab==='heures' ? 'active' : '']" @click="activeTab='heures'">Heures supplémentaires</button>
      <button :class="['tab', activeTab==='variables' ? 'active' : '']" @click="activeTab='variables'">Éléments variables</button>
    </div>

    <div v-if="activeTab==='cotisations'" class="card-grid">
      <div class="card">
        <h2 class="card-title">Cotisations CNAPS / OSTIE</h2>
        <table class="table">
          <thead>
            <tr>
              <th>Libellé</th>
              <th class="right">Taux (%)</th>
              <th class="right">Plafond</th>
              <th>Date effet</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in cotisations" :key="c.id">
              <td>{{ c.libelle }}</td>
              <td class="right">{{ fmtNumber(c.taux) }}</td>
              <td class="right">{{ money(c.plafondsalarial) }}</td>
              <td>{{ c.dateeffet }}</td>
            </tr>
          </tbody>
        </table>

        <div class="form-inline">
          <select v-model="formCot.libelle" class="form-input">
            <option disabled value="">Choisir...</option>
            <option value="CNAPS_SALARIE">CNAPS_SALARIE</option>
            <option value="CNAPS_EMPLOYEUR">CNAPS_EMPLOYEUR</option>
            <option value="OSTIE_SALARIE">OSTIE_SALARIE</option>
            <option value="OSTIE_EMPLOYEUR">OSTIE_EMPLOYEUR</option>
          </select>
          <input v-model.number="formCot.taux" type="number" step="0.01" class="form-input" placeholder="Taux %" />
          <input v-model.number="formCot.plafondsalarial" type="number" step="1" class="form-input" placeholder="Plafond" />
          <button class="btn btn-primary" :disabled="!formCot.libelle" @click="saveCotisation">Enregistrer</button>
        </div>
      </div>

      <div class="card">
        <h2 class="card-title">Barème IRSA</h2>
        <table class="table small">
          <thead>
            <tr>
              <th>Min</th>
              <th>Max</th>
              <th>Taux (%)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(b,idx) in irsaForm" :key="idx">
              <td><input v-model.number="b.borne_min" type="number" class="form-input" /></td>
              <td><input v-model.number="b.borne_max" type="number" class="form-input" /></td>
              <td><input v-model.number="b.taux" type="number" step="0.01" class="form-input" /></td>
            </tr>
          </tbody>
        </table>
        <div class="form-actions">
          <button class="btn" @click="loadIrsa">Recharger depuis base</button>
          <button class="btn btn-primary" @click="saveIrsa">Remplacer le barème</button>
        </div>
      </div>
    </div>

    <div v-else-if="activeTab==='heures'" class="card">
      <h2 class="card-title">Coefficients d'heures supplémentaires</h2>
      <table class="table">
        <thead>
          <tr>
            <th>Code</th>
            <th>Libellé</th>
            <th class="right">Coefficient</th>
            <th>Date effet</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="h in heuresSup" :key="h.id">
            <td>{{ h.code }}</td>
            <td>{{ h.libelle }}</td>
            <td class="right">{{ fmtNumber(h.coefficient) }}</td>
            <td>{{ h.dateeffet }}</td>
          </tr>
        </tbody>
      </table>

      <div class="form-inline">
        <input v-model="formHs.code" type="text" class="form-input" placeholder="Code (ex: HS_NORMALE)" />
        <input v-model="formHs.libelle" type="text" class="form-input" placeholder="Libellé" />
        <input v-model.number="formHs.coefficient" type="number" step="0.01" class="form-input" placeholder="Coefficient" />
        <button class="btn btn-primary" :disabled="!formHs.code" @click="saveHeuresSup">Enregistrer</button>
      </div>
    </div>

    <div v-else class="card-grid">
      <div class="card">
        <h2 class="card-title">Filtre & éléments variables</h2>
        <div class="filters">
          <div class="field">
            <label>Employé (ID)</label>
            <input v-model.number="filter.idemploye" type="number" class="form-input" placeholder="ID employé" />
          </div>
          <div class="field">
            <label>Mois</label>
            <input v-model.number="filter.mois" type="number" min="1" max="12" class="form-input" />
          </div>
          <div class="field">
            <label>Année</label>
            <input v-model.number="filter.annee" type="number" class="form-input" />
          </div>
          <div class="field actions">
            <button class="btn btn-primary" :disabled="!filter.idemploye || !filter.mois || !filter.annee" @click="loadElements">Charger</button>
          </div>
        </div>

        <table class="table">
          <thead>
            <tr>
              <th>Type</th>
              <th class="right">Montant</th>
              <th>Commentaire</th>
              <th>Date enreg.</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="e in elements" :key="e.id">
              <td>{{ e.libelle }}</td>
              <td class="right">{{ money(e.montant) }}</td>
              <td>{{ e.commentaire }}</td>
              <td>{{ e.dateenregistrement }}</td>
              <td class="right">
                <button class="btn btn-danger btn-xs" @click="deleteElement(e.id)">Supprimer</button>
              </td>
            </tr>
            <tr v-if="elements.length===0">
              <td colspan="5" class="muted">Aucun élément pour cette période.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card">
        <h2 class="card-title">Ajouter un élément variable</h2>
        <div class="form-vertical">
          <label>Type d'élément</label>
          <select v-model.number="formVar.idtypeelementpaie" class="form-input">
            <option disabled value="">Choisir...</option>
            <option v-for="t in typesElements" :key="t.id" :value="t.id">{{ t.libelle }}</option>
          </select>

          <label>Montant</label>
          <input v-model.number="formVar.montant" type="number" step="0.01" class="form-input" />

          <label>Commentaire</label>
          <textarea v-model="formVar.commentaire" class="form-input" rows="3"></textarea>

          <button class="btn btn-primary" :disabled="!canCreateElement" @click="createElement">Ajouter</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'PayrollSettingsView',
  data() {
    const now = new Date()
    return {
      activeTab: 'cotisations',
      cotisations: [],
      irsaForm: [],
      heuresSup: [],
      elements: [],
      typesElements: [],
      filter: {
        idemploye: null,
        mois: now.getMonth() + 1,
        annee: now.getFullYear()
      },
      formCot: {
        libelle: '',
        taux: null,
        plafondsalarial: null
      },
      formHs: {
        code: 'HS_NORMALE',
        libelle: 'Heures supplémentaires normales',
        coefficient: 1.3
      },
      formVar: {
        idtypeelementpaie: '',
        montant: null,
        commentaire: ''
      }
    }
  },
  computed: {
    canCreateElement() {
      return (
        this.filter.idemploye &&
        this.filter.mois &&
        this.filter.annee &&
        this.formVar.idtypeelementpaie &&
        this.formVar.montant !== null
      )
    }
  },
  methods: {
    async loadCotisations() {
      const res = await axios.get('/api/paie/parametres/cotisations')
      this.cotisations = res.data || []
    },
    async saveCotisation() {
      await axios.post('/api/paie/parametres/cotisations', this.formCot)
      await this.loadCotisations()
    },
    async loadIrsa() {
      const res = await axios.get('/api/paie/parametres/irsa')
      const data = res.data || []
      if (data.length > 0) {
        this.irsaForm = data.map(b => ({
          borne_min: Number(b.borne_min),
          borne_max: b.borne_max != null ? Number(b.borne_max) : null,
          taux: Number(b.taux)
        }))
      } else {
        this.irsaForm = [
          { borne_min: 0, borne_max: 350000, taux: 0 },
          { borne_min: 350001, borne_max: 400000, taux: 5 },
          { borne_min: 400001, borne_max: 500000, taux: 10 },
          { borne_min: 500001, borne_max: 600000, taux: 15 },
          { borne_min: 600001, borne_max: null, taux: 20 }
        ]
      }
    },
    async saveIrsa() {
      await axios.post('/api/paie/parametres/irsa', this.irsaForm)
      await this.loadIrsa()
    },
    async loadHeuresSup() {
      const res = await axios.get('/api/paie/parametres/heures-sup')
      this.heuresSup = res.data || []
    },
    async saveHeuresSup() {
      await axios.post('/api/paie/parametres/heures-sup', this.formHs)
      await this.loadHeuresSup()
    },
    async loadTypesElements() {
      const res = await axios.get('/api/paie/parametres/types-elements')
      this.typesElements = res.data || []
    },
    async loadElements() {
      const params = {
        idemploye: this.filter.idemploye,
        mois: this.filter.mois,
        annee: this.filter.annee
      }
      const res = await axios.get('/api/paie/parametres/elements', { params })
      this.elements = res.data || []
    },
    async createElement() {
      const payload = {
        idemploye: this.filter.idemploye,
        mois: this.filter.mois,
        annee: this.filter.annee,
        idtypeelementpaie: this.formVar.idtypeelementpaie,
        montant: this.formVar.montant,
        commentaire: this.formVar.commentaire
      }
      await axios.post('/api/paie/parametres/elements', payload)
      this.formVar.montant = null
      this.formVar.commentaire = ''
      await this.loadElements()
    },
    async deleteElement(id) {
      await axios.delete(`/api/paie/parametres/elements/${id}`)
      await this.loadElements()
    },
    money(v) {
      if (v == null) return '-'
      const n = Number(v)
      return n.toLocaleString('fr-FR', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    fmtNumber(v) {
      if (v == null) return '-'
      const n = Number(v)
      return n.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    }
  },
  async mounted() {
    await Promise.all([
      this.loadCotisations(),
      this.loadIrsa(),
      this.loadHeuresSup(),
      this.loadTypesElements()
    ])
  }
}
</script>

<style scoped>
.page-container {
  padding: 24px;
}

.page-header {
  margin-bottom: 16px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 4px;
}

.page-subtitle {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.tab {
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
}

.tab.active {
  background: #059669;
  color: #fff;
  border-color: #059669;
}

.card-grid {
  display: grid;
  grid-template-columns: 2fr 2fr;
  gap: 16px;
}

.card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 12px;
  font-size: 13px;
}

.table th,
.table td {
  padding: 6px 8px;
  border-bottom: 1px solid #e5e7eb;
}

.table th {
  text-align: left;
  font-weight: 600;
  background: #f9fafb;
}

.table.small th,
.table.small td {
  padding: 4px 6px;
}

.right {
  text-align: right;
}

.muted {
  color: #9ca3af;
  text-align: center;
}

.form-inline {
  display: flex;
  gap: 8px;
  align-items: center;
}

.form-input {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 6px 8px;
  font-size: 13px;
  width: 100%;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.filters {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  margin-bottom: 12px;
}

.field label {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.field.actions {
  display: flex;
  align-items: flex-end;
}

.form-vertical {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.btn {
  border-radius: 999px;
  padding: 6px 12px;
  border: 1px solid #d1d5db;
  background: #f9fafb;
  font-size: 13px;
  cursor: pointer;
}

.btn-primary {
  background: #059669;
  border-color: #059669;
  color: white;
}

.btn-danger {
  background: #dc2626;
  border-color: #b91c1c;
  color: white;
}

.btn-xs {
  padding: 2px 8px;
  font-size: 12px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
