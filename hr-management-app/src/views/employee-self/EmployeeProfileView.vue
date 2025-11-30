<template>
  <div class="emp-layout">
    <EmployeeSidebar />
    <div class="emp-content">
      <div class="emp-container">
        <div class="emp-header">
          <h1 class="emp-title">Mon profil</h1>
          <p class="emp-subtitle">Gérez vos informations personnelles</p>
        </div>

        <div v-if="!loaded" class="emp-loading">
          <div class="emp-spinner"></div>
          <span>Chargement de votre profil...</span>
        </div>

        <div v-else class="emp-profile">
          <div class="emp-profile-header">
            <div class="emp-avatar">
              <span class="emp-avatar-text">{{ profilInitials }}</span>
            </div>
            <div class="emp-profile-info">
              <h2 class="emp-profile-name">{{ profil.prenom }} {{ profil.nom }}</h2>
              <p class="emp-profile-matricule">{{ profil.matricule }}</p>
            </div>
          </div>

          <form @submit.prevent="save" class="emp-form">
            <div class="emp-form-grid">
              <div class="emp-form-group">
                <label class="emp-label">Genre</label>
                <select v-model="genreCode" class="emp-select">
                  <option value="">-- Sélectionner --</option>
                  <option value="M">Masculin</option>
                  <option value="F">Féminin</option>
                  <option value="A">Autre</option>
                </select>
              </div>
              
              <div class="emp-form-group">
                <label class="emp-label">Situation familiale</label>
                <input v-model="profil.situationfamiliale" type="text" class="emp-input" placeholder="Votre situation familiale" />
              </div>
              
              <div class="emp-form-group emp-form-full">
                <label class="emp-label">Adresse</label>
                <input v-model="profil.adresse" type="text" class="emp-input" placeholder="Votre adresse complète" />
              </div>
              
              <div class="emp-form-group">
                <label class="emp-label">Téléphone</label>
                <input v-model="profil.telephone" type="text" class="emp-input" placeholder="Votre numéro de téléphone" />
              </div>
              
              <div class="emp-form-group">
                <label class="emp-label">Email</label>
                <input v-model="profil.email" type="email" class="emp-input" placeholder="votre.email@entreprise.com" />
              </div>
              
              <div class="emp-form-group">
                <label class="emp-label">Nombre d'enfants</label>
                <input v-model.number="profil.nombreenfants" type="number" min="0" class="emp-input" placeholder="0" />
              </div>
            </div>

            <div v-if="message" class="emp-message" :class="success ? 'emp-message-success' : 'emp-message-error'">
              <span class="emp-message-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"></circle>
                  <path d="m9 12 2 2 4-4"></path>
                </svg>
              </span>
              {{ message }}
            </div>

            <div class="emp-form-actions">
              <button type="submit" class="emp-button emp-button-primary">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                  <polyline points="17 21 17 13 7 13 7 21"></polyline>
                  <polyline points="7 3 7 8 15 8"></polyline>
                </svg>
                Enregistrer les modifications
              </button>
            </div>
          </form>

          <div class="emp-quick-links">
            <router-link to="/espace-employe/solde-conges" class="emp-quick-link">
              <div class="emp-quick-link-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
              </div>
              <div class="emp-quick-link-content">
                <h3>Solde de congés</h3>
                <p>Consultez vos jours disponibles</p>
              </div>
              <div class="emp-quick-link-arrow">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="m9 18 6-6-6-6"></path>
                </svg>
              </div>
            </router-link>
            
            <router-link to="/espace-employe/bulletins" class="emp-quick-link">
              <div class="emp-quick-link-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                  <polyline points="14 2 14 8 20 8"></polyline>
                  <line x1="16" y1="13" x2="8" y2="13"></line>
                  <line x1="16" y1="17" x2="8" y2="17"></line>
                  <polyline points="10 9 9 9 8 9"></polyline>
                </svg>
              </div>
              <div class="emp-quick-link-content">
                <h3>Bulletins de paie</h3>
                <p>Accédez à vos documents de paie</p>
              </div>
              <div class="emp-quick-link-arrow">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="m9 18 6-6-6-6"></path>
                </svg>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EmployeeSidebar from '../../components/EmployeeSidebar.vue'

export default {
  name: 'EmployeeProfileView',
  components: { EmployeeSidebar },
  data() {
    return {
      profil: null,
      loaded: false,
      message: '',
      success: false,
      genreCode: ''
    }
  },
  computed: {
    profilInitials() {
      if (!this.profil) return ''
      return `${this.profil.prenom?.[0] || ''}${this.profil.nom?.[0] || ''}`.toUpperCase()
    }
  },
  created() {
    const stored = localStorage.getItem('selfEmployee')
    if (!stored) {
      this.$router.push('/espace-employe/login')
      return
    }
    const emp = JSON.parse(stored)
    this.loadProfil(emp.id)
  },
  methods: {
    async loadProfil(id) {
      try {
        const res = await fetch(`http://localhost:8080/api/employe/self/profil?idemploye=${id}`)
        if (!res.ok) throw new Error('Erreur serveur')
        this.profil = await res.json()
        // mapper libellé de genre vers code M/F/A si possible
        const g = (this.profil.genre || '').toLowerCase()
        if (g.startsWith('h')) this.genreCode = 'M'
        else if (g.startsWith('f')) this.genreCode = 'F'
        else if (g) this.genreCode = 'A'
        this.loaded = true
      } catch (e) {
        console.error(e)
        this.message = "Erreur lors du chargement du profil"
      }
    },
    async save() {
      this.message = ''
      this.success = false
      const stored = localStorage.getItem('selfEmployee')
      if (!stored) {
        this.$router.push('/espace-employe/login')
        return
      }
      const emp = JSON.parse(stored)
      try {
        const res = await fetch(`http://localhost:8080/api/employe/self/profil?idemploye=${emp.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            genre: this.genreCode,
            adresse: this.profil.adresse,
            telephone: this.profil.telephone,
            email: this.profil.email,
            situationfamiliale: this.profil.situationfamiliale,
            nombreenfants: this.profil.nombreenfants
          })
        })
        const data = await res.json()
        if (data.success) {
          this.success = true
          this.message = 'Profil mis à jour avec succès'
        } else {
          this.message = "Aucune mise à jour effectuée"
        }
      } catch (e) {
        console.error(e)
        this.message = "Erreur lors de la sauvegarde"
      }
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
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 24px;
}

.emp-header {
  margin-bottom: 32px;
  text-align: center;
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

.emp-profile {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}

.emp-profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f3f4f6;
}

.emp-avatar {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #10b981, #059669);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.emp-profile-info {
  flex: 1;
}

.emp-profile-name {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.emp-profile-matricule {
  font-size: 14px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
}

.emp-form {
  space-y: 24px;
}

.emp-form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.emp-form-full {
  grid-column: 1 / -1;
}

.emp-form-group {
  space-y: 8px;
}

.emp-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.emp-input, .emp-select {
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: #fafafa;
}

.emp-input:focus, .emp-select:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.emp-form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

.emp-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.emp-button-primary {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.emp-button-primary:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.4);
}

.emp-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.emp-message-success {
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.emp-message-error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.emp-message-icon {
  display: flex;
}

.emp-quick-links {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 40px;
}

.emp-quick-link {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  transition: all 0.2s ease;
}

.emp-quick-link:hover {
  background: #ffffff;
  border-color: #10b981;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.1);
}

.emp-quick-link-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.emp-quick-link-content {
  flex: 1;
}

.emp-quick-link-content h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.emp-quick-link-content p {
  font-size: 14px;
  color: #6b7280;
}

.emp-quick-link-arrow {
  color: #9ca3af;
  transition: transform 0.2s ease;
}

.emp-quick-link:hover .emp-quick-link-arrow {
  transform: translateX(4px);
  color: #10b981;
}

@keyframes emp-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .emp-container {
    padding: 24px 16px;
  }
  
  .emp-form-grid {
    grid-template-columns: 1fr;
  }
  
  .emp-quick-links {
    grid-template-columns: 1fr;
  }
  
  .emp-profile {
    padding: 24px 20px;
  }
}
</style>