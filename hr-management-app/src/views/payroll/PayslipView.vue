<template>
  <div>
    <div class="page-header no-print">
      <h1 class="page-title">Bulletin de paie</h1>
      <div class="filters">
        <div class="field">
          <label>Employé</label>
          <div class="employee-select">
            <input v-model="employeeSearch" type="text" class="form-input" placeholder="Rechercher nom ou matricule" @focus="showDropdown=true" @blur="onBlur" />
            <div v-if="showDropdown" class="dropdown">
              <div v-if="filteredEmployees.length===0" class="dropdown-item">Aucun employé</div>
              <div v-for="emp in filteredEmployees" :key="emp.id" class="dropdown-item" @mousedown="selectEmployee(emp)">
                <div class="employee-initials">{{ (emp.prenom?.[0]||'') + (emp.nom?.[0]||'') }}</div>
                <div class="employee-info">
                  <div class="employee-name">{{ emp.prenom }} {{ emp.nom }}</div>
                  <div class="employee-matricule">{{ emp.matricule }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="field">
          <label>Mois</label>
          <select v-model.number="mois" class="form-input">
            <option v-for="(m,i) in moisOptions" :key="i" :value="i+1">{{ m }}</option>
          </select>
        </div>
        <div class="field">
          <label>Année</label>
          <input v-model.number="annee" type="number" class="form-input" />
        </div>
        <div class="actions">
          <button class="btn btn-primary" :disabled="!selectedEmployee" @click="loadPayslip">Charger</button>
          <button class="btn btn-secondary" :disabled="!payslip" @click="printPayslip">Imprimer</button>
          <button class="btn" :disabled="!payslip" @click="exportPdf">Exporter PDF</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="no-print">Chargement...</div>
    <div v-else-if="!payslip" class="no-print">Sélectionnez un employé et chargez un bulletin.</div>

    <div v-else class="sheet A4">
      <div class="sheet-header">
        <div class="title">BULLETIN DE PAIE</div>
        <div class="period">{{ twoDigits(mois) }}/{{ payslip.annee }}</div>
      </div>

      <div class="party-grid">
        <div class="block">
          <div class="block-title">Employeur</div>
          <div class="block-body">
            <div>Entreprise</div>
            <div class="muted">Département: {{ payslip.departement || '-' }}</div>
          </div>
        </div>
        <div class="block">
          <div class="block-title">Salarié</div>
          <div class="block-body">
            <div class="emp-name">{{ payslip.nom_complet }}</div>
            <div class="muted">Matricule: {{ payslip.matricule }}</div>
            <div class="muted">Catégorie: {{ payslip.categorie || '-' }}</div>
          </div>
        </div>
      </div>

      <div class="period-row">
        <div>Période: {{ moisLabel(mois) }} {{ payslip.annee }}</div>
      </div>

      <div class="tables-grid">
        <div class="table-card">
          <div class="table-title">Gains</div>
          <table class="table">
            <thead>
              <tr>
                <th>Désignation</th>
                <th class="right">Base</th>
                <th class="right">Taux</th>
                <th class="right">Montant</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Salaire de base</td>
                <td class="right">173,33 h</td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.salaire_base) }}</td>
              </tr>
              <tr>
                <td>Heures supplémentaires</td>
                <td class="right">{{ fmtHours(payslip.heures_supplementaires) }}</td>
                <td class="right">1,30</td>
                <td class="right">{{ money(payslip.montant_heures_sup) }}</td>
              </tr>
              <tr>
                <td>Prime de rendement</td>
                <td class="right"></td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.prime_rendement) }}</td>
              </tr>
              <tr>
                <td>Prime d'ancienneté</td>
                <td class="right"></td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.prime_anciennete) }}</td>
              </tr>
              <tr>
                <td>Prime d'assiduité</td>
                <td class="right"></td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.prime_assiduite) }}</td>
              </tr>
              <tr>
                <td>Indemnité transport</td>
                <td class="right"></td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.indemnite_transport) }}</td>
              </tr>
              <tr>
                <td>Indemnité logement</td>
                <td class="right"></td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.indemnite_logement) }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <th colspan="3">TOTAL BRUT</th>
                <th class="right">{{ money(payslip.total_gains) }}</th>
              </tr>
            </tfoot>
          </table>
        </div>

        <div class="table-card">
          <div class="table-title">Retenues et cotisations (salarié)</div>
          <table class="table">
            <thead>
              <tr>
                <th>Désignation</th>
                <th class="right">Taux</th>
                <th class="right">Montant</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Déduction retards</td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.deduction_retards) }}</td>
              </tr>
              <tr>
                <td>Déduction absences</td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.deduction_absences) }}</td>
              </tr>
              <tr>
                <td>CNAPS (part salarié)</td>
                <td class="right">{{ fmtRate(payslip.cnaps_sal_taux) }}</td>
                <td class="right">{{ money(payslip.cnaps_salarie) }}</td>
              </tr>
              <tr>
                <td>OSTIE (part salarié)</td>
                <td class="right">{{ fmtRate(payslip.ostie_sal_taux) }}</td>
                <td class="right">{{ money(payslip.ostie_salarie) }}</td>
              </tr>
              <tr>
                <td>IRSA</td>
                <td class="right"></td>
                <td class="right">{{ money(payslip.irsa) }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <th colspan="2">TOTAL RETENUES</th>
                <th class="right">{{ money(payslip.total_retenues) }}</th>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>

      <div class="totals-row">
        <div class="kpi">
          <div class="kpi-label">Salaire brut</div>
          <div class="kpi-value">{{ money(payslip.salaire_brut) }}</div>
        </div>
        <div class="kpi">
          <div class="kpi-label">Net à payer</div>
          <div class="kpi-value accent">{{ money(payslip.salaire_net) }}</div>
        </div>
        <div class="kpi">
          <div class="kpi-label">Coût employeur</div>
          <div class="kpi-value">{{ money(payslip.cout_total_employeur) }}</div>
        </div>
      </div>

      <div class="employer-costs">
        <div class="table-card">
          <div class="table-title">Charges employeur (information)</div>
          <table class="table">
            <thead>
              <tr>
                <th>Désignation</th>
                <th class="right">Taux</th>
                <th class="right">Montant</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>CNAPS (part employeur)</td>
                <td class="right">{{ fmtRate(payslip.cnaps_emp_taux) }}</td>
                <td class="right">{{ money(payslip.cnaps_employeur) }}</td>
              </tr>
              <tr>
                <td>OSTIE (part employeur)</td>
                <td class="right">{{ fmtRate(payslip.ostie_emp_taux) }}</td>
                <td class="right">{{ money(payslip.ostie_employeur) }}</td>
              </tr>
              <tr>
                <th colspan="2">Coût total employeur</th>
                <th class="right">{{ money(payslip.cout_total_employeur) }}</th>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="footer-grid">
        <div class="tiny">
          Nb d'heures: {{ fmtNum(payslip.jours_travailles*8) }}
        </div>
        <div class="tiny">
          Minutes de retard: {{ payslip.minutes_retard_total }}
        </div>
        <div class="tiny">
          Heures sup: {{ fmtHours(payslip.heures_supplementaires) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PayslipView',
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
      payslip: null
    }
  },
  computed: {
    filteredEmployees(){
      const s = this.employeeSearch.toLowerCase().replace(/[-\s]/g,'').trim()
      if (!s) return this.employees.slice(0,8)
      return this.employees.filter(e =>
        (e.nom||'').toLowerCase().includes(s) ||
        (e.prenom||'').toLowerCase().includes(s) ||
        (e.matricule||'').toLowerCase().replace(/[-\s]/g,'').includes(s)
      ).slice(0,8)
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
          if (e && !map.has(e.id)) map.set(e.id, { id:e.id, nom:e.nom, prenom:e.prenom, matricule:e.matricule })
        })
        this.employees = Array.from(map.values())
      }catch(e){ console.error(e) }
    },
    onBlur(){ setTimeout(()=> this.showDropdown=false, 200) },
    selectEmployee(emp){ this.selectedEmployee = emp; this.employeeSearch = `${emp.prenom} ${emp.nom}`; this.showDropdown=false; this.loadPayslip() },
    money(v){
      const n = Number(v||0); return n.toLocaleString('fr-FR', { style: 'currency', currency: 'MGA', maximumFractionDigits: 2 })
    },
    fmtRate(v){ if (v==null) return ''; const n = Number(v); if (isNaN(n)) return ''; return (n).toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %' },
    fmtNum(v){ const n = Number(v||0); return n.toLocaleString('fr-FR', { maximumFractionDigits: 2 }) },
    fmtHours(v){ const n = Number(v||0); return n.toLocaleString('fr-FR', { maximumFractionDigits: 2 }) + ' h' },
    twoDigits(m){ return String(m).padStart(2,'0') },
    moisLabel(m){ return this.moisOptions[m-1] || m },
    async loadPayslip(){
      if (!this.selectedEmployee) return
      this.loading = true
      try{
        const url = `/api/paie/bulletin?matricule=${encodeURIComponent(this.selectedEmployee.matricule)}&mois=${this.mois}&annee=${this.annee}`
        const res = await fetch(url)
        if (res.status === 404) { this.payslip = null; alert('Aucun bulletin pour cette période'); return }
        this.payslip = await res.json()
      }catch(e){ console.error(e); alert('Erreur chargement bulletin') }
      finally{ this.loading = false }
    },
    printPayslip(){ window.print() }
    ,
    async ensureHtml2Pdf(){
      if (window.html2pdf) return
      await new Promise((resolve, reject) => {
        const s = document.createElement('script')
        s.src = 'https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js'
        s.onload = resolve
        s.onerror = reject
        document.body.appendChild(s)
      })
    },
    async exportPdf(){
      try{
        await this.ensureHtml2Pdf()
        const el = this.$el.querySelector('.sheet')
        if (!el) return
        const filename = `bulletin_${this.selectedEmployee?.matricule || 'employe'}_${this.twoDigits(this.mois)}-${this.annee}.pdf`
        const opt = {
          margin:       [5,5,5,5],
          filename,
          image:        { type: 'jpeg', quality: 0.98 },
          html2canvas:  { scale: 2, useCORS: true },
          jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
        }
        window.html2pdf().set(opt).from(el).save()
      }catch(e){ console.error(e); alert('Export PDF impossible. Réessayez.') }
    }
  },
  mounted(){ this.loadEmployees() }
}
</script>

<style scoped>
.no-print { }
.filters { display:flex; gap:10px; flex-wrap:wrap; margin:12px 0; align-items:flex-end; }
.field { display:flex; flex-direction:column; min-width:200px; }
.actions { display:flex; gap:8px; }
.employee-select { position:relative; }
.dropdown { position:absolute; top:100%; left:0; right:0; background:#fff; border:1px solid #e5e7eb; border-radius:6px; max-height:280px; overflow:auto; z-index:5; }
.dropdown-item { display:flex; gap:10px; padding:8px 12px; cursor:pointer; }
.dropdown-item:hover { background:#f3f4f6; }
.employee-initials { width:28px; height:28px; border-radius:50%; background:#059669; color:#fff; display:flex; align-items:center; justify-content:center; font-weight:600; }

/* Printable sheet */
.sheet { background:#fff; color:#111827; padding:18px; border:1px solid #e5e7eb; border-radius:8px; }
.A4 { width: 794px; max-width: 100%; margin: 0 auto 24px; }
.sheet-header { display:flex; justify-content:space-between; align-items:center; border-bottom:2px solid #e5e7eb; padding-bottom:8px; margin-bottom:8px; }
.title { font-weight:800; font-size:22px; letter-spacing: .5px; }
.period { font-weight:800; font-size:22px; color:#374151; }
.party-grid { display:grid; grid-template-columns:1fr 1fr; gap:12px; margin-bottom:8px; }
.block { border:1px solid #e5e7eb; border-radius:8px; }
.block-title { background:#f3f4f6; padding:6px 10px; font-weight:700; border-bottom:1px solid #e5e7eb; }
.block-body { padding:10px; line-height:1.4; }
.emp-name { font-weight:700; }
.muted { color:#6b7280; font-size:12px; }
.period-row { background:#f9fafb; border:1px solid #e5e7eb; border-radius:8px; padding:6px 10px; margin-bottom:8px; font-weight:600; }
.tables-grid { display:grid; grid-template-columns:1fr 1fr; gap:12px; }
.table-card { border:1px solid #e5e7eb; border-radius:8px; overflow:hidden; }
.table-title { background:#f3f4f6; padding:6px 10px; font-weight:700; border-bottom:1px solid #e5e7eb; }
.table { width:100%; border-collapse:collapse; }
.table th, .table td { padding:6px 8px; border-bottom:1px solid #f3f4f6; font-size:13px; }
.table tfoot th { background:#f9fafb; }
.right { text-align:right; }
.totals-row { display:grid; grid-template-columns: repeat(3, 1fr); gap:12px; margin:10px 0; }
.kpi { border:1px solid #e5e7eb; border-radius:8px; padding:8px 10px; }
.kpi-label { font-size:12px; color:#6b7280; }
.kpi-value { font-size:18px; font-weight:700; }
.kpi-value.accent { color:#065f46; }
.employer-costs { margin-top:8px; }
.footer-grid { display:grid; grid-template-columns: repeat(3, 1fr); gap:8px; margin-top:8px; color:#6b7280; font-size:12px; }

@media print {
  .no-print, .page-header { display:none !important; }
  .sheet { border: none; padding: 0; }
  .A4 { width: 100%; }
}
</style>
