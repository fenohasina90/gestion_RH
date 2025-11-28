<template>
  <div class="docs-page">
    <div class="page-header">
      <div>
        <h1>Documents RH</h1>
        <p>Liste des documents liés à l'employé</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-secondary" @click="$router.back()">← Retour</button>
      </div>
    </div>

    <div class="card">
      <div class="table-responsive">
        <table class="table">
          <thead>
          <tr>
            <th style="width: 56px;">Aperçu</th>
            <th>Type</th>
            <th>Nom fichier</th>
            <th>Date upload</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="isLoading">
            <td colspan="5">Chargement...</td>
          </tr>
          <tr v-else-if="!documents.length">
            <td colspan="5">Aucun document trouvé.</td>
          </tr>
          <tr v-else v-for="d in documents" :key="d.id">
            <td>
              <div class="preview-box">
                <img v-if="isImage(d.chemin)" :src="getFileUrl(d.chemin)" alt="aperçu" />
                <span v-else-if="isPdf(d.chemin)" class="file-icon">PDF</span>
                <span v-else class="file-icon">FILE</span>
              </div>
            </td>
            <td>{{ d.idtypedocument?.nom || '—' }}</td>
            <td>{{ d.nomfichier }}</td>
            <td>{{ d.dateupload ? new Date(d.dateupload).toLocaleString('fr-FR') : '—' }}</td>
            <td>
              <router-link class="btn btn-sm btn-primary" :to="`/employees/${empId}/documents/${d.id}`">Détails</router-link>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'EmployeeDocumentsView',
  data() {
    return {
      empId: this.$route.params.empId,
      documents: [],
      isLoading: false,
    }
  },
  async mounted() {
    await this.loadDocuments()
  },
  methods: {
    async loadDocuments() {
      try {
        this.isLoading = true
        const res = await axios.get(`/api/employes/${this.empId}/documents`)
        this.documents = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('Erreur chargement documents', e)
        this.documents = []
      } finally {
        this.isLoading = false
      }
    },
    isImage(path) {
      return !!path && /(\.png|\.jpg|\.jpeg|\.webp|\.gif)$/i.test(path)
    },
    isPdf(path) {
      return !!path && /\.pdf$/i.test(path)
    },
    getFileUrl(path) {
      if (!path) return ''
      if (/^https?:\/\//i.test(path)) return path
      const normalized = path.startsWith('/') ? path : `/${path}`
      const base = import.meta.env.VITE_API_BASE_URL || ''
      return `${base}${normalized}`
    }
  }
}
</script>

<style scoped>
.docs-page { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.card { background: #fff; border-radius: 12px; padding: 0; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.table-responsive { width: 100%; overflow: auto; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 12px 14px; border-bottom: 1px solid #eef2f7; text-align: left; }
.table thead th { background: #f9fafb; color: #374151; font-weight: 600; }

.preview-box { width: 40px; height: 40px; border-radius: 6px; background: #f3f4f6; display:flex;align-items:center;justify-content:center; overflow:hidden; }
.preview-box img { width:100%; height:100%; object-fit:cover; }
.file-icon { font-size: 11px; font-weight: 700; color: #374151; background: #e5e7eb; padding: 4px 6px; border-radius: 6px; }

.btn { padding: 8px 12px; border-radius: 8px; border: none; cursor: pointer; }
.btn-secondary { background: #e5e7eb; color: #111827; }
.btn-primary { background: #3b82f6; color: #fff; }
</style>
