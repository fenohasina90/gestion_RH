<template>
  <div class="add-employee-container">
    <div class="page-header">
      <h1>Ajouter un Employé</h1>
      <p>Renseignez les informations de l'employé puis validez.</p>
    </div>

    <form class="employee-form" @submit.prevent="submit">
      <div class="form-grid">
        <div class="form-group">
          <label>Prénom</label>
          <input v-model="form.prenom" type="text" required />
        </div>
        <div class="form-group">
          <label>Nom</label>
          <input v-model="form.nom" type="text" required />
        </div>
        <div class="form-group">
          <label>Email</label>
          <input v-model="form.email" type="email" />
        </div>
        <div class="form-group">
          <label>Téléphone</label>
          <input v-model="form.telephone" type="text" />
        </div>
        <div class="form-group">
          <label>Adresse</label>
          <input v-model="form.adresse" type="text" />
        </div>
        <div class="form-group">
          <label>Date de naissance</label>
          <input v-model="form.datenaissance" type="date" />
        </div>
        <div class="form-group">
          <label>CIN</label>
          <input v-model="form.cin" type="text" />
        </div>
        <div class="form-group">
          <label>Lieu de naissance</label>
          <input v-model="form.lieunaissance" type="text" />
        </div>
        <div class="form-group">
          <label>Nationalité</label>
          <input v-model="form.nationalite" type="text" />
        </div>
        <div class="form-group">
          <label>Situation familiale</label>
          <select v-model="form.situationfamiliale">
            <option value="">-- Sélectionner --</option>
            <option value="Célibataire">Célibataire</option>
            <option value="Marié(e)">Marié(e)</option>
            <option value="Divorcé(e)">Divorcé(e)</option>
            <option value="Veuf(ve)">Veuf(ve)</option>
          </select>
        </div>
        <div class="form-group">
          <label>Nombre d'enfants</label>
          <input v-model.number="form.nombreenfants" type="number" min="0" />
        </div>
        <div class="form-group">
          <label>Numéro CNAPS</label>
          <input v-model="form.numerocnaps" type="text" />
        </div>
        <div class="form-group">
          <label>Numéro OSTIE</label>
          <input v-model="form.numeroostie" type="text" />
        </div>
        <div class="form-group">
          <label>Matricule</label>
          <input v-model="form.matricule" type="text" />
        </div>
        <div class="form-group">
          <label>Département</label>
          <select v-model="form.iddept" required>
            <option value="">-- Sélectionner --</option>
            <option v-for="d in departements" :key="d.id" :value="d.id">{{ d.nom }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>Catégorie</label>
          <select v-model="form.idcategorie">
            <option value="">-- Optionnel --</option>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nom }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>Poste</label>
          <input v-model="form.poste" type="text" />
        </div>
        <div class="form-group">
          <label>Salaire (brut)</label>
          <input v-model.number="form.salaire" type="number" step="0.01" min="0" />
        </div>
        <div class="form-group">
          <label>Date d'embauche</label>
          <input v-model="form.datedembauche" type="date" />
        </div>
        <div class="form-group">
          <label>Type de contrat</label>
          <select v-model="form.typecontrat">
            <option value="">-- Sélectionner --</option>
            <option v-for="t in typecontrats" :key="t.id" :value="t.libelle">{{ t.libelle }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>Statut du contrat</label>
          <select v-model="form.statutId">
            <option value="">-- Sélectionner --</option>
            <option v-for="s in statuts" :key="s.id" :value="s.id">{{ s.nom }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>Période d’essai (mois)</label>
          <input v-model.number="form.periodessai" type="number" min="0" />
        </div>
        <div class="form-group" v-if="shouldShowDuration">
          <label>Durée (mois)</label>
          <input v-model.number="form.nombremois" type="number" min="1" />
        </div>
        <div class="form-group">
          <label>Photo (optionnel)</label>
          <input type="file" accept="image/*" @change="onFileChange" />
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn-secondary" @click="$router.push('/employees')">Annuler</button>
        <button type="submit" class="btn btn-primary" :disabled="submitting">
          {{ submitting ? 'Enregistrement...' : 'Enregistrer' }}
        </button>
      </div>
    </form>

    <div v-if="error" class="error-box">{{ error }}</div>
    <div v-if="success" class="success-box">Employé créé avec succès.</div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AddEmployeeView',
  data() {
    return {
      departements: [],
      categories: [],
      typecontrats: [],
      statuts: [],
      submitting: false,
      error: '',
      success: false,
      selectedFile: null,
      form: {
        nom: 'Dupont',
        prenom: 'Jean',
        email: 'jean.dupont@example.com',
        telephone: '0321234567',
        adresse: 'Lot II A 123, Antananarivo',
        datenaissance: '1990-01-01',
        cin: '101010101010',
        lieunaissance: 'Antananarivo',
        nationalite: 'Malgache',
        situationfamiliale: 'Célibataire',
        nombreenfants: 0,
        numerocnaps: 'CNAPS123456',
        numeroostie: 'OSTIE7890',
        matricule: 'EMP-0001',
        iddept: '',
        idcategorie: '',
        poste: 'Développeur',
        salaire: 1200,
        datedembauche: '',
        typecontrat: '',
        nombremois: '',
        statutId: '',
        periodessai: 2
      }
    }
  },
  async mounted() {
    await Promise.all([this.loadDepartements(), this.loadCategories(), this.loadTypecontrats(), this.loadStatuts()])
    // Defaults after data loaded
    const today = new Date().toISOString().slice(0, 10)
    if (!this.form.datedembauche) this.form.datedembauche = today
    if (!this.form.iddept && this.departements.length > 0) this.form.iddept = this.departements[0].id
    if (!this.form.idcategorie && this.categories.length > 0) this.form.idcategorie = this.categories[0].id
    if (!this.form.typecontrat && this.typecontrats.length > 0) {
      const hasCdd = this.typecontrats.find(t => String(t.libelle).toLowerCase() === 'cdd')
      this.form.typecontrat = hasCdd ? hasCdd.libelle : this.typecontrats[0].libelle
    }
    if (this.shouldShowDuration && !this.form.nombremois) this.form.nombremois = 6
    if (!this.form.statutId && this.statuts.length > 0) {
      const actif = this.statuts.find(s => String(s.nom).toLowerCase() === 'actif')
      this.form.statutId = actif ? actif.id : this.statuts[0].id
    }
  },
  watch: {
    'form.typecontrat'(val) {
      if (!val) return
      if (String(val).toLowerCase() === 'cdi') {
        this.form.nombremois = ''
      } else if (!this.form.nombremois || Number(this.form.nombremois) <= 0) {
        this.form.nombremois = 6
      }
    }
  },
  computed: {
    shouldShowDuration() {
      // Afficher la durée pour tous les types SAUF CDI
      const t = (this.form.typecontrat || '').toLowerCase()
      return t && t !== 'cdi'
    }
  },
  methods: {
    onFileChange(e) {
      const files = e.target.files
      this.selectedFile = files && files.length ? files[0] : null
    },
    async loadDepartements() {
      try {
        const res = await axios.get('/api/departements')
        this.departements = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement départements', e)
      }
    },
    async loadCategories() {
      try {
        const res = await axios.get('/api/categories')
        this.categories = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement catégories', e)
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
        console.error('Erreur chargement statuts de contrat', e)
      }
    },
    async submit() {
      this.error = ''
      this.success = false
      this.submitting = true
      try {
        // Adapter la charge utile selon votre API EmployeController
        const payload = {
          nom: this.form.nom,
          prenom: this.form.prenom,
          email: this.form.email,
          telephone: this.form.telephone,
          adresse: this.form.adresse,
          datenaissance: this.form.datenaissance || null,
          cin: this.form.cin || null,
          lieunaissance: this.form.lieunaissance || null,
          nationalite: this.form.nationalite || null,
          situationfamiliale: this.form.situationfamiliale || null,
          nombreenfants: this.form.nombreenfants || 0,
          numerocnaps: this.form.numerocnaps || null,
          numeroostie: this.form.numeroostie || null,
          matricule: this.form.matricule || null,
          iddept: this.form.iddept ? { id: this.form.iddept } : null,
          idcategorie: this.form.idcategorie ? { id: this.form.idcategorie } : null,
          poste: this.form.poste,
          salaire: this.form.salaire || 0,
          datedembauche: this.form.datedembauche || null,
          typecontrat: this.form.typecontrat || null,
          nombremois: this.form.nombremois || null
        }
        const createRes = await axios.post('/api/employes', payload)
        const saved = createRes?.data
        // Create a contract so the new employee appears in the contract-based list
        if (saved && saved.id) {
          const contratReq = {
            poste: this.form.poste || null,
            salaire: this.form.salaire ? Number(this.form.salaire) : null,
            typecontratLibelle: this.form.typecontrat || null,
            datedebut: this.form.datedembauche || null,
            nombremois: this.shouldShowDuration && this.form.nombremois ? Number(this.form.nombremois) : null,
            periodessai: this.form.periodessai !== '' && this.form.periodessai != null ? Number(this.form.periodessai) : null,
            statutId: this.form.statutId ? Number(this.form.statutId) : null
          }
          await axios.post(`/api/employes/${saved.id}/contrat`, contratReq)
        }
        // Upload photo if provided and id is available
        if (this.selectedFile && saved && saved.id) {
          const fd = new FormData()
          fd.append('file', this.selectedFile)
          await axios.post(`/api/employes/${saved.id}/photo`, fd, {
            headers: { 'Content-Type': 'multipart/form-data' }
          })
        }
        this.success = true
        setTimeout(() => this.$router.push('/employees'), 800)
      } catch (e) {
        console.error('Erreur création employé', e)
        this.error = e?.response?.data?.message || 'Erreur lors de la création.'
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.add-employee-container {
  padding: 24px;
  max-width: 1000px;
  margin: 0 auto;
}
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 0 0 6px 0; }

.employee-form { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 14px; color: #374151; font-weight: 500; }
.form-group input, .form-group select { padding: 10px 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; }

.form-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 16px; }
.btn { padding: 10px 14px; border-radius: 8px; cursor: pointer; border: none; font-weight: 600; }
.btn-primary { background: #059669; color: white; }
.btn-secondary { background: #e5e7eb; color: #111827; }

.error-box { margin-top: 12px; color: #991b1b; background: #fee2e2; padding: 10px; border-radius: 8px; }
.success-box { margin-top: 12px; color: #166534; background: #dcfce7; padding: 10px; border-radius: 8px; }
</style>
