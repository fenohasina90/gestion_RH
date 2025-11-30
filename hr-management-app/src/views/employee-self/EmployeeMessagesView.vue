<template>
  <div class="emp-layout">
    <EmployeeSidebar />
    <div class="emp-content">
      <div class="emp-container">
        <div class="emp-header">
          <h1 class="emp-title">Messagerie RH</h1>
          <p class="emp-subtitle">Échangez avec le service des ressources humaines</p>
        </div>

        <div class="emp-messaging-grid">
          <!-- Colonne gauche : historique des messages -->
          <div class="emp-conversations">
            <div class="emp-section-card">
              <div class="emp-section-header">
                <h2 class="emp-section-title">Mes échanges</h2>
                <div class="emp-section-actions">
                  <button @click="loadMessages" class="emp-icon-button" title="Actualiser">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M23 4v6h-6"></path>
                      <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path>
                    </svg>
                  </button>
                </div>
              </div>

              <div v-if="loading" class="emp-loading">
                <div class="emp-spinner"></div>
                <span>Chargement des messages...</span>
              </div>

              <div v-else-if="messages.length === 0" class="emp-empty-state">
                <div class="emp-empty-icon">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                  </svg>
                </div>
                <h3 class="emp-empty-title">Aucun message</h3>
                <p class="emp-empty-description">Vous n'avez pas encore d'échange avec la RH.</p>
              </div>

              <div v-else class="emp-messages-list">
                <div
                  v-for="m in messages"
                  :key="m.id"
                  class="emp-message-card"
                  :class="{ 'emp-message-unread': !m.lu }"
                >
                  <div class="emp-message-header">
                    <div class="emp-message-subject">{{ m.sujet }}</div>
                    <div class="emp-message-meta">
                      <span class="emp-message-date">{{ formatDate(m.date_envoi) }}</span>
                      <span v-if="!m.lu" class="emp-message-badge">Nouveau</span>
                    </div>
                  </div>
                  
                  <div class="emp-message-content">
                    <p class="emp-message-text">{{ truncateText(m.contenu, 120) }}</p>
                  </div>

                  <div v-if="m.reponse" class="emp-message-reply">
                    <div class="emp-reply-header">
                      <div class="emp-reply-label">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="9 10 4 15 9 20"></polyline>
                          <path d="M20 4v7a4 4 0 0 1-4 4H4"></path>
                        </svg>
                        Réponse RH
                      </div>
                      <span class="emp-reply-date">{{ formatDate(m.date_reponse) }}</span>
                    </div>
                    <p class="emp-reply-text">{{ truncateText(m.reponse, 100) }}</p>
                  </div>

                  <div class="emp-message-actions">
                    <button class="emp-action-link" @click="openMessage(m)">
                      Voir la discussion
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Colonne droite : nouveau message -->
          <div class="emp-new-message">
            <div class="emp-section-card">
              <div class="emp-section-header">
                <h2 class="emp-section-title">Nouveau message</h2>
                <div class="emp-section-info">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                  </svg>
                  Réponse sous 48h
                </div>
              </div>

              <form @submit.prevent="envoyer" class="emp-message-form">
                <div class="emp-form-group">
                  <label class="emp-label">
                    Sujet
                    <span class="emp-required">*</span>
                  </label>
                  <input 
                    v-model="form.sujet" 
                    type="text" 
                    class="emp-input" 
                    placeholder="Objet de votre message..."
                    required 
                  />
                </div>

                <div class="emp-form-group">
                  <label class="emp-label">
                    Votre message
                    <span class="emp-required">*</span>
                  </label>
                  <textarea 
                    v-model="form.contenu" 
                    rows="6" 
                    class="emp-textarea" 
                    placeholder="Décrivez votre demande ou question en détail..."
                    required
                  ></textarea>
                  <div class="emp-char-count">
                    {{ form.contenu.length }}/1000 caractères
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
                  <button
                    type="submit"
                    class="emp-button emp-button-primary"
                    :disabled="sending || !form.sujet || !form.contenu"
                  >
                    <span v-if="!sending" class="emp-button-content">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="22" y1="2" x2="11" y2="13"></line>
                        <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                      </svg>
                      Envoyer à la RH
                    </span>
                    <span v-else class="emp-button-loading">
                      <div class="emp-spinner-small"></div>
                      Envoi en cours...
                    </span>
                  </button>
                </div>
              </form>
            </div>

            <!-- Conseils d'utilisation -->
            <div class="emp-tips-card">
              <div class="emp-tips-header">
                <h3 class="emp-tips-title">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                  </svg>
                  Conseils pour votre message
                </h3>
              </div>
              <div class="emp-tips-content">
                <ul class="emp-tips-list">
                  <li>Précisez clairement l'objet de votre demande</li>
                  <li>Fournissez tous les détails nécessaires</li>
                  <li>Joignez des documents si nécessaire (via d'autres canaux)</li>
                  <li>Vérifiez l'exactitude des informations</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal de détail du message -->
        <div v-if="selectedMessage" class="emp-modal-overlay" @click="closeMessage">
          <div class="emp-modal" @click.stop>
            <div class="emp-modal-header">
              <h3 class="emp-modal-title">{{ selectedMessage.sujet }}</h3>
              <button class="emp-modal-close" @click="closeMessage">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>
            
            <div class="emp-modal-content">
              <div class="emp-conversation-thread">
                <!-- Message original -->
                <div class="emp-message-bubble emp-message-sent">
                  <div class="emp-bubble-header">
                    <strong>Vous</strong>
                    <span class="emp-bubble-time">{{ formatDate(selectedMessage.date_envoi) }}</span>
                  </div>
                  <div class="emp-bubble-content">
                    <p class="whitespace-pre-line">{{ selectedMessage.contenu }}</p>
                  </div>
                </div>

                <!-- Réponse RH -->
                <div v-if="selectedMessage.reponse" class="emp-message-bubble emp-message-received">
                  <div class="emp-bubble-header">
                    <strong>Service RH</strong>
                    <span class="emp-bubble-time">{{ formatDate(selectedMessage.date_reponse) }}</span>
                  </div>
                  <div class="emp-bubble-content">
                    <p class="whitespace-pre-line">{{ selectedMessage.reponse }}</p>
                  </div>
                </div>

                <div v-else class="emp-waiting-response">
                  <div class="emp-waiting-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10"></circle>
                      <polyline points="12 6 12 12 16 14"></polyline>
                    </svg>
                  </div>
                  <p class="emp-waiting-text">En attente d'une réponse du service RH</p>
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
  name: 'EmployeeMessagesView',
  components: { EmployeeSidebar },
  data() {
    return {
      messages: [],
      loading: false,
      sending: false,
      message: '',
      success: false,
      selectedMessage: null,
      form: {
        sujet: '',
        contenu: ''
      }
    }
  },
  created() {
    const stored = localStorage.getItem('selfEmployee')
    if (!stored) {
      this.$router.push('/espace-employe/login')
      return
    }
    this.loadMessages()
  },
  methods: {
    async loadMessages() {
      const stored = localStorage.getItem('selfEmployee')
      if (!stored) {
        this.$router.push('/espace-employe/login')
        return
      }
      const emp = JSON.parse(stored)
      this.loading = true
      try {
        const res = await fetch(`http://localhost:8080/api/employe/self/messages?idemploye=${emp.id}`)
        if (!res.ok) throw new Error('Erreur serveur')
        this.messages = await res.json()
        // Trier par date décroissante
        this.messages.sort((a, b) => new Date(b.date_envoi) - new Date(a.date_envoi))
      } catch (e) {
        console.error(e)
        this.messages = []
      } finally {
        this.loading = false
      }
    },
    async envoyer() {
      this.message = ''
      this.success = false
      const stored = localStorage.getItem('selfEmployee')
      if (!stored) {
        this.$router.push('/espace-employe/login')
        return
      }
      const emp = JSON.parse(stored)
      this.sending = true
      try {
        const res = await fetch('http://localhost:8080/api/employe/self/messages', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            idemploye: emp.id,
            sujet: this.form.sujet,
            contenu: this.form.contenu
          })
        })
        const data = await res.json()
        this.success = !!data.success
        this.message = data.message || ''
        if (data.success) {
          this.form.sujet = ''
          this.form.contenu = ''
          this.loadMessages()
        }
      } catch (e) {
        console.error(e)
        this.message = 'Erreur lors de l\'envoi du message'
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
    openMessage(message) {
      this.selectedMessage = message
    },
    closeMessage() {
      this.selectedMessage = null
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

.emp-messaging-grid {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 32px;
}

.emp-section-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}

.emp-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.emp-section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.emp-section-info {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 4px 8px;
  border-radius: 6px;
}

.emp-section-actions {
  display: flex;
  gap: 8px;
}

.emp-icon-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emp-icon-button:hover {
  background: #e5e7eb;
}

.emp-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px 20px;
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

.emp-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: emp-spin 1s linear infinite;
}

.emp-empty-state {
  text-align: center;
  padding: 40px 20px;
}

.emp-empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.emp-empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.emp-empty-description {
  font-size: 14px;
  color: #6b7280;
  max-width: 200px;
  margin: 0 auto;
}

.emp-messages-list {
  space-y: 16px;
  max-height: 600px;
  overflow-y: auto;
}

.emp-message-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.2s ease;
  cursor: pointer;
  position: relative;
}

.emp-message-card:hover {
  border-color: #10b981;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.emp-message-unread {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.emp-message-unread::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #10b981;
  border-radius: 12px 0 0 12px;
}

.emp-message-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 8px;
}

.emp-message-subject {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  flex: 1;
}

.emp-message-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 12px;
}

.emp-message-date {
  font-size: 11px;
  color: #6b7280;
  white-space: nowrap;
}

.emp-message-badge {
  background: #ef4444;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
}

.emp-message-content {
  margin-bottom: 12px;
}

.emp-message-text {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.4;
}

.emp-message-reply {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  margin-top: 12px;
}

.emp-reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.emp-reply-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  font-weight: 600;
  color: #059669;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.emp-reply-date {
  font-size: 10px;
  color: #9ca3af;
}

.emp-reply-text {
  font-size: 12px;
  color: #4b5563;
  line-height: 1.4;
}

.emp-message-actions {
  text-align: right;
}

.emp-action-link {
  background: none;
  border: none;
  color: #10b981;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: underline;
  transition: color 0.2s ease;
}

.emp-action-link:hover {
  color: #059669;
}

.emp-message-form {
  space-y: 20px;
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

.emp-required {
  color: #ef4444;
}

.emp-input, .emp-textarea {
  width: 100%;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: #fafafa;
  font-family: inherit;
}

.emp-input:focus, .emp-textarea:focus {
  outline: none;
  border-color: #10b981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.emp-textarea {
  resize: vertical;
  min-height: 120px;
}

.emp-char-count {
  font-size: 12px;
  color: #9ca3af;
  text-align: right;
}

.emp-form-actions {
  text-align: right;
  padding-top: 8px;
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

.emp-button-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.4);
}

.emp-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.emp-button-content, .emp-button-loading {
  display: flex;
  align-items: center;
  gap: 8px;
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

.emp-tips-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.emp-tips-header {
  margin-bottom: 12px;
}

.emp-tips-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.emp-tips-content {
  space-y: 8px;
}

.emp-tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
  space-y: 6px;
}

.emp-tips-list li {
  font-size: 13px;
  color: #6b7280;
  position: relative;
  padding-left: 16px;
}

.emp-tips-list li::before {
  content: '•';
  color: #10b981;
  position: absolute;
  left: 0;
}

/* Modal */
.emp-modal-overlay {
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
  padding: 20px;
}

.emp-modal {
  background: #ffffff;
  border-radius: 16px;
  width: 100%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.emp-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.emp-modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.emp-modal-close {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.emp-modal-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.emp-modal-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.emp-conversation-thread {
  space-y: 16px;
}

.emp-message-bubble {
  border-radius: 12px;
  padding: 16px;
  max-width: 80%;
}

.emp-message-sent {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  margin-left: auto;
}

.emp-message-received {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  margin-right: auto;
}

.emp-bubble-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.emp-bubble-header strong {
  font-size: 13px;
  color: #374151;
}

.emp-bubble-time {
  font-size: 11px;
  color: #6b7280;
}

.emp-bubble-content {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.5;
}

.emp-waiting-response {
  text-align: center;
  padding: 32px 20px;
  color: #6b7280;
}

.emp-waiting-icon {
  margin-bottom: 12px;
  color: #d1d5db;
}

.emp-waiting-text {
  font-size: 14px;
  font-style: italic;
}

@keyframes emp-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .emp-messaging-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .emp-new-message {
    order: -1;
  }
}

@media (max-width: 768px) {
  .emp-container {
    padding: 24px 16px;
  }
  
  .emp-section-card {
    padding: 20px;
  }
  
  .emp-message-card {
    padding: 12px;
  }
  
  .emp-message-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .emp-message-meta {
    margin-left: 0;
    align-self: flex-start;
  }
  
  .emp-message-bubble {
    max-width: 90%;
  }
  
  .emp-modal {
    margin: 20px;
    max-height: calc(100vh - 40px);
  }
  
  .emp-modal-header,
  .emp-modal-content {
    padding: 20px;
  }
}
</style>