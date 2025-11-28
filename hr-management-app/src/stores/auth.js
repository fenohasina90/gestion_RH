import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isAuthenticated: false,
    token: localStorage.getItem('auth_token') || null
  }),

  getters: {
    isLoggedIn: (state) => state.isAuthenticated || state.token !== null
  },

  actions: {
    setUser(user) {
      this.user = user
      this.isAuthenticated = true
    },

    setToken(token) {
      this.token = token
      localStorage.setItem('auth_token', token)
    },

    async logout() {
      try {
        // Appeler l'endpoint de déconnexion backend
        if (this.token) {
          await axios.post('/api/auth/logout', {}, {
            headers: {
              'Authorization': this.token
            }
          })
        }
      } catch (error) {
        console.error('Erreur lors de la déconnexion:', error)
      } finally {
        // Nettoyer le state local même si l'appel backend échoue
        this.user = null
        this.isAuthenticated = false
        this.token = null
        localStorage.removeItem('auth_token')
      }
    },

    // Vérifier si l'utilisateur est connecté au démarrage
    checkAuth() {
      if (this.token) {
        // Ici on pourrait vérifier la validité du token avec le backend
        this.isAuthenticated = true
        // Simuler un utilisateur connecté pour éviter la redirection
        if (!this.user) {
          this.user = { id: 1, email: 'user@example.com' }
        }
      }
    },

    // Initialiser l'authentification au démarrage de l'app
    initAuth() {
      const token = localStorage.getItem('auth_token')
      if (token) {
        this.token = token
        this.isAuthenticated = true
        this.user = { id: 1, email: 'user@example.com' }
      }
    }
  }
})
