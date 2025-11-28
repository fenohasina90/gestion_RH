<template>
  <div class="contract-view">
    <div class="container">
      <div class="contract-header">
        <h1>Contrat d'Essai</h1>
        <div class="contract-actions">
          <button @click="printContract" class="btn btn-secondary">
            🖨️ Imprimer
          </button>
          <button @click="exportToPDF" class="btn btn-primary">
            📄 Exporter en PDF
          </button>
          <button @click="confirmHiring" class="btn btn-success" :disabled="isProcessing">
            ✅ Confirmer l'embauche
          </button>
        </div>
      </div>

      <div v-if="isLoading" class="loading">
        <p>Chargement du contrat...</p>
      </div>

      <div v-else-if="contract" class="contract-document">
        <div class="contract-content">
          <div class="company-header">
            <h2>CONTRAT DE TRAVAIL À DURÉE DÉTERMINÉE</h2>
            <h3>(Contrat d'essai)</h3>
          </div>

          <div class="contract-section">
            <h4>ENTRE LES SOUSSIGNÉS :</h4>
            <p><strong>L'EMPLOYEUR :</strong></p>
            <p>Société : Dil'S SOCIETY</p>
            <p>Adresse : 108, Antsirabe, Madagascar</p>
            <p>Représentée par : Odilah Randria</p>
            <p>Ci-après dénommée "l'Employeur"</p>
          </div>

          <div class="contract-section">
            <p><strong>ET</strong></p>
            <p><strong>LE SALARIÉ :</strong></p>
            <p>Nom : {{ contract.candidat?.nom || contract.idemploye?.nom }}</p>
            <p>Prénom : {{ contract.candidat?.prenom || contract.idemploye?.prenom }}</p>
            <p>Email : {{ getCandidatEmail(contract) }}</p>
            <p>Adresse : {{ contract.candidat?.adresse || contract.idemploye?.adresse }}</p>
            <p>Ci-après dénommé(e) "le Salarié"</p>
          </div>

          <div class="contract-section">
            <h4>IL A ÉTÉ CONVENU CE QUI SUIT :</h4>
            
            <div class="article">
              <h5>Article 1 - OBJET DU CONTRAT</h5>
              <p>Le présent contrat a pour objet l'engagement du salarié en qualité de <strong>{{ contract.poste }}</strong>.</p>
            </div>

            <div class="article">
              <h5>Article 2 - DURÉE DU CONTRAT</h5>
              <p>Le présent contrat est conclu pour une durée déterminée de <strong>{{ contract.duration }} mois</strong>.</p>
              <p>Il prendra effet le <strong>{{ formatDate(contract.startDate) }}</strong> et se terminera le <strong>{{ formatDate(contract.endDate) }}</strong>.</p>
            </div>

            <div class="article">
              <h5>Article 3 - PÉRIODE D'ESSAI</h5>
              <p>Le présent contrat est soumis à une période d'essai dont la durée est fixée conformément aux dispositions légales en vigueur.</p>
            </div>

            <div class="article">
              <h5>Article 4 - FONCTIONS</h5>
              <p>Le salarié s'engage à exercer les fonctions de <strong>{{ contract.poste }}</strong> et toutes missions qui lui seront confiées par sa hiérarchie dans le cadre de son emploi.</p>
            </div>

            <div class="article">
              <h5>Article 5 - LIEU DE TRAVAIL</h5>
              <p>Le salarié exercera ses fonctions au siège social de l'entreprise ou en tout autre lieu désigné par l'employeur.</p>
            </div>

            <div class="article">
              <h5>Article 6 - RÉMUNÉRATION</h5>
              <p>La rémunération mensuelle brute est fixée à <strong>{{ formatSalaire(contract.salaire) }}</strong>.</p>
              <p>Cette rémunération sera versée mensuellement, conformément aux dispositions de la convention collective applicable.</p>
            </div>

            <div class="article">
              <h5>Article 7 - HORAIRES DE TRAVAIL</h5>
              <p>Les horaires de travail sont ceux en vigueur dans l'entreprise, conformément à la réglementation du travail.</p>
            </div>
          </div>

          <div class="contract-section">
            <div class="signatures">
              <div class="signature-block">
                <p><strong>L'EMPLOYEUR</strong></p>
                <p>Date : ________________</p>
                <p>Signature :</p>
                <div class="signature-line"></div>
              </div>
              <div class="signature-block">
                <p><strong>LE SALARIÉ</strong></p>
                <p>Date : ________________</p>
                <p>Signature :</p>
                <div class="signature-line"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="error">
        <p>Erreur lors du chargement du contrat.</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContractView',
  data() {
    return {
      contract: null,
      isLoading: false,
      isProcessing: false
    }
  },
  async mounted() {
    await this.loadContract()
  },
  methods: {
    async loadContract() {
      this.isLoading = true
      try {
        const contractId = this.$route.params.id
        const response = await fetch(`/api/contrats/${contractId}`)
        if (response.ok) {
          this.contract = await response.json()
        } else {
          console.error('Erreur lors du chargement du contrat')
        }
      } catch (error) {
        console.error('Erreur:', error)
      } finally {
        this.isLoading = false
      }
    },

    printContract() {
      window.print()
    },

    async exportToPDF() {
      try {
        // Dynamically import html2pdf
        const html2pdf = (await import('html2pdf.js')).default
        
        const element = document.querySelector('.contract-document')
        const options = {
          margin: [10, 10, 10, 10],
          filename: `Contrat_${this.contract.candidat?.nom || 'Employe'}_${this.contract.candidat?.prenom || ''}.pdf`,
          image: { type: 'jpeg', quality: 0.98 },
          html2canvas: { 
            scale: 2,
            useCORS: true,
            backgroundColor: '#ffffff'
          },
          jsPDF: { 
            unit: 'mm', 
            format: 'a4', 
            orientation: 'portrait',
            putOnlyUsedFonts: true,
            floatPrecision: 16
          }
        }
        
        await html2pdf().set(options).from(element).save()
      } catch (error) {
        console.error('Erreur lors de l\'export PDF:', error)
        alert('Erreur lors de l\'export PDF. Veuillez réessayer.')
      }
    },

    async confirmHiring() {
      if (!confirm('Êtes-vous sûr de vouloir confirmer l\'embauche de ce candidat ?')) {
        return
      }

      this.isProcessing = true
      try {
        const response = await fetch(`http://localhost:8080/api/contrats/${this.contract.id}/confirm-hiring`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          }
        })

        if (response.ok) {
          alert('Embauche confirmée avec succès ! Le candidat est maintenant un employé.')
          this.$router.push('/contracts')
        } else {
          alert('Erreur lors de la confirmation de l\'embauche')
        }
      } catch (error) {
        console.error('Erreur:', error)
        alert('Erreur lors de la confirmation de l\'embauche')
      } finally {
        this.isProcessing = false
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },

    formatSalaire(salaire) {
      if (!salaire) return 'Non spécifié'
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
      }).format(salaire)
    },

    getCandidatEmail(contract) {
      console.log('Contract object:', contract)
      console.log('Contract candidat:', contract.candidat)
      
      // Essayer d'abord depuis le candidat transient
      if (contract.candidat?.idcomptecandidat?.email) {
        console.log('Email trouvé via idcomptecandidat:', contract.candidat.idcomptecandidat.email)
        return contract.candidat.idcomptecandidat.email
      }
      
      // Sinon depuis le candidat direct
      if (contract.candidat?.email) {
        console.log('Email trouvé directement:', contract.candidat.email)
        return contract.candidat.email
      }
      
      // Essayer depuis l'employé si pas de candidat
      if (contract.idemploye?.email) {
        console.log('Email trouvé via employé:', contract.idemploye.email)
        return contract.idemploye.email
      }
      
      console.log('Aucun email trouvé')
      // Valeur par défaut
      return 'Email non spécifié'
    }
  }
}
</script>

<style scoped>
.contract-view {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.contract-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: #2c3e50;
  color: white;
}

.contract-header h1 {
  margin: 0;
  font-size: 24px;
}

.contract-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-success {
  background: #28a745;
  color: white;
}

.btn-success:hover {
  background: #218838;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading, .error {
  text-align: center;
  padding: 40px;
  color: #6c757d;
}

.contract-document {
  padding: 40px;
}

.company-header {
  text-align: center;
  margin-bottom: 40px;
  border-bottom: 2px solid #2c3e50;
  padding-bottom: 20px;
}

.company-header h2 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 20px;
}

.company-header h3 {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
  font-weight: normal;
}

.contract-section {
  margin-bottom: 30px;
}

.contract-section h4 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 16px;
}

.contract-section p {
  margin: 8px 0;
  line-height: 1.6;
  color: #34495e;
}

.article {
  margin-bottom: 25px;
  padding: 15px;
  background: #f8f9fa;
  border-left: 4px solid #3498db;
}

.article h5 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.article p {
  margin: 8px 0;
  line-height: 1.6;
}

.signatures {
  display: flex;
  justify-content: space-between;
  margin-top: 50px;
  padding-top: 30px;
  border-top: 1px solid #dee2e6;
}

.signature-block {
  width: 45%;
  text-align: center;
}

.signature-block p {
  margin: 5px 0;
  font-weight: 600;
}

.signature-line {
  height: 60px;
  border-bottom: 1px solid #000;
  margin-top: 20px;
}

/* Styles pour l'export PDF - préserver les couleurs */
.contract-document {
  background: white;
  color: #2c3e50;
}

.contract-document .company-header {
  color: #2c3e50 !important;
  border-bottom: 2px solid #2c3e50 !important;
}

.contract-document .article {
  background: #f8f9fa !important;
  border-left: 4px solid #3498db !important;
  color: #2c3e50 !important;
}

.contract-document .article h5 {
  color: #2c3e50 !important;
}

.contract-document .contract-section h4 {
  color: #2c3e50 !important;
}

.contract-document .contract-section p {
  color: #34495e !important;
}

@media print {
  .contract-header {
    background: white !important;
    color: black !important;
    border-bottom: 2px solid #000;
  }
  
  .contract-actions {
    display: none !important;
  }
  
  .contract-view {
    background: white !important;
    padding: 0 !important;
  }
  
  .container {
    box-shadow: none !important;
    border-radius: 0 !important;
  }
  
  .article {
    background: #f8f9fa !important;
    border-left: 4px solid #3498db !important;
    -webkit-print-color-adjust: exact !important;
    print-color-adjust: exact !important;
  }
  
  .company-header {
    color: #2c3e50 !important;
    border-bottom: 2px solid #2c3e50 !important;
    -webkit-print-color-adjust: exact !important;
    print-color-adjust: exact !important;
  }
}
</style>
