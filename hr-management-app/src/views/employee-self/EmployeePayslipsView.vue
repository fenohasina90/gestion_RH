<template>
  <div class="emp-layout">
    <EmployeeSidebar />
    <div class="emp-content">
      <div class="emp-container">
        <div class="emp-header">
          <h1 class="emp-title">Mes bulletins de paie</h1>
          <p class="emp-subtitle">Consultez et téléchargez vos documents de paie</p>
        </div>

        <div class="emp-search-card">
          <div class="emp-search-header">
            <h3 class="emp-search-title">Recherche par période</h3>
          </div>
          <div class="emp-search-filters">
            <div class="emp-filter-group">
              <label class="emp-filter-label">Année</label>
              <input v-model.number="annee" type="number" min="2000" :max="currentYear" class="emp-filter-input" />
            </div>
            <div class="emp-filter-group">
              <label class="emp-filter-label">Mois</label>
              <select v-model.number="mois" class="emp-filter-select">
                <option value="">Tous les mois</option>
                <option v-for="m in 12" :key="m" :value="m">{{ moisLabel(m) }}</option>
              </select>
            </div>
            <button @click="load" class="emp-search-button">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
              Rechercher
            </button>
          </div>
        </div>

        <div v-if="loading" class="emp-loading">
          <div class="emp-spinner"></div>
          <span>Chargement des bulletins...</span>
        </div>

        <div v-else>
          <div v-if="bulletins.length" class="emp-bulletins-grid">
            <div v-for="b in bulletins" :key="b.annee + '-' + b.mois" 
                 class="emp-bulletin-card" :class="{ 'emp-bulletin-selected': selectedPayslip === b }"
                 @click="selectPayslip(b)">
              <div class="emp-bulletin-header">
                <div class="emp-bulletin-period">
                  <span class="emp-bulletin-month">{{ moisLabel(b.mois) }}</span>
                  <span class="emp-bulletin-year">{{ b.annee }}</span>
                </div>
                <div class="emp-bulletin-status">
                  <span class="emp-status-badge">Disponible</span>
                </div>
              </div>
              <div class="emp-bulletin-amounts">
                <div class="emp-amount-group">
                  <span class="emp-amount-label">Brut</span>
                  <span class="emp-amount-value">{{ money(b.salaire_brut) }}</span>
                </div>
                <div class="emp-amount-group">
                  <span class="emp-amount-label">Net</span>
                  <span class="emp-amount-value emp-amount-net">{{ money(b.salaire_net) }}</span>
                </div>
              </div>
              <div class="emp-bulletin-actions">
                <button class="emp-action-button" @click.stop="selectPayslip(b)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                  Voir le détail
                </button>
              </div>
            </div>
          </div>
          <div v-else class="emp-empty-state">
            <div class="emp-empty-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
            </div>
            <h3 class="emp-empty-title">Aucun bulletin trouvé</h3>
            <p class="emp-empty-description">Aucun bulletin de paie ne correspond à votre recherche.</p>
          </div>

          <div v-if="selectedPayslip" class="emp-payslip-detail">
            <div class="emp-detail-header">
              <h2 class="emp-detail-title">Bulletin de paie détaillé</h2>
              <button class="emp-print-button" @click="printPayslip">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="6 9 6 2 18 2 18 9"></polyline>
                  <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                  <rect x="6" y="14" width="12" height="8"></rect>
                </svg>
                Imprimer
              </button>
            </div>
            
            <div class="emp-payslip-sheet">
              <div class="emp-sheet-header">
                <div class="emp-company-info">
                  <h3 class="emp-company-name">ENTREPRISE RH PRO</h3>
                  <p class="emp-company-address">123 Avenue des Entreprises, 75001 Paris</p>
                </div>
                <div class="emp-payslip-title">
                  <h1>BULLETIN DE PAIE</h1>
                  <div class="emp-payslip-period">{{ twoDigits(selectedPayslip.mois) }}/{{ selectedPayslip.annee }}</div>
                </div>
              </div>

              <div class="emp-parties-grid">
                <div class="emp-party-card">
                  <div class="emp-party-header">Employeur</div>
                  <div class="emp-party-body">
                    <div class="emp-party-name">Entreprise RH Pro</div>
                    <div class="emp-party-detail">Département: {{ selectedPayslip.departement || '-' }}</div>
                  </div>
                </div>
                <div class="emp-party-card">
                  <div class="emp-party-header">Salarié</div>
                  <div class="emp-party-body">
                    <div class="emp-party-name">{{ selectedPayslip.nom_complet }}</div>
                    <div class="emp-party-detail">Matricule: {{ selectedPayslip.matricule }}</div>
                    <div class="emp-party-detail">Catégorie: {{ selectedPayslip.categorie || '-' }}</div>
                  </div>
                </div>
              </div>

              <div class="emp-period-info">
                Période: {{ moisLabel(selectedPayslip.mois) }} {{ selectedPayslip.annee }}
              </div>

              <div class="emp-tables-grid">
                <div class="emp-table-section">
                  <div class="emp-table-title">Gains et rémunérations</div>
                  <table class="emp-data-table">
                    <thead>
                      <tr>
                        <th>Désignation</th>
                        <th class="emp-text-right">Base</th>
                        <th class="emp-text-right">Taux</th>
                        <th class="emp-text-right">Montant</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>Salaire de base</td>
                        <td class="emp-text-right">173,33 h</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.salaire_base) }}</td>
                      </tr>
                      <tr>
                        <td>Heures supplémentaires</td>
                        <td class="emp-text-right">{{ fmtHours(selectedPayslip.heures_supplementaires) }}</td>
                        <td class="emp-text-right">1,30</td>
                        <td class="emp-text-right">{{ money(selectedPayslip.montant_heures_sup) }}</td>
                      </tr>
                      <tr>
                        <td>Prime de rendement</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.prime_rendement) }}</td>
                      </tr>
                      <tr>
                        <td>Prime d'ancienneté</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.prime_anciennete) }}</td>
                      </tr>
                      <tr>
                        <td>Prime d'assiduité</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.prime_assiduite) }}</td>
                      </tr>
                      <tr>
                        <td>Indemnité transport</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.indemnite_transport) }}</td>
                      </tr>
                      <tr>
                        <td>Indemnité logement</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.indemnite_logement) }}</td>
                      </tr>
                    </tbody>
                    <tfoot>
                      <tr>
                        <th colspan="3">TOTAL BRUT</th>
                        <th class="emp-text-right">{{ money(selectedPayslip.total_gains || selectedPayslip.salaire_brut) }}</th>
                      </tr>
                    </tfoot>
                  </table>
                </div>

                <div class="emp-table-section">
                  <div class="emp-table-title">Retenues et cotisations</div>
                  <table class="emp-data-table">
                    <thead>
                      <tr>
                        <th>Désignation</th>
                        <th class="emp-text-right">Taux</th>
                        <th class="emp-text-right">Montant</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>Déduction retards</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.deduction_retards) }}</td>
                      </tr>
                      <tr>
                        <td>Déduction absences</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.deduction_absences) }}</td>
                      </tr>
                      <tr>
                        <td>CNAPS (part salarié)</td>
                        <td class="emp-text-right">{{ fmtRate(selectedPayslip.cnaps_sal_taux) }}</td>
                        <td class="emp-text-right">{{ money(selectedPayslip.cnaps_salarie) }}</td>
                      </tr>
                      <tr>
                        <td>OSTIE (part salarié)</td>
                        <td class="emp-text-right">{{ fmtRate(selectedPayslip.ostie_sal_taux) }}</td>
                        <td class="emp-text-right">{{ money(selectedPayslip.ostie_salarie) }}</td>
                      </tr>
                      <tr>
                        <td>IRSA</td>
                        <td class="emp-text-right"></td>
                        <td class="emp-text-right">{{ money(selectedPayslip.irsa) }}</td>
                      </tr>
                    </tbody>
                    <tfoot>
                      <tr>
                        <th colspan="2">TOTAL RETENUES</th>
                        <th class="emp-text-right">{{ money(selectedPayslip.total_retenues) }}</th>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>

              <div class="emp-summary-grid">
                <div class="emp-summary-card">
                  <div class="emp-summary-label">Salaire brut</div>
                  <div class="emp-summary-value">{{ money(selectedPayslip.salaire_brut) }}</div>
                </div>
                <div class="emp-summary-card emp-summary-primary">
                  <div class="emp-summary-label">Net à payer</div>
                  <div class="emp-summary-value">{{ money(selectedPayslip.salaire_net) }}</div>
                </div>
                <div class="emp-summary-card">
                  <div class="emp-summary-label">Coût employeur</div>
                  <div class="emp-summary-value">{{ money(selectedPayslip.cout_total_employeur) }}</div>
                </div>
              </div>
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
  name: 'EmployeePayslipsView',
  components: { EmployeeSidebar },
  data() {
    return {
      bulletins: [],
      loading: false,
      annee: new Date().getFullYear(),
      mois: null,
      selectedPayslip: null,
      currentYear: new Date().getFullYear()
    }
  },
  created() {
    const stored = localStorage.getItem('selfEmployee')
    if (!stored) {
      this.$router.push('/espace-employe/login')
      return
    }
    this.load()
  },
  methods: {
    async load() {
      const stored = localStorage.getItem('selfEmployee')
      if (!stored) {
        this.$router.push('/espace-employe/login')
        return
      }
      const emp = JSON.parse(stored)
      this.loading = true
      try {
        let url = `http://localhost:8080/api/employe/self/bulletins?idemploye=${emp.id}`
        if (this.annee && this.mois) {
          url += `&annee=${this.annee}&mois=${this.mois}`
        }
        const res = await fetch(url)
        if (!res.ok) throw new Error('Erreur serveur')
        this.bulletins = await res.json()
      } catch (e) {
        console.error(e)
        this.bulletins = []
      } finally {
        this.loading = false
      }
    },
    selectPayslip(b) {
      this.selectedPayslip = b
    },
    printPayslip() {
      window.print()
    },
    money(v) {
      const n = Number(v || 0)
      return n.toLocaleString('fr-FR', { style: 'currency', currency: 'MGA', maximumFractionDigits: 2 })
    },
    fmtRate(v) {
      if (v == null) return ''
      const n = Number(v)
      if (isNaN(n)) return ''
      return n.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %'
    },
    fmtHours(v) {
      const n = Number(v || 0)
      return n.toLocaleString('fr-FR', { maximumFractionDigits: 2 }) + ' h'
    },
    twoDigits(m) {
      return String(m).padStart(2, '0')
    },
    moisLabel(m) {
      const moisOptions = ['Janvier','Février','Mars','Avril','Mai','Juin','Juillet','Août','Septembre','Octobre','Novembre','Décembre']
      return moisOptions[m - 1] || m
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

.emp-search-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}

.emp-search-header {
  margin-bottom: 20px;
}

.emp-search-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.emp-search-filters {
  display: flex;
  align-items: end;
  gap: 16px;
  flex-wrap: wrap;
}

.emp-filter-group {
  flex: 1;
  min-width: 120px;
}

.emp-filter-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.emp-filter-input, .emp-filter-select {
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: #fafafa;
}

.emp-filter-input:focus, .emp-filter-select:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.emp-search-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.emp-search-button:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
}

.emp-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 60px 20px;
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

.emp-bulletins-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.emp-bulletin-card {
  background: #ffffff;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.emp-bulletin-card:hover {
  border-color: #10b981;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.1);
}

.emp-bulletin-selected {
  border-color: #10b981;
  background: #f0fdf4;
}

.emp-bulletin-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 16px;
}

.emp-bulletin-period {
  text-align: left;
}

.emp-bulletin-month {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.emp-bulletin-year {
  font-size: 14px;
  color: #6b7280;
}

.emp-bulletin-status {
  text-align: right;
}

.emp-status-badge {
  background: #dcfce7;
  color: #166534;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.emp-bulletin-amounts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.emp-amount-group {
  text-align: center;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.emp-amount-label {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.emp-amount-value {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.emp-amount-net {
  color: #059669;
}

.emp-bulletin-actions {
  text-align: center;
}

.emp-action-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emp-action-button:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.emp-empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #ffffff;
  border-radius: 16px;
  border: 2px dashed #e5e7eb;
}

.emp-empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.emp-empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.emp-empty-description {
  font-size: 14px;
  color: #6b7280;
  max-width: 300px;
  margin: 0 auto;
}

.emp-payslip-detail {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}

.emp-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.emp-detail-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.emp-print-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emp-print-button:hover {
  background: #e5e7eb;
}

.emp-payslip-sheet {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 32px;
}

.emp-sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e5e7eb;
}

.emp-company-info {
  text-align: left;
}

.emp-company-name {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.emp-company-address {
  font-size: 12px;
  color: #6b7280;
}

.emp-payslip-title {
  text-align: right;
}

.emp-payslip-title h1 {
  font-size: 24px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 4px;
}

.emp-payslip-period {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
}

.emp-parties-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 16px;
}

.emp-party-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.emp-party-header {
  background: #f3f4f6;
  padding: 8px 12px;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}

.emp-party-body {
  padding: 12px;
}

.emp-party-name {
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.emp-party-detail {
  font-size: 12px;
  color: #6b7280;
}

.emp-period-info {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 8px 12px;
  margin-bottom: 20px;
  font-weight: 600;
  color: #374151;
}

.emp-tables-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.emp-table-section {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.emp-table-title {
  background: #f3f4f6;
  padding: 10px 12px;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}

.emp-data-table {
  width: 100%;
  border-collapse: collapse;
}

.emp-data-table th,
.emp-data-table td {
  padding: 8px 12px;
  border-bottom: 1px solid #f3f4f6;
  font-size: 12px;
}

.emp-data-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  text-align: left;
}

.emp-data-table tfoot th {
  background: #f3f4f6;
  font-weight: 700;
}

.emp-text-right {
  text-align: right;
}

.emp-summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 24px;
}

.emp-summary-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.emp-summary-primary {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.emp-summary-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 8px;
}

.emp-summary-value {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.emp-summary-primary .emp-summary-value {
  color: #059669;
}

@keyframes emp-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .emp-container {
    padding: 24px 16px;
  }
  
  .emp-search-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .emp-bulletins-grid {
    grid-template-columns: 1fr;
  }
  
  .emp-tables-grid {
    grid-template-columns: 1fr;
  }
  
  .emp-summary-grid {
    grid-template-columns: 1fr;
  }
  
  .emp-parties-grid {
    grid-template-columns: 1fr;
  }
  
  .emp-payslip-sheet {
    padding: 20px 16px;
  }
  
  .emp-sheet-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .emp-company-info,
  .emp-payslip-title {
    text-align: center;
  }
}

@media print {
  .emp-layout,
  .emp-content,
  .emp-container {
    padding: 0;
    margin: 0;
    background: white;
  }
  
  .emp-header,
  .emp-search-card,
  .emp-bulletins-grid,
  .emp-detail-header {
    display: none;
  }
  
  .emp-payslip-detail {
    box-shadow: none;
    border: none;
    padding: 0;
  }
  
  .emp-payslip-sheet {
    border: none;
    padding: 0;
  }
}
</style>