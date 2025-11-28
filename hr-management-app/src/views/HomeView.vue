<template>
  <div class="home">
    <div class="welcome-section">
      <h1 class="page-title">Tableau de Bord - Gestion RH</h1>
      <p class="subtitle">Bienvenue dans votre système de gestion des ressources humaines</p>
    </div>

    <div class="dashboard-grid">
      <div class="dashboard-card">
        <div class="card-icon">👥</div>
        <div class="card-content">
          <h3>Profils</h3>
          <p class="card-number">{{ stats.profiles }}</p>
          <p class="card-description">Profils enregistrés</p>
          <router-link to="/profiles" class="btn btn-primary">Voir les profils</router-link>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-icon">📢</div>
        <div class="card-content">
          <h3>Annonces</h3>
          <p class="card-number">{{ stats.announcements }}</p>
          <p class="card-description">Annonces actives</p>
          <router-link to="/announcements" class="btn btn-primary">Voir les annonces</router-link>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-icon">📄</div>
        <div class="card-content">
          <h3>Candidatures</h3>
          <p class="card-number">{{ stats.applications }}</p>
          <p class="card-description">Candidatures reçues</p>
          <router-link to="/applications" class="btn btn-primary">Voir les candidatures</router-link>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-icon">❓</div>
        <div class="card-content">
          <h3>Tests QCM</h3>
          <p class="card-number">{{ stats.qcm }}</p>
          <p class="card-description">Tests disponibles</p>
          <router-link to="/qcm/take" class="btn btn-primary">Passer un test</router-link>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-icon">📅</div>
        <div class="card-content">
          <h3>Entretiens</h3>
          <p class="card-number">{{ stats.interviews }}</p>
          <p class="card-description">Entretiens programmés</p>
          <router-link to="/interviews/calendar" class="btn btn-primary">Voir le calendrier</router-link>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-icon">📋</div>
        <div class="card-content">
          <h3>Contrats</h3>
          <p class="card-number">{{ stats.contracts }}</p>
          <p class="card-description">Contrats d'essai</p>
          <router-link to="/contracts" class="btn btn-primary">Voir les contrats</router-link>
        </div>
      </div>
    </div>

    <div class="recent-activity">
      <h2>Activité Récente</h2>
      <div class="activity-list">
        <div class="activity-item" v-for="activity in recentActivities" :key="activity.id">
          <div class="activity-icon">{{ activity.icon }}</div>
          <div class="activity-content">
            <p class="activity-text">{{ activity.text }}</p>
            <p class="activity-time">{{ activity.time }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  data() {
    return {
      stats: {
        profiles: 45,
        announcements: 12,
        applications: 28,
        qcm: 8,
        interviews: 15,
        contracts: 6
      },
      recentActivities: [
        {
          id: 1,
          icon: '👤',
          text: 'Nouveau profil ajouté: Jean Dupont - Développeur Senior',
          time: 'Il y a 2 heures'
        },
        {
          id: 2,
          icon: '📢',
          text: 'Nouvelle annonce publiée: Développeur Full Stack',
          time: 'Il y a 4 heures'
        },
        {
          id: 3,
          icon: '📄',
          text: 'Candidature reçue pour le poste de Designer UX',
          time: 'Il y a 6 heures'
        },
        {
          id: 4,
          icon: '📅',
          text: 'Entretien programmé avec Marie Martin',
          time: 'Il y a 1 jour'
        }
      ]
    }
  }
}
</script>

<style scoped>
.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.subtitle {
  font-size: 18px;
  color: #666;
  margin-top: 10px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.dashboard-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  align-items: center;
  gap: 20px;
}

.dashboard-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.card-icon {
  font-size: 48px;
  opacity: 0.8;
}

.card-content h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 20px;
}

.card-number {
  font-size: 32px;
  font-weight: bold;
  color: #3498db;
  margin: 10px 0;
}

.card-description {
  color: #666;
  margin-bottom: 15px;
}

.recent-activity {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.recent-activity h2 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.activity-item:hover {
  background: #e9ecef;
}

.activity-icon {
  font-size: 24px;
}

.activity-content {
  flex: 1;
}

.activity-text {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-weight: 500;
}

.activity-time {
  margin: 0;
  color: #666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  
  .dashboard-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
