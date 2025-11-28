<template>
    <div class="sidebar" :class="{ collapsed: isCollapsed }">
        <!-- Header avec logo -->
        <div class="sidebar-header">
            <div class="logo-section">
                <div class="logo-circle">
                    <span class="logo-text">HR</span>
                </div>
                <div class="header-info">
                    <h2>ADMIN</h2>
                    <span class="status-indicator">
                        <span class="status-dot"></span>
                        connected
                    </span>
                </div>
            </div>
        </div>

        <!-- Theme toggle -->
        <div class="theme-toggle">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="5"></circle>
                <line x1="12" y1="1" x2="12" y2="3"></line>
                <line x1="12" y1="21" x2="12" y2="23"></line>
                <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
                <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                <line x1="1" y1="12" x2="3" y2="12"></line>
                <line x1="21" y1="12" x2="23" y2="12"></line>
                <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
                <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
            </svg>
            <span>Tema</span>
            <div class="toggle-switch">
                <input type="checkbox" id="theme-toggle" v-model="isDarkMode">
                <label for="theme-toggle" class="toggle-label"></label>
            </div>
        </div>

        <nav class="sidebar-nav">
            <!-- Section Tableaux de bord RH -->
            <div class="nav-section">
                <router-link to="/dashboard/rh" class="nav-item simple" :class="{ active: isActiveExact('/dashboard/rh') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M3 13h4v8H3z"></path>
                                <path d="M10 9h4v12h-4z"></path>
                                <path d="M17 5h4v16h-4z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Tableau de bord RH</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                <router-link to="/dashboard/performance" class="nav-item simple" :class="{ active: isActiveExact('/dashboard/performance') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M4 19l4-9 4 6 4-10 4 13"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Performance</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                <router-link to="/dashboard/competences" class="nav-item simple" :class="{ active: isActiveExact('/dashboard/competences') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 2l3 7h7l-5.5 4.5L18 21l-6-3.5L6 21l1.5-7.5L2 9h7z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Compétences</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- Section Annonces -->
            <div class="nav-section">
                <button class="accordion" :class="{ open: isOpen('annonces'), active: isAnyActive(['/announcements','/announcements/create']) }" @click="toggle('annonces')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M3 11l18-5v12L3 13V6z"></path>
                                <path d="M11 19a4 4 0 0 0 8 0"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Gestion des Annonces</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('annonces') }">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6,9 12,15 18,9"></polyline>
            </svg>
          </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('annonces')" class="submenu">
                        <router-link to="/announcements" class="nav-item sub" :class="{ active: isActiveExact('/announcements') }">Liste des Annonces</router-link>
                        <router-link to="/announcements/create" class="nav-item sub" :class="{ active: isActiveExact('/announcements/create') }">Créer Annonce</router-link>
                    </div>
                </transition>
            </div>

            <!-- Section Paie -->
            <div class="nav-section">
                <router-link to="/paie/bulletin" class="nav-item simple" :class="{ active: isActiveExact('/paie/bulletin') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="4" width="18" height="14" rx="2"></rect>
                                <path d="M8 10h8"></path>
                                <path d="M8 14h6"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Bulletin de paie</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                <router-link to="/paie/etat" class="nav-item simple" :class="{ active: isActiveExact('/paie/etat') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="4" width="18" height="14" rx="2"></rect>
                                <path d="M8 10h8"></path>
                                <path d="M8 14h8"></path>
                            </svg>
                        </span>
                        <span class="nav-label">État de paie</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- Section Pointage -->
            <div class="nav-section">
                <button class="accordion" :class="{ open: isOpen('pointage'), active: isAnyActive(['/pointage/aujourd-hui','/pointage/saisir']) }" @click="toggle('pointage')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <path d="M12 6v6l4 2"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Pointage</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('pointage') }">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('pointage')" class="submenu">
                        <router-link to="/pointage/aujourd-hui" class="nav-item sub" :class="{ active: isActiveExact('/pointage/aujourd-hui') }">Aujourd'hui</router-link>
                        <router-link to="/pointage/saisir" class="nav-item sub" :class="{ active: isActiveExact('/pointage/saisir') }">Saisie manuelle</router-link>
                        <router-link to="/pointage/releve" class="nav-item sub" :class="{ active: isActiveExact('/pointage/releve') }">Relevé de présence</router-link>
                    </div>
                </transition>
            </div>

            <!-- Section Candidatures -->
            <div class="nav-section">
                <router-link to="/applications" class="nav-item simple" :class="{ active: isActiveExact('/applications') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16l4-4h8a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Candidatures</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- Section QCM -->
            <div class="nav-section">
                <button class="accordion" :class="{ open: isOpen('qcm'), active: isAnyActive(['/qcm/create','/resultat-qcm']) }" @click="toggle('qcm')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <path d="M9.09 9a3 3 0 1 1 5.82 1c0 2-3 2-3 4"></path>
                                <line x1="12" y1="17" x2="12" y2="17"></line>
                            </svg>
                        </span>
                        <span class="nav-label">Tests QCM</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('qcm') }">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6,9 12,15 18,9"></polyline>
            </svg>
          </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('qcm')" class="submenu">
                        <router-link to="/qcm/create" class="nav-item sub" :class="{ active: isActiveExact('/qcm/create') }">Créer Test QCM</router-link>
                        <router-link to="/resultat-qcm" class="nav-item sub" :class="{ active: isActiveExact('/resultat-qcm') }">Résultats QCM</router-link>
                    </div>
                </transition>
            </div>

            <!-- Section Entretiens -->
            <div class="nav-section">
                <button class="accordion" :class="{ open: isOpen('entretiens'), active: isAnyActive(['/interviews/calendar','/interviews/results','/interviews/good-level']) }" @click="toggle('entretiens')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M3 4h18"></path>
                                <path d="M8 2v4"></path>
                                <path d="M16 2v4"></path>
                                <rect x="3" y="6" width="18" height="14" rx="2"></rect>
                                <path d="M8 10h8"></path>
                                <path d="M8 14h6"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Entretiens</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('entretiens') }">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6,9 12,15 18,9"></polyline>
            </svg>
          </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('entretiens')" class="submenu">
                        <router-link to="/interviews/calendar" class="nav-item sub" :class="{ active: isActiveExact('/interviews/calendar') }">Calendrier</router-link>
                        <router-link to="/interviews/results" class="nav-item sub" :class="{ active: isActiveExact('/interviews/results') }">Résultats</router-link>
                        <router-link to="/interviews/good-level" class="nav-item sub" :class="{ active: isActiveExact('/interviews/good-level') }">Bon Niveau</router-link>
                    </div>
                </transition>
            </div>

            <!-- Section Employés -->
            <div class="nav-section">
                <button class="accordion" :class="{ open: isOpen('employes'), active: isAnyActive(['/employees','/employees/add']) }" @click="toggle('employes')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                                <circle cx="9" cy="7" r="4"></circle>
                                <path d="m22 21-3-3m0 0a5.5 5.5 0 1 0-7.78-7.78 5.5 5.5 0 0 0 7.78 7.78Z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Employés</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('employes') }">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('employes')" class="submenu">
                        <router-link to="/employees" class="nav-item sub" :class="{ active: isActiveExact('/employees') }">Lister</router-link>
                        <router-link to="/employees/add" class="nav-item sub" :class="{ active: isActiveExact('/employees/add') }">Ajouter</router-link>
                    </div>
                </transition>
            </div>

            <!-- Section Congés -->
            <div class="nav-section">
                <button class="accordion" :class="{ open: isOpen('conges'), active: isAnyActive(['/leaves/balances']) }" @click="toggle('conges')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <path d="M12 6v6l4 2"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Congés</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('conges') }">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('conges')" class="submenu">
                        <router-link to="/leaves/balances" class="nav-item sub" :class="{ active: isActiveExact('/leaves/balances') }">Solde</router-link>
                        <router-link to="/leaves/pending" class="nav-item sub" :class="{ active: isActiveExact('/leaves/pending') }">Demandes en attente</router-link>
                        <router-link to="/leaves/calendar" class="nav-item sub" :class="{ active: isActiveExact('/leaves/calendar') }">Calendrier</router-link>
                    </div>
                </transition>
            </div>

            <!-- Section Contrats -->
            <div class="nav-section">
                <router-link to="/contracts" class="nav-item simple" :class="{ active: isActiveExact('/contracts') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M9 12h6"></path>
                                <path d="M9 16h6"></path>
                                <path d="M9 8h6"></path>
                                <path d="M4 19.5V5a2 2 0 0 1 2-2h12"></path>
                                <path d="M20 14v7"></path>
                                <path d="M20 21l-2-2"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Contrats d'Essai</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- Section Profils -->
            <div class="nav-section">
                <button class="accordion" :class="{ open: isOpen('profils'), active: isAnyActive(['/profils','/criteres']) }" @click="toggle('profils')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20 21v-2a4 4 0 0 0-3-3.87"></path>
                                <path d="M4 21v-2a4 4 0 0 1 3-3.87"></path>
                                <circle cx="12" cy="7" r="4"></circle>
                            </svg>
                        </span>
                        <span class="nav-label">Profils et Critères</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('profils') }">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6,9 12,15 18,9"></polyline>
            </svg>
          </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('profils')" class="submenu">
                        <router-link to="/profils" class="nav-item sub" :class="{ active: isActiveExact('/profils') }">Gestion des Profils</router-link>
                        <router-link to="/criteres" class="nav-item sub" :class="{ active: isActiveExact('/criteres') }">Gestion des Critères</router-link>
                    </div>
                </transition>
            </div>

            <!-- Section Système -->
            <div class="nav-section">
                <router-link to="/connection-test" class="nav-item simple" :class="{ active: isActiveExact('/connection-test') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 2v4"></path>
                                <path d="M12 18v4"></path>
                                <rect x="4" y="8" width="16" height="8" rx="2"></rect>
                            </svg>
                        </span>
                        <span class="nav-label">Test Connexion Backend</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>
        </nav>

        <!-- User profile -->
        <div class="user-profile">
            <div class="profile-card">
                <div class="profile-avatar">
                    <span class="avatar-text">BD</span>
                    <div class="online-indicator"></div>
                </div>
                <div class="profile-info">
                    <div class="profile-name">Berkay Derin</div>
                    <div class="profile-role">Frontend Software Engineer</div>
                    <div class="profile-email">derinberkay67@gmail.com</div>
                </div>
                <button class="profile-action">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M9 18l6-6-6-6"></path>
                    </svg>
                </button>
            </div>
        </div>
    </div>
</template>


<script>
export default {
    name: 'Sidebar',
    props: {
        isCollapsed: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            isDarkMode: false,
            open: {
                annonces: false,
                qcm: false,
                entretiens: false,
                profils: false,
                employes: false,
                conges: false,
                pointage: false
            }
        }
    },
    created() {
        const path = this.$route.path
        if (this.isAnyActive(['/announcements','/announcements/create'])) this.open.annonces = true
        if (this.isAnyActive(['/qcm/create','/resultat-qcm'])) this.open.qcm = true
        if (this.isAnyActive(['/interviews/calendar','/interviews/results','/interviews/good-level'])) this.open.entretiens = true
        if (this.isAnyActive(['/profils','/criteres'])) this.open.profils = true
        if (this.isAnyActive(['/employees','/employees/add'])) this.open.employes = true
        if (this.isAnyActive(['/leaves/balances'])) this.open.conges = true
        if (this.isAnyActive(['/pointage/aujourd-hui','/pointage/saisir'])) this.open.pointage = true
    },
    methods: {
        toggle(key) {
            Object.keys(this.open).forEach(k => {
                this.open[k] = (k === key) ? !this.open[k] : false
            })
        },
        isOpen(key) {
            return !!this.open[key]
        },
        isActive(prefix) {
            return this.$route.path === prefix || this.$route.path.startsWith(prefix)
        },
        isAnyActive(prefixes) {
            return prefixes.some(p => this.isActive(p))
        },
        isActiveExact(path) {
            return this.$route.path === path
        }
    },
    watch: {
        '$route.path'(newPath) {
            const map = [
                { key: 'annonces', paths: ['/announcements','/announcements/create'] },
                { key: 'qcm', paths: ['/qcm/create','/resultat-qcm'] },
                { key: 'entretiens', paths: ['/interviews/calendar','/interviews/results','/interviews/good-level'] },
                { key: 'profils', paths: ['/profils','/criteres'] },
                { key: 'employes', paths: ['/employees','/employees/add'] },
                { key: 'conges', paths: ['/leaves/balances'] },
                { key: 'pointage', paths: ['/pointage/aujourd-hui','/pointage/saisir'] }
            ]
            Object.keys(this.open).forEach(k => this.open[k] = false)
            const found = map.find(m => this.isAnyActive(m.paths))
            if (found) this.open[found.key] = true
        }
    }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.sidebar {
    width: 280px;
    height: 100vh;
    background: #ffffff;
    border-right: 1px solid #e5e7eb;
    display: flex;
    flex-direction: column;
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    overflow-x: hidden; /* empêcher le scroll horizontal */
}

/* Header Section */
.sidebar-header {
    padding: 20px;
    border-bottom: 1px solid #f3f4f6;
}

.logo-section {
    display: flex;
    align-items: center;
    gap: 12px;
}

.logo-circle {
    width: 40px;
    height: 40px;
    background: #059669;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 16px;
    color: white;
}

.header-info h2 {
    font-size: 16px;
    font-weight: 600;
    color: #111827;
    margin: 0 0 4px 0;
}

.status-indicator {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #6b7280;
}

.status-dot {
    width: 6px;
    height: 6px;
    background: #10b981;
    border-radius: 50%;
}

/* Search Section */
.search-container {
    padding: 0 20px 20px 20px;
}

.search-input-wrapper {
    position: relative;
}

.search-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
}

.search-input {
    width: 100%;
    height: 40px;
    padding: 0 12px 0 40px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 14px;
    background: #f9fafb;
    color: #111827;
    transition: all 0.2s ease;
}

.search-input:focus {
    outline: none;
    border-color: #059669;
    background: #ffffff;
    box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.1);
}

.search-input::placeholder {
    color: #9ca3af;
}

/* Theme Toggle */
.theme-toggle {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    border-bottom: 1px solid #f3f4f6;
    font-size: 14px;
    color: #374151;
}

.theme-toggle svg {
    color: #6b7280;
}

.toggle-switch {
    margin-left: auto;
}

.toggle-switch input {
    display: none;
}

.toggle-label {
    display: block;
    width: 40px;
    height: 22px;
    background: #059669;
    border-radius: 11px;
    position: relative;
    cursor: pointer;
    transition: all 0.2s ease;
}

.toggle-label::after {
    content: '';
    position: absolute;
    top: 2px;
    right: 2px;
    width: 18px;
    height: 18px;
    background: white;
    border-radius: 50%;
    transition: all 0.2s ease;
}

/* Navigation */
.sidebar-nav {
    flex: 1;
    padding: 20px 0;
    overflow-y: auto;
    overflow-x: hidden; /* pas de scroll horizontal dans le nav */
}

.nav-section {
    margin-bottom: 32px;
}

.section-title {
    font-size: 11px;
    font-weight: 600;
    color: #6b7280;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin: 0 0 12px 0;
    padding: 0 20px;
}

/* Navigation Items */
.nav-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 20px;
    margin: 0 16px;
    border-radius: 8px;
    text-decoration: none;
    color: #374151;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s ease;
    position: relative;
    white-space: nowrap;         /* empêcher le wrap qui pourrait déborder */
    overflow: hidden;            /* masquer tout débordement horizontal */
    text-overflow: ellipsis;     /* ellipsis si trop long */
}

.nav-item.simple {
    margin: 2px 16px;
}

.nav-item:hover {
    background: #f3f4f6;
    color: #111827;
}

.nav-item.active {
    background: #dcfce7;
    color: #166534;
    font-weight: 600;
}

.nav-content {
    display: flex;
    align-items: center;
    gap: 12px;
}

.nav-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6b7280;
}

.nav-item.active .nav-icon {
    color: #059669;
}

.active-indicator {
    position: absolute;
    right: 12px;
    width: 4px;
    height: 20px;
    background: #059669;
    border-radius: 2px;
    opacity: 0;
    transition: opacity 0.2s ease;
}

.nav-item.active .active-indicator {
    opacity: 1;
}

/* Badges */
.badge {
    font-size: 11px;
    font-weight: 600;
    padding: 2px 8px;
    border-radius: 12px;
    background: #f3f4f6;
    color: #6b7280;
    min-width: 20px;
    text-align: center;
}

.badge.green {
    background: #dcfce7;
    color: #166534;
}

/* Accordion */
.accordion {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 12px 20px;
    margin: 2px 16px;
    border: none;
    background: transparent;
    color: #374151;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    overflow: hidden;            /* sécurité anti-scroll horizontal */
}

.accordion:hover {
    background: #f3f4f6;
    color: #111827;
}

.accordion.active,
.accordion.open {
    background: #dcfce7;
    color: #166534;
    font-weight: 600;
}

.accordion-right {
    display: flex;
    align-items: center;
    gap: 8px;
}

.chevron {
    transition: transform 0.2s ease;
    color: #9ca3af;
}

.chevron.rotate {
    transform: rotate(180deg);
}

/* Submenu */
.submenu {
    margin-top: 4px;
    margin-left: 16px;
    border-left: 2px solid #e5e7eb;
}

.nav-item.sub {
    margin: 0;
    padding: 8px 20px 8px 24px;
    font-size: 13px;
    color: #6b7280;
}

.nav-item.sub:hover {
    background: #f9fafb;
    color: #374151;
}

.nav-item.sub.active {
    background: #f0fdf4;
    color: #166534;
    border-left: 2px solid #059669;
    margin-left: -2px;
}

/* User Profile */
.user-profile {
    padding: 20px;
    border-top: 1px solid #f3f4f6;
}

.profile-card {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #059669;
    border-radius: 12px;
    color: white;
}

.profile-avatar {
    position: relative;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 14px;
}

.online-indicator {
    position: absolute;
    top: -2px;
    right: -2px;
    width: 8px;
    height: 8px;
    background: #10b981;
    border: 2px solid white;
    border-radius: 50%;
}

.profile-info {
    flex: 1;
    min-width: 0;
}

.profile-name {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 2px;
}

.profile-role {
    font-size: 12px;
    opacity: 0.8;
    margin-bottom: 2px;
}

.profile-email {
    font-size: 11px;
    opacity: 0.7;
}

.profile-action {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    opacity: 0.7;
    transition: opacity 0.2s ease;
}

.profile-action:hover {
    opacity: 1;
}

/* Animations */
.slide-fade-enter-active,
.slide-fade-leave-active {
    transition: all 0.2s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
    opacity: 0;
    transform: translateY(-8px);
}

/* Collapsed state */
.sidebar.collapsed {
    width: 72px;
    overflow-x: hidden; /* s'assurer qu'il n'y a jamais de scroll horizontal */
}

.sidebar.collapsed .sidebar-header,
.sidebar.collapsed .search-container,
.sidebar.collapsed .theme-toggle,
.sidebar.collapsed .section-title,
.sidebar.collapsed .user-profile {
    display: none;
}

.sidebar.collapsed .nav-item span,
.sidebar.collapsed .badge,
.sidebar.collapsed .chevron {
    display: none;
}

.sidebar.collapsed .nav-item {
    justify-content: center;
    margin: 4px 12px;
}
</style>