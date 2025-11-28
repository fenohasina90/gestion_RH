<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Relevé de présence</h1>
    </div>

    <div class="card">
      <div class="flex gap-10 align-center" style="margin-bottom: 12px; flex-wrap: wrap;">
        <!-- Employee selector -->
        <div style="min-width:280px;flex:1;">
          <label class="form-label">Employé</label>
          <div class="employee-select">
            <input
              v-model="employeeSearch"
              type="text"
              class="form-input"
              placeholder="Rechercher nom ou matricule"
              @focus="showDropdown = true"
              @blur="onBlurHandler"
            />
            <div v-if="showDropdown" class="dropdown">
              <div v-if="filteredEmployees.length === 0" class="dropdown-item">Aucun employé trouvé</div>
              <div v-for="emp in filteredEmployees" :key="emp.id" class="dropdown-item" @mousedown="selectEmployee(emp)">
                <img v-if="emp.photo" :src="emp.photo" class="employee-photo" />
                <div v-else class="employee-initials">{{ (emp.prenom?.[0]||'') + (emp.nom?.[0]||'') }}</div>
                <div class="employee-info">
                  <div class="employee-name">{{ emp.prenom }} {{ emp.nom }}</div>
                  <div class="employee-matricule">{{ emp.matricule || 'Pas de matricule' }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Month / Year -->
        <div>
          <label class="form-label">Mois</label>
          <select v-model.number="mois" class="form-input" style="width:160px;">
            <option v-for="(m,i) in moisOptions" :key="i+1" :value="i+1">{{ m }}</option>
          </select>
        </div>
        <div>
          <label class="form-label">Année</label>
          <input v-model.number="annee" type="number" class="form-input" style="width:140px;" />
        </div>
        <div style="align-self:flex-end;">
          <button class="btn btn-primary" @click="loadTimesheet" :disabled="!selectedEmployee">Charger</button>
        </div>
      </div>

      <div v-if="loading">Chargement...</div>
      <div v-else-if="!selectedEmployee" class="text-muted">Veuillez sélectionner un employé.</div>
      <div v-else>
        <div class="flex gap-10" style="flex-wrap:wrap;">
          <div class="card" style="flex:1; min-width:220px;">
            <div style="font-weight:600;">Jours travaillés</div>
            <div style="font-size:22px;">{{ aggregates.joursTravailles }}</div>
          </div>
          <div class="card" style="flex:1; min-width:220px;">
            <div style="font-weight:600;">Heures supplémentaires</div>
            <div style="font-size:22px;">{{ aggregates.heuresSup }}</div>
          </div>
          <div class="card" style="flex:1; min-width:220px;">
            <div style="font-weight:600;">Absences</div>
            <div style="font-size:22px;">{{ aggregates.absences }}</div>
          </div>
          <div class="card" style="flex:1; min-width:220px;">
            <div style="font-weight:600;">Retards</div>
            <div style="font-size:22px;">{{ aggregates.retards }}</div>
          </div>
        </div>

        <div class="card" style="margin-top:12px;">
          <h2 style="margin-bottom:8px;">Détails du mois</h2>
          <table class="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Entrée</th>
                <th>Sortie</th>
                <th>Heures travaillées</th>
                <th>Heures sup</th>
                <th>Absent</th>
                <th>Commentaire</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="d in details" :key="d.date">
                <td>{{ d.date }}</td>
                <td>{{ d.entree || '-' }}</td>
                <td>{{ d.sortie || '-' }}</td>
                <td>{{ fmtNum(d.heuresTravaillees) }}</td>
                <td>{{ fmtNum(d.heuresSup) }}</td>
                <td>
                  <span :class="d.estAbsent ? 'badge badge-danger' : 'badge badge-success'">
                    {{ d.estAbsent ? 'Oui' : 'Non' }}
                  </span>
                </td>
                <td>{{ d.commentaire || '-' }}</td>
              </tr>
              <tr v-if="details.length === 0">
                <td colspan="7" class="text-center text-muted">Aucun enregistrement pour ce mois.</td>
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
  name: 'AttendanceReportView',
  data(){
    const now = new Date()
    return {
      employees: [],
      employeeSearch: '',
      selectedEmployee: null,
      showDropdown: false,
      mois: now.getMonth() + 1,
      annee: now.getFullYear(),
      moisOptions: ['Jan','Fév','Mar','Avr','Mai','Juin','Juil','Aoû','Sep','Oct','Nov','Déc'],
      loading: false,
      aggregates: { joursTravailles: '0.00', heuresSup: '0.00', absences: '0', retards: 0 },
      details: []
    }
  },
  computed: {
    filteredEmployees(){
      const search = this.employeeSearch.toLowerCase().trim().replace(/[-\s]/g,'')
      if (!search) return this.employees.slice(0,8)
      return this.employees.filter(emp => {
        const mat = (emp.matricule||'').toLowerCase().replace(/[-\s]/g,'')
        return emp.nom?.toLowerCase().includes(search) || emp.prenom?.toLowerCase().includes(search) || mat.includes(search)
      }).slice(0,8)
    }
  },
  methods: {
    async loadEmployees(){
      try{
        const res = await fetch('/api/employes')
        const contrats = await res.json()
        const map = new Map()
        contrats.forEach(c => {
          const e = c.idemploye
          if (e && !map.has(e.id)) {
            map.set(e.id, { id:e.id, nom:e.nom, prenom:e.prenom, matricule:e.matricule, photo:e.photo })
          }
        })
        this.employees = Array.from(map.values())
      }catch(e){ console.error(e) }
    },
    onBlurHandler(){ setTimeout(()=>{ this.showDropdown=false }, 200) },
    selectEmployee(emp){ this.selectedEmployee = emp; this.employeeSearch = `${emp.prenom} ${emp.nom}`; this.showDropdown=false; this.loadTimesheet() },
    fmtNum(v){ if (v==null) return '-'; const n = typeof v==='number'? v : parseFloat(v); return isNaN(n)? '-' : n.toFixed(2) },
    async loadTimesheet(){
      if (!this.selectedEmployee) return
      this.loading = true
      try{
        const url = `/api/feuilletemps/employee/${this.selectedEmployee.id}?mois=${this.mois}&annee=${this.annee}`
        const res = await fetch(url)
        const data = await res.json()
        this.aggregates.joursTravailles = this.fmtNum(data.joursTravailles)
        this.aggregates.heuresSup = this.fmtNum(data.heuresSupplementaires)
        this.aggregates.absences = (data.absences||0).toString()
        this.aggregates.retards = data.retards || 0
        this.details = (data.details||[]).map(d => ({
          date: d.date,
          entree: d.entree || '',
          sortie: d.sortie || '',
          heuresTravaillees: d.heuresTravaillees,
          heuresSup: d.heuresSup,
          estAbsent: !!d.estAbsent,
          commentaire: d.commentaire
        }))
      }catch(e){ console.error(e); alert('Erreur chargement relevé') }
      finally{ this.loading=false }
    }
  },
  mounted(){ this.loadEmployees() }
}
</script>

<style scoped>
.employee-select { position: relative; }
.dropdown {
  position:absolute; top:100%; left:0; right:0; max-height:300px; overflow:auto;
  background:#fff; border:1px solid #e5e7eb; border-radius:6px; box-shadow:0 4px 6px rgba(0,0,0,.1);
}
.dropdown-item { display:flex; align-items:center; gap:10px; padding:8px 12px; cursor:pointer; }
.dropdown-item:hover { background:#f3f4f6; }
.employee-photo { width:32px; height:32px; border-radius:50%; object-fit:cover; }
.employee-initials { width:32px; height:32px; border-radius:50%; background:#059669; color:#fff; display:flex; align-items:center; justify-content:center; font-weight:600; }
.badge { padding:2px 8px; border-radius:10px; font-size:12px; }
.badge-success { background:#ecfdf5; color:#065f46; }
.badge-danger { background:#fee2e2; color:#991b1b; }
</style>
