<template>
  <div class="interview-calendar-view">
    <div class="page-header">
      <h1 class="page-title">Calendrier des Entretiens</h1>
      <div class="page-actions">
        <button @click="showCreateModal = true" class="btn btn-primary">
          ➕ Programmer un entretien
        </button>
        <router-link to="/interviews/results" class="btn btn-secondary">
          📊 Voir les résultats
        </router-link>
      </div>
    </div>

    <!-- Filtres et navigation -->
    <div class="calendar-controls">
      <div class="view-controls">
        <button 
          @click="currentView = 'month'" 
          class="btn btn-sm"
          :class="{ 'btn-primary': currentView === 'month', 'btn-secondary': currentView !== 'month' }"
        >
          Mois
        </button>
        <button 
          @click="currentView = 'week'" 
          class="btn btn-sm"
          :class="{ 'btn-primary': currentView === 'week', 'btn-secondary': currentView !== 'week' }"
        >
          Semaine
        </button>
        <button 
          @click="currentView = 'day'" 
          class="btn btn-sm"
          :class="{ 'btn-primary': currentView === 'day', 'btn-secondary': currentView !== 'day' }"
        >
          Jour
        </button>
      </div>

      <div class="navigation-controls">
        <button @click="previousPeriod" class="btn btn-secondary btn-sm">
          ← Précédent
        </button>
        <h2 class="current-period">{{ currentPeriodLabel }}</h2>
        <button @click="nextPeriod" class="btn btn-secondary btn-sm">
          Suivant →
        </button>
        <button @click="goToToday" class="btn btn-primary btn-sm">
          Aujourd'hui
        </button>
      </div>

      <div class="filter-controls">
        <select v-model="selectedInterviewer" class="form-input filter-select">
          <option value="">Tous les interviewers</option>
          <option v-for="interviewer in interviewers" :key="interviewer.id" :value="interviewer.id">
            {{ interviewer.name }}
          </option>
        </select>
        
        <select v-model="selectedStatus" class="form-input filter-select">
          <option value="">Tous les statuts</option>
          <option value="scheduled">Programmé</option>
          <option value="confirmed">Confirmé</option>
          <option value="completed">Terminé</option>
          <option value="cancelled">Annulé</option>
        </select>
      </div>
    </div>

    <!-- Calendrier -->
    <div class="calendar-container">
      <!-- Vue mensuelle -->
      <div v-if="currentView === 'month'" class="month-view">
        <div class="calendar-header">
          <div v-for="day in weekDays" :key="day" class="day-header">
            {{ day }}
          </div>
        </div>
        
        <div class="calendar-grid">
          <div 
            v-for="day in monthDays" 
            :key="day.date"
            class="calendar-day"
            :class="{ 
              'other-month': !day.isCurrentMonth,
              'today': day.isToday,
              'has-interviews': day.interviews.length > 0
            }"
            @click="selectDay(day)"
          >
            <div class="day-number">{{ day.day }}</div>
            <div class="day-interviews">
              <div 
                v-for="interview in day.interviews.slice(0, 3)" 
                :key="interview.id"
                class="interview-item"
                :class="`status-${interview.status}`"
                @click.stop="viewInterview(interview)"
              >
                <span class="interview-time">{{ formatTime(interview.startTime) }}</span>
                <span class="interview-candidate">{{ interview.candidateName }}</span>
              </div>
              <div v-if="day.interviews.length > 3" class="more-interviews">
                +{{ day.interviews.length - 3 }} autres
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Vue hebdomadaire -->
      <div v-if="currentView === 'week'" class="week-view">
        <div class="week-header">
          <div class="time-column"></div>
          <div v-for="day in weekDays" :key="day.date" class="day-column">
            <div class="day-label">{{ day.label }}</div>
            <div class="day-date">{{ day.date }}</div>
          </div>
        </div>
        
        <div class="week-grid">
          <div class="time-slots">
            <div v-for="hour in workingHours" :key="hour" class="time-slot">
              <div class="time-label">{{ hour }}:00</div>
              <div class="hour-row">
                <div v-for="day in weekDays" :key="`${day.date}-${hour}`" class="hour-cell">
                  <div 
                    v-for="interview in getInterviewsForHour(day.date, hour)" 
                    :key="interview.id"
                    class="interview-block"
                    :class="`status-${interview.status}`"
                    @click="viewInterview(interview)"
                  >
                    <div class="interview-title">{{ interview.candidateName }}</div>
                    <div class="interview-details">
                      {{ formatTime(interview.startTime) }} - {{ formatTime(interview.endTime) }}
                    </div>
                    <div class="interview-position">{{ interview.position }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Vue journalière -->
      <div v-if="currentView === 'day'" class="day-view">
        <div class="day-header">
          <h3>{{ selectedDayLabel }}</h3>
        </div>
        
        <div class="day-schedule">
          <div v-for="hour in workingHours" :key="hour" class="hour-block">
            <div class="hour-label">{{ hour }}:00</div>
            <div class="hour-content">
              <div 
                v-for="interview in getInterviewsForHour(selectedDay, hour)" 
                :key="interview.id"
                class="interview-card"
                :class="`status-${interview.status}`"
                @click="viewInterview(interview)"
              >
                <div class="interview-header">
                  <h4>{{ interview.candidateName }}</h4>
                  <span class="interview-status">{{ getStatusLabel(interview.status) }}</span>
                </div>
                <div class="interview-info">
                  <div class="info-item">
                    <span class="info-label">Poste:</span>
                    <span>{{ interview.position }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Horaire:</span>
                    <span>{{ formatTime(interview.startTime) }} - {{ formatTime(interview.endTime) }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Interviewer:</span>
                    <span>{{ interview.interviewer }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Type:</span>
                    <span>{{ interview.type }}</span>
                  </div>
                </div>
                <div class="interview-actions">
                  <button @click.stop="editInterview(interview)" class="btn btn-sm btn-secondary">
                    ✏️ Modifier
                  </button>
                  <button @click.stop="rateInterview(interview)" class="btn btn-sm btn-success">
                    ⭐ Noter
                  </button>
                  <button @click.stop="cancelInterview(interview)" class="btn btn-sm btn-danger">
                    ❌ Annuler
                  </button>
                </div>
              </div>
              
              <div v-if="getInterviewsForHour(selectedDay, hour).length === 0" class="empty-hour">
                <button @click="scheduleInterview(hour)" class="btn btn-sm btn-primary">
                  ➕ Programmer un entretien
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de création/édition d'entretien -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModals">
      <div class="modal interview-modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? 'Modifier l\'entretien' : 'Programmer un entretien' }}</h3>
          <button @click="closeModals" class="modal-close">×</button>
        </div>
        
        <div class="modal-content">
          <form @submit.prevent="saveInterview" class="interview-form">
            <div class="form-row">
              <div class="form-group">
                <label for="candidateName" class="form-label">Candidat *</label>
                <input
                  type="text"
                  id="candidateName"
                  v-model="interviewForm.candidateName"
                  class="form-input"
                  placeholder="Nom du candidat"
                  required
                />
              </div>

              <div class="form-group">
                <label for="candidateEmail" class="form-label">Email du candidat</label>
                <input
                  type="email"
                  id="candidateEmail"
                  v-model="interviewForm.candidateEmail"
                  class="form-input"
                  placeholder="email@example.com"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="position" class="form-label">Poste *</label>
                <input
                  type="text"
                  id="position"
                  v-model="interviewForm.position"
                  class="form-input"
                  placeholder="Développeur Full Stack"
                  required
                />
              </div>

              <div class="form-group">
                <label for="interviewer" class="form-label">Interviewer *</label>
                <select
                  id="interviewer"
                  v-model="interviewForm.interviewer"
                  class="form-input"
                  required
                >
                  <option value="">Sélectionner...</option>
                  <option v-for="interviewer in interviewers" :key="interviewer.id" :value="interviewer.name">
                    {{ interviewer.name }}
                  </option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="date" class="form-label">Date *</label>
                <input
                  type="date"
                  id="date"
                  v-model="interviewForm.date"
                  class="form-input"
                  required
                />
              </div>

              <div class="form-group">
                <label for="startTime" class="form-label">Heure de début *</label>
                <input
                  type="time"
                  id="startTime"
                  v-model="interviewForm.startTime"
                  class="form-input"
                  required
                />
              </div>

              <div class="form-group">
                <label for="duration" class="form-label">Durée (minutes) *</label>
                <select
                  id="duration"
                  v-model="interviewForm.duration"
                  class="form-input"
                  required
                >
                  <option value="30">30 minutes</option>
                  <option value="45">45 minutes</option>
                  <option value="60">60 minutes</option>
                  <option value="90">90 minutes</option>
                  <option value="120">120 minutes</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="type" class="form-label">Type d'entretien *</label>
                <select
                  id="type"
                  v-model="interviewForm.type"
                  class="form-input"
                  required
                >
                  <option value="">Sélectionner...</option>
                  <option value="Téléphonique">Téléphonique</option>
                  <option value="Visioconférence">Visioconférence</option>
                  <option value="Présentiel">Présentiel</option>
                  <option value="Technique">Technique</option>
                </select>
              </div>

              <div class="form-group">
                <label for="location" class="form-label">Lieu/Lien</label>
                <input
                  type="text"
                  id="location"
                  v-model="interviewForm.location"
                  class="form-input"
                  placeholder="Salle de réunion A / https://meet.google.com/..."
                />
              </div>
            </div>

            <div class="form-group">
              <label for="notes" class="form-label">Notes</label>
              <textarea
                id="notes"
                v-model="interviewForm.notes"
                class="form-input"
                rows="3"
                placeholder="Notes sur l'entretien, questions à poser, etc."
              ></textarea>
            </div>
          </form>
        </div>
        
        <div class="modal-actions">
          <button @click="closeModals" class="btn btn-secondary">
            Annuler
          </button>
          <button @click="saveInterview" class="btn btn-primary" :disabled="isLoading">
            <span v-if="isLoading">Sauvegarde...</span>
            <span v-else>{{ showEditModal ? 'Mettre à jour' : 'Programmer' }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Modal de détails d'entretien -->
    <div v-if="showDetailsModal" class="modal-overlay" @click="showDetailsModal = false">
      <div class="modal details-modal" @click.stop>
        <div class="modal-header">
          <h3>Détails de l'entretien</h3>
          <button @click="showDetailsModal = false" class="modal-close">×</button>
        </div>
        
        <div class="modal-content" v-if="selectedInterview">
          <div class="interview-details">
            <div class="detail-section">
              <h4>Candidat</h4>
              <p><strong>{{ selectedInterview.candidateName }}</strong></p>
              <p>{{ selectedInterview.candidateEmail }}</p>
              <p>Poste: {{ selectedInterview.position }}</p>
            </div>

            <div class="detail-section">
              <h4>Entretien</h4>
              <p><strong>Date:</strong> {{ formatDate(selectedInterview.date) }}</p>
              <p><strong>Horaire:</strong> {{ formatTime(selectedInterview.startTime) }} - {{ formatTime(selectedInterview.endTime) }}</p>
              <p><strong>Interviewer:</strong> {{ selectedInterview.interviewer }}</p>
              <p><strong>Type:</strong> {{ selectedInterview.type }}</p>
              <p><strong>Statut:</strong> {{ getStatusLabel(selectedInterview.status) }}</p>
            </div>

            <div class="detail-section" v-if="selectedInterview.location">
              <h4>Lieu/Lien</h4>
              <p>{{ selectedInterview.location }}</p>
            </div>

            <div class="detail-section" v-if="selectedInterview.notes">
              <h4>Notes</h4>
              <p>{{ selectedInterview.notes }}</p>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="editInterview(selectedInterview)" class="btn btn-primary">
            ✏️ Modifier
          </button>
          <button @click="showDetailsModal = false" class="btn btn-secondary">
            Fermer
          </button>
        </div>
      </div>
    </div>

    <!-- Modal de notation d'entretien -->
    <div v-if="showRatingModal" class="modal-overlay" @click="showRatingModal = false">
      <div class="modal rating-modal" @click.stop>
        <div class="modal-header">
          <h3>Noter l'entretien</h3>
          <button @click="closeRatingModal" class="modal-close">×</button>
        </div>
        
        <div class="modal-content">
          <div v-if="selectedInterview" class="interview-summary">
            <h4>{{ selectedInterview.candidateName }}</h4>
            <p>{{ selectedInterview.position }}</p>
            <p>{{ formatDate(selectedInterview.date) }} à {{ formatTime(selectedInterview.startTime) }}</p>
          </div>

          <div class="form-group">
            <label for="rating" class="form-label">Résultat de l'entretien *</label>
            <select
              id="rating"
              v-model="selectedRating"
              class="form-input"
              required
            >
              <option value="">Sélectionner un résultat...</option>
              <option v-for="resultat in resultats" :key="resultat.id" :value="resultat.id">
                {{ resultat.note }}/5 - {{ resultat.appreciation }}
              </option>
            </select>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="closeRatingModal" class="btn btn-secondary">
            Annuler
          </button>
          <button @click="confirmRating" class="btn btn-primary" :disabled="!selectedRating || isLoading">
            <span v-if="isLoading">Sauvegarde...</span>
            <span v-else>Valider la note</span>
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'InterviewCalendarView',
  data() {
    return {
      currentView: 'month',
      currentDate: new Date(),
      selectedDay: null,
      selectedInterviewer: '',
      selectedStatus: '',
      showCreateModal: false,
      showEditModal: false,
      showDetailsModal: false,
      showRatingModal: false,
      selectedInterview: null,
      isLoading: false,
      weekDays: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'],
      workingHours: [8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18],
      interviewers: [
        { id: 1, name: 'Marie Dupont' },
        { id: 2, name: 'Jean Martin' },
        { id: 3, name: 'Sophie Bernard' },
        { id: 4, name: 'Pierre Durand' }
      ],
      interviewForm: {
        candidateName: '',
        candidateEmail: '',
        position: '',
        interviewer: '',
        date: '',
        startTime: '',
        duration: '60',
        type: '',
        location: '',
        notes: ''
      },
      interviews: [],
      resultats: [],
      selectedRating: null
    }
  },
  computed: {
    currentPeriodLabel() {
      const options = { year: 'numeric', month: 'long' }
      if (this.currentView === 'week') {
        return `Semaine du ${this.currentDate.toLocaleDateString('fr-FR')}`
      } else if (this.currentView === 'day') {
        return this.currentDate.toLocaleDateString('fr-FR', { 
          weekday: 'long', 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric' 
        })
      }
      return this.currentDate.toLocaleDateString('fr-FR', options)
    },
    selectedDayLabel() {
      if (!this.selectedDay) return ''
      return new Date(this.selectedDay).toLocaleDateString('fr-FR', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      })
    },
    monthDays() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()
      const firstDay = new Date(year, month, 1)
      const lastDay = new Date(year, month + 1, 0)
      const startDate = new Date(firstDay)
      const endDate = new Date(lastDay)
      
      // Ajuster pour commencer le lundi
      startDate.setDate(startDate.getDate() - (startDate.getDay() + 6) % 7)
      endDate.setDate(endDate.getDate() + (7 - endDate.getDay()) % 7)
      
      const days = []
      const currentDate = new Date(startDate)
      
      while (currentDate <= endDate) {
        const dateStr = currentDate.toISOString().split('T')[0]
        const dayInterviews = this.getInterviewsForDate(dateStr)
        
        days.push({
          date: dateStr,
          day: currentDate.getDate(),
          isCurrentMonth: currentDate.getMonth() === month,
          isToday: this.isToday(currentDate),
          interviews: dayInterviews
        })
        
        currentDate.setDate(currentDate.getDate() + 1)
      }
      
      return days
    }
  },
  methods: {
    isToday(date) {
      const today = new Date()
      return date.toDateString() === today.toDateString()
    },
    getInterviewsForDate(dateStr) {
      return this.interviews.filter(interview => {
        if (this.selectedInterviewer && interview.interviewer !== this.getInterviewerName(this.selectedInterviewer)) {
          return false
        }
        if (this.selectedStatus && interview.status !== this.selectedStatus) {
          return false
        }
        return interview.date === dateStr
      })
    },
    getInterviewsForHour(dateStr, hour) {
      return this.getInterviewsForDate(dateStr).filter(interview => {
        const startHour = parseInt(interview.startTime.split(':')[0])
        return startHour === hour
      })
    },
    getInterviewerName(id) {
      const interviewer = this.interviewers.find(i => i.id === parseInt(id))
      return interviewer ? interviewer.name : ''
    },
    getStatusLabel(status) {
      const labels = {
        scheduled: 'Programmé',
        confirmed: 'Confirmé',
        completed: 'Terminé',
        cancelled: 'Annulé'
      }
      return labels[status] || status
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleDateString('fr-FR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    formatTime(timeStr) {
      return timeStr.substring(0, 5)
    },
    previousPeriod() {
      if (this.currentView === 'month') {
        this.currentDate.setMonth(this.currentDate.getMonth() - 1)
      } else if (this.currentView === 'week') {
        this.currentDate.setDate(this.currentDate.getDate() - 7)
      } else {
        this.currentDate.setDate(this.currentDate.getDate() - 1)
      }
      this.currentDate = new Date(this.currentDate)
    },
    nextPeriod() {
      if (this.currentView === 'month') {
        this.currentDate.setMonth(this.currentDate.getMonth() + 1)
      } else if (this.currentView === 'week') {
        this.currentDate.setDate(this.currentDate.getDate() + 7)
      } else {
        this.currentDate.setDate(this.currentDate.getDate() + 1)
      }
      this.currentDate = new Date(this.currentDate)
    },
    goToToday() {
      this.currentDate = new Date()
    },
    selectDay(day) {
      this.selectedDay = day.date
      this.currentView = 'day'
    },
    scheduleInterview(hour) {
      this.interviewForm.date = this.selectedDay
      this.interviewForm.startTime = `${hour.toString().padStart(2, '0')}:00`
      this.showCreateModal = true
    },
    viewInterview(interview) {
      this.selectedInterview = interview
      this.showDetailsModal = true
    },
    async fetchInterviews() {
      try {
        const response = await fetch('http://localhost:8080/api/entretiens')
        if (response.ok) {
          const entretiens = await response.json()
          this.interviews = entretiens.map(entretien => ({
            id: entretien.id,
            candidateName: entretien.idcandidat ? `${entretien.idcandidat.nom} ${entretien.idcandidat.prenom}` : 'N/A',
            candidateEmail: entretien.idcandidat?.idcomptecandidat?.email || 'N/A',
            position: entretien.idannonce?.nomposte || entretien.idcandidat?.idannonce?.nomposte || 'N/A',
            interviewer: 'N/A', // This field might need to be added to the backend
            date: entretien.dateheure ? entretien.dateheure.split('T')[0] : '',
            startTime: entretien.dateheure ? entretien.dateheure.split('T')[1].substring(0, 5) : '',
            endTime: entretien.dateheure ? this.calculateEndTimeFromStart(entretien.dateheure.split('T')[1].substring(0, 5), 60) : '',
            type: 'Présentiel', // Default type, might need to be added to backend
            location: 'N/A',
            status: entretien.idstatut?.nom?.toLowerCase() === 'planifie' ? 'scheduled' : 
                   entretien.idstatut?.nom?.toLowerCase() === 'en cours' ? 'confirmed' :
                   entretien.idstatut?.nom?.toLowerCase() === 'termine' ? 'completed' : 'scheduled',
            notes: ''
          }))
        }
      } catch (error) {
        console.error('Erreur lors du chargement des entretiens:', error)
      }
    },
    async fetchInterviewsByDate(date) {
      try {
        const response = await fetch(`http://localhost:8080/api/entretiens/date/${date}`)
        if (response.ok) {
          const entretiens = await response.json()
          return entretiens.map(entretien => ({
            id: entretien.id,
            candidateName: entretien.idcandidat ? `${entretien.idcandidat.nom} ${entretien.idcandidat.prenom}` : 'N/A',
            candidateEmail: entretien.idcandidat?.idcomptecandidat?.email || 'N/A',
            position: entretien.idannonce?.nomposte || entretien.idcandidat?.idannonce?.nomposte || 'N/A',
            interviewer: 'N/A',
            date: entretien.dateheure ? entretien.dateheure.split('T')[0] : '',
            startTime: entretien.dateheure ? entretien.dateheure.split('T')[1].substring(0, 5) : '',
            endTime: entretien.dateheure ? this.calculateEndTimeFromStart(entretien.dateheure.split('T')[1].substring(0, 5), 60) : '',
            type: 'Présentiel',
            location: 'N/A',
            status: entretien.idstatut?.nom?.toLowerCase() === 'planifie' ? 'scheduled' : 
                   entretien.idstatut?.nom?.toLowerCase() === 'en cours' ? 'confirmed' :
                   entretien.idstatut?.nom?.toLowerCase() === 'termine' ? 'completed' : 'scheduled',
            notes: ''
          }))
        }
        return []
      } catch (error) {
        console.error('Erreur lors du chargement des entretiens par date:', error)
        return []
      }
    },
    calculateEndTimeFromStart(startTime, durationMinutes) {
      const [hours, minutes] = startTime.split(':').map(Number)
      const totalMinutes = hours * 60 + minutes + durationMinutes
      const endHours = Math.floor(totalMinutes / 60)
      const endMins = totalMinutes % 60
      return `${endHours.toString().padStart(2, '0')}:${endMins.toString().padStart(2, '0')}`
    },
    editInterview(interview) {
      this.selectedInterview = interview
      this.interviewForm = {
        candidateName: interview.candidateName,
        candidateEmail: interview.candidateEmail,
        position: interview.position,
        interviewer: interview.interviewer,
        date: interview.date,
        startTime: interview.startTime,
        duration: this.calculateDuration(interview.startTime, interview.endTime).toString(),
        type: interview.type,
        location: interview.location || '',
        notes: interview.notes || ''
      }
      this.showDetailsModal = false
      this.showEditModal = true
    },
    calculateDuration(startTime, endTime) {
      const start = new Date(`2000-01-01T${startTime}:00`)
      const end = new Date(`2000-01-01T${endTime}:00`)
      return (end - start) / (1000 * 60) // en minutes
    },
    calculateEndTime(startTime, duration) {
      const start = new Date(`2000-01-01T${startTime}:00`)
      start.setMinutes(start.getMinutes() + parseInt(duration))
      return start.toTimeString().substring(0, 5)
    },
    async saveInterview() {
      this.isLoading = true
      
      try {
        // Simulation de sauvegarde
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        const endTime = this.calculateEndTime(this.interviewForm.startTime, this.interviewForm.duration)
        
        if (this.showEditModal) {
          // Mise à jour
          const index = this.interviews.findIndex(i => i.id === this.selectedInterview.id)
          if (index !== -1) {
            this.interviews[index] = {
              ...this.interviews[index],
              ...this.interviewForm,
              endTime
            }
          }
        } else {
          // Création
          const newInterview = {
            id: Date.now(),
            ...this.interviewForm,
            endTime,
            status: 'scheduled'
          }
          this.interviews.push(newInterview)
        }
        
        this.closeModals()
        
      } catch (error) {
        alert('Erreur lors de la sauvegarde')
      } finally {
        this.isLoading = false
      }
    },
    cancelInterview(interview) {
      if (confirm('Êtes-vous sûr de vouloir annuler cet entretien ?')) {
        interview.status = 'cancelled'
      }
    },
    async fetchResultats() {
      try {
        const response = await fetch('http://localhost:8080/api/resultats')
        if (response.ok) {
          this.resultats = await response.json()
        }
      } catch (error) {
        console.error('Erreur lors du chargement des résultats:', error)
      }
    },
    rateInterview(interview) {
      this.selectedInterview = interview
      this.selectedRating = null
      this.showRatingModal = true
    },
    closeRatingModal() {
      this.showRatingModal = false
      this.selectedInterview = null
      this.selectedRating = null
    },
    async confirmRating() {
      if (!this.selectedRating || !this.selectedInterview) return
      
      this.isLoading = true
      try {
        const response = await fetch(`http://localhost:8080/api/entretiens/${this.selectedInterview.id}/rate`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            resultatId: this.selectedRating
          })
        })
        
        if (response.ok) {
          alert('Entretien noté avec succès!')
          this.closeRatingModal()
          // Reload interviews to reflect changes
          await this.fetchInterviews()
        } else {
          alert('Erreur lors de la notation de l\'entretien')
        }
      } catch (error) {
        console.error('Erreur lors de la notation:', error)
        alert('Erreur lors de la notation de l\'entretien')
      } finally {
        this.isLoading = false
      }
    },
    closeModals() {
      this.showCreateModal = false
      this.showEditModal = false
      this.showDetailsModal = false
      this.showRatingModal = false
      this.selectedInterview = null
      this.selectedRating = null
      this.interviewForm = {
        candidateName: '',
        candidateEmail: '',
        position: '',
        interviewer: '',
        date: '',
        startTime: '',
        duration: '60',
        type: '',
        location: '',
        notes: ''
      }
    }
  },
  mounted() {
    this.selectedDay = new Date().toISOString().split('T')[0]
    this.fetchInterviews()
    this.fetchResultats()
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

.page-actions {
  display: flex;
  gap: 10px;
}

.calendar-controls {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.view-controls {
  display: flex;
  gap: 5px;
}

.navigation-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-period {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  min-width: 200px;
  text-align: center;
}

.filter-controls {
  display: flex;
  gap: 10px;
}

.filter-select {
  min-width: 150px;
}

.calendar-container {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

/* Vue mensuelle */
.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  margin-bottom: 10px;
}

.day-header {
  padding: 15px;
  text-align: center;
  font-weight: 600;
  color: #555;
  background: #f8f9fa;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background: #e9ecef;
}

.calendar-day {
  background: white;
  min-height: 120px;
  padding: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.calendar-day:hover {
  background: #f8fbff;
}

.calendar-day.other-month {
  background: #f8f9fa;
  color: #adb5bd;
}

.calendar-day.today {
  background: #e3f2fd;
}

.calendar-day.has-interviews {
  border-left: 4px solid #3498db;
}

.day-number {
  font-weight: 600;
  margin-bottom: 5px;
}

.day-interviews {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.interview-item {
  background: #3498db;
  color: white;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.interview-item:hover {
  background: #2980b9;
}

.interview-item.status-confirmed {
  background: #27ae60;
}

.interview-item.status-completed {
  background: #95a5a6;
}

.interview-item.status-cancelled {
  background: #e74c3c;
}

.interview-time {
  font-weight: 600;
}

.interview-candidate {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.more-interviews {
  font-size: 10px;
  color: #666;
  text-align: center;
  margin-top: 2px;
}

/* Vue hebdomadaire */
.week-header {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  gap: 1px;
  margin-bottom: 10px;
}

.time-column {
  background: #f8f9fa;
}

.day-column {
  background: #f8f9fa;
  padding: 10px;
  text-align: center;
}

.day-label {
  font-weight: 600;
  color: #555;
}

.day-date {
  font-size: 14px;
  color: #666;
}

.week-grid {
  border: 1px solid #e9ecef;
}

.time-slots {
  display: flex;
  flex-direction: column;
}

.time-slot {
  display: flex;
  border-bottom: 1px solid #e9ecef;
  min-height: 60px;
}

.time-label {
  width: 80px;
  padding: 10px;
  background: #f8f9fa;
  border-right: 1px solid #e9ecef;
  font-size: 14px;
  color: #666;
}

.hour-row {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
}

.hour-cell {
  background: white;
  padding: 5px;
  position: relative;
}

.interview-block {
  background: #3498db;
  color: white;
  padding: 5px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  margin-bottom: 2px;
}

.interview-block.status-confirmed {
  background: #27ae60;
}

.interview-block.status-completed {
  background: #95a5a6;
}

.interview-block.status-cancelled {
  background: #e74c3c;
}

.interview-title {
  font-weight: 600;
}

.interview-details {
  font-size: 10px;
  opacity: 0.9;
}

.interview-position {
  font-size: 10px;
  opacity: 0.8;
}

/* Vue journalière */
.day-header {
  text-align: center;
  margin-bottom: 20px;
}

.day-header h3 {
  color: #2c3e50;
  margin: 0;
}

.day-schedule {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.hour-block {
  display: flex;
  gap: 20px;
  min-height: 80px;
  border-bottom: 1px solid #e9ecef;
  padding: 10px 0;
}

.hour-label {
  width: 80px;
  font-weight: 600;
  color: #666;
  text-align: center;
}

.hour-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.interview-card {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.interview-card:hover {
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.interview-card.status-confirmed {
  border-left: 4px solid #27ae60;
}

.interview-card.status-completed {
  border-left: 4px solid #95a5a6;
}

.interview-card.status-cancelled {
  border-left: 4px solid #e74c3c;
  opacity: 0.7;
}

.interview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.interview-header h4 {
  margin: 0;
  color: #2c3e50;
}

.interview-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  background: #e9ecef;
  color: #495057;
}

.interview-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  gap: 5px;
}

.info-label {
  font-weight: 600;
  color: #555;
  min-width: 80px;
}

.interview-actions {
  display: flex;
  gap: 10px;
}

.empty-hour {
  text-align: center;
  padding: 20px;
  color: #666;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

/* Modal de notation */
.rating-modal {
  max-width: 500px;
  width: 90%;
}

.interview-summary {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.interview-summary h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.interview-summary p {
  margin: 5px 0;
  color: #666;
}

/* Modals */
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

.interview-modal {
  max-width: 600px;
  width: 90%;
}

.details-modal {
  max-width: 500px;
  width: 90%;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 0;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  padding: 20px;
}

.interview-form .form-row {
  display: flex;
  gap: 15px;
}

.interview-form .form-row .form-group {
  flex: 1;
}

.interview-form .form-group {
  margin-bottom: 20px;
}

.interview-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  padding-bottom: 5px;
}

.detail-section p {
  margin: 0 0 5px 0;
  color: #555;
}

.modal-actions {
  padding: 20px;
  border-top: 1px solid #eee;
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
  
  .calendar-controls {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .navigation-controls {
    justify-content: center;
  }
  
  .filter-controls {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .calendar-grid {
    grid-template-columns: repeat(7, 1fr);
  }
  
  .calendar-day {
    min-height: 80px;
    font-size: 12px;
  }
  
  .week-header {
    grid-template-columns: 60px repeat(7, 1fr);
  }
  
  .time-label {
    width: 60px;
    font-size: 12px;
  }
  
  .hour-block {
    flex-direction: column;
    gap: 10px;
  }
  
  .hour-label {
    width: auto;
    text-align: left;
  }
  
  .interview-info {
    grid-template-columns: 1fr;
  }
  
  .interview-form .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .interview-modal, .details-modal {
    width: 95%;
    max-height: 90vh;
  }
}
</style>
