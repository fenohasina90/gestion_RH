import { defineStore } from 'pinia'
import axios from 'axios'

export const useCandidateAuthStore = defineStore('candidateAuth', {
  state: () => ({
    candidate: null,
    isAuthenticated: false,
    token: localStorage.getItem('candidate_auth_token') || null
  }),

  getters: {
    isLoggedIn: (state) => state.isAuthenticated || state.token !== null
  },

  actions: {
    setCandidate(candidate) {
      this.candidate = candidate
      this.isAuthenticated = true
    },

    setToken(token) {
      this.token = token
      localStorage.setItem('candidate_auth_token', token)
    },

    async logout() {
      try {
        // Appeler l'endpoint de déconnexion backend
        if (this.token) {
          await axios.post('/api/comptecandidat/logout', {}, {
            headers: {
              'Authorization': this.token
            }
          })
        }
      } catch (error) {
        console.error('Erreur lors de la déconnexion:', error)
      } finally {
        // Nettoyer le state local même si l'appel backend échoue
        this.candidate = null
        this.isAuthenticated = false
        this.token = null
        localStorage.removeItem('candidate_auth_token')
      }
    },

    async login(email, motdepasse) {
      try {
        const response = await axios.post('/api/comptecandidat/login', {
          email,
          motdepasse
        })
        
        if (response.data.success) {
          this.setCandidate(response.data.candidate)
          this.setToken(response.data.token)
          return { success: true }
        } else {
          return { success: false, message: response.data.message }
        }
      } catch (error) {
        console.error('Erreur de connexion:', error)
        return { success: false, message: 'Erreur de connexion' }
      }
    },

    async register(email, motdepasse) {
      try {
        const response = await axios.post('/api/comptecandidat/register', {
          email,
          motdepasse
        })
        
        if (response.data.success) {
          this.setCandidate(response.data.candidate)
          this.setToken(response.data.token)
          return { success: true }
        } else {
          return { success: false, message: response.data.message }
        }
      } catch (error) {
        console.error('Erreur d\'inscription:', error)
        return { success: false, message: 'Erreur d\'inscription' }
      }
    },

    // Vérifier si l'utilisateur est connecté au démarrage
    checkAuth() {
      if (this.token) {
        this.isAuthenticated = true
        // Ici on pourrait vérifier la validité du token avec le backend
      }
    },

    // Initialiser l'authentification au démarrage de l'app
    async initAuth() {
      const token = localStorage.getItem('candidate_auth_token')
      if (token) {
        this.token = token
        this.isAuthenticated = true
        // Récupérer les infos du candidat depuis l'API
        try {
          const response = await axios.get('/api/comptecandidat/profile', {
            headers: {
              'Authorization': token
            }
          })
          if (response.data.success) {
            this.candidate = response.data.candidate
          } else {
            // Token invalide, nettoyer
            this.logout()
          }
        } catch (error) {
          console.error('Erreur lors de la récupération du profil:', error)
          // Token invalide, nettoyer
          this.logout()
        }
      }
    }
  }
})
