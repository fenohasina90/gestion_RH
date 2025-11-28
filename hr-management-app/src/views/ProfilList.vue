<template>
  <div class="profil-list-container">
    <div class="header-section">
      <h1 class="page-title">Gestion des Profils</h1>
      <button @click="addProfil" class="btn btn-primary">
        <i class="fas fa-plus"></i> Ajouter un profil
      </button>
    </div>

    <div class="table-container">
      <div v-if="loading" class="loading">
        Chargement des profils...
      </div>

      <div v-else-if="profils.length === 0" class="empty-state">
        <i class="fas fa-user-tag"></i>
        <p>Aucun profil trouvé</p>
        <button @click="addProfil" class="btn btn-primary">
          Créer le premier profil
        </button>
      </div>

      <table v-else class="profils-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nom du profil</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="profil in profils" :key="profil.id">
            <td>{{ profil.id }}</td>
            <td>{{ profil.nom }}</td>
            <td class="actions">
              <button @click="editProfil(profil)" class="btn btn-edit" title="Modifier">
                <i class="fas fa-edit"></i>
              </button>
              <button @click="assignCriteres(profil)" class="btn btn-assign" title="Assigner critères">
                <i class="fas fa-link"></i>
              </button>
              <button @click="assignDiplomes(profil)" class="btn btn-assign-diplome" title="Assigner diplômes">
                <i class="fas fa-graduation-cap"></i>
              </button>
              <button @click="deleteProfil(profil)" class="btn btn-delete" title="Supprimer">
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
        <h3>Modifier le profil</h3>
        <form @submit.prevent="updateProfil">
          <div class="form-group">
            <label for="editNom">Nom du profil</label>
            <input
              type="text"
              id="editNom"
              v-model="editingProfil.nom"
              class="form-input"
              required
            />
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

    <!-- Modal d'assignement de diplômes -->
    <div v-if="showAssignDiplomeModal" class="modal-overlay" @click="closeAssignDiplomeModal">
      <div class="modal-content assign-modal" @click.stop>
        <h3>Assigner des diplômes à "{{ selectedProfil?.nom }}"</h3>
        
        <div v-if="loadingDiplomes" class="loading">
          Chargement des diplômes...
        </div>
        
        <div v-else class="diplomes-selection">
          <div class="diplome-item" v-for="diplome in availableDiplomes" :key="diplome.id">
            <label class="diplome-label">
              <input
                type="checkbox"
                :value="diplome.id"
                v-model="selectedDiplomeIds"
                class="diplome-checkbox"
              />
              <span>{{ diplome.nom }}</span>
            </label>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="saveDiplomeAssignement" class="btn btn-primary" :disabled="assigningDiplomes">
            {{ assigningDiplomes ? 'Assignement...' : 'Assigner les diplômes' }}
          </button>
          <button type="button" @click="closeAssignDiplomeModal" class="btn btn-secondary">
            Annuler
          </button>
        </div>
      </div>
    </div>

    <!-- Modal d'assignement de critères -->
    <div v-if="showAssignModal" class="modal-overlay" @click="closeAssignModal">
      <div class="modal-content assign-modal" @click.stop>
        <h3>Assigner des critères à "{{ selectedProfil?.nom }}"</h3>
        
        <div v-if="loadingCriteres" class="loading">
          Chargement des critères...
        </div>
        
        <div v-else class="criteres-selection">
          <div class="critere-item" v-for="critere in availableCriteres" :key="critere.id">
            <div class="critere-row">
              <label class="critere-label">
                <input
                  type="checkbox"
                  :value="critere.id"
                  v-model="selectedCritereIds"
                  class="critere-checkbox"
                />
                <span>{{ critere.nom }}</span>
              </label>
              <div v-if="selectedCritereIds.includes(critere.id)" class="obligatoire-selection">
                <label class="radio-label">
                  <input
                    type="radio"
                    :name="`obligatoire_${critere.id}`"
                    :value="true"
                    v-model="critereObligatoire[critere.id]"
                    class="radio-input"
                  />
                  <span>Obligatoire</span>
                </label>
                <label class="radio-label">
                  <input
                    type="radio"
                    :name="`obligatoire_${critere.id}`"
                    :value="false"
                    v-model="critereObligatoire[critere.id]"
                    class="radio-input"
                  />
                  <span>Optionnel</span>
                </label>
              </div>

              <div v-if="selectedCritereIds.includes(critere.id)" class="critere-value-row">
                <label class="value-label">Valeur attendue</label>
                <!-- Number types -->
                <input
                  v-if="isType(critere, ['number','numeric','double','decimal'])"
                  type="number"
                  class="form-input"
                  step="0.01"
                  v-model.number="critereValeur[critere.id]"
                />
                <!-- Date -->
                <input
                  v-else-if="isType(critere, ['date'])"
                  type="date"
                  class="form-input"
                  v-model="critereValeur[critere.id]"
                />
                <!-- Email -->
                <input
                  v-else-if="isType(critere, ['email'])"
                  type="email"
                  class="form-input"
                  v-model="critereValeur[critere.id]"
                />
                <!-- Boolean -->
                <label v-else-if="isType(critere, ['checkbox','boolean'])" class="checkbox-label">
                  <input type="checkbox" v-model="critereValeur[critere.id]" />
                  <span>Oui / Non</span>
                </label>
                <!-- Textarea -->
                <textarea
                  v-else-if="isType(critere, ['textarea'])"
                  class="form-input"
                  rows="3"
                  v-model="critereValeur[critere.id]"
                ></textarea>
                <!-- Default text -->
                <input
                  v-else
                  type="text"
                  class="form-input"
                  v-model="critereValeur[critere.id]"
                />
              </div>
            </div>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="saveAssignement" class="btn btn-primary" :disabled="assigning">
            {{ assigning ? 'Assignement...' : 'Assigner les critères' }}
          </button>
          <button type="button" @click="closeAssignModal" class="btn btn-secondary">
            Annuler
          </button>
        </div>
      </div>
    </div>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProfilList',
  data() {
    return {
      profils: [],
      loading: false,
      showEditModal: false,
      showAssignModal: false,
      showAssignDiplomeModal: false,
      editingProfil: {},
      selectedProfil: null,
      updating: false,
      message: '',
      messageType: '',
      availableCriteres: [],
      selectedCritereIds: [],
      critereObligatoire: {},
      critereValeur: {},
      loadingCriteres: false,
      assigning: false,
      availableDiplomes: [],
      selectedDiplomeIds: [],
      loadingDiplomes: false,
      assigningDiplomes: false
    }
  },
  async mounted() {
    await this.loadProfils();
  },
  methods: {
    async loadProfils() {
      this.loading = true;
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/profils`);
        if (response.ok) {
          this.profils = await response.json();
        } else {
          throw new Error('Erreur lors du chargement des profils');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors du chargement des profils', 'error');
      } finally {
        this.loading = false;
      }
    },

    async loadCriteres() {
      this.loadingCriteres = true;
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/criteres`);
        if (response.ok) {
          this.availableCriteres = await response.json();
        } else {
          throw new Error('Erreur lors du chargement des critères');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors du chargement des critères', 'error');
      } finally {
        this.loadingCriteres = false;
      }
    },

    async loadProfilCriteres(profilId) {
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/profils/${profilId}/criteres`);
        if (response.ok) {
          const critereProfils = await response.json();
          this.selectedCritereIds = critereProfils.map(cp => cp.idcritere.id);
          
          // Charger les statuts obligatoire/optionnel
          this.critereObligatoire = {};
          this.critereValeur = {};
          critereProfils.forEach(cp => {
            this.critereObligatoire[cp.idcritere.id] = cp.obligatoire || false;
            // Pré-remplir la valeur selon la colonne non nulle
            if (cp.valeurdouble !== null && cp.valeurdouble !== undefined) {
              this.critereValeur[cp.idcritere.id] = cp.valeurdouble;
            } else if (cp.valeurbool !== null && cp.valeurbool !== undefined) {
              this.critereValeur[cp.idcritere.id] = cp.valeurbool;
            } else if (cp.valeurvarchar) {
              this.critereValeur[cp.idcritere.id] = cp.valeurvarchar;
            }
          });
        }
      } catch (error) {
        console.error('Erreur:', error);
      }
    },

    addProfil() {
      this.$router.push('/profils/add');
    },

    editProfil(profil) {
      this.editingProfil = { ...profil };
      this.showEditModal = true;
    },

    async assignCriteres(profil) {
      this.selectedProfil = profil;
      this.selectedCritereIds = [];
      this.critereObligatoire = {};
      this.critereValeur = {};
      this.showAssignModal = true;
      await this.loadCriteres();
      await this.loadProfilCriteres(profil.id);
    },

    async assignDiplomes(profil) {
      this.selectedProfil = profil;
      this.selectedDiplomeIds = [];
      this.showAssignDiplomeModal = true;
      await this.loadDiplomes();
      await this.loadProfilDiplomes(profil.id);
    },

    async updateProfil() {
      this.updating = true;
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/profils/${this.editingProfil.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.editingProfil)
        });

        if (response.ok) {
          this.showMessage('Profil mis à jour avec succès', 'success');
          this.closeEditModal();
          await this.loadProfils();
        } else {
          throw new Error('Erreur lors de la mise à jour');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors de la mise à jour du profil', 'error');
      } finally {
        this.updating = false;
      }
    },

    async saveAssignement() {
      this.assigning = true;
      try {
        // Validation minimale: exiger une valeur pour les types non booléens
        for (const critere of this.availableCriteres) {
          if (this.selectedCritereIds.includes(critere.id)) {
            const t = this.getTypeLibelle(critere)
            const val = this.critereValeur[critere.id]
            if (t !== 'checkbox' && t !== 'boolean') {
              if (val === undefined || val === null || String(val).trim() === '') {
                this.showMessage(`Veuillez renseigner la valeur pour le critère "${critere.nom}"`, 'error')
                this.assigning = false
                return
              }
            }
            if (t === 'number' || t === 'numeric' || t === 'double' || t === 'decimal') {
              if (isNaN(Number(val))) {
                this.showMessage(`La valeur du critère "${critere.nom}" doit être numérique`, 'error')
                this.assigning = false
                return
              }
            }
          }
        }

        // Créer le tableau des assignements avec critère ID, obligatoire et valeur
        const critereAssignments = this.selectedCritereIds.map(critereId => ({
          critereId: critereId,
          obligatoire: this.critereObligatoire[critereId] || false,
          valeur: this.serializeValeur(critereId)
        }));

        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/profils/${this.selectedProfil.id}/criteres`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ critereAssignments: critereAssignments })
        });

        if (response.ok) {
          this.showMessage('Critères assignés avec succès', 'success');
          this.closeAssignModal();
        } else {
          throw new Error('Erreur lors de l\'assignement');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors de l\'assignement des critères', 'error');
      } finally {
        this.assigning = false;
      }
    },
    getTypeLibelle(critere) {
      return (critere && critere.idtypechamp && critere.idtypechamp.libelle || '').toLowerCase();
    },
    isType(critere, list) {
      const t = this.getTypeLibelle(critere)
      return list.includes(t)
    },
    getInputTag(critere) {
      const t = this.getTypeLibelle(critere);
      if (t === 'textarea') return 'textarea';
      if (t === 'checkbox' || t === 'boolean') return 'input';
      return 'input';
    },
    getInputType(critere) {
      const t = this.getTypeLibelle(critere);
      if (t === 'number' || t === 'numeric' || t === 'double' || t === 'decimal') return 'number';
      if (t === 'email') return 'email';
      if (t === 'date') return 'date';
      if (t === 'checkbox' || t === 'boolean') return 'checkbox';
      return 'text';
    },
    getTextareaRows(critere) {
      return this.getTypeLibelle(critere) === 'textarea' ? 3 : undefined;
    },
    getStep(critere) {
      return (this.getTypeLibelle(critere) === 'number' || this.getTypeLibelle(critere) === 'double' || this.getTypeLibelle(critere) === 'decimal') ? '0.01' : undefined;
    },
    serializeValeur(critereId) {
      const val = this.critereValeur[critereId];
      // Convert booleans to string for backend mapping
      if (typeof val === 'boolean') return val ? 'true' : 'false';
      return val !== undefined && val !== null ? String(val) : '';
    },

    async loadDiplomes() {
      this.loadingDiplomes = true;
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/diplomes`);
        if (response.ok) {
          this.availableDiplomes = await response.json();
        } else {
          throw new Error('Erreur lors du chargement des diplômes');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors du chargement des diplômes', 'error');
      } finally {
        this.loadingDiplomes = false;
      }
    },

    async loadProfilDiplomes(profilId) {
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/profils/${profilId}/diplomes`);
        if (response.ok) {
          const profilDiplomes = await response.json();
          this.selectedDiplomeIds = profilDiplomes.map(pd => pd.iddiplome.id);
        }
      } catch (error) {
        console.error('Erreur:', error);
      }
    },

    async saveDiplomeAssignement() {
      this.assigningDiplomes = true;
      try {
        // Créer le tableau des assignements avec diplôme ID
        const diplomeAssignments = this.selectedDiplomeIds.map(diplomeId => ({
          diplomeId: diplomeId
        }));

        const baseUrl = import.meta.env.VITE_API_BASE_URL;
        const response = await fetch(`${baseUrl}/api/profils/${this.selectedProfil.id}/diplomes`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ diplomeAssignments: diplomeAssignments })
        });

        if (response.ok) {
          this.showMessage('Diplômes assignés avec succès', 'success');
          this.closeAssignDiplomeModal();
        } else {
          throw new Error('Erreur lors de l\'assignement');
        }
      } catch (error) {
        console.error('Erreur:', error);
        this.showMessage('Erreur lors de l\'assignement des diplômes', 'error');
      } finally {
        this.assigningDiplomes = false;
      }
    },

    async deleteProfil(profil) {
      if (confirm(`Êtes-vous sûr de vouloir supprimer le profil "${profil.nom}" ?`)) {
        try {
          const baseUrl = import.meta.env.VITE_API_BASE_URL;
          const response = await fetch(`${baseUrl}/api/profils/${profil.id}`, {
            method: 'DELETE'
          });

          if (response.ok) {
            this.showMessage('Profil supprimé avec succès', 'success');
            await this.loadProfils();
          } else {
            throw new Error('Erreur lors de la suppression');
          }
        } catch (error) {
          console.error('Erreur:', error);
          this.showMessage('Erreur lors de la suppression du profil', 'error');
        }
      }
    },

    closeEditModal() {
      this.showEditModal = false;
      this.editingProfil = {};
    },

    closeAssignModal() {
      this.showAssignModal = false;
      this.selectedProfil = null;
      this.selectedCritereIds = [];
      this.critereObligatoire = {};
      this.availableCriteres = [];
    },

    closeAssignDiplomeModal() {
      this.showAssignDiplomeModal = false;
      this.selectedProfil = null;
      this.selectedDiplomeIds = [];
      this.availableDiplomes = [];
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
.profil-list-container {
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

.profils-table {
  width: 100%;
  border-collapse: collapse;
}

.profils-table th,
.profils-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.profils-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.profils-table tbody tr:hover {
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

.btn-assign {
  background: #17a2b8;
  color: white;
  padding: 6px 10px;
}

.btn-assign:hover {
  background: #138496;
  transform: translateY(-1px);
}

.btn-assign-diplome {
  background: #6f42c1;
  color: white;
  padding: 6px 10px;
}

.btn-assign-diplome:hover {
  background: #5a32a3;
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

.assign-modal {
  max-width: 600px;
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

.criteres-selection {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 20px;
}

.diplomes-selection {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 20px;
}

.diplome-item {
  margin-bottom: 10px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  padding: 10px;
}

.diplome-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.diplome-label:hover {
  background-color: #f8f9fa;
}

.diplome-checkbox {
  margin-right: 10px;
  transform: scale(1.2);
}

.critere-item {
  margin-bottom: 15px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  padding: 10px;
}

.critere-row {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.critere-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.critere-label:hover {
  background-color: #f8f9fa;
}

.critere-checkbox {
  margin-right: 10px;
  transform: scale(1.2);
}

.obligatoire-selection {
  display: flex;
  gap: 15px;
  margin-left: 30px;
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
}

.radio-input {
  margin-right: 5px;
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

  .profils-table {
    font-size: 14px;
  }

  .profils-table th,
  .profils-table td {
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
