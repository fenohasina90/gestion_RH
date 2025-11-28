<template>
  <div>
    <div class="page-header">
      <div class="flex align-center gap-10">
        <div style="width:40px;height:40px;background:#059669;color:#fff;border-radius:10px;display:flex;align-items:center;justify-content:center;font-weight:700;">
          {{ initials(employee?.prenom, employee?.nom) }}
        </div>
        <div>
          <div class="page-title" style="margin:0; font-size:20px;">{{ employee?.prenom }} {{ employee?.nom }}</div>
          <div style="font-size:12px;color:#6b7280;">Matricule: {{ matricule }} — Date: {{ date }}</div>
        </div>
      </div>
      <div class="flex gap-10">
        <button @click="goPrev" class="btn btn-secondary">← Jour précédent</button>
        <button @click="goNext" class="btn btn-secondary">Jour suivant →</button>
      </div>
    </div>

    <div class="flex gap-10" style="flex-wrap:wrap;">
      <div style="flex:2; min-width: 320px;" class="card">
        <h2 style="margin-bottom:10px;">Timeline des pointages</h2>
        <div v-if="timeline.length === 0" style="color:#6b7280;font-size:14px;">Aucun pointage enregistré pour cette journée.</div>
        <table class="table" v-else>
          <thead>
            <tr>
              <th style="width:100px;">Heure</th>
              <th>Type</th>
              <th>Commentaire</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in timeline" :key="idx">
              <td><span style="font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;">{{ item.time }}</span></td>
              <td>
                <span :class="badgeClass(item.typeId)" style="padding:4px 10px;border-radius:12px;font-size:12px;">{{ item.type }}</span>
              </td>
              <td>{{ item.commentaire || '' }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div style="flex:1; min-width: 280px;" class="card">
        <h2 style="margin-bottom:10px;">Calculs</h2>
        <ul style="list-style:none; padding:0; margin:0; font-size:14px;">
          <li><strong>Heure d'arrivée:</strong> {{ metrics.arrivee || '-' }}</li>
          <li><strong>Heure de départ:</strong> {{ metrics.depart || '-' }}</li>
          <li><strong>Temps de pause:</strong> {{ metrics.pause.toFixed(2) }} h</li>
          <li><strong>Heures travaillées:</strong> {{ metrics.travail.toFixed(2) }} h</li>
          <li><strong>Heures supplémentaires:</strong> {{ metrics.sup.toFixed(2) }} h</li>
          <li><strong>Retard matin:</strong> {{ metrics.latenessAm }} min (seuil 10 min)</li>
          <li><strong>Retard après-midi:</strong> {{ metrics.latenessPm }} min (seuil 10 min)</li>
          <li><strong>Retard total:</strong> {{ metrics.retard }} min</li>
          <li><strong>Statut journée:</strong>
            <span v-if="metrics.journeeComplete" style="color:#065f46;">Journée complète</span>
            <span v-else style="color:#b91c1c;">Journée incomplète</span>
          </li>
        </ul>
        <h3 style="margin:12px 0 6px 0;">Anomalies</h3>
        <ul v-if="anomalies.length" style="padding-left:18px;color:#b91c1c;">
          <li v-for="(a,i) in anomalies" :key="i">{{ a }}</li>
        </ul>
        <div v-else style="color:#065f46;">Aucune anomalie détectée.</div>
        <div style="margin-top:10px;">
          <router-link :to="`/pointage/saisir`" class="btn btn-primary">Ajouter un pointage</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DayDetailView',
  data(){
    return {
      matricule: this.$route.params.matricule,
      date: this.$route.params.date,
      pointages: [],
      employee: null,
      // Règles de retard (config par défaut)
      MORNING_START: '08:00',
      AFTERNOON_START: '14:00',
      LATE_LIMIT_MIN: 10
    }
  },
  computed: {
    timeline(){
      const mapType = {1:'Entrée',2:'Sortie',3:'Début pause',4:'Fin pause'}
      return this.pointages
        .slice()
        .sort((a,b)=>a.dateheure.localeCompare(b.dateheure))
        .map(p=>({
          time: new Date(p.dateheure).toTimeString().slice(0,5),
          typeId: p.idtypepointage?.id,
          type: mapType[p.idtypepointage?.id] || 'Inconnu',
          commentaire: p.commentaire || ''
        }))
    },
    metrics(){
      // Compute arrival, departure, pause, work hours, OT and late
      const ENTR=1, SORT=2, PDB=3, PFIN=4
      const sorted = this.pointages.slice().sort((a,b)=>a.dateheure.localeCompare(b.dateheure))
      const firstIn = sorted.find(p=>p.idtypepointage?.id===ENTR)
      const lastOut = [...sorted].reverse().find(p=>p.idtypepointage?.id===SORT)
      const arrivee = firstIn ? new Date(firstIn.dateheure).toTimeString().slice(0,5) : ''
      const depart  = lastOut ? new Date(lastOut.dateheure).toTimeString().slice(0,5) : ''
      // total pause: sum of pairs (PDB -> PFIN)
      let pauseMs = 0
      let stack = null
      for(const p of sorted){
        if(p.idtypepointage?.id===PDB) stack = new Date(p.dateheure)
        if(p.idtypepointage?.id===PFIN && stack){ pauseMs += (new Date(p.dateheure) - stack); stack=null }
      }
      const dayStart = firstIn ? new Date(firstIn.dateheure) : null
      const dayEnd   = lastOut ? new Date(lastOut.dateheure) : null
      let workMs = 0
      if(dayStart && dayEnd){ workMs = (dayEnd - dayStart) - pauseMs }
      const travail = workMs / 3600000
      const pause = pauseMs / 3600000
      // Heures supplémentaires: tout ce qui dépasse 8h de travail réelles
      const sup = Math.max(0, travail - 8)
      // Retards (matin et après-midi) selon seuils
      let latenessAm = 0
      let latenessPm = 0

      if (arrivee){
        const refAm = this.MORNING_START
        latenessAm = arrivee > refAm ? diffMinutes(arrivee, refAm) : 0
      }

      // Retour après-midi: premier ENTRÉE ou FIN PAUSE après AFTERNOON_START
      const refPm = this.AFTERNOON_START
      const afternoonReturn = sorted.find(p=>{
        const t = new Date(p.dateheure).toTimeString().slice(0,5)
        return t >= refPm && (p.idtypepointage?.id===ENTR || p.idtypepointage?.id===PFIN)
      })
      const returnPmTime = afternoonReturn ? new Date(afternoonReturn.dateheure).toTimeString().slice(0,5) : ''
      if (returnPmTime){
        latenessPm = returnPmTime > refPm ? diffMinutes(returnPmTime, refPm) : 0
      }

      // Détermination journée complète selon règle: <= 10 min matin ET <= 10 min après-midi
      const journeeComplete = (arrivee && depart) && (latenessAm <= this.LATE_LIMIT_MIN) && (latenessPm <= this.LATE_LIMIT_MIN)

      return { arrivee, depart, pause, travail, sup, retard: latenessAm + latenessPm, latenessAm, latenessPm, journeeComplete }

      function diffMinutes(a,b){
        const [ah,am]=a.split(':').map(Number), [bh,bm]=b.split(':').map(Number)
        return Math.max(0,(ah*60+am) - (bh*60+bm))
      }
    },
    anomalies(){
      const list = []
      const ENTR=1, SORT=2
      const hasIn = this.pointages.some(p=>p.idtypepointage?.id===ENTR)
      const hasOut = this.pointages.some(p=>p.idtypepointage?.id===SORT)
      if (hasIn && !hasOut) list.push('Pas de sortie enregistrée')
      if (!hasIn && !hasOut) list.push('Journée sans pointage')
      if (this.metrics.travail > 0 && this.metrics.travail < 7 && !this.metrics.journeeComplete) {
        list.push('Journée incomplète (< 7h)')
      }
      // double pointage simple: deux Entrée consécutives
      const sorted = this.pointages.slice().sort((a,b)=>a.dateheure.localeCompare(b.dateheure))
      for(let i=1;i<sorted.length;i++){
        if (sorted[i-1].idtypepointage?.id === sorted[i].idtypepointage?.id) list.push('Double pointage détecté')
      }
      return [...new Set(list)]
    }
  },
  methods: {
    initials(p,n){ return ((p||'')[0]||'') + ((n||'')[0]||'') },
    badgeClass(typeId){
      // Basic mapping to semantic classes; actual colors handled inline in template
      // 1 Entrée, 2 Sortie, 3 Début pause, 4 Fin pause
      if (typeId === 1) return 'badge-in'
      if (typeId === 2) return 'badge-out'
      if (typeId === 3) return 'badge-pause-start'
      if (typeId === 4) return 'badge-pause-end'
      return ''
    },
    async load(){
      try{
        const res = await fetch(`/api/pointages/employee/matricule/${this.matricule}/date/${this.date}`)
        this.pointages = await res.json()
        this.employee = this.pointages[0]?.idemploye || null
      }catch(e){ console.error(e) }
    },
    goPrev(){
      const d = new Date(this.date)
      d.setDate(d.getDate()-1)
      const s = d.toISOString().slice(0,10)
      this.$router.push(`/pointage/detail/${this.matricule}/${s}`)
    },
    goNext(){
      const d = new Date(this.date)
      d.setDate(d.getDate()+1)
      const s = d.toISOString().slice(0,10)
      this.$router.push(`/pointage/detail/${this.matricule}/${s}`)
    }
  },
  watch: {
    '$route.params.date': {
      immediate: true,
      handler(newVal){ this.date = newVal; this.load() }
    }
  },
  mounted(){ this.load() }
}
</script>

<style scoped>
</style>
