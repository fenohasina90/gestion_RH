<template>
  <div class="critere-list-container">
    <div class="header-section">
      <h1 class="page-title">Gestion des Critères</h1>
      <button @click="addCritere" class="btn btn-primary">
        <i class="fas fa-plus"></i> Ajouter un critère
      </button>
    </div>

    <div class="table-container">
      <div v-if="loading" class="loading">
        Chargement des critères...
      </div>

      <div v-else-if="criteres.length === 0" class="empty-state">
        <i class="fas fa-list-ul"></i>
        <p>Aucun critère trouvé</p>
        <button @click="addCritere" class="btn btn-primary">
          Créer le premier critère
        </button>
      </div>

      <table v-else class="criteres-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nom du critère</th>
            <th>Type de champ</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="critere in criteres" :key="critere.id">
            <td>{{ critere.id }}</td>
            <td>{{ critere.nom }}</td>
            <td>{{ critere.idtypechamp?.libelle || '—' }}</td>
            <td class="actions">
              <button @click="editCritere(critere)" class="btn btn-edit" title="Modifier">
                <i class="fas fa-edit"></i>
              </button>
              <button @click="deleteCritere(critere)" class="btn btn-delete" title="Supprimer">
                <i class="fas fa-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal de modification -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <h3>Modifier le critère</h3>
        <form @submit.prevent="updateCritere">
          <div class="form-group">
            <label for="editNom">Nom du critère</label>
            <input
              type="text"
              id="editNom"
              v-model="editingCritere.nom"
              class="form-input"
              required
            />
          </div>
          <div class="form-group">
            <label for="editTypechamp">Type de champ</label>
            <select id="editTypechamp" v-model.number="editingCritere.typechampId" class="form-input" required>
              <option value="" disabled>-- Sélectionner un type --</option>
              <option v-for="t in typechamps" :key="t.id" :value="t.id">{{ t.libelle }}</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" :disabled="updating">
              {{ updating ? 'Mise à jour...' : 'Mettre à jour' }}
            </button>
            <button type="button" @click="closeEditModal" class="btn btn-secondary">
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'CritereList',
  data() {
    return {
      criteres: [],
      loading: false,
      showEditModal: false,
      editingCritere: {},
      updating: false,
      message: '',
      messageType: '',
      typechamps: []
    }
  },
  async mounted() {
    await Promise.all([this.loadTypechamps(), this.loadCriteres()]);
  },
  methods: {
    async loadTypechamps() {
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const res = await fetch(`${baseUrl}/api/typechamps`);
        if (!res.ok) throw new Error('Erreur API typechamps');
        this.typechamps = await res.json();
      } catch (e) {
        console.error('Erreur chargement typechamps:', e);
        this.typechamps = [];
      }
    },
    async loadCriteres() {
      this.loading = true;
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/criteres`);
        if (response.ok) {
          const data = await response.json();
          // Normaliser editingCritere.typechampId pour l'édition
          this.criteres = data.map(c => ({
            ...c,
            typechampId: c?.idtypechamp?.id || ''
          }));
        } else {
          throw new Error('Erreur lors du chargement des critères');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors du chargement des critères', 'error');
      } finally {
        this.loading = false;
      }
    },

    addCritere() {
      this.$router.push('/criteres/add');
    },

    editCritere(critere) {
      this.editingCritere = { ...critere, typechampId: critere?.idtypechamp?.id || critere.typechampId || '' };
      this.showEditModal = true;
    },

    async updateCritere() {
      this.updating = true;
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/criteres/${this.editingCritere.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            id: this.editingCritere.id,
            nom: this.editingCritere.nom,
            idtypechamp: { id: this.editingCritere.typechampId }
          })
        });

        if (response.ok) {
          this.showMessage('Critère mis à jour avec succès', 'success');
          this.closeEditModal();
          await this.loadCriteres();
        } else {
          throw new Error('Erreur lors de la mise à jour');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors de la mise à jour du critère', 'error');
      } finally {
        this.updating = false;
      }
    },

    async deleteCritere(critere) {
      if (confirm(`Êtes-vous sûr de vouloir supprimer le critère "${critere.nom}" ?`)) {
        try {
          const baseUrl = import.meta.env.VITE_API_BASE_URL;
          const response = await fetch(`${baseUrl}/api/criteres/${critere.id}`, {
            method: 'DELETE'
          });

          if (response.ok) {
            this.showMessage('Critère supprimé avec succès', 'success');
            await this.loadCriteres();
          } else {
            throw new Error('Erreur lors de la suppression');
          }
        } catch (error) {
          console.error('Erreur:', error);
          this.showMessage('Erreur lors de la suppression du critère', 'error');
        }
      }
    },

    closeEditModal() {
      this.showEditModal = false;
      this.editingCritere = {};
    },

    showMessage(text, type) {
      this.message = text;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 5000);
    }
  }
}
</script>

<style scoped>
.critere-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  color: #333;
  font-size: 28px;
  font-weight: 600;
  margin: 0;
}

.table-container {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 20px;
  color: #ccc;
}

.criteres-table {
  width: 100%;
  border-collapse: collapse;
}

.criteres-table th,
.criteres-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.criteres-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.criteres-table tbody tr:hover {
  background-color: #f8f9fa;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-edit {
  background: #28a745;
  color: white;
  padding: 6px 10px;
}

.btn-edit:hover {
  background: #218838;
  transform: translateY(-1px);
}

.btn-delete {
  background: #dc3545;
  color: white;
  padding: 6px 10px;
}

.btn-delete:hover {
  background: #c82333;
  transform: translateY(-1px);
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 10px;
  padding: 30px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e1e5e9;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.message {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 15px 20px;
  border-radius: 6px;
  font-weight: 500;
  z-index: 1001;
  max-width: 400px;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .criteres-table {
    font-size: 14px;
  }

  .criteres-table th,
  .criteres-table td {
    padding: 10px 8px;
  }

  .actions {
    flex-direction: column;
    gap: 4px;
  }

  .modal-content {
    margin: 20px;
    padding: 20px;
  }

  .modal-actions {
    flex-direction: column;
  }
}
</style>
