<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Saisie de pointage manuel</h1>
    </div>

    <div class="card" style="max-width: 800px; margin: 0 auto;">
      <form @submit.prevent="submit">
        <div class="form-group">
          <label class="form-label">Employé</label>
          <div class="employee-select">
            <input
              v-model="employeeSearch"
              type="text"
              class="form-input"
              placeholder="Rechercher par nom ou matricule..."
              @focus="showDropdown = true"
              @blur="onBlurHandler"
            />
            <div v-if="showDropdown" class="dropdown">
              <div v-if="filteredEmployees.length === 0" class="dropdown-item">
                Aucun employé trouvé
              </div>
              <div
                v-for="emp in filteredEmployees"
                :key="emp.id"
                class="dropdown-item"
                @mousedown="selectEmployee(emp)"
              >
                <img v-if="emp.photo" :src="emp.photo" class="employee-photo" />
                <div v-else class="employee-initials">
                  {{ (emp.prenom?.[0] || '') + (emp.nom?.[0] || '') }}
                </div>
                <div class="employee-info">
                  <div class="employee-name">{{ emp.prenom }} {{ emp.nom }}</div>
                  <div class="employee-matricule">{{ emp.matricule || 'Pas de matricule' }}</div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="selectedEmployee" class="selected-employee">
            <div class="flex align-center gap-10">
              <img v-if="selectedEmployee.photo" :src="selectedEmployee.photo" class="selected-photo" />
              <div v-else class="selected-initials">
                {{ (selectedEmployee.prenom?.[0] || '') + (selectedEmployee.nom?.[0] || '') }}
              </div>
              <div>
                <div>{{ selectedEmployee.prenom }} {{ selectedEmployee.nom }}</div>
                <div class="text-muted">Matricule: {{ selectedEmployee.matricule || 'Non défini' }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="flex gap-10">
          <div class="form-group" style="flex:1;">
            <label class="form-label">Date</label>
            <input v-model="form.date" type="date" class="form-input" required />
          </div>
          <div class="form-group" style="flex:1;">
            <label class="form-label">Heure</label>
            <input v-model="form.time" type="time" class="form-input" required />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Type de pointage</label>
          <select v-model.number="form.typeId" class="form-input" required>
            <option disabled value="">Choisir...</option>
            <option v-for="t in types" :key="t.id" :value="t.id">{{ t.libelle }}</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">Commentaire</label>
          <textarea v-model="form.commentaire" class="form-input" rows="3" required placeholder="Oubli de pointer - Validé par manager"></textarea>
        </div>

        <div class="card" style="background:#ecfdf5;border:1px solid #a7f3d0;">
          <div style="font-weight:600;margin-bottom:6px;">Prévisualisation</div>
          <div>Employé ID: {{ form.employeId || '-' }}</div>
          <div>Date/Heure: {{ form.date || '-' }} {{ form.time || '-' }}</div>
          <div>Type: {{ typeLabel(form.typeId) }}</div>
          <div>Commentaire: {{ form.commentaire || '-' }}</div>
        </div>

        <div class="flex gap-10">
          <button :disabled="submitting" type="submit" class="btn btn-primary">Enregistrer le pointage</button>
          <router-link to="/pointage/aujourd-hui" class="btn btn-secondary">Annuler</router-link>
        </div>
      </form>
    </div>

    <!-- Déclaration d'absence -->
    <div class="card" style="max-width: 800px; margin: 16px auto;">
      <h2 style="margin-bottom:10px;">Déclarer une absence</h2>
      <div class="form-group">
        <label class="form-label">Employé</label>
        <!-- Réutilise la même zone de recherche/selection -->
        <div class="employee-select">
          <input
            v-model="absence.employeeSearch"
            type="text"
            class="form-input"
            placeholder="Rechercher par nom ou matricule..."
            @focus="absence.showDropdown = true"
            @blur="onAbsenceBlur"
          />
          <div v-if="absence.showDropdown" class="dropdown">
            <div v-if="filteredEmployeesAbs.length === 0" class="dropdown-item">Aucun employé trouvé</div>
            <div
              v-for="emp in filteredEmployeesAbs"
              :key="emp.id"
              class="dropdown-item"
              @mousedown="selectEmployeeForAbsence(emp)"
            >
              <img v-if="emp.photo" :src="emp.photo" class="employee-photo" />
              <div v-else class="employee-initials">{{ (emp.prenom?.[0]||'') + (emp.nom?.[0]||'') }}</div>
              <div class="employee-info">
                <div class="employee-name">{{ emp.prenom }} {{ emp.nom }}</div>
                <div class="employee-matricule">{{ emp.matricule || 'Pas de matricule' }}</div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="absence.selectedEmployee" class="selected-employee">
          <div class="flex align-center gap-10">
            <img v-if="absence.selectedEmployee.photo" :src="absence.selectedEmployee.photo" class="selected-photo" />
            <div v-else class="selected-initials">{{ (absence.selectedEmployee.prenom?.[0]||'') + (absence.selectedEmployee.nom?.[0]||'') }}</div>
            <div>
              <div>{{ absence.selectedEmployee.prenom }} {{ absence.selectedEmployee.nom }}</div>
              <div class="text-muted">Matricule: {{ absence.selectedEmployee.matricule || 'Non défini' }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex gap-10">
        <div class="form-group" style="flex:1;">
          <label class="form-label">Date d'absence</label>
          <input v-model="absence.date" type="date" class="form-input" />
        </div>
        <div class="form-group" style="flex:2;">
          <label class="form-label">Commentaire</label>
          <input v-model="absence.commentaire" type="text" class="form-input" placeholder="Motif d'absence" />
        </div>
      </div>

      <div class="flex gap-10">
        <button :disabled="absenceSubmitting" @click="submitAbsence" class="btn btn-primary" type="button">Enregistrer l'absence</button>
      </div>
    </div>

    <div v-if="success" class="card" style="max-width: 800px; margin: 16px auto; background:#ecfdf5;border:1px solid #a7f3d0;">
      <div style="font-weight:600;color:#065f46;">Pointage enregistré</div>
      <div style="font-size:14px;color:#6b7280;">Vous pouvez saisir un autre pointage ou consulter le récapitulatif du jour.</div>
      <div class="flex gap-10" style="margin-top: 8px;">
        <button @click="resetForm" class="btn btn-secondary">Saisir un autre pointage</button>
        <router-link :to="`/pointage/detail/${lastMatricule || ''}/${form.date}`" class="btn btn-primary">Voir récapitulatif</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ManualEntryView',
  data(){
    const now = new Date()
    return {
      types: [],
      employees: [],
      employeeSearch: '',
      showDropdown: false,
      selectedEmployee: null,
      // Absence form state
      absence: {
        employeeSearch: '',
        showDropdown: false,
        selectedEmployee: null,
        date: now.toISOString().slice(0,10),
        commentaire: ''
      },
      absenceSubmitting: false,
      submitting: false,
      success: false,
      lastMatricule: '',
      form: {
        employeId: null,
        date: now.toISOString().slice(0,10),
        time: now.toTimeString().slice(0,5),
        typeId: '',
        commentaire: ''
      }
    }
  },
  computed: {
    filteredEmployees() {
      if (!this.employeeSearch) return this.employees.slice(0, 5)
      const search = this.employeeSearch.toLowerCase().trim()
      console.log('Recherche en cours:', search)
      console.log('Employés disponibles:', this.employees)
      
      const results = this.employees
        .filter(emp => {
          if (!emp) return false
          
          // Vérifier si l'objet employé a les propriétés attendues
          if (!emp.nom || !emp.prenom) {
            console.warn('Employé avec des propriétés manquantes:', emp)
            return false
          }
          
          // Normaliser le matricule (supprimer les tirets/espaces pour la recherche)
          const matriculeNormalise = emp.matricule ? emp.matricule.replace(/[-\s]/g, '').toLowerCase() : ''
          const searchNormalise = search.replace(/[-\s]/g, '').toLowerCase()
          
          const match = (
            (emp.nom && emp.nom.toLowerCase().includes(search)) ||
            (emp.prenom && emp.prenom.toLowerCase().includes(search)) ||
            (matriculeNormalise && matriculeNormalise.includes(searchNormalise))
          )
          
          console.log(`Recherche '${search}' sur ${emp.prenom} ${emp.nom} (${emp.matricule}):`, match)
          return match
        })
        .slice(0, 5)
      
      console.log('Résultats de la recherche:', results)
      return results
    },
    filteredEmployeesAbs() {
      const search = (this.absence.employeeSearch || '').toLowerCase().trim()
      if (!search) return this.employees.slice(0,5)
      const searchNorm = search.replace(/[-\s]/g, '')
      return this.employees.filter(emp => {
        if (!emp) return false
        const mat = (emp.matricule || '').toLowerCase().replace(/[-\s]/g, '')
        return (
          emp.nom?.toLowerCase().includes(search) ||
          emp.prenom?.toLowerCase().includes(search) ||
          mat.includes(searchNorm)
        )
      }).slice(0,5)
    }
  },
  methods: {
    async loadEmployees() {
      try {
        console.log('Chargement des employés...')
        const res = await fetch('/api/employes')
        const data = await res.json()
        console.log('Données brutes reçues:', data)
        
        // Vérifier la structure des données
        const contrats = Array.isArray(data) ? data : []
        console.log('Nombre de contrats chargés:', contrats.length)
        
        // Extract unique employees from contrats
        const employeeMap = new Map()
        contrats.forEach((contrat, index) => {
          console.log(`Contrat ${index}:`, contrat)
          if (contrat.idemploye) {
            const employe = contrat.idemploye
            console.log(`  - Employé trouvé:`, employe)
            if (!employeeMap.has(employe.id)) {
              employeeMap.set(employe.id, {
                id: employe.id,
                nom: employe.nom,
                prenom: employe.prenom,
                matricule: employe.matricule,
                photo: employe.photo
              })
            }
          } else {
            console.log(`  - Pas d'employé dans le contrat ${index}`)
          }
        })
        
        this.employees = Array.from(employeeMap.values())
        console.log('Employés extraits:', this.employees)
      } catch (e) {
        console.error('Erreur chargement employés:', e)
      }
    },
    async loadTypes(){
      try{ const res = await fetch('/api/typepointages'); this.types = await res.json() } catch(e){ console.error(e) }
    },
    selectEmployee(employee) {
      this.selectedEmployee = employee
      this.form.employeId = employee.id
      this.employeeSearch = `${employee.prenom} ${employee.nom}`
      this.showDropdown = false
    },
    onBlurHandler() {
      // Small timeout to allow click event on dropdown items to fire first
      setTimeout(() => {
        this.showDropdown = false
      }, 200)
    },
    // Absence helpers
    selectEmployeeForAbsence(emp) {
      this.absence.selectedEmployee = emp
      this.absence.employeeSearch = `${emp.prenom} ${emp.nom}`
      this.absence.showDropdown = false
    },
    onAbsenceBlur() {
      setTimeout(() => { this.absence.showDropdown = false }, 200)
    },
    typeLabel(id){ return this.types.find(t=>t.id===id)?.libelle || '-' },
    resetForm(){ this.success=false; this.form.commentaire=''; },
    async submit(){
      this.submitting = true
      try{
        const res = await fetch('/api/pointages', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(this.form) })
        if(!res.ok){ const txt = await res.text(); throw new Error(txt || 'Erreur API') }
        const created = await res.json()
        // try to fetch employee matricule from payload response if available
        this.lastMatricule = created?.idemploye?.matricule || ''
        this.success = true
      }catch(e){ alert('Erreur: ' + (e.message||e)) }
      finally{ this.submitting=false }
    },
    async submitAbsence(){
      if (!this.absence.selectedEmployee) { alert('Veuillez choisir un employé'); return }
      if (!this.absence.date) { alert("Veuillez choisir une date d'absence"); return }
      this.absenceSubmitting = true
      try{
        const payload = {
          employeId: this.absence.selectedEmployee.id,
          date: this.absence.date,
          commentaire: this.absence.commentaire || ''
        }
        const res = await fetch('/api/absences', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload) })
        if(!res.ok){ const txt = await res.text(); throw new Error(txt || 'Erreur API') }
        alert("Absence enregistrée")
      }catch(e){ alert('Erreur: ' + (e.message||e)) }
      finally{ this.absenceSubmitting = false }
    }
  },
  mounted(){ 
    this.loadTypes()
    this.loadEmployees()
  }
}
</script>

<style scoped>
.employee-select {
  position: relative;
  margin-bottom: 8px;
}

.dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 300px;
  overflow-y: auto;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 50;
  margin-top: 2px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f3f4f6;
}

.employee-photo {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.employee-initials {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #059669;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 12px;
  flex-shrink: 0;
}

.employee-info {
  flex: 1;
  min-width: 0;
}

.employee-name {
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.employee-matricule {
  font-size: 0.8em;
  color: #6b7280;
}

.selected-employee {
  margin-top: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.selected-photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.selected-initials {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #059669;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

.text-muted {
  color: #6b7280;
  font-size: 0.85em;
  margin-top: 2px;
}
</style>
