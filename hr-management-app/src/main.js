import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/style.css'
import { useAuthStore } from './stores/auth'
import { useCandidateAuthStore } from './stores/candidateAuth'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Initialiser l'authentification au démarrage
const authStore = useAuthStore()
const candidateAuthStore = useCandidateAuthStore()

authStore.initAuth()
candidateAuthStore.initAuth()

app.mount('#app')
