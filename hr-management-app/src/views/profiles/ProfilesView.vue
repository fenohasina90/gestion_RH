<template>
  <div class="profiles-view">
    <div class="page-header">
      <h1 class="page-title">Gestion des Profils</h1>
      <div class="page-actions">
        <router-link to="/profiles/create" class="btn btn-primary">
          ➕ Ajouter un profil
        </router-link>
      </div>
    </div>

    <div class="filters-section">
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Rechercher par nom, poste ou compétences..."
          class="form-input search-input"
        />
      </div>
      
      <div class="filters">
        <select v-model="selectedExperience" class="form-input filter-select">
          <option value="">Toute expérience</option>
          <option value="junior">Junior (0-2 ans)</option>
          <option value="intermediate">Intermédiaire (3-5 ans)</option>
          <option value="senior">Senior (6+ ans)</option>
        </select>
        
        <select v-model="selectedEducation" class="form-input filter-select">
          <option value="">Tout niveau d'études</option>
          <option value="bac">Baccalauréat</option>
          <option value="licence">Licence</option>
          <option value="master">Master</option>
          <option value="doctorat">Doctorat</option>
        </select>
      </div>
    </div>

    <div class="profiles-grid">
      <div 
        v-for="profile in filteredProfiles" 
        :key="profile.id" 
        class="profile-card"
      >
        <div class="profile-header">
          <div class="profile-avatar">
            {{ profile.firstName.charAt(0) }}{{ profile.lastName.charAt(0) }}
          </div>
          <div class="profile-info">
            <h3 class="profile-name">{{ profile.firstName }} {{ profile.lastName }}</h3>
            <p class="profile-title">{{ profile.position }}</p>
            <p class="profile-experience">{{ profile.experience }} ans d'expérience</p>
          </div>
        </div>

        <div class="profile-details">
          <div class="detail-item">
            <span class="detail-label">Éducation:</span>
            <span class="detail-value">{{ profile.education }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Email:</span>
            <span class="detail-value">{{ profile.email }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Téléphone:</span>
            <span class="detail-value">{{ profile.phone }}</span>
          </div>
        </div>

        <div class="profile-skills">
          <h4>Compétences:</h4>
          <div class="skills-tags">
            <span 
              v-for="skill in profile.skills" 
              :key="skill" 
              class="skill-tag"
            >
              {{ skill }}
            </span>
          </div>
        </div>

        <div class="profile-actions">
          <router-link 
            :to="`/profiles/edit/${profile.id}`" 
            class="btn btn-secondary btn-sm"
          >
            ✏️ Modifier
          </router-link>
          <button 
            @click="publishProfile(profile)" 
            class="btn btn-success btn-sm"
            :disabled="profile.published"
          >
            <span v-if="profile.published">✅ Publié</span>
            <span v-else>📢 Publier</span>
          </button>
          <button 
            @click="deleteProfile(profile.id)" 
            class="btn btn-danger btn-sm"
            @click.stop
          >
            🗑️ Supprimer
          </button>
        </div>
      </div>
    </div>

    <div v-if="filteredProfiles.length === 0" class="empty-state">
      <div class="empty-icon">👥</div>
      <h3>Aucun profil trouvé</h3>
      <p>{{ searchQuery ? 'Aucun profil ne correspond à votre recherche.' : 'Commencez par ajouter votre premier profil.' }}</p>
      <router-link to="/profiles/create" class="btn btn-primary">
        Ajouter un profil
      </router-link>
    </div>

    <!-- Modal de confirmation de suppression -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal" @click.stop>
        <h3>Confirmer la suppression</h3>
        <p>Êtes-vous sûr de vouloir supprimer ce profil ? Cette action est irréversible.</p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="btn btn-secondary">
            Annuler
          </button>
          <button @click="confirmDelete" class="btn btn-danger">
            Supprimer
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProfilesView',
  data() {
    return {
      searchQuery: '',
      selectedExperience: '',
      selectedEducation: '',
      showDeleteModal: false,
      profileToDelete: null,
      profiles: [
        {
          id: 1,
          firstName: 'Jean',
          lastName: 'Dupont',
          position: 'Développeur Full Stack',
          experience: 5,
          education: 'Master en Informatique',
          email: 'jean.dupont@email.com',
          phone: '+33 1 23 45 67 89',
          skills: ['JavaScript', 'Vue.js', 'Node.js', 'MongoDB'],
          published: false
        },
        {
          id: 2,
          firstName: 'Marie',
          lastName: 'Martin',
          position: 'Designer UX/UI',
          experience: 3,
          education: 'Licence en Design',
          email: 'marie.martin@email.com',
          phone: '+33 1 98 76 54 32',
          skills: ['Figma', 'Adobe XD', 'Photoshop', 'Prototypage'],
          published: true
        },
        {
          id: 3,
          firstName: 'Pierre',
          lastName: 'Durand',
          position: 'Data Scientist',
          experience: 7,
          education: 'Doctorat en Mathématiques',
          email: 'pierre.durand@email.com',
          phone: '+33 1 11 22 33 44',
          skills: ['Python', 'R', 'Machine Learning', 'TensorFlow'],
          published: false
        },
        {
          id: 4,
          firstName: 'Sophie',
          lastName: 'Bernard',
          position: 'Chef de Projet',
          experience: 8,
          education: 'Master en Management',
          email: 'sophie.bernard@email.com',
          phone: '+33 1 55 66 77 88',
          skills: ['Gestion de projet', 'Scrum', 'Leadership', 'Communication'],
          published: true
        }
      ]
    }
  },
  computed: {
    filteredProfiles() {
      let filtered = this.profiles

      // Filtrage par recherche
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filtered = filtered.filter(profile => 
          profile.firstName.toLowerCase().includes(query) ||
          profile.lastName.toLowerCase().includes(query) ||
          profile.position.toLowerCase().includes(query) ||
          profile.skills.some(skill => skill.toLowerCase().includes(query))
        )
      }

      // Filtrage par expérience
      if (this.selectedExperience) {
        filtered = filtered.filter(profile => {
          switch (this.selectedExperience) {
            case 'junior':
              return profile.experience <= 2
            case 'intermediate':
              return profile.experience >= 3 && profile.experience <= 5
            case 'senior':
              return profile.experience >= 6
            default:
              return true
          }
        })
      }

      // Filtrage par éducation
      if (this.selectedEducation) {
        filtered = filtered.filter(profile => 
          profile.education.toLowerCase().includes(this.selectedEducation)
        )
      }

      return filtered
    }
  },
  methods: {
    publishProfile(profile) {
      if (!profile.published) {
        profile.published = true
        // Ici, vous pourriez ajouter une logique pour publier le profil comme annonce
        alert(`Profil de ${profile.firstName} ${profile.lastName} publié avec succès !`)
      }
    },
    deleteProfile(profileId) {
      this.profileToDelete = profileId
      this.showDeleteModal = true
    },
    confirmDelete() {
      if (this.profileToDelete) {
        this.profiles = this.profiles.filter(p => p.id !== this.profileToDelete)
        this.showDeleteModal = false
        this.profileToDelete = null
      }
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.filters-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  max-width: 400px;
}

.filters {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-select {
  min-width: 200px;
}

.profiles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.profile-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.profile-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.profile-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.profile-name {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 20px;
}

.profile-title {
  margin: 0 0 5px 0;
  color: #3498db;
  font-weight: 600;
}

.profile-experience {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.profile-details {
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  margin-bottom: 8px;
}

.detail-label {
  font-weight: 600;
  color: #555;
  min-width: 80px;
}

.detail-value {
  color: #333;
}

.profile-skills h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 16px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.skill-tag {
  background: #ecf0f1;
  color: #2c3e50;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.profile-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-state p {
  color: #666;
  margin-bottom: 30px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 30px;
  border-radius: 12px;
  max-width: 400px;
  width: 90%;
}

.modal h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.modal p {
  margin: 0 0 25px 0;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .profiles-grid {
    grid-template-columns: 1fr;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .profile-actions {
    justify-content: center;
  }
}
</style>
