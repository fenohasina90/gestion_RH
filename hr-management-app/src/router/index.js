import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Import des vues
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'

// Profils
import ProfilesView from '../views/profiles/ProfilesView.vue'
import CreateProfileView from '../views/profiles/CreateProfileView.vue'
import EditProfileView from '../views/profiles/EditProfileView.vue'

// Annonces
import AnnouncementsView from '../views/announcements/AnnouncementsView.vue'
import CreateAnnouncementView from '../views/announcements/CreateAnnouncementView.vue'

// Candidatures
import ApplicationsView from '../views/applications/ApplicationsView.vue'
import SubmitCVView from '../views/applications/SubmitCVView.vue'

// Tests QCM
import CreateQCMView from '../views/qcm/CreateQCMView.vue'
import TakeQCMView from '../views/qcm/TakeQCMView.vue'
import QCMResultsView from '../views/qcm/QCMResultsView.vue'

// Entretiens
import InterviewCalendarView from '../views/interviews/InterviewCalendarView.vue'
import InterviewResultsView from '../views/interviews/InterviewResultsView.vue'
import GoodInterviewsView from '../views/interviews/GoodInterviewsView.vue'

// Contrats
import ContractsView from '../views/contracts/ContractsView.vue'
import CreateContractView from '../views/contracts/CreateContractView.vue'

// Pages publiques
import HomeView from '../views/public/HomeView.vue'
import JobOffersView from '../views/public/JobOffersView.vue'
import ApplyJobView from '../views/public/ApplyJobView.vue'
import CandidateQcmView from '../views/public/CandidateQcmView.vue'
import TakeQcmView from '../views/public/TakeQcmView.vue'
import QcmResultsView from '../views/public/QcmResultsView.vue'
import PublicClientLayout from '../layouts/PublicClientLayout.vue'

// Test de connexion
import ConnectionTest from '../views/ConnectionTest.vue'

// Profils et Critères
import ProfilList from '../views/ProfilList.vue'
import ProfilAdd from '../views/ProfilAdd.vue'
import CritereList from '../views/CritereList.vue'
import CritereAdd from '../views/CritereAdd.vue'
// Pointage / Feuilles de temps
import TodayAttendanceView from '../views/attendance/TodayAttendanceView.vue'
import ManualEntryView from '../views/attendance/ManualEntryView.vue'
// Day detail view will be lazy-loaded
// Tableau de bord compétences
import CompetenceDashboardView from '../views/CompetenceDashboardView.vue'

const routes = [
  // Groupe des pages publiques client avec sidebar dédié
  {
    path: '/',
    component: PublicClientLayout,
    meta: { requiresAuth: false, clientLayout: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: HomeView,
        meta: { requiresAuth: false, clientLayout: true }
      },
      {
        path: 'jobs',
        name: 'JobOffers',
        component: JobOffersView,
        meta: { requiresAuth: false, clientLayout: true }
      },
      {
        path: 'jobs/:id/apply',
        name: 'ApplyJob',
        component: ApplyJobView,
        meta: { requiresAuth: false, clientLayout: true }
      },
      {
        path: 'mes-qcm',
        name: 'CandidateQcm',
        component: CandidateQcmView,
        meta: { requiresAuth: false, clientLayout: true }
      },
      {
        path: 'qcm/:id',
        name: 'TakeQcm',
        component: TakeQcmView,
        meta: { requiresAuth: false, clientLayout: true }
      },
      {
        path: 'qcm/:id/results',
        name: 'QcmResults',
        component: QcmResultsView,
        meta: { requiresAuth: false, clientLayout: true }
      }
    ]
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: AnnouncementsView
  },
  {
    path: '/dashboard/rh',
    name: 'RhDashboard',
    component: () => import('../views/RhDashboardView.vue')
  },
  {
    path: '/dashboard/performance',
    name: 'PerformanceDashboard',
    component: () => import('../views/PerformanceDashboardView.vue')
  },
  {
    path: '/dashboard/competences',
    name: 'CompetenceDashboard',
    component: CompetenceDashboardView
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  },
  // Routes Profils
  {
    path: '/profiles',
    name: 'Profiles',
    component: ProfilesView
  },
  {
    path: '/profiles/create',
    name: 'CreateProfile',
    component: CreateProfileView
  },
  {
    path: '/profiles/edit/:id',
    name: 'EditProfile',
    component: EditProfileView
  },
  // Routes Annonces
  {
    path: '/announcements',
    name: 'Announcements',
    component: AnnouncementsView
  },
  {
    path: '/announcements/create',
    name: 'CreateAnnouncement',
    component: CreateAnnouncementView
  },
  // Routes Candidatures
  {
    path: '/applications',
    name: 'Applications',
    component: ApplicationsView
  },
  {
    path: '/cv/submit',
    name: 'SubmitCV',
    component: SubmitCVView
  },
  // Routes QCM
  {
    path: '/qcm/create',
    name: 'CreateQCM',
    component: CreateQCMView
  },
  {
    path: '/qcm/take',
    name: 'TakeQCM',
    component: TakeQCMView
  },
  {
    path: '/qcm/results',
    name: 'QCMResults',
    component: QCMResultsView
  },
  // Routes Entretiens
  {
    path: '/interviews/calendar',
    name: 'InterviewCalendar',
    component: InterviewCalendarView
  },
  {
    path: '/interviews/results',
    name: 'InterviewResults',
    component: InterviewResultsView
  },
  {
    path: '/interviews/good-level',
    name: 'GoodInterviews',
    component: GoodInterviewsView
  },
  {
    path: '/contracts/view/:id',
    name: 'ContractView',
    component: () => import('../views/contracts/ContractView.vue')
  },
  // Routes Employés
  {
    path: '/employees',
    name: 'Employees',
    component: () => import('../views/employees/EmployeesView.vue')
  },
  {
    path: '/employees/add',
    name: 'AddEmployee',
    component: () => import('../views/employees/AddEmployeeView.vue')
  },
  {
    path: '/employees/contract/:id',
    name: 'EmployeeContractDetail',
    component: () => import('../views/employees/EmployeesDetailView.vue')
  },
  {
    path: '/employees/:empId/documents',
    name: 'EmployeeDocuments',
    component: () => import('../views/employees/EmployeeDocumentsView.vue')
  },
  {
    path: '/employees/:empId/documents/:docId',
    name: 'EmployeeDocumentDetail',
    component: () => import('../views/employees/EmployeeDocumentDetailView.vue')
  },
  // Routes Contrats
  {
    path: '/contracts',
    name: 'Contracts',
    component: ContractsView
  },
  {
    path: '/contracts/create',
    name: 'CreateContract',
    component: CreateContractView
  },
  {
    path: '/leaves/balances',
    name: 'LeaveBalances',
    component: () => import('../views/leaves/LeaveBalancesView.vue')
  },
  {
    path: '/leaves/periods',
    name: 'LeavePeriods',
    component: () => import('../views/leaves/LeavePeriodsView.vue')
  },
  {
    path: '/leaves/pending',
    name: 'LeavePending',
    component: () => import('../views/leaves/LeavePendingView.vue')
  },
  {
    path: '/leaves/calendar',
    name: 'LeavesCalendar',
    component: () => import('../views/leaves/LeavesCalendarView.vue')
  },
  // Note: routes publiques ci-dessus sont désormais enfants de PublicClientLayout
  // Route admin pour voir tous les résultats QCM
  {
    path: '/resultat-qcm',
    name: 'AdminQcmResults',
    component: () => import('../views/qcm/QCMResultsView.vue'),
    meta: { requiresAuth: true }
  },
  // Route Test de connexion
  {
    path: '/connection-test',
    name: 'ConnectionTest',
    component: ConnectionTest
  },
  // Routes Profils
  {
    path: '/profils',
    name: 'ProfilList',
    component: ProfilList
  },
  {
    path: '/profils/add',
    name: 'ProfilAdd',
    component: ProfilAdd
  },
  // Routes Critères
  {
    path: '/criteres',
    name: 'CritereList',
    component: CritereList
  },
  {
    path: '/criteres/add',
    name: 'CritereAdd',
    component: CritereAdd
  }
  ,
  // Routes Pointage (Phase 1)
  {
    path: '/pointage/aujourd-hui',
    name: 'TodayAttendance',
    component: TodayAttendanceView
  },
  {
    path: '/pointage/saisir',
    name: 'ManualEntry',
    component: ManualEntryView
  },
  {
    path: '/pointage/detail/:matricule/:date',
    name: 'DayDetail',
    component: () => import('../views/attendance/DayDetailView.vue')
  }
  ,
  {
    path: '/pointage/releve',
    name: 'AttendanceReport',
    component: () => import('../views/attendance/AttendanceReportView.vue')
  }
  ,
  {
    path: '/paie/bulletin',
    name: 'Payslip',
    component: () => import('../views/payroll/PayslipView.vue')
  }
  ,
  {
    path: '/paie/etat',
    name: 'EtatPaie',
    component: () => import('../views/payroll/EtatPaieView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Guard de navigation pour vérifier l'authentification
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Routes publiques (pas besoin d'authentification)
  const publicRoutes = ['/', '/login', '/register', '/jobs', '/jobs/:id/apply']
  
  // Si l'utilisateur va vers une route publique, laisser passer
  if (publicRoutes.includes(to.path) || to.path.startsWith('/jobs/') || to.meta?.requiresAuth === false) {
    next()
    return
  }
  
  // Initialiser l'auth si pas encore fait
  if (!authStore.isAuthenticated && authStore.token) {
    authStore.initAuth()
  }
  
  // Vérifier l'authentification pour les autres routes
  if (!authStore.isLoggedIn) {
    // Rediriger vers login si pas connecté
    next('/login')
  } else {
    next()
  }
})

export default router
