# Application de Gestion RH

Une application Vue.js complète pour la gestion des ressources humaines avec sidebar navigable et toutes les fonctionnalités demandées.

## 🚀 Fonctionnalités

### 🔐 Authentification
- **Page de connexion** : Connexion vers l'accueil avec message de succès
- **Page d'inscription** : Inscription avec gestion des erreurs
- **Navbar** : Composant de navigation principal

### 👥 Gestion des Profils
- **Liste des profils** : Affichage de tous les profils avec filtres et recherche
- **Création de profil** : Formulaire complet pour ajouter un nouveau profil
- **Édition de profil** : Modification des informations existantes
- **CRUD complet** : Création, lecture, mise à jour et suppression

### 📢 Gestion des Annonces
- **Liste des annonces** : Affichage des offres d'emploi disponibles
- **Création d'annonce (Admin)** : Interface pour publier de nouvelles offres
- **Publication de profils** : Bouton pour publier un profil directement

### 📄 Candidatures et CV
- **Gestion des candidatures** : Interface admin pour gérer les candidatures
- **Soumission de CV (Client)** : Formulaire côté client pour postuler
- **Suivi des candidatures** : Statuts et progression des candidatures

### ❓ Tests QCM
- **Création de tests (Admin)** : Interface pour créer des questionnaires
- **Passage de tests (Client)** : Interface utilisateur pour répondre aux QCM
- **Résultats QCM** : Affichage et analyse des résultats

### 📅 Entretiens
- **Calendrier d'entretiens** : Planification et gestion des rendez-vous
- **Résultats d'entretiens** : Évaluation et suivi des candidats

### 📋 Contrats d'Essai
- **Liste des contrats** : Gestion de tous les contrats d'essai
- **Création de contrats** : Génération automatique de contrats
- **Export PDF** : Téléchargement des contrats au format PDF

## 🛠️ Technologies Utilisées

- **Vue.js 3** : Framework JavaScript progressif
- **Vue Router** : Routage côté client
- **Vite** : Outil de build rapide
- **CSS3** : Styles modernes avec animations
- **JavaScript ES6+** : Syntaxe moderne

## 📁 Structure du Projet

```
hr-management-app/
├── src/
│   ├── components/          # Composants réutilisables
│   │   ├── Navbar.vue      # Barre de navigation
│   │   └── Sidebar.vue     # Menu latéral
│   ├── views/              # Pages de l'application
│   │   ├── HomeView.vue    # Page d'accueil
│   │   ├── LoginView.vue   # Page de connexion
│   │   ├── RegisterView.vue # Page d'inscription
│   │   ├── profiles/       # Gestion des profils
│   │   ├── announcements/  # Gestion des annonces
│   │   ├── applications/   # Gestion des candidatures
│   │   ├── qcm/           # Tests QCM
│   │   ├── interviews/    # Entretiens
│   │   └── contracts/     # Contrats d'essai
│   ├── router/            # Configuration du routage
│   ├── assets/           # Ressources statiques
│   ├── App.vue          # Composant racine
│   └── main.js         # Point d'entrée
├── public/             # Fichiers publics
├── package.json       # Dépendances et scripts
└── vite.config.js    # Configuration Vite
```

## 🎨 Caractéristiques de l'Interface

### Sidebar Navigable
- **Ouverture/Fermeture** : Sidebar rétractable pour optimiser l'espace
- **Navigation structurée** : Organisation par sections logiques
- **Indicateurs visuels** : États actifs et hover

### Design Responsive
- **Adaptation mobile** : Interface optimisée pour tous les écrans
- **Grilles flexibles** : Layouts adaptatifs
- **Navigation mobile** : Menu hamburger sur petits écrans

### Expérience Utilisateur
- **Animations fluides** : Transitions CSS pour une navigation agréable
- **Feedback visuel** : Messages de succès/erreur
- **États de chargement** : Indicateurs pendant les opérations

## 🚦 Installation et Démarrage

### Prérequis
- Node.js (version 16 ou supérieure)
- npm ou yarn

### Installation
```bash
# Cloner le projet
git clone [url-du-repo]
cd hr-management-app

# Installer les dépendances
npm install

# Démarrer le serveur de développement
npm run dev
```

### Scripts Disponibles
```bash
# Développement
npm run dev

# Build de production
npm run build

# Prévisualisation du build
npm run preview
```

## 📱 Pages et Fonctionnalités Détaillées

### 🔐 Authentification
- **Login** : Formulaire de connexion avec validation
- **Register** : Inscription avec gestion d'erreurs
- **Protection des routes** : Accès sécurisé aux sections admin

### 👥 Profils
- **Liste** : Tableau avec filtres, recherche et pagination
- **Création** : Formulaire complet (nom, expérience, diplômes, compétences)
- **Édition** : Modification en ligne des informations
- **Suppression** : Confirmation avant suppression

### 📢 Annonces
- **Affichage** : Cards avec détails des postes
- **Création** : Formulaire admin pour nouvelles offres
- **Publication** : Lien direct depuis les profils

### 📄 Candidatures
- **Gestion Admin** : Vue d'ensemble des candidatures
- **Soumission Client** : Formulaire de candidature avec upload CV
- **Statuts** : Suivi de l'avancement des candidatures

### ❓ QCM
- **Création** : Interface admin pour créer des questionnaires
- **Passage** : Interface client avec timer et validation
- **Résultats** : Analyse détaillée des performances

### 📅 Entretiens
- **Calendrier** : Vue mensuelle/hebdomadaire/journalière
- **Planification** : Création de créneaux d'entretien
- **Résultats** : Évaluation et recommandations

### 📋 Contrats
- **Liste** : Gestion de tous les contrats d'essai
- **Création** : Génération automatique avec aperçu
- **Export** : Téléchargement PDF des contrats

## 🎯 Rôles et Permissions

### Admin
- Accès complet à toutes les fonctionnalités
- Gestion des profils, annonces, QCM
- Planification des entretiens
- Génération des contrats

### Client
- Soumission de candidatures
- Passage des tests QCM
- Consultation des annonces
- Pas d'accès aux fonctions admin

## 🔧 Configuration

### Variables d'Environnement
```env
VITE_API_URL=http://localhost:3001
VITE_APP_TITLE=Gestion RH
```

### Personnalisation
- **Thème** : Couleurs et styles dans `src/assets/style.css`
- **Logo** : Remplacer dans `public/`
- **Configuration** : Paramètres dans `vite.config.js`

## 📊 Fonctionnalités Avancées

### Export PDF
- Contrats d'essai
- Résultats de tests
- Rapports de candidatures

### Recherche et Filtres
- Recherche textuelle globale
- Filtres par catégorie, statut, date
- Tri par colonnes

### Notifications
- Messages de succès/erreur
- Alertes de confirmation
- Indicateurs de progression

## 🚀 Déploiement

### Build de Production
```bash
npm run build
```

### Serveur Statique
```bash
npm run preview
```

### Hébergement
- Compatible avec Netlify, Vercel, GitHub Pages
- Serveur web standard (Apache, Nginx)

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Support

Pour toute question ou problème :
- Créer une issue sur GitHub
- Contacter l'équipe de développement

---

**Développé avec ❤️ pour la gestion RH moderne**
