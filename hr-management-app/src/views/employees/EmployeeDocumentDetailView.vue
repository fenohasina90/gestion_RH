<template>
  <div class="doc-detail-page">
    <div class="page-header">
      <div>
        <h1>Détails du document</h1>
        <p>Clés en gras, valeurs en normal</p>
      </div>
      <div class="header-actions">
        <router-link class="btn btn-secondary" :to="`/employees/${empId}/documents`">← Retour</router-link>
      </div>
    </div>

    <div class="grid">
      <div class="card">
        <h2 class="card-title">Informations</h2>
        <div class="info-table">
          <div class="row"><div class="label">Type</div><div class="value">{{ doc?.idtypedocument?.nom || '—' }}</div></div>
          <div class="row"><div class="label">Nom du fichier</div><div class="value">{{ doc?.nomfichier }}</div></div>
          <div class="row"><div class="label">Date d'upload</div><div class="value">{{ doc?.dateupload ? new Date(doc.dateupload).toLocaleString('fr-FR') : '—' }}</div></div>
          <div class="row"><div class="label">Commentaire</div><div class="value">{{ doc?.commentaire || '—' }}</div></div>
        </div>
      </div>

      <div class="card">
        <h2 class="card-title">Pièce justificative</h2>
        <div class="preview">
          <img v-if="isImage(doc?.chemin)" :src="fileUrl" alt="aperçu document" />
          <iframe v-else-if="isPdf(doc?.chemin)" :src="fileUrl" class="pdf-frame"></iframe>
          <div v-else class="file-fallback">Aperçu non disponible</div>
        </div>
        <div class="preview-actions" v-if="fileUrl">
          <button class="btn btn-primary" @click="openFullscreen">Voir en grand</button>
          <a class="btn btn-secondary" :href="fileUrl" target="_blank" :download="doc?.nomfichier || true">Télécharger</a>
        </div>
      </div>

      <div class="card full">
        <h2 class="card-title">Contenu</h2>
        <div v-if="parsed" class="json-tree">
          <!-- Top-level object keys -->
          <div v-for="(val, key) in parsed" :key="key" class="json-pair">
            <!-- Primitive value -->
            <div v-if="isPrimitive(val)"><span class="json-key">{{ key }}</span>: <span class="json-value">{{ val }}</span></div>

            <!-- Array value -->
            <div v-else-if="Array.isArray(val)">
              <div class="json-key" style="margin-bottom:4px;">{{ key }}</div>
              <!-- Array of primitives: join with commas -->
              <div v-if="isArrayOfPrimitives(val)" class="json-indent json-value">{{ val.join(', ') }}</div>
              <!-- Array of objects: render each item as a group without indices -->
              <div v-else class="json-indent">
                <div v-for="(item, idx) in val" :key="idx" class="json-group">
                  <div v-for="(v2, k2) in item" :key="k2" class="json-pair">
                    <span class="json-key">{{ k2 }}</span>: <span class="json-value" v-if="isPrimitive(v2)">{{ v2 }}</span>
                    <div v-else class="json-indent">
                      <div v-for="(v3, k3) in v2" :key="k3" class="json-pair">
                        <span class="json-key">{{ k3 }}</span>: <span class="json-value">{{ v3 }}</span>
                      </div>
                    </div>
                  </div>
                  <hr class="json-sep" />
                </div>
              </div>
            </div>

            <!-- Object value -->
            <div v-else class="json-indent">
              <div v-for="(subVal, subKey) in val" :key="subKey" class="json-pair">
                <span class="json-key">{{ subKey }}</span>: <span class="json-value" v-if="isPrimitive(subVal)">{{ subVal }}</span>
                <div v-else class="json-indent">
                  <div v-for="(sub2, key2) in subVal" :key="key2" class="json-pair">
                    <span class="json-key">{{ key2 }}</span>: <span class="json-value">{{ sub2 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else>Aucun contenu JSON.</div>
      </div>
      <!-- Fullscreen overlay inside main template -->
      <div v-if="showFull" class="overlay" @click="closeFullscreen">
        <div class="full-box" @click.stop>
          <img v-if="isImage(doc?.chemin)" :src="fileUrl" alt="document" />
          <iframe v-else-if="isPdf(doc?.chemin)" :src="fileUrl"></iframe>
          <div v-else class="file-fallback" style="color:#fff">Aperçu non disponible</div>
          <div style="position:absolute; top:14px; right:14px; display:flex; gap:8px;">
            <a class="btn btn-primary" :href="fileUrl" target="_blank" :download="doc?.nomfichier || true">Télécharger</a>
            <button class="btn btn-secondary" @click="closeFullscreen">Fermer</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'EmployeeDocumentDetailView',
  data() {
    return {
      empId: this.$route.params.empId,
      docId: this.$route.params.docId,
      doc: null,
      parsed: null,
      loading: false,
      showFull: false
    }
  },
  async mounted() {
    await this.loadDocument()
  },
  methods: {
    async loadDocument() {
      try {
        this.loading = true
        const res = await axios.get(`/api/employes/documents/${this.docId}`)
        this.doc = res.data
        this.parsed = this.tryParse(this.doc?.contenujson)
      } catch (e) {
        console.error('Erreur chargement document', e)
        this.doc = null
        this.parsed = null
      } finally {
        this.loading = false
      }
    },
    tryParse(text) { try { return text ? JSON.parse(text) : null } catch { return null } },
    isObject(v) { return v && typeof v === 'object' },
    isImage(path) { return !!path && /(\.png|\.jpg|\.jpeg|\.webp|\.gif)$/i.test(path) },
    isPdf(path) { return !!path && /\.pdf$/i.test(path) },
    getFileUrl(path) {
      if (!path) return ''
      if (/^https?:\/\//i.test(path)) return path
      const normalized = path.startsWith('/') ? path : `/${path}`
      const base = import.meta.env.VITE_API_BASE_URL || ''
      return `${base}${normalized}`
    },
    openFullscreen() { this.showFull = true },
    closeFullscreen() { this.showFull = false },
    // Helpers for JSON rendering
    isPrimitive(v) {
      return v === null || ['string','number','boolean'].includes(typeof v)
    },
    isArrayOfPrimitives(arr) {
      return Array.isArray(arr) && arr.every(it => this.isPrimitive(it))
    }
  },
  computed: {
    fileUrl() {
      return this.getFileUrl(this.doc?.chemin)
    }
  }
}
</script>

<style scoped>
.doc-detail-page { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.card.full { grid-column: 1 / -1; }
.card-title { margin: 0 0 10px 0; font-size: 16px; font-weight: 700; }
.info-table { display: grid; grid-template-columns: 220px 1fr; row-gap: 8px; }
.info-table .row { display: contents; }
.info-table .label { color: #6b7280; }
.preview { height: 360px; display:flex; align-items:center; justify-content:center; background: #f9fafb; border-radius: 10px; overflow: hidden; }
.preview img { max-width: 100%; max-height: 100%; object-fit: contain; }
.pdf-frame { width: 100%; height: 100%; border: none; }
.file-fallback { color: #6b7280; }
.preview-actions { margin-top: 10px; display: flex; gap: 10px; }
.json-tree { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; font-size: 13px; line-height: 1.6; }
.json-key { font-weight: 700; color: #111827; }
.json-value { color: #374151; }
.json-indent { margin-left: 14px; }
.btn { padding: 8px 12px; border-radius: 8px; border: none; cursor: pointer; }
.btn-secondary { background: #e5e7eb; color: #111827; }
.btn-primary { background: #3b82f6; color: #fff; }

/* Fullscreen modal */
.overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); display:flex; align-items:center; justify-content:center; z-index: 1000; }
.full-box { width: 92vw; height: 92vh; background: #111827; border-radius: 10px; padding: 10px; display:flex; align-items:center; justify-content:center; }
.full-box img { max-width: 100%; max-height: 100%; object-fit: contain; }
.full-box iframe { width: 100%; height: 100%; border: none; background: #fff; }
</style>

