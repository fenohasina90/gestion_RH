<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Pointage du jour</h1>
      <div>
        <span class="text-center">Présents: {{ presentCount }} / {{ rows.length }}</span>
      </div>
    </div>

    <div class="card">
      <div class="flex gap-10 align-center" style="margin-bottom: 12px;">
        <input v-model="search" type="text" placeholder="Recherche nom ou matricule" class="form-input" style="max-width: 320px;" />
        <select v-model.number="department" class="form-input filter-select" style="max-width: 260px;">
          <option :value="0">Tous les départements</option>
          <option v-for="d in departements" :key="d.id" :value="d.id">{{ d.nom }}</option>
        </select>
        <div style="margin-left: auto;">
          <router-link to="/pointage/saisir" class="btn btn-primary">Pointer manuellement</router-link>
        </div>
      </div>

      <table class="table">
        <thead>
          <tr>
            <th>Employé</th>
            <th>Matricule</th>
            <th>Département</th>
            <th>Entrée</th>
            <th>Sortie</th>
            <th>Statut</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredRows" :key="row.emp.id">
            <td>
              <div class="flex align-center gap-10">
                <img v-if="row.emp.photo" :src="row.emp.photo" style="width:32px;height:32px;border-radius:8px;object-fit:cover;" />
                <div v-else style="width:32px;height:32px;border-radius:8px;background:#059669;color:#fff;display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:600;">
                  {{ initials(row.emp.prenom, row.emp.nom) }}
                </div>
                <div style="font-weight:600;">{{ row.emp.prenom }} {{ row.emp.nom }}</div>
              </div>
            </td>
            <td>{{ row.emp.matricule || '-' }}</td>
            <td>{{ row.emp.departement || '-' }}</td>
            <td>{{ row.firstIn || '-' }}</td>
            <td>{{ row.lastOut || '-' }}</td>
            <td>
              <span :class="statusClass(row.status)" style="padding:4px 10px;border-radius:12px;font-size:12px;">
                {{ row.statusLabel }}
              </span>
              <span v-if="row.late" style="margin-left:8px;font-size:12px;background:#fee2e2;color:#b91c1c;padding:2px 8px;border-radius:12px;">Retard</span>
            </td>
            <td>
              <router-link :to="`/pointage/detail/${row.emp.matricule}/${todayStr}`" class="btn btn-secondary">Détails</router-link>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TodayAttendanceView',
  data() {
    return {
      loading: false,
      timer: null,
      pointages: [],
      departements: [],
      department: 0,
      search: ''
    }
  },
  computed: {
    todayStr() {
      const d = new Date()
      return d.toISOString().slice(0,10)
    },
    rows() {
      // Map pointages to per-employee row with first in, last out, status, late
      const ENTR=1, SORT=2, PAUSE_DEB=3, PAUSE_FIN=4
      const byEmp = {}
      for (const p of this.pointages) {
        const emp = p.idemploye
        if (!byEmp[emp.id]) byEmp[emp.id] = { emp, entries: [], exits: [], pausesDeb: [], pausesFin: [] }
        if (p.idtypepointage?.id === ENTR) byEmp[emp.id].entries.push(p)
        if (p.idtypepointage?.id === SORT) byEmp[emp.id].exits.push(p)
        if (p.idtypepointage?.id === PAUSE_DEB) byEmp[emp.id].pausesDeb.push(p)
        if (p.idtypepointage?.id === PAUSE_FIN) byEmp[emp.id].pausesFin.push(p)
      }
      const rows = Object.values(byEmp).map(b => {
        const firstIn = b.entries.sort((a,b)=>a.dateheure.localeCompare(b.dateheure))[0]
        const lastOut = b.exits.sort((a,b)=>a.dateheure.localeCompare(b.dateheure)).slice(-1)[0]
        const status = lastOut ? 'PARTI' : (b.pausesDeb.length> b.pausesFin.length ? 'PAUSE' : (firstIn ? 'PRESENT' : 'ABSENT'))
        const statusLabel = status==='PRESENT'?'Présent': status==='PAUSE'?'En pause': status==='PARTI'?'Parti':'Absent'
        const late = firstIn ? new Date(firstIn.dateheure).toTimeString().slice(0,5) > '08:10' : false
        return {
          emp: {
            id: b.emp.id,
            nom: b.emp.nom,
            prenom: b.emp.prenom,
            photo: b.emp.photo,
            matricule: b.emp.matricule,
            departement: b.emp.iddept?.nom
          },
          firstIn: firstIn ? new Date(firstIn.dateheure).toTimeString().slice(0,5) : null,
          lastOut: lastOut ? new Date(lastOut.dateheure).toTimeString().slice(0,5) : null,
          status,
          statusLabel,
          late
        }
      })
      return rows
    },
    filteredRows() {
      return this.rows.filter(r => {
        const matchDept = this.department === 0 || r.emp.departementId === this.department || true
        const s = this.search.trim().toLowerCase()
        const matchSearch = !s || (r.emp.nom+ ' ' + r.emp.prenom).toLowerCase().includes(s) || (r.emp.matricule||'').toLowerCase().includes(s)
        return matchDept && matchSearch
      })
    },
    presentCount() {
      return this.rows.filter(r => r.status==='PRESENT').length
    }
  },
  methods: {
    initials(p, n){ return ((p||'')[0]||'') + ((n||'')[0]||'') },
    statusClass(s){
      // map to palette defined in brief but with inline bg via style attribute above; here keep class hook if needed later
      return s
    },
    async load() {
      this.loading = true
      try {
        const res = await fetch('/api/pointages/today')
        this.pointages = await res.json()
      } catch(e) { console.error(e) } finally { this.loading=false }
    }
  },
  mounted(){
    this.load()
    this.timer = setInterval(this.load, 30000)
  },
  beforeUnmount(){ if (this.timer) clearInterval(this.timer) }
}
</script>

<style scoped>
/* S'aligne sur le design global via style.css (page-header, card, table, btn, form-input) */
</style>
