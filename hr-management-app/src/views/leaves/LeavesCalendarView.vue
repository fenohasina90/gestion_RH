<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h1>Calendrier des congés</h1>
        <div class="jumper">
          <select v-model.number="month" class="input">
            <option v-for="m in monthOptions" :key="m.value" :value="m.value">{{ m.label }}</option>
          </select>
          <select v-model.number="year" class="input">
            <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}</option>
          </select>
        </div>
      </div>
      <div class="header-actions">
        <button class="btn" @click="prevMonth">◀</button>
        <button class="btn" @click="today">Aujourd'hui</button>
        <button class="btn" @click="nextMonth">▶</button>
      </div>
    </div>

    <div class="calendar">
      <div class="cal-header">
        <div v-for="d in weekdays" :key="d" class="cal-cell cal-head">{{ d }}</div>
      </div>
      <div class="cal-grid">
        <div v-for="(cell, idx) in cells" :key="idx" class="cal-cell" :class="{
            other: !cell.inMonth,
            weekend: cell.weekend,
            holiday: cell.holiday
          }">
          <div class="cal-date">{{ cell.day }}</div>
          <div class="cal-count" v-if="cell.data && cell.data.count">
            <span class="badge" :title="namesTooltip(cell.data.names)">{{ cell.data.count }}</span>
          </div>
          <div class="tooltip" v-if="hoverIndex === idx && cell.data && cell.data.names && cell.data.names.length">
            <div class="tooltip-title">En congé:</div>
            <ul>
              <li v-for="(n, i) in cell.data.names" :key="i">{{ n }}</li>
            </ul>
          </div>
          <div class="hover-capture" @mouseenter="hoverIndex = idx" @mouseleave="hoverIndex = -1"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'LeavesCalendarView',
  data() {
    const today = new Date()
    return {
      year: today.getFullYear(),
      month: today.getMonth() + 1, // 1-12
      daysData: [],
      hoverIndex: -1,
      weekdays: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim']
    }
  },
  computed: {
    monthName() {
      return new Date(this.year, this.month - 1, 1).toLocaleString('fr-FR', { month: 'long' })
    },
    monthOptions() {
      const base = []
      for (let i = 1; i <= 12; i++) {
        base.push({ value: i, label: new Date(2025, i - 1, 1).toLocaleString('fr-FR', { month: 'long' }) })
      }
      return base
    },
    yearOptions() {
      const now = new Date().getFullYear()
      const start = now - 5
      const end = now + 5
      const arr = []
      for (let y = start; y <= end; y++) arr.push(y)
      return arr
    },
    firstDayOfMonth() {
      return new Date(this.year, this.month - 1, 1)
    },
    startOffset() {
      // compute how many empty cells before the 1st; our week starts Monday
      // JS getDay(): 0=Sun ... 6=Sat; convert to 1=Mon..7=Sun
      const js = this.firstDayOfMonth.getDay()
      const dow = js === 0 ? 7 : js
      return dow - 1
    },
    daysInMonth() {
      return new Date(this.year, this.month, 0).getDate()
    },
    cells() {
      const total = this.startOffset + this.daysInMonth
      const rows = Math.ceil(total / 7)
      const cells = []
      const prevMonthDate = new Date(this.year, this.month - 1, 0)
      const prevMonthDays = prevMonthDate.getDate()

      // days before 1st
      for (let i = 0; i < this.startOffset; i++) {
        const day = prevMonthDays - this.startOffset + 1 + i
        const date = new Date(this.year, this.month - 2, day)
        cells.push(this.buildCell(date, false))
      }
      // days in current month
      for (let d = 1; d <= this.daysInMonth; d++) {
        const date = new Date(this.year, this.month - 1, d)
        cells.push(this.buildCell(date, true))
      }
      // trailing cells to complete weeks
      while (cells.length % 7 !== 0) {
        const last = cells[cells.length - 1]
        const date = new Date(last.date)
        date.setDate(date.getDate() + 1)
        cells.push(this.buildCell(date, false))
      }
      return cells
    }
  },
  watch: {
    year() { this.load() },
    month() { this.load() }
  },
  mounted() { this.load() },
  methods: {
    async load() {
      try {
        const res = await axios.get('/api/conges/calendar', {
          params: { year: this.year, month: this.month }
        })
        this.daysData = Array.isArray(res.data?.days) ? res.data.days : []
      } catch (e) {
        this.daysData = []
        console.error('Erreur chargement calendrier', e)
      }
    },
    buildCell(date, inMonth) {
      const js = date.getDay()
      const weekend = (js === 6 || js === 0) // Sat or Sun
      // Use local date (no UTC shift) to match backend LocalDate strings
      const iso = [
        date.getFullYear(),
        String(date.getMonth() + 1).padStart(2, '0'),
        String(date.getDate()).padStart(2, '0')
      ].join('-')
      const dd = this.daysData.find(d => d.date === iso) || { count: 0, names: [], holiday: false, weekend }
      return {
        date,
        inMonth,
        weekend: dd.weekend,
        holiday: dd.holiday,
        day: date.getDate(),
        data: dd
      }
    },
    namesTooltip(list) {
      return list && list.length ? list.join('\n') : ''
    },
    prevMonth() {
      if (this.month === 1) { this.month = 12; this.year -= 1 } else { this.month -= 1 }
    },
    nextMonth() {
      if (this.month === 12) { this.month = 1; this.year += 1 } else { this.month += 1 }
    },
    today() {
      const t = new Date()
      this.year = t.getFullYear()
      this.month = t.getMonth() + 1
    }
  }
}
</script>

<style scoped>
.page { padding: 24px; max-width: 1100px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.jumper { display: flex; gap: 8px; margin-top: 6px; }
.btn { padding: 6px 10px; border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; cursor: pointer; }
.btn + .btn { margin-left: 6px; }
.calendar { background: #fff; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); padding: 10px; }
.cal-header { display: grid; grid-template-columns: repeat(7, 1fr); }
.cal-grid { display: grid; grid-template-columns: repeat(7, 1fr); grid-auto-rows: 120px; }
.cal-cell { position: relative; border: 1px solid #eef2f7; padding: 6px; overflow: hidden; }
.cal-head { background: #f9fafb; font-weight: 600; text-align: center; }
.cal-date { font-size: 12px; color: #6b7280; }
.cal-count { position: absolute; bottom: 6px; right: 6px; }
.badge { background: #2563eb; color: #fff; border-radius: 999px; padding: 2px 8px; font-size: 12px; font-weight: 600; }
.weekend { background: #fafafa; }
.holiday { background: #fff4f4; }
.other { color: #9ca3af; background: #fbfbfb; }
.tooltip { position: absolute; left: 6px; right: 6px; bottom: 26px; background: #111827; color: #fff; border-radius: 8px; padding: 8px; font-size: 12px; box-shadow: 0 6px 20px rgba(0,0,0,0.2); }
.tooltip-title { font-weight: 700; margin-bottom: 6px; }
.hover-capture { position: absolute; inset: 0; }
.input { min-width: 130px; height: 36px; padding: 0 10px; border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; }
</style>
