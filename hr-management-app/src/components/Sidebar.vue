<template>
    <div class="sidebar" :class="{ collapsed: isCollapsed }">
        <!-- Header avec logo -->
        <div class="sidebar-header">
            <div class="logo-section">
                <div class="logo-circle">
                    <span class="logo-text">HR</span>
                </div>
                <div class="header-info">
                    <h2>RH PRO</h2>
                    <span class="status-indicator">
                        <span class="status-dot"></span>
                        Système RH
                    </span>
                </div>
            </div>
        </div>

        <!-- Barre de recherche -->
        <div class="search-container">
            <div class="search-input-wrapper">
                <span class="search-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                </span>
                <input type="text" class="search-input" placeholder="Rechercher..." />
            </div>
        </div>

        <!-- Navigation principale -->
        <nav class="sidebar-nav">
            <!-- SECTION 1: DASHBOARD & TABLEAUX DE BORD -->
            <div class="nav-section">
                <h3 class="section-title">Tableaux de bord</h3>
                <router-link to="/dashboard/rh" class="nav-item simple" :class="{ active: isActiveExact('/dashboard/rh') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="3" width="7" height="9"></rect>
                                <rect x="14" y="3" width="7" height="5"></rect>
                                <rect x="14" y="12" width="7" height="9"></rect>
                                <rect x="3" y="16" width="7" height="5"></rect>
                            </svg>
                        </span>
                        <span class="nav-label">Dashboard RH</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                <router-link to="/dashboard/performance" class="nav-item simple" :class="{ active: isActiveExact('/dashboard/performance') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M18 20V10"></path>
                                <path d="M12 20V4"></path>
                                <path d="M6 20v-6"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Performance</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- SECTION 2: GESTION DES EMPLOYÉS -->
            <div class="nav-section">
                <h3 class="section-title">Gestion des employés</h3>
                <button class="accordion" :class="{ open: isOpen('employes'), active: isAnyActive(['/employees','/employees/add']) }" @click="toggle('employes')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="9" cy="7" r="4"></circle>
                                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Employés</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('employes') }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('employes')" class="submenu">
                        <router-link to="/employees" class="nav-item sub" :class="{ active: isActiveExact('/employees') }">
                            <span class="submenu-text">Liste des employés</span>
                        </router-link>
                        <router-link to="/employees/add" class="nav-item sub" :class="{ active: isActiveExact('/employees/add') }">
                            <span class="submenu-text">Ajouter un employé</span>
                        </router-link>
                        <router-link to="/employees/departments" class="nav-item sub" :class="{ active: isActiveExact('/employees/departments') }">
                            <span class="submenu-text">Départements</span>
                        </router-link>
                    </div>
                </transition>
                
                <button class="accordion" :class="{ open: isOpen('conges'), active: isAnyActive(['/leaves/balances','/leaves/pending','/leaves/calendar']) }" @click="toggle('conges')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                <line x1="16" y1="2" x2="16" y2="6"></line>
                                <line x1="8" y1="2" x2="8" y2="6"></line>
                                <line x1="3" y1="10" x2="21" y2="10"></line>
                            </svg>
                        </span>
                        <span class="nav-label">Congés</span>
                        <span class="badge green" v-if="pendingLeavesCount > 0">{{ pendingLeavesCount }}</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('conges') }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('conges')" class="submenu">
                        <router-link to="/leaves/pending" class="nav-item sub" :class="{ active: isActiveExact('/leaves/pending') }">
                            <span class="submenu-text">Demandes en attente</span>
                            <span v-if="pendingLeavesCount > 0" class="badge">{{ pendingLeavesCount }}</span>
                        </router-link>
                        <router-link to="/leaves/balances" class="nav-item sub" :class="{ active: isActiveExact('/leaves/balances') }">
                            <span class="submenu-text">Soldes de congés</span>
                        </router-link>
                        <router-link to="/leaves/calendar" class="nav-item sub" :class="{ active: isActiveExact('/leaves/calendar') }">
                            <span class="submenu-text">Calendrier</span>
                        </router-link>
                    </div>
                </transition>
                
                <button class="accordion" :class="{ open: isOpen('pointage'), active: isAnyActive(['/pointage/aujourd-hui','/pointage/saisir','/pointage/releve']) }" @click="toggle('pointage')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <polyline points="12 6 12 12 16 14"></polyline>
                            </svg>
                        </span>
                        <span class="nav-label">Pointage</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('pointage') }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('pointage')" class="submenu">
                        <router-link to="/pointage/aujourd-hui" class="nav-item sub" :class="{ active: isActiveExact('/pointage/aujourd-hui') }">
                            <span class="submenu-text">Aujourd'hui</span>
                        </router-link>
                        <router-link to="/pointage/saisir" class="nav-item sub" :class="{ active: isActiveExact('/pointage/saisir') }">
                            <span class="submenu-text">Saisie manuelle</span>
                        </router-link>
                        <router-link to="/pointage/releve" class="nav-item sub" :class="{ active: isActiveExact('/pointage/releve') }">
                            <span class="submenu-text">Relevé de présence</span>
                        </router-link>
                    </div>
                </transition>
            </div>

            <!-- SECTION 3: GESTION DE LA PAIE -->
            <div class="nav-section">
                <h3 class="section-title">Gestion de la paie</h3>
                <router-link to="/paie/bulletin" class="nav-item simple" :class="{ active: isActiveExact('/paie/bulletin') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="2" y="4" width="20" height="16" rx="2"></rect>
                                <path d="M6 8h12"></path>
                                <path d="M6 12h12"></path>
                                <path d="M6 16h8"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Bulletins de paie</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                <router-link to="/paie/etat" class="nav-item simple" :class="{ active: isActiveExact('/paie/etat') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="2" y="3" width="20" height="14" rx="2"></rect>
                                <line x1="8" y1="21" x2="16" y2="21"></line>
                                <line x1="12" y1="17" x2="12" y2="21"></line>
                            </svg>
                        </span>
                        <span class="nav-label">États de paie</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                <router-link to="/paie/parametres" class="nav-item simple" :class="{ active: isActiveExact('/paie/parametres') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="3"></circle>
                                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 1 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 1 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 1 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 1 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Paramètres</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- SECTION 4: RECRUTEMENT & ONBOARDING -->
            <div class="nav-section">
                <h3 class="section-title">Recrutement</h3>
                <button class="accordion" :class="{ open: isOpen('annonces'), active: isAnyActive(['/announcements','/announcements/create']) }" @click="toggle('annonces')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="2" y="7" width="20" height="14" rx="2"></rect>
                                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Annonces</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('annonces') }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('annonces')" class="submenu">
                        <router-link to="/announcements" class="nav-item sub" :class="{ active: isActiveExact('/announcements') }">
                            <span class="submenu-text">Liste des annonces</span>
                        </router-link>
                        <router-link to="/announcements/create" class="nav-item sub" :class="{ active: isActiveExact('/announcements/create') }">
                            <span class="submenu-text">Créer une annonce</span>
                        </router-link>
                    </div>
                </transition>
                
                <router-link to="/applications" class="nav-item simple" :class="{ active: isActiveExact('/applications') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16l4-4h8a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Candidatures</span>
                        <span class="badge green" v-if="pendingApplicationsCount > 0">{{ pendingApplicationsCount }}</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                
                <button class="accordion" :class="{ open: isOpen('entretiens'), active: isAnyActive(['/interviews/calendar','/interviews/results','/interviews/good-level']) }" @click="toggle('entretiens')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Entretiens</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('entretiens') }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('entretiens')" class="submenu">
                        <router-link to="/interviews/calendar" class="nav-item sub" :class="{ active: isActiveExact('/interviews/calendar') }">
                            <span class="submenu-text">Calendrier</span>
                        </router-link>
                        <router-link to="/interviews/results" class="nav-item sub" :class="{ active: isActiveExact('/interviews/results') }">
                            <span class="submenu-text">Résultats</span>
                        </router-link>
                        <router-link to="/interviews/good-level" class="nav-item sub" :class="{ active: isActiveExact('/interviews/good-level') }">
                            <span class="submenu-text">Bon niveau</span>
                        </router-link>
                    </div>
                </transition>
                
                <button class="accordion" :class="{ open: isOpen('qcm'), active: isAnyActive(['/qcm/create','/resultat-qcm']) }" @click="toggle('qcm')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <path d="M9.09 9a3 3 0 1 1 5.82 1c0 2-3 2-3 4"></path>
                                <line x1="12" y1="17" x2="12" y2="17"></line>
                            </svg>
                        </span>
                        <span class="nav-label">Tests QCM</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('qcm') }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('qcm')" class="submenu">
                        <router-link to="/qcm/create" class="nav-item sub" :class="{ active: isActiveExact('/qcm/create') }">
                            <span class="submenu-text">Créer test</span>
                        </router-link>
                        <router-link to="/resultat-qcm" class="nav-item sub" :class="{ active: isActiveExact('/resultat-qcm') }">
                            <span class="submenu-text">Résultats</span>
                        </router-link>
                    </div>
                </transition>
                
                <router-link to="/contracts" class="nav-item simple" :class="{ active: isActiveExact('/contracts') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                <polyline points="14 2 14 8 20 8"></polyline>
                                <line x1="16" y1="13" x2="8" y2="13"></line>
                                <line x1="16" y1="17" x2="8" y2="17"></line>
                                <polyline points="10 9 9 9 8 9"></polyline>
                            </svg>
                        </span>
                        <span class="nav-label">Contrats</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- SECTION 5: GESTION DES COMPÉTENCES -->
            <div class="nav-section">
                <h3 class="section-title">Compétences & Évaluation</h3>
                <router-link to="/dashboard/competences" class="nav-item simple" :class="{ active: isActiveExact('/dashboard/competences') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Compétences</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                
                <button class="accordion" :class="{ open: isOpen('profils'), active: isAnyActive(['/profils','/criteres']) }" @click="toggle('profils')">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20 21v-2a4 4 0 0 0-3-3.87"></path>
                                <path d="M4 21v-2a4 4 0 0 1 3-3.87"></path>
                                <circle cx="12" cy="7" r="4"></circle>
                            </svg>
                        </span>
                        <span class="nav-label">Profils & Critères</span>
                    </div>
                    <span class="chevron" :class="{ rotate: isOpen('profils') }">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"></polyline>
                        </svg>
                    </span>
                </button>
                <transition name="slide-fade">
                    <div v-show="isOpen('profils')" class="submenu">
                        <router-link to="/profils" class="nav-item sub" :class="{ active: isActiveExact('/profils') }">
                            <span class="submenu-text">Gestion des profils</span>
                        </router-link>
                        <router-link to="/criteres" class="nav-item sub" :class="{ active: isActiveExact('/criteres') }">
                            <span class="submenu-text">Gestion des critères</span>
                        </router-link>
                    </div>
                </transition>
            </div>

            <!-- SECTION 6: COMMUNICATION & SUPPORT -->
            <div class="nav-section">
                <h3 class="section-title">Communication</h3>
                <router-link to="/dashboard/rh/messages" class="nav-item simple" :class="{ active: isActiveExact('/dashboard/rh/messages') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Messagerie RH</span>
                        <span class="badge green" v-if="unreadMessagesCount > 0">{{ unreadMessagesCount }}</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
                <router-link to="/chat/rh" class="nav-item simple" :class="{ active: isActiveExact('/chat/rh') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                            </svg>
                        </span>
                        <span class="nav-label">Chatbot RH</span>
                    </div>
                    <span class="active-indicator"></span>
                </router-link>
            </div>

            <!-- SECTION 7: ADMINISTRATION & SUPPORT -->
            <div class="nav-section">
                <h3 class="section-title">Administration</h3>
                <router-link to="/connection-test" class="nav-item simple" :class="{ active: isActiveExact('/connection-test') }">
                    <div class="nav-content">
                        <span class="nav-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M4 12V6a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v6"></path>
                                <path d="M4 18h16"></path>
                                <circle cx="12" cy="14" r="2"></circle>
                            </svg>
                        </span>
                        <span class="nav-label">Test Connexion</span>
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
                <button class="profile-action" @click="logout">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                        <polyline points="16 17 21 12 16 7"></polyline>
                        <line x1="21" y1="12" x2="9" y2="12"></line>
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
            open: {
                annonces: false,
                qcm: false,
                entretiens: false,
                profils: false,
                employes: false,
                conges: false,
                pointage: false
            },
            pendingLeavesCount: 0,
            pendingApplicationsCount: 0,
            unreadMessagesCount: 0
        }
    },
    created() {
        const path = this.$route.path
        if (this.isAnyActive(['/announcements','/announcements/create'])) this.open.annonces = true
        if (this.isAnyActive(['/qcm/create','/resultat-qcm'])) this.open.qcm = true
        if (this.isAnyActive(['/interviews/calendar','/interviews/results','/interviews/good-level'])) this.open.entretiens = true
        if (this.isAnyActive(['/profils','/criteres'])) this.open.profils = true
        if (this.isAnyActive(['/employees','/employees/add'])) this.open.employes = true
        if (this.isAnyActive(['/leaves/balances','/leaves/pending','/leaves/calendar'])) this.open.conges = true
        if (this.isAnyActive(['/pointage/aujourd-hui','/pointage/saisir','/pointage/releve'])) this.open.pointage = true
        
        // Charger les compteurs
        this.loadCounters()
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
        },
        async loadCounters() {
            // Simuler le chargement des compteurs
            try {
                this.pendingLeavesCount = 3
                this.pendingApplicationsCount = 5
                this.unreadMessagesCount = 2
            } catch (error) {
                console.error('Erreur chargement compteurs:', error)
            }
        },
        logout() {
            this.$router.push('/login')
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
                { key: 'conges', paths: ['/leaves/balances','/leaves/pending','/leaves/calendar'] },
                { key: 'pointage', paths: ['/pointage/aujourd-hui','/pointage/saisir','/pointage/releve'] }
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
    width: 300px;
    height: 100vh;
    background: linear-gradient(180deg, #ffffff 0%, #f9fafb 100%);
    border-right: 1px solid #e5e7eb;
    display: flex;
    flex-direction: column;
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    overflow-x: hidden;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    position: relative;
    z-index: 50;
}

/* Header Section - MEILLEUR CONTRASTE */
.sidebar-header {
    padding: 24px;
    border-bottom: 1px solid #e5e7eb;
    background: #ffffff;
}

.logo-section {
    display: flex;
    align-items: center;
    gap: 14px;
}

.logo-circle {
    width: 46px;
    height: 46px;
    background: linear-gradient(135deg, #059669, #10b981);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 18px;
    color: white;
    box-shadow: 0 4px 6px -1px rgba(5, 150, 105, 0.2);
}

.header-info h2 {
    font-size: 20px;
    font-weight: 700;
    color: #111827;
    margin: 0 0 6px 0;
    letter-spacing: -0.025em;
    line-height: 1.2;
}

.status-indicator {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    font-weight: 600;
    color: #059669;
    background: #f0fdf4;
    padding: 6px 10px;
    border-radius: 8px;
    width: fit-content;
}

.status-dot {
    width: 10px;
    height: 10px;
    background: #10b981;
    border-radius: 50%;
    animation: pulse 2s infinite;
}

@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

/* Search Section */
.search-container {
    padding: 20px 24px;
    background: #ffffff;
    border-bottom: 1px solid #e5e7eb;
}

.search-input-wrapper {
    position: relative;
}

.search-icon {
    position: absolute;
    left: 14px;
    top: 50%;
    transform: translateY(-50%);
    color: #6b7280;
}

.search-input {
    width: 100%;
    height: 44px;
    padding: 0 16px 0 44px;
    border: 2px solid #d1d5db;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 400;
    background: #f9fafb;
    color: #111827;
    transition: all 0.2s ease;
    font-family: 'Inter', sans-serif;
}

.search-input:focus {
    outline: none;
    border-color: #059669;
    background: #ffffff;
    box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.1);
}

.search-input::placeholder {
    color: #9ca3af;
    font-weight: 400;
}

/* Navigation */
.sidebar-nav {
    flex: 1;
    padding: 20px 0;
    overflow-y: auto;
    overflow-x: hidden;
}

.nav-section {
    margin-bottom: 28px;
}

.section-title {
    font-size: 12px;
    font-weight: 700;
    color: #6b7280;
    text-transform: uppercase;
    letter-spacing: 0.8px;
    margin: 0 0 14px 0;
    padding: 0 24px 10px 24px;
    border-bottom: 2px solid #f3f4f6;
    line-height: 1.3;
}

/* Navigation Items - MEILLEURE LISIBILITÉ */
.nav-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 24px;
    margin: 4px 16px;
    border-radius: 10px;
    text-decoration: none;
    color: #374151;
    font-size: 15px;
    font-weight: 500;
    transition: all 0.2s ease;
    position: relative;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    border: 1px solid transparent;
    line-height: 1.4;
}

.nav-item:hover {
    background: #f3f4f6;
    color: #111827;
    border-color: #e5e7eb;
}

.nav-item.active {
    background: #f0fdf4;
    color: #166534;
    font-weight: 600;
    border-color: #bbf7d0;
    box-shadow: 0 2px 4px rgba(5, 150, 105, 0.1);
}

.nav-content {
    display: flex;
    align-items: center;
    gap: 14px;
    flex: 1;
    min-width: 0;
}

.nav-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6b7280;
    flex-shrink: 0;
    width: 22px;
    height: 22px;
}

.nav-item.active .nav-icon {
    color: #059669;
}

.nav-label {
    font-weight: 500;
    letter-spacing: -0.01em;
}

.nav-item.active .nav-label {
    font-weight: 600;
}

.active-indicator {
    position: absolute;
    right: 16px;
    width: 8px;
    height: 8px;
    background: #059669;
    border-radius: 50%;
    opacity: 0;
    transition: opacity 0.2s ease;
}

.nav-item.active .active-indicator {
    opacity: 1;
}

/* Badges */
.badge {
    font-size: 12px;
    font-weight: 700;
    padding: 4px 10px;
    border-radius: 12px;
    background: #f3f4f6;
    color: #6b7280;
    min-width: 24px;
    text-align: center;
    flex-shrink: 0;
    margin-left: 12px;
    line-height: 1;
}

.badge.green {
    background: #dcfce7;
    color: #166534;
}

/* Accordion - MEILLEUR CONTRASTE */
.accordion {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 14px 24px;
    margin: 4px 16px;
    border: 1px solid transparent;
    background: transparent;
    color: #374151;
    font-size: 15px;
    font-weight: 500;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s ease;
    overflow: hidden;
    line-height: 1.4;
}

.accordion:hover {
    background: #f3f4f6;
    color: #111827;
    border-color: #e5e7eb;
}

.accordion.active,
.accordion.open {
    background: #f0fdf4;
    color: #166534;
    font-weight: 600;
    border-color: #bbf7d0;
}

.chevron {
    transition: transform 0.2s ease;
    color: #9ca3af;
    flex-shrink: 0;
    margin-left: 12px;
}

.chevron.rotate {
    transform: rotate(180deg);
    color: #059669;
}

/* Submenu - MEILLEURE LISIBILITÉ */
.submenu {
    margin-top: 4px;
    margin-left: 28px;
    padding-left: 16px;
    border-left: 2px solid #e5e7eb;
}

.nav-item.sub {
    margin: 0;
    padding: 12px 24px 12px 28px;
    font-size: 14px;
    color: #6b7280;
    border-radius: 8px;
    border: none;
    font-weight: 500;
}

.submenu-text {
    font-weight: 500;
    letter-spacing: -0.01em;
}

.nav-item.sub:hover {
    background: #f9fafb;
    color: #374151;
}

.nav-item.sub:hover .submenu-text {
    font-weight: 600;
}

.nav-item.sub.active {
    background: #dcfce7;
    color: #166534;
    border-left: 2px solid #059669;
    margin-left: -2px;
    font-weight: 600;
}

.nav-item.sub.active .submenu-text {
    font-weight: 600;
}

/* User Profile - MEILLEUR CONTRASTE */
.user-profile {
    padding: 24px;
    border-top: 1px solid #e5e7eb;
    background: #ffffff;
    margin-top: auto;
}

.profile-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: linear-gradient(135deg, #059669, #10b981);
    border-radius: 14px;
    color: white;
    position: relative;
    overflow: hidden;
}

.profile-card::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 60px;
    height: 60px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transform: translate(30px, -30px);
}

.profile-avatar {
    position: relative;
    width: 48px;
    height: 48px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 18px;
    backdrop-filter: blur(4px);
    flex-shrink: 0;
    z-index: 1;
}

.avatar-text {
    font-weight: 700;
    color: white;
}

.online-indicator {
    position: absolute;
    top: -2px;
    right: -2px;
    width: 12px;
    height: 12px;
    background: #10b981;
    border: 2px solid #059669;
    border-radius: 50%;
    z-index: 2;
}

.profile-info {
    flex: 1;
    min-width: 0;
    z-index: 1;
}

.profile-name {
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 4px;
    letter-spacing: -0.025em;
    line-height: 1.3;
}

.profile-role {
    font-size: 13px;
    opacity: 0.95;
    margin-bottom: 4px;
    font-weight: 500;
    line-height: 1.3;
}

.profile-email {
    font-size: 12px;
    opacity: 0.85;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-weight: 400;
    line-height: 1.3;
}

.profile-action {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    opacity: 0.9;
    transition: all 0.2s ease;
    padding: 6px;
    border-radius: 8px;
    flex-shrink: 0;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
}

.profile-action:hover {
    opacity: 1;
    background: rgba(255, 255, 255, 0.15);
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
    width: 80px;
    overflow-x: hidden;
    transition: width 0.3s ease;
}

.sidebar.collapsed .sidebar-header,
.sidebar.collapsed .search-container,
.sidebar.collapsed .section-title,
.sidebar.collapsed .user-profile {
    display: none;
}

.sidebar.collapsed .nav-item span,
.sidebar.collapsed .badge,
.sidebar.collapsed .chevron,
.sidebar.collapsed .nav-label {
    display: none;
}

.sidebar.collapsed .nav-item {
    justify-content: center;
    margin: 6px 16px;
    padding: 16px;
}

.sidebar.collapsed .nav-icon {
    margin: 0;
    width: 24px;
    height: 24px;
}

.sidebar.collapsed .nav-item.active {
    background: #059669;
    color: transparent;
}

.sidebar.collapsed .nav-item.active .nav-icon {
    color: white;
}

.sidebar.collapsed .submenu {
    display: none;
}

/* Améliorations pour mobile */
@media (max-width: 768px) {
    .sidebar {
        position: fixed;
        left: 0;
        top: 0;
        z-index: 1000;
        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.15);
        transform: translateX(-100%);
        transition: transform 0.3s ease;
    }
    
    .sidebar.open {
        transform: translateX(0);
    }
    
    .sidebar.collapsed {
        width: 300px;
    }
}

/* Améliorations d'accessibilité */
@media (prefers-reduced-motion: reduce) {
    .status-dot,
    .slide-fade-enter-active,
    .slide-fade-leave-active,
    .chevron {
        animation: none;
        transition: none;
    }
}

/* High contrast mode */
@media (prefers-contrast: high) {
    .nav-item {
        border-width: 2px;
    }
    
    .nav-item.active {
        border-width: 3px;
    }
    
    .badge {
        border: 1px solid currentColor;
    }
}

/* Dark mode adjustments */
@media (prefers-color-scheme: dark) {
    .sidebar {
        background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
        border-right: 1px solid #374151;
    }
    
    .sidebar-header,
    .search-container,
    .user-profile {
        background: #1f2937;
        border-color: #374151;
    }
    
    .header-info h2 {
        color: #f9fafb;
    }
    
    .nav-item {
        color: #d1d5db;
    }
    
    .nav-item:hover {
        background: #374151;
        color: #f9fafb;
    }
    
    .nav-item.active {
        background: #064e3b;
        color: #bbf7d0;
    }
    
    .section-title {
        color: #9ca3af;
    }
    
    .nav-icon {
        color: #9ca3af;
    }
    
    .nav-item.active .nav-icon {
        color: #10b981;
    }
    
    .search-input {
        background: #374151;
        border-color: #4b5563;
        color: #f9fafb;
    }
    
    .search-input:focus {
        border-color: #059669;
    }
}
</style>