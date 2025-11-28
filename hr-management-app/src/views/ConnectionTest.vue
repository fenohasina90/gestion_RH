<template>
  <div class="connection-test">
    <div class="header">
      <h1>Test de Connexion Backend</h1>
      <p>Vérifiez la connectivité avec le serveur backend</p>
    </div>

    <div class="test-container">
      <div class="test-info">
        <div class="info-item">
          <strong>URL Backend:</strong>
          <span class="url">{{ backendUrl }}</span>
        </div>
        <div class="info-item">
          <strong>Statut:</strong>
          <span class="status" :class="statusClass">{{ connectionStatus }}</span>
        </div>
      </div>

      <div class="test-actions">
        <button 
          @click="testConnection" 
          :disabled="isLoading"
          class="test-button"
        >
          <span v-if="isLoading">🔄 Test en cours...</span>
          <span v-else>🔌 Tester la Connexion</span>
        </button>
      </div>

      <div v-if="testResult" class="test-result" :class="testResult.success ? 'success' : 'error'">
        <h3>{{ testResult.success ? '✅ Connexion Réussie' : '❌ Échec de Connexion' }}</h3>
        <div class="result-details">
          <p><strong>Temps de réponse:</strong> {{ testResult.responseTime }}ms</p>
          <p><strong>Message:</strong> {{ testResult.message }}</p>
          <p><strong>Timestamp:</strong> {{ testResult.timestamp }}</p>
        </div>
      </div>

      <div v-if="error" class="error-message">
        <h3>❌ Erreur</h3>
        <p>{{ error }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConnectionTest',
  data() {
    return {
      isLoading: false,
      testResult: null,
      error: null,
      backendUrl: import.meta.env.VITE_API_BASE_URL
    }
  },
  computed: {
    connectionStatus() {
      if (this.isLoading) return 'Test en cours...'
      if (this.testResult) {
        return this.testResult.success ? 'Connecté' : 'Déconnecté'
      }
      return 'Non testé'
    },
    statusClass() {
      if (this.isLoading) return 'loading'
      if (this.testResult) {
        return this.testResult.success ? 'connected' : 'disconnected'
      }
      return 'untested'
    }
  },
  methods: {
    async testConnection() {
      this.isLoading = true
      this.testResult = null
      this.error = null
      
      const startTime = Date.now()
      
      try {
        // Test de ping simple vers le backend
        const response = await fetch(`${this.backendUrl}/api/test-connection`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
          // Timeout de 5 secondes
          signal: AbortSignal.timeout(5000)
        })
        
        const endTime = Date.now()
        const responseTime = endTime - startTime
        
        if (response.ok) {
          const data = await response.text()
          this.testResult = {
            success: true,
            message: data || 'Backend accessible',
            responseTime,
            timestamp: new Date().toLocaleString('fr-FR')
          }
        } else {
          this.testResult = {
            success: false,
            message: `Erreur HTTP ${response.status}: ${response.statusText}`,
            responseTime,
            timestamp: new Date().toLocaleString('fr-FR')
          }
        }
      } catch (err) {
        const endTime = Date.now()
        const responseTime = endTime - startTime
        
        let errorMessage = 'Erreur de connexion'
        if (err.name === 'AbortError') {
          errorMessage = 'Timeout - Le serveur ne répond pas'
        } else if (err.message.includes('fetch')) {
          errorMessage = 'Impossible de joindre le serveur'
        } else {
          errorMessage = err.message
        }
        
        this.testResult = {
          success: false,
          message: errorMessage,
          responseTime,
          timestamp: new Date().toLocaleString('fr-FR')
        }
      } finally {
        this.isLoading = false
      }
    }
  },
  mounted() {
    // Test automatique au chargement de la page
    this.testConnection()
  }
}
</script>

<style scoped>
.connection-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.header p {
  color: #7f8c8d;
  font-size: 16px;
}

.test-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.test-info {
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ecf0f1;
}

.info-item:last-child {
  border-bottom: none;
}

.url {
  font-family: 'Courier New', monospace;
  background: #f8f9fa;
  padding: 5px 10px;
  border-radius: 5px;
  color: #2c3e50;
}

.status {
  padding: 5px 15px;
  border-radius: 20px;
  font-weight: bold;
  text-transform: uppercase;
  font-size: 12px;
}

.status.connected {
  background: #d4edda;
  color: #155724;
}

.status.disconnected {
  background: #f8d7da;
  color: #721c24;
}

.status.loading {
  background: #fff3cd;
  color: #856404;
}

.status.untested {
  background: #e2e3e5;
  color: #6c757d;
}

.test-actions {
  text-align: center;
  margin-bottom: 30px;
}

.test-button {
  background: #3498db;
  color: white;
  border: none;
  padding: 15px 30px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.test-button:hover:not(:disabled) {
  background: #2980b9;
  transform: translateY(-2px);
}

.test-button:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
}

.test-result {
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
}

.test-result.success {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  color: #155724;
}

.test-result.error {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
}

.test-result h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
}

.result-details p {
  margin: 8px 0;
  font-size: 14px;
}

.error-message {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
}

.error-message h3 {
  margin: 0 0 10px 0;
}

@media (max-width: 768px) {
  .connection-test {
    padding: 10px;
  }
  
  .test-container {
    padding: 20px;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
