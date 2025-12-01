<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Chatbot RH</h1>
        <p class="subtitle">Posez vos questions sur les congés, la paie, les règles RH, etc.</p>
      </div>
    </div>

    <div class="chat-container">
      <div class="chat-panel">
        <div class="messages" ref="messagesEl">
          <div v-for="(m, idx) in messages" :key="idx" class="msg-row" :class="m.from">
            <div class="msg-bubble">
              <div class="msg-meta">
                <span class="msg-author">{{ m.from === 'user' ? 'Vous' : 'Chatbot RH' }}</span>
                <span class="msg-time">{{ formatTime(m.at) }}</span>
              </div>
              <div class="msg-text">{{ m.text }}</div>
            </div>
          </div>
          <div v-if="loading" class="msg-row bot">
            <div class="msg-bubble">
              <div class="msg-meta"><span class="msg-author">Chatbot RH</span></div>
              <div class="msg-text">Réflexion en cours...</div>
            </div>
          </div>
        </div>
        <form class="input-bar" @submit.prevent="send">
          <input v-model="question" type="text" class="input" placeholder="Écrivez votre question RH..." />
          <input v-model="matricule" type="text" class="input small" placeholder="Matricule (optionnel)" />
          <button class="btn" :disabled="!question || loading">Envoyer</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'RhChatbotView',
  data () {
    return {
      question: '',
      matricule: '',
      loading: false,
      messages: []
    }
  },
  methods: {
    async send () {
      const q = this.question.trim()
      if (!q) return
      this.messages.push({ from: 'user', text: q, at: new Date() })
      this.question = ''
      this.loading = true
      try {
        const payload = { question: q, matricule: this.matricule || null }
        const res = await axios.post('/api/chat/rh', payload)
        const answer = res.data && res.data.answer ? res.data.answer : 'Aucune réponse.'
        this.messages.push({ from: 'bot', text: answer, at: new Date() })
        this.$nextTick(this.scrollToBottom)
      } catch (e) {
        this.messages.push({ from: 'bot', text: 'Erreur lors de l\'appel au chatbot.', at: new Date() })
      } finally {
        this.loading = false
      }
    },
    scrollToBottom () {
      const el = this.$refs.messagesEl
      if (el) el.scrollTop = el.scrollHeight
    },
    formatTime (d) {
      if (!d) return ''
      const dt = d instanceof Date ? d : new Date(d)
      return dt.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
    }
  }
}
</script>

<style scoped>
.page {
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 16px;
}

.subtitle {
  margin: 4px 0 0;
  font-size: 14px;
  color: #6b7280;
}

.chat-container {
  display: flex;
}

.chat-panel {
  flex: 1;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(15,23,42,0.08);
  display: flex;
  flex-direction: column;
  height: 520px;
}

.messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.msg-row {
  display: flex;
  margin-bottom: 10px;
}

.msg-row.user {
  justify-content: flex-end;
}

.msg-row.bot {
  justify-content: flex-start;
}

.msg-bubble {
  max-width: 70%;
  background: #f3f4f6;
  border-radius: 12px;
  padding: 8px 10px;
  font-size: 14px;
}

.msg-row.user .msg-bubble {
  background: #059669;
  color: #ffffff;
}

.msg-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  opacity: 0.8;
  margin-bottom: 2px;
}

.msg-text {
  white-space: pre-wrap;
}

.input-bar {
  display: flex;
  gap: 8px;
  padding: 10px 12px;
  border-top: 1px solid #e5e7eb;
}

.input {
  flex: 1;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  padding: 6px 10px;
  font-size: 14px;
}

.input.small {
  flex: 0 0 160px;
}

.btn {
  border-radius: 999px;
  border: none;
  padding: 6px 14px;
  background: #059669;
  color: #ffffff;
  font-size: 14px;
  cursor: pointer;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
