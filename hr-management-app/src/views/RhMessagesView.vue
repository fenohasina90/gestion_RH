<template>
  <div class="rh-container">
    <div class="rh-header">
      <div class="rh-header-content">
        <h1 class="rh-title">Messagerie RH</h1>
        <p class="rh-subtitle">Consultez et répondez aux messages des employés</p>
      </div>
      <div class="rh-header-actions">
        <button @click="loadMessages" class="rh-action-button">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M23 4v6h-6"></path>
            <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path>
          </svg>
          Actualiser
        </button>
      </div>
    </div>

    <!-- Filtres améliorés -->
    <div class="rh-filters-card">
      <div class="rh-filters-header">
        <h3 class="rh-filters-title">Filtres et recherche</h3>
      </div>
      <div class="rh-filters-grid">
        <div class="rh-filter-group">
          <label class="rh-filter-label">Département</label>
          <select v-model="selectedDept" class="rh-filter-select">
            <option value="">Tous les départements</option>
            <option
              v-for="d in departements"
              :key="d"
              :value="d"
            >
              {{ d }}
            </option>
          </select>
        </div>
        
        <div class="rh-filter-group">
          <label class="rh-filter-label">Période - Du</label>
          <input v-model="dateFrom" type="date" class="rh-filter-input" />
        </div>
        
        <div class="rh-filter-group">
          <label class="rh-filter-label">Au</label>
          <input v-model="dateTo" type="date" class="rh-filter-input" />
        </div>
        
        <div class="rh-filter-group rh-filter-checkbox">
          <label class="rh-checkbox-label">
            <input v-model="onlyUnread" type="checkbox" class="rh-checkbox" />
            <span class="rh-checkbox-custom"></span>
            Non lus uniquement
          </label>
        </div>
        
        <div class="rh-filter-actions">
          <button @click="resetFilters" class="rh-reset-button">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
              <path d="M3 3v5h5"></path>
            </svg>
            Réinitialiser
          </button>
        </div>
      </div>
    </div>

    <!-- Grille principale -->
    <div class="rh-messaging-grid">
      <!-- Colonne des messages -->
      <div class="rh-messages-column">
        <div class="rh-section-card">
          <div class="rh-section-header">
            <h2 class="rh-section-title">Messages reçus</h2>
            <div class="rh-messages-stats">
              <span class="rh-stat-badge">{{ filteredMessages.length }} message(s)</span>
              <span v-if="unreadCount > 0" class="rh-stat-badge rh-stat-alert">
                {{ unreadCount }} non lu(s)
              </span>
            </div>
          </div>

          <div v-if="loading" class="rh-loading">
            <div class="rh-spinner"></div>
            <span>Chargement des messages...</span>
          </div>

          <div v-else-if="filteredMessages.length === 0" class="rh-empty-state">
            <div class="rh-empty-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
            </div>
            <h3 class="rh-empty-title">Aucun message</h3>
            <p class="rh-empty-description">
              {{ hasActiveFilters ? 'Aucun message ne correspond à vos filtres' : 'Aucun message reçu pour le moment' }}
            </p>
            <button v-if="hasActiveFilters" @click="resetFilters" class="rh-button rh-button-outline">
              Réinitialiser les filtres
            </button>
          </div>

          <div v-else class="rh-messages-list">
            <div
              v-for="m in filteredMessages"
              :key="m.id"
              class="rh-message-item"
              :class="{
                'rh-message-selected': selected?.id === m.id,
                'rh-message-unread': !m.lu,
                'rh-message-replied': m.reponse
              }"
              @click="selectMessage(m)"
            >
              <div class="rh-message-header">
                <div class="rh-message-subject">{{ m.sujet }}</div>
                <div class="rh-message-meta">
                  <span class="rh-message-date">{{ formatDate(m.date_envoi) }}</span>
                  <span v-if="!m.lu" class="rh-message-badge">Nouveau</span>
                </div>
              </div>
              
              <div class="rh-message-preview">
                <p class="rh-message-text">{{ truncateText(m.contenu, 80) }}</p>
              </div>

              <div class="rh-message-footer">
                <div class="rh-message-sender">
                  <span class="rh-sender-name">{{ m.prenom }} {{ m.nom }}</span>
                  <span class="rh-sender-details">
                    {{ m.matricule }}
                    <span v-if="m.departement"> • {{ m.departement }}</span>
                  </span>
                </div>
                <div class="rh-message-status">
                  <span v-if="m.reponse" class="rh-status-replied">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20 6L9 17l-5-5"></path>
                    </svg>
                    Répondu
                  </span>
                  <span v-else class="rh-status-pending">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10"></circle>
                    </svg>
                    En attente
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Colonne de détail et réponse -->
      <div class="rh-detail-column">
        <div class="rh-section-card">
          <div class="rh-section-header">
            <h2 class="rh-section-title">
              {{ selected ? 'Détail du message' : 'Sélectionnez un message' }}
            </h2>
          </div>

          <div v-if="!selected" class="rh-placeholder">
            <div class="rh-placeholder-icon">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
            </div>
            <h3 class="rh-placeholder-title">Aucun message sélectionné</h3>
            <p class="rh-placeholder-description">
              Sélectionnez un message dans la liste pour voir le détail et répondre.
            </p>
          </div>

          <div v-else class="rh-message-detail">
            <!-- En-tête du message -->
            <div class="rh-detail-header">
              <div class="rh-detail-sender">
                <div class="rh-sender-avatar">
                  {{ getInitials(selected.prenom, selected.nom) }}
                </div>
                <div class="rh-sender-info">
                  <h3 class="rh-sender-name">{{ selected.prenom }} {{ selected.nom }}</h3>
                  <div class="rh-sender-details">
                    <span class="rh-sender-matricule">{{ selected.matricule }}</span>
                    <span v-if="selected.departement" class="rh-sender-department">
                      • {{ selected.departement }}
                    </span>
                  </div>
                </div>
              </div>
              <div class="rh-detail-meta">
                <div class="rh-meta-date">{{ formatDate(selected.date_envoi) }}</div>
                <div class="rh-meta-status" :class="selected.reponse ? 'rh-status-replied' : 'rh-status-pending'">
                  {{ selected.reponse ? 'Répondu' : 'En attente' }}
                </div>
              </div>
            </div>

            <!-- Sujet -->
            <div class="rh-detail-subject">
              <label class="rh-detail-label">Sujet</label>
              <div class="rh-detail-content">{{ selected.sujet }}</div>
            </div>

            <!-- Message original -->
            <div class="rh-detail-message">
              <label class="rh-detail-label">Message</label>
              <div class="rh-message-content">
                <p class="whitespace-pre-line">{{ selected.contenu }}</p>
              </div>
            </div>

            <!-- Formulaire de réponse -->
            <div class="rh-response-section">
              <label class="rh-detail-label">
                Réponse RH
                <span class="rh-required">*</span>
              </label>
              <textarea
                v-model="reponse"
                rows="6"
                class="rh-response-textarea"
                placeholder="Rédigez votre réponse à l'employé..."
                :class="{ 'rh-textarea-error': showError && !reponse.trim() }"
              ></textarea>
              <div class="rh-char-count">
                {{ reponse.length }}/2000 caractères
              </div>

              <!-- Réponse existante -->
              <div v-if="selected.reponse && selected.date_reponse" class="rh-existing-response">
                <div class="rh-existing-header">
                  <span class="rh-existing-label">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20 6L9 17l-5-5"></path>
                    </svg>
                    Réponse envoyée le {{ formatDate(selected.date_reponse) }}
                  </span>
                </div>
                <div class="rh-existing-content">
                  <p class="whitespace-pre-line">{{ selected.reponse }}</p>
                </div>
              </div>

              <div v-if="message" class="rh-message" :class="success ? 'rh-message-success' : 'rh-message-error'">
                <span class="rh-message-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <path d="m9 12 2 2 4-4"></path>
                  </svg>
                </span>
                {{ message }}
              </div>

              <div class="rh-response-actions">
                <button
                  @click="envoyerReponse"
                  class="rh-button rh-button-primary"
                  :disabled="sending || !reponse.trim()"
                >
                  <span v-if="!sending" class="rh-button-content">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="22" y1="2" x2="11" y2="13"></line>
                      <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                    </svg>
                    {{ selected.reponse ? 'Mettre à jour la réponse' : 'Envoyer la réponse' }}
                  </span>
                  <span v-else class="rh-button-loading">
                    <div class="rh-spinner-small"></div>
                    Envoi en cours...
                  </span>
                </button>
                
                <button
                  v-if="selected.reponse"
                  @click="reponse = selected.reponse"
                  class="rh-button rh-button-outline"
                >
                  Réinitialiser
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RhMessagesView',
  data() {
    return {
      messages: [],
      selected: null,
      loading: false,
      sending: false,
      reponse: '',
      message: '',
      success: false,
      selectedDept: '',
      onlyUnread: false,
      dateFrom: '',
      dateTo: '',
      showError: false
    }
  },
  computed: {
    departements() {
      const set = new Set()
      this.messages.forEach(m => {
        if (m.departement) set.add(m.departement)
      })
      return Array.from(set).sort()
    },
    filteredMessages() {
      return this.messages.filter(m => {
        if (this.selectedDept && m.departement !== this.selectedDept) return false
        if (this.onlyUnread && m.lu) return false

        if (this.dateFrom || this.dateTo) {
          if (!m.date_envoi) return false
          const d = new Date(m.date_envoi)
          if (this.dateFrom) {
            const from = new Date(this.dateFrom + 'T00:00:00')
            if (d < from) return false
          }
          if (this.dateTo) {
            const to = new Date(this.dateTo + 'T23:59:59')
            if (d > to) return false
          }
        }

        return true
      }).sort((a, b) => new Date(b.date_envoi) - new Date(a.date_envoi))
    },
    unreadCount() {
      return this.messages.filter(m => !m.lu).length
    },
    hasActiveFilters() {
      return this.selectedDept || this.onlyUnread || this.dateFrom || this.dateTo
    }
  },
  watch: {
    selected(newVal) {
      if (newVal) {
        this.reponse = newVal.reponse || ''
        this.message = ''
        this.success = false
        this.showError = false
      }
    }
  },
  created() {
    this.loadMessages()
  },
  methods: {
    async loadMessages() {
      this.loading = true
      this.message = ''
      try {
        const res = await fetch('http://localhost:8080/api/rh/messages')
        if (!res.ok) throw new Error('Erreur serveur')
        this.messages = await res.json()
        if (this.selected) {
          const updated = this.messages.find(m => m.id === this.selected.id)
          if (updated) this.selected = updated
        }
      } catch (e) {
        console.error(e)
        this.messages = []
      } finally {
        this.loading = false
      }
    },
    resetFilters() {
      this.selectedDept = ''
      this.onlyUnread = false
      this.dateFrom = ''
      this.dateTo = ''
    },
    selectMessage(m) {
      this.selected = m
    },
    async envoyerReponse() {
      if (!this.selected || !this.reponse.trim()) {
        this.showError = true
        return
      }
      
      this.sending = true
      this.message = ''
      this.success = false
      this.showError = false
      
      try {
        const res = await fetch(`http://localhost:8080/api/rh/messages/${this.selected.id}/reponse`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ reponse: this.reponse })
        })
        const data = await res.json()
        this.success = !!data.success
        this.message = data.message || (this.success ? 'Réponse envoyée avec succès' : '')
        
        if (data.success) {
          await this.loadMessages()
        }
      } catch (e) {
        console.error(e)
        this.message = "Erreur lors de l'envoi de la réponse"
      } finally {
        this.sending = false
      }
    },
    formatDate(v) {
      if (!v) return ''
      try {
        const d = new Date(v)
        return d.toLocaleDateString('fr-FR', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        })
      } catch (e) {
        return v
      }
    },
    truncateText(text, length) {
      if (!text) return ''
      return text.length > length ? text.substring(0, length) + '...' : text
    },
    getInitials(prenom, nom) {
      return `${prenom?.[0] || ''}${nom?.[0] || ''}`.toUpperCase()
    }
  }
}
</script>

<style scoped>
.rh-container {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
}

.rh-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 24px;
}

.rh-header-content {
  flex: 1;
}

.rh-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.rh-subtitle {
  font-size: 16px;
  color: #6b7280;
}

.rh-header-actions {
  display: flex;
  gap: 12px;
}

.rh-action-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.rh-action-button:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

/* Filtres */
.rh-filters-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

.rh-filters-header {
  margin-bottom: 16px;
}

.rh-filters-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.rh-filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  align-items: end;
}

.rh-filter-group {
  space-y: 6px;
}

.rh-filter-label {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: #374151;
}

.rh-filter-select, .rh-filter-input {
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: #fafafa;
}

.rh-filter-select:focus, .rh-filter-input:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.rh-filter-checkbox {
  display: flex;
  align-items: center;
}

.rh-checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
}

.rh-checkbox {
  display: none;
}

.rh-checkbox-custom {
  width: 16px;
  height: 16px;
  border: 2px solid #d1d5db;
  border-radius: 4px;
  background: #ffffff;
  transition: all 0.2s ease;
  position: relative;
}

.rh-checkbox:checked + .rh-checkbox-custom {
  background: #10b981;
  border-color: #10b981;
}

.rh-checkbox:checked + .rh-checkbox-custom::after {
  content: '';
  position: absolute;
  left: 4px;
  top: 1px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.rh-filter-actions {
  display: flex;
  justify-content: flex-end;
}

.rh-reset-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.rh-reset-button:hover {
  background: #f3f4f6;
  color: #374151;
}

/* Grille principale */
.rh-messaging-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.rh-section-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  height: fit-content;
  max-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.rh-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.rh-section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.rh-messages-stats {
  display: flex;
  gap: 8px;
}

.rh-stat-badge {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.rh-stat-alert {
  background: #fef3f2;
  color: #dc2626;
}

/* États de chargement et vides */
.rh-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 20px;
  color: #6b7280;
}

.rh-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top: 3px solid #10b981;
  border-radius: 50%;
  animation: rh-spin 1s linear infinite;
}

.rh-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: rh-spin 1s linear infinite;
}

.rh-empty-state, .rh-placeholder {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.rh-empty-icon, .rh-placeholder-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.rh-empty-title, .rh-placeholder-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.rh-empty-description, .rh-placeholder-description {
  font-size: 14px;
  margin-bottom: 16px;
}

/* Liste des messages */
.rh-messages-list {
  space-y: 12px;
  overflow-y: auto;
  flex: 1;
}

.rh-message-item {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #ffffff;
}

.rh-message-item:hover {
  border-color: #10b981;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.rh-message-selected {
  border-color: #10b981;
  background: #f0fdf4;
}

.rh-message-unread {
  background: #f0fdf4;
  border-left: 4px solid #10b981;
}

.rh-message-replied {
  border-left: 4px solid #d1fae5;
}

.rh-message-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 8px;
}

.rh-message-subject {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  flex: 1;
}

.rh-message-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 12px;
}

.rh-message-date {
  font-size: 11px;
  color: #6b7280;
  white-space: nowrap;
}

.rh-message-badge {
  background: #ef4444;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
}

.rh-message-preview {
  margin-bottom: 12px;
}

.rh-message-text {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.4;
}

.rh-message-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rh-message-sender {
  flex: 1;
}

.rh-sender-name {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  display: block;
}

.rh-sender-details {
  font-size: 11px;
  color: #6b7280;
}

.rh-message-status {
  font-size: 11px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.rh-status-replied {
  color: #059669;
}

.rh-status-pending {
  color: #d97706;
}

/* Détail du message */
.rh-message-detail {
  space-y: 20px;
}

.rh-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.rh-detail-sender {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rh-sender-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: white;
}

.rh-sender-info {
  space-y: 2px;
}

.rh-sender-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.rh-sender-details {
  display: flex;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
}

.rh-detail-meta {
  text-align: right;
}

.rh-meta-date {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.rh-meta-status {
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
}

.rh-detail-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #374151;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
}

.rh-detail-content {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}

.rh-message-content {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  font-size: 14px;
  color: #4b5563;
  line-height: 1.5;
}

/* Section réponse */
.rh-response-section {
  space-y: 12px;
}

.rh-required {
  color: #ef4444;
}

.rh-response-textarea {
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: all 0.2s ease;
  background: #fafafa;
}

.rh-response-textarea:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.rh-textarea-error {
  border-color: #ef4444;
}

.rh-char-count {
  font-size: 12px;
  color: #9ca3af;
  text-align: right;
}

.rh-existing-response {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  padding: 16px;
}

.rh-existing-header {
  margin-bottom: 8px;
}

.rh-existing-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #059669;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.rh-existing-content {
  font-size: 14px;
  color: #065f46;
  line-height: 1.5;
}

.rh-response-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* Boutons */
.rh-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.rh-button-primary {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.rh-button-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 8px 20px -5px rgba(16, 185, 129, 0.4);
}

.rh-button-outline {
  background: #ffffff;
  border: 1px solid #d1d5db;
  color: #374151;
}

.rh-button-outline:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.rh-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.rh-button-content, .rh-button-loading {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Messages */
.rh-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.rh-message-success {
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.rh-message-error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.rh-message-icon {
  display: flex;
}

@keyframes rh-spin {
  to { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 1024px) {
  .rh-messaging-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .rh-filters-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .rh-container {
    padding: 16px;
  }
  
  .rh-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .rh-section-card {
    padding: 20px;
  }
  
  .rh-detail-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .rh-detail-meta {
    text-align: left;
  }
  
  .rh-response-actions {
    flex-direction: column;
  }
  
  .rh-button {
    justify-content: center;
  }
}
</style>