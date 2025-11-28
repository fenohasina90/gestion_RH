<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">État de paie</h1>
      <div class="filters">
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
          <button class="btn btn-primary" @click="loadEtat">Charger</button>
          <button class="btn" :disabled="rows.length === 0" @click="exportPdf">Exporter PDF</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="status">Chargement...</div>
    <div v-else-if="rows.length === 0" class="status">Aucune donnée pour cette période.</div>

    <div v-else class="table-wrapper" ref="etatWrapper">
      <table class="etat-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Matricule</th>
            <th>N° CNAPS</th>
            <th>Nom et Prénoms</th>
            <th>Catégorie</th>
            <th>Fonction / Département</th>
            <th>Salaire de base</th>
            <th>Salaire brut</th>
            <th>CNAPS salarié</th>
            <th>OSTIE salarié</th>
            <th>IRSA</th>
            <th>Total retenues</th>
            <th>Salaire net</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in rows" :key="row.employe_id">
            <td>{{ today }}</td>
            <td>{{ row.matricule }}</td>
            <td>{{ row.numero_cnaps }}</td>
            <td>{{ row.nom_complet }}</td>
            <td>{{ row.categorie }}</td>
            <td>{{ row.departement }}</td>
            <td class="right">{{ money(row.salaire_base) }}</td>
            <td class="right">{{ money(row.salaire_brut) }}</td>
            <td class="right">{{ money(row.cnaps_salarie) }}</td>
            <td class="right">{{ money(row.ostie_salarie) }}</td>
            <td class="right">{{ money(row.irsa) }}</td>
            <td class="right">{{ money(row.total_retenues) }}</td>
            <td class="right">{{ money(row.salaire_net) }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td class="right"><strong>Total</strong></td>
            <td class="right"><strong>{{ money(totaux.salaire_base) }}</strong></td>
            <td class="right"><strong>{{ money(totaux.salaire_brut) }}</strong></td>
            <td class="right"><strong>{{ money(totaux.cnaps_salarie) }}</strong></td>
            <td class="right"><strong>{{ money(totaux.ostie_salarie) }}</strong></td>
            <td class="right"><strong>{{ money(totaux.irsa) }}</strong></td>
            <td class="right"><strong>{{ money(totaux.total_retenues) }}</strong></td>
            <td class="right"><strong>{{ money(totaux.salaire_net) }}</strong></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EtatPaieView',
  data(){
    const now = new Date()
    return {
      mois: now.getMonth() + 1,
      annee: now.getFullYear(),
      moisOptions: ['Jan','Fév','Mar','Avr','Mai','Juin','Juil','Aoû','Sep','Oct','Nov','Déc'],
      loading: false,
      rows: []
    }
  },
  computed: {
    today(){
      const d = new Date()
      return d.toLocaleDateString('fr-FR')
    },
    totaux(){
      const sum = (key) => this.rows.reduce((acc, r) => acc + Number(r[key] || 0), 0)
      return {
        salaire_base: sum('salaire_base'),
        salaire_brut: sum('salaire_brut'),
        cnaps_salarie: sum('cnaps_salarie'),
        ostie_salarie: sum('ostie_salarie'),
        irsa: sum('irsa'),
        total_retenues: sum('total_retenues'),
        salaire_net: sum('salaire_net')
      }
    }
  },
  methods: {
    money(v){
      const n = Number(v || 0)
      return n.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    async loadEtat(){
      this.loading = true
      try {
        const url = `/api/paie/etat?mois=${this.mois}&annee=${this.annee}`
        const res = await fetch(url)
        if (!res.ok) {
          this.rows = []
          console.error('Erreur API état paie', res.status)
          return
        }
        this.rows = await res.json()
      } catch (e) {
        console.error(e)
        this.rows = []
      } finally {
        this.loading = false
      }
    },
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
      if (this.rows.length === 0) return
      try {
        await this.ensureHtml2Pdf()
        const el = this.$refs.etatWrapper
        if (!el) return
        const filename = `etat_paie_${String(this.mois).padStart(2,'0')}-${this.annee}.pdf`
        const opt = {
          margin:       [5,5,5,5],
          filename,
          image:        { type: 'jpeg', quality: 0.98 },
          html2canvas:  { scale: 2, useCORS: true },
          jsPDF:        { unit: 'mm', format: 'a4', orientation: 'landscape' }
        }
        window.html2pdf().set(opt).from(el).save()
      } catch (e) {
        console.error(e)
        alert('Export PDF impossible. Réessayez.')
      }
    }
  },
  mounted(){
    this.loadEtat()
  }
}
</script>

<style scoped>
.page-header {
  display:flex;
  justify-content:space-between;
  align-items:flex-end;
  margin-bottom: 12px;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
}
.filters {
  display:flex;
  gap:10px;
  flex-wrap:wrap;
  align-items:flex-end;
}
.field {
  display:flex;
  flex-direction:column;
  min-width:160px;
}
.actions {
  display:flex;
  gap:8px;
}
.status {
  margin-top: 16px;
}
.table-wrapper {
  margin-top: 16px;
  overflow-x:auto;
}
.etat-table {
  width:100%;
  border-collapse:collapse;
  font-size: 13px;
}
.etat-table th,
.etat-table td {
  padding:6px 8px;
  border-bottom:1px solid #e5e7eb;
}
.etat-table thead th {
  background:#f9fafb;
  font-weight:600;
}
.right {
  text-align:right;
}
</style>
