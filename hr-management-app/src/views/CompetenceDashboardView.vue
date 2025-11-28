<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">Gestion des compétences</h1>
      <div class="filters">
        <div class="field">
          <label>Employé</label>
          <select v-model.number="selectedEmployeId" class="form-input">
            <option :value="null">Sélectionner...</option>
            <option v-for="e in employeOptions" :key="e.id" :value="e.id">
              {{ e.matricule }} - {{ e.nom }} {{ e.prenom }}
            </option>
          </select>
        </div>
        <div class="field">
          <label>Profil</label>
          <select v-model.number="selectedProfilId" class="form-input">
            <option :value="null">Sélectionner...</option>
            <option v-for="p in profils" :key="p.id" :value="p.id">
              {{ p.nom }}
            </option>
          </select>
        </div>
        <div class="actions">
          <button class="btn btn-primary" @click="reload" :disabled="!selectedEmployeId || !selectedProfilId">
            Actualiser
          </button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="status">Chargement des données de compétences...</div>

    <div v-else>
      <div class="summary-grid">
        <div class="summary-card">
          <div class="summary-label">Employé</div>
          <div class="summary-value">
            <span v-if="currentEmploye">{{ currentEmploye.matricule }} - {{ currentEmploye.nom }} {{ currentEmploye.prenom }}</span>
            <span v-else>Non sélectionné</span>
          </div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Profil cible</div>
          <div class="summary-value">
            <span v-if="currentProfil">{{ currentProfil.nom }}</span>
            <span v-else>Non sélectionné</span>
          </div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Compétences OK</div>
          <div class="summary-value highlight">{{ stats.ok }}</div>
          <div class="summary-sub">Niveau &ge; attendu</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">A renforcer / Manquantes</div>
          <div class="summary-value danger">{{ stats.aRenforcer + stats.critiques + stats.manquantes }}</div>
          <div class="summary-sub">Inclut critiques & manquantes</div>
        </div>
      </div>

      <div class="content-grid">
        <div class="table-card">
          <div class="table-title">Matching compétences employé / profil</div>
          <table class="table">
            <thead>
              <tr>
                <th>Compétence</th>
                <th>Niveau cible</th>
                <th>Niveau actuel</th>
                <th>Écart</th>
                <th>Statut</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="!matching || !matching.competences || matching.competences.length === 0">
                <td colspan="5">Sélectionner un employé et un profil puis cliquer sur Actualiser.</td>
              </tr>
              <tr v-for="c in matching.competences" :key="c.idcompetence">
                <td>
                  <div class="comp-label">{{ c.libelle }}</div>
                  <div class="comp-code">{{ c.code }}</div>
                </td>
                <td>
                  <span v-if="c.code_niveau_cible">
                    {{ c.code_niveau_cible }} - {{ c.libelle_niveau_cible }}
                  </span>
                  <span v-else>Non défini</span>
                </td>
                <td>
                  <span v-if="c.code_niveau_actuel">
                    {{ c.code_niveau_actuel }} - {{ c.libelle_niveau_actuel }}
                  </span>
                  <span v-else>Non évalué</span>
                </td>
                <td class="right">{{ c.ecart }}</td>
                <td>
                  <span
                    class="badge"
                    :class="{
                      green: c.statut === 'OK',
                      orange: c.statut === 'A renforcer',
                      red: c.statut === 'Critique' || c.statut === 'Manquante',
                      neutral: !['OK','A renforcer','Critique','Manquante'].includes(c.statut)
                    }"
                  >
                    {{ c.statut }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="table-card">
          <div class="table-title">Formations suggérées</div>
          <table class="table">
            <thead>
              <tr>
                <th>Formation</th>
                <th>Compétence ciblée</th>
                <th>Durée (h)</th>
                <th>Fournisseur</th>
                <th class="right">Coût</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="suggestions.length === 0">
                <td colspan="6">Aucune suggestion de formation pour l'instant.</td>
              </tr>
              <tr v-for="(f, idx) in suggestions" :key="idx">
                <td>
                  <div class="comp-label">{{ f.code_formation }} - {{ f.libelle_formation }}</div>
                </td>
                <td>
                  <div class="comp-label">{{ f.libelle_competence }}</div>
                  <div class="comp-code">{{ f.code_competence }}</div>
                </td>
                <td class="right">{{ fmtNum(f.duree_heures) }}</td>
                <td>{{ f.fournisseur }}</td>
                <td class="right">{{ fmtCurrency(f.cout) }}</td>
                <td>
                  <button
                    class="btn btn-primary"
                    style="padding:4px 8px; font-size:12px;"
                    @click="applyFormation(f)"
                    :disabled="!selectedEmployeId"
                  >
                    Appliquer
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CompetenceDashboardView',
  data() {
    return {
      loading: false,
      employes: [],
      profils: [],
      selectedEmployeId: null,
      selectedProfilId: null,
      matching: { competences: [] },
      suggestions: []
    }
  },
  computed: {
    currentEmploye() {
      return this.employeOptions.find(e => e.id === this.selectedEmployeId) || null
    },
    currentProfil() {
      return this.profils.find(p => p.id === this.selectedProfilId) || null
    },
    employeOptions() {
      // /api/employes renvoie une liste de Contrat incluant un objet idemploye
      const map = new Map()
      for (const c of this.employes || []) {
        const emp = c.idemploye
        if (!emp || !emp.id) continue
        if (!map.has(emp.id)) {
          map.set(emp.id, {
            id: emp.id,
            matricule: emp.matricule,
            nom: emp.nom,
            prenom: emp.prenom
          })
        }
      }
      return Array.from(map.values()).sort((a, b) => {
        return (a.nom || '').localeCompare(b.nom || '') || (a.prenom || '').localeCompare(b.prenom || '')
      })
    },
    stats() {
      const res = { ok: 0, aRenforcer: 0, critiques: 0, manquantes: 0 }
      if (!this.matching || !this.matching.competences) return res
      for (const c of this.matching.competences) {
        if (c.statut === 'OK') res.ok++
        else if (c.statut === 'A renforcer') res.aRenforcer++
        else if (c.statut === 'Critique') res.critiques++
        else if (c.statut === 'Manquante') res.manquantes++
      }
      return res
    }
  },
  methods: {
    fmtNum(v) {
      const n = Number(v || 0)
      return n.toLocaleString('fr-FR', { maximumFractionDigits: 1 })
    },
    fmtCurrency(v) {
      const n = Number(v || 0)
      if (!n) return '-'
      return n.toLocaleString('fr-FR', { style: 'currency', currency: 'MGA', maximumFractionDigits: 0 })
    },
    async loadEmployes() {
      try {
        const res = await fetch('/api/employes')
        this.employes = await res.json()
      } catch (e) {
        console.error(e)
      }
    },
    async loadProfils() {
      try {
        const res = await fetch('/api/profils')
        this.profils = await res.json()
      } catch (e) {
        console.error(e)
      }
    },
    async reload() {
      if (!this.selectedEmployeId || !this.selectedProfilId) return
      this.loading = true
      try {
        const [matchRes, suggRes] = await Promise.all([
          fetch(`/api/competences/matching?idemploye=${this.selectedEmployeId}&idprofil=${this.selectedProfilId}`),
          fetch(`/api/competences/suggestions-formation?idemploye=${this.selectedEmployeId}&idprofil=${this.selectedProfilId}`)
        ])
        this.matching = await matchRes.json()
        this.suggestions = await suggRes.json()
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    async applyFormation(f) {
      if (!this.selectedEmployeId || !f || !f.idformation) return
      this.loading = true
      try {
        await fetch(`/api/competences/attribuer-apres-formation?idemploye=${this.selectedEmployeId}&idformation=${f.idformation}`, {
          method: 'POST'
        })
        // Recharger matching + suggestions pour voir l'effet
        await this.reload()
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    }
  },
  async mounted() {
    await Promise.all([this.loadEmployes(), this.loadProfils()])
  }
}
</script>

<style scoped>
.dashboard { padding: 16px; }
.page-header { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:16px; }
.page-title { font-size:22px; font-weight:700; }
.filters { display:flex; gap:10px; flex-wrap:wrap; align-items:flex-end; }
.field { display:flex; flex-direction:column; min-width:220px; }
.actions { display:flex; gap:8px; }
.status { margin-top: 12px; }

.summary-grid { display:grid; grid-template-columns: repeat(4, minmax(0,1fr)); gap:12px; margin-bottom:16px; }
.summary-card { border:1px solid #e5e7eb; border-radius:8px; padding:10px 12px; background:#fff; }
.summary-label { font-size:13px; color:#6b7280; margin-bottom:4px; }
.summary-value { font-size:16px; font-weight:700; color:#111827; }
.summary-sub { font-size:12px; color:#6b7280; margin-top:4px; }
.summary-value.highlight { color:#166534; }
.summary-value.danger { color:#b91c1c; }

.content-grid { display:grid; grid-template-columns: 2fr 1.5fr; gap:16px; }
.table-card { border:1px solid #e5e7eb; border-radius:8px; background:#fff; overflow:hidden; }
.table-title { background:#f9fafb; padding:8px 12px; font-weight:600; border-bottom:1px solid #e5e7eb; }
.table { width:100%; border-collapse:collapse; font-size:13px; }
.table th, .table td { padding:6px 8px; border-bottom:1px solid #e5e7eb; }
.table thead th { background:#f9fafb; }
.right { text-align:right; }

.badge { font-size:11px; font-weight:600; padding:2px 8px; border-radius:999px; display:inline-block; }
.badge.green { background:#dcfce7; color:#166534; }
.badge.orange { background:#ffedd5; color:#c05621; }
.badge.red { background:#fee2e2; color:#b91c1c; }
.badge.neutral { background:#e5e7eb; color:#374151; }

.comp-label { font-weight:500; }
.comp-code { font-size:11px; color:#6b7280; }

@media (max-width: 1024px) {
  .summary-grid { grid-template-columns: repeat(2, minmax(0,1fr)); }
  .content-grid { grid-template-columns: 1fr; }
}

@media (max-width: 640px) {
  .summary-grid { grid-template-columns: 1fr; }
}
</style>
