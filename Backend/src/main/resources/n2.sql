-- ===========================================================
-- EXTENSION GESTION RH COMPLÈTE
-- Sans UPDATE, avec historisation complète
-- ===========================================================

-- ===============================================
-- 1. CATÉGORIES ET CLASSIFICATION DU PERSONNEL
-- ===============================================

-- Table des catégories professionnelles (Ouvrier, Employé, TAM, Cadre, Dirigeant)
CREATE TABLE categoriepersonnel (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Table des types de contrat
CREATE TABLE typecontrat (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

-- Table des statuts de contrat
CREATE TABLE statutcontrat (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE
);

-- Modifier la table contrat pour ajouter les nouvelles colonnes
ALTER TABLE contrat
    ADD COLUMN datefin DATE,
    ADD COLUMN periodessai INT, -- en jours
    ADD COLUMN idstatut INT REFERENCES statutcontrat(id),
    ADD COLUMN idtypecontrat INT REFERENCES typecontrat(id);

-- Supprimer l'ancienne colonne typecontrat (si elle existe)
-- ALTER TABLE contrat DROP COLUMN IF EXISTS typecontrat;

-- Historique des contrats (pour traçabilité sans UPDATE)
CREATE TABLE historiquecontrat (
    id SERIAL PRIMARY KEY,
    idcontrat INT REFERENCES contrat(id),
    idstatut INT REFERENCES statutcontrat(id),
    datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT
);

-- Ajouter la catégorie à l'employé
ALTER TABLE employe
    ADD COLUMN idcategorie INT REFERENCES categoriepersonnel(id);

-- Historique des catégories et postes
CREATE TABLE historiqueposte (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    posteoccupe VARCHAR(150),
    idcategorie INT REFERENCES categoriepersonnel(id),
    iddepartement INT REFERENCES departement(id),
    datedebut DATE NOT NULL,
    datefin DATE,
    motif VARCHAR(200) -- promotion, mobilité, etc.
);

-- ===============================================
-- 2. GESTION DES DOCUMENTS RH
-- ===============================================

-- Types de documents
CREATE TABLE typedocument (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    estobligatoire BOOLEAN DEFAULT FALSE
);

-- Documents des employés
CREATE TABLE documentemploye (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    idtypedocument INT REFERENCES typedocument(id),
    nomfichier VARCHAR(255) NOT NULL,
    chemin TEXT, -- chemin du fichier
    dateupload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    datexpiration DATE,
    commentaire TEXT
);

-- ===============================================
-- 3. GESTION DES CONGÉS
-- ===============================================

-- Types de congés
CREATE TABLE typeconge (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    estremunere BOOLEAN DEFAULT TRUE,
    description TEXT
);

-- Paramétrage des droits de congés par catégorie
CREATE TABLE droitconge (
    id SERIAL PRIMARY KEY,
    idcategorie INT REFERENCES categoriepersonnel(id),
    idtypeconge INT REFERENCES typeconge(id),
    joursparannee NUMERIC(5,2), -- peut être décimal (ex: 2.5 jours/mois = 30/an)
    joursparanciennete NUMERIC(5,2), -- jours supplémentaires selon ancienneté
    anneesanciennete INT, -- seuil d'ancienneté
    dureevalidite INT DEFAULT 3, -- années de cumul possible
    CONSTRAINT uk_categorie_typeconge UNIQUE (idcategorie, idtypeconge)
);

-- Solde des congés par employé (snapshot à une date donnée)
CREATE TABLE soldeconge (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    idtypeconge INT REFERENCES typeconge(id),
    annee INT NOT NULL,
    joursacquis NUMERIC(6,2) DEFAULT 0,
    jourspris NUMERIC(6,2) DEFAULT 0,
    joursrestants NUMERIC(6,2) DEFAULT 0,
    datecalcul TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_employe_typeconge_annee UNIQUE (idemploye, idtypeconge, annee)
);

-- Statuts des demandes de congé
CREATE TABLE statutdemande (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE
);

-- Demandes de congés
CREATE TABLE demandeconge (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    idtypeconge INT REFERENCES typeconge(id),
    datedebut DATE NOT NULL,
    datefin DATE NOT NULL,
    nombrejoursouvres NUMERIC(5,2), -- calcul automatique
    motif TEXT,
    datedemande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    idvalideur INT REFERENCES employe(id), -- qui valide
    idstatut INT REFERENCES statutdemande(id)
);

-- Historique des demandes de congés (workflow)
CREATE TABLE historiquedemande (
    id SERIAL PRIMARY KEY,
    iddemandeconge INT REFERENCES demandeconge(id),
    idstatut INT REFERENCES statutdemande(id),
    idemploye INT REFERENCES employe(id), -- qui a fait l'action
    datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT
);

-- Calendrier des congés pris (pour éviter conflits)
CREATE TABLE congeeffectue (
    id SERIAL PRIMARY KEY,
    iddemandeconge INT REFERENCES demandeconge(id),
    idemploye INT REFERENCES employe(id),
    idtypeconge INT REFERENCES typeconge(id),
    datedebut DATE NOT NULL,
    datefin DATE NOT NULL,
    nombrejourspris NUMERIC(5,2)
);

-- ===============================================
-- 4. GESTION DU TEMPS ET PRÉSENCES
-- ===============================================

-- Types de pointage
CREATE TABLE typepointage (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

-- Enregistrement des pointages
CREATE TABLE pointage (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    dateheure TIMESTAMP NOT NULL,
    idtypepointage INT REFERENCES typepointage(id), -- entrée/sortie/pause
    latitude NUMERIC(10,8),
    longitude NUMERIC(11,8),
    commentaire TEXT
);

-- Types d'absence
CREATE TABLE typeabsence (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    impactesalaire BOOLEAN DEFAULT TRUE
);

-- Absences non planifiées
CREATE TABLE absence (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    idtypeabsence INT REFERENCES typeabsence(id),
    datedebut DATE NOT NULL,
    datefin DATE NOT NULL,
    nombreheures NUMERIC(6,2),
    justificatif TEXT, -- chemin du fichier
    dateenregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    idvalideur INT REFERENCES employe(id)
);

-- Feuilles de temps mensuelles
CREATE TABLE feuilletemps (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    mois INT NOT NULL,
    annee INT NOT NULL,
    jourstravailles NUMERIC(5,2),
    heuressupplementaires NUMERIC(6,2),
    absences NUMERIC(5,2),
    retards INT, -- en minutes
    datecloture TIMESTAMP,
    idvalideur INT REFERENCES employe(id),
    CONSTRAINT uk_employe_mois_annee UNIQUE (idemploye, mois, annee)
);

-- Détail journalier des feuilles de temps
CREATE TABLE detailfeuilletemps (
    id SERIAL PRIMARY KEY,
    idfeuilletemps INT REFERENCES feuilletemps(id),
    datejour DATE NOT NULL,
    heuresentree TIME,
    heuressortie TIME,
    heurestravaillees NUMERIC(5,2),
    heuressup NUMERIC(5,2),
    estabsent BOOLEAN DEFAULT FALSE,
    commentaire TEXT
);

-- ===============================================
-- 5. GESTION DE LA PAIE
-- ===============================================

-- Types d'éléments de paie
CREATE TABLE typeelementpaie (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    estgain BOOLEAN DEFAULT TRUE, -- TRUE = gain, FALSE = retenue
    estsoumiscotisation BOOLEAN DEFAULT TRUE
);

-- Paramétrage des cotisations
CREATE TABLE parametrecotisation (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    taux NUMERIC(5,4), -- ex: 0.13 pour 13%
    plafondsalarial NUMERIC(12,2),
    dateeffet DATE NOT NULL,
    datefin DATE
);

-- Grille salariale par catégorie
CREATE TABLE grillesalariale (
    id SERIAL PRIMARY KEY,
    idcategorie INT REFERENCES categoriepersonnel(id),
    niveauechelon VARCHAR(50),
    salairebrut NUMERIC(12,2) NOT NULL,
    dateeffet DATE NOT NULL,
    datefin DATE
);

-- Salaire de base de l'employé (historisé)
CREATE TABLE salairebase (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    salairebrut NUMERIC(12,2) NOT NULL,
    dateeffet DATE NOT NULL,
    datefin DATE,
    motif VARCHAR(200) -- augmentation, promotion, etc.
);

-- Éléments variables de paie (primes, avances, etc.)
CREATE TABLE elementvariable (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    idtypeelementpaie INT REFERENCES typeelementpaie(id),
    montant NUMERIC(12,2) NOT NULL,
    mois INT NOT NULL,
    annee INT NOT NULL,
    commentaire TEXT,
    dateenregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Statuts de bulletin de paie
CREATE TABLE statutbulletin (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE
);

-- Bulletins de paie
CREATE TABLE bulletinpaie (
    id SERIAL PRIMARY KEY,
    idemploye INT REFERENCES employe(id),
    mois INT NOT NULL,
    annee INT NOT NULL,
    salairebrut NUMERIC(12,2) NOT NULL,
    totalcotisations NUMERIC(12,2) DEFAULT 0,
    totalgains NUMERIC(12,2) DEFAULT 0,
    totalretenues NUMERIC(12,2) DEFAULT 0,
    salaireimposable NUMERIC(12,2) NOT NULL,
    impot NUMERIC(12,2) DEFAULT 0, -- IRSA
    netapayer NUMERIC(12,2) NOT NULL,
    dategeneration TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    idstatut INT REFERENCES statutbulletin(id),
    cheminpdf TEXT,
    CONSTRAINT uk_employe_mois_annee_paie UNIQUE (idemploye, mois, annee)
);

-- Détail du bulletin (lignes)
CREATE TABLE detailbulletin (
    id SERIAL PRIMARY KEY,
    idbulletin INT REFERENCES bulletinpaie(id),
    idtypeelementpaie INT REFERENCES typeelementpaie(id),
    libelle VARCHAR(200) NOT NULL,
    base NUMERIC(12,2),
    taux NUMERIC(5,4),
    montant NUMERIC(12,2) NOT NULL,
    estgain BOOLEAN DEFAULT TRUE
);

-- Historique des bulletins
CREATE TABLE historiquebulletin (
    id SERIAL PRIMARY KEY,
    idbulletin INT REFERENCES bulletinpaie(id),
    idstatut INT REFERENCES statutbulletin(id),
    datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    idemploye INT REFERENCES employe(id), -- qui a fait l'action
    commentaire TEXT
);

-- ===============================================
-- 6. ALERTES ET NOTIFICATIONS
-- ===============================================

-- Types d'alertes
CREATE TABLE typealerte (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Alertes système
CREATE TABLE alerte (
    id SERIAL PRIMARY KEY,
    idtypealerte INT REFERENCES typealerte(id),
    idemploye INT REFERENCES employe(id), -- concerné
    titre VARCHAR(200) NOT NULL,
    message TEXT,
    estlu BOOLEAN DEFAULT FALSE,
    dategeneration TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateecheance DATE
);

-- ===============================================
-- 7. COMPLÉMENTS POUR PHOTO ET INFORMATIONS
-- ===============================================

-- Ajouter des colonnes manquantes à la table employe
ALTER TABLE employe
    ADD COLUMN datenaissance DATE,
    ADD COLUMN telephone VARCHAR(20),
    ADD COLUMN email VARCHAR(150),
    ADD COLUMN photo TEXT, -- chemin de la photo
    ADD COLUMN cin VARCHAR(50),
    ADD COLUMN datedembauche DATE,
    ADD COLUMN lieunaissance VARCHAR(150),
    ADD COLUMN nationalite VARCHAR(50),
    ADD COLUMN situationfamiliale VARCHAR(50),
    ADD COLUMN nombreenfants INT DEFAULT 0;

-- ===============================================
-- INDEXES POUR PERFORMANCES
-- ===============================================

CREATE INDEX idx_demandeconge_employe ON demandeconge(idemploye);
CREATE INDEX idx_demandeconge_statut ON demandeconge(idstatut);
CREATE INDEX idx_bulletinpaie_employe ON bulletinpaie(idemploye);
CREATE INDEX idx_bulletinpaie_periode ON bulletinpaie(annee, mois);
CREATE INDEX idx_pointage_employe ON pointage(idemploye);
CREATE INDEX idx_pointage_date ON pointage(dateheure);
CREATE INDEX idx_historiqueposte_employe ON historiqueposte(idemploye);
CREATE INDEX idx_soldeconge_employe ON soldeconge(idemploye);

-- ===============================================
-- DONNÉES DE RÉFÉRENCE (EXEMPLES)
-- ===============================================

-- Catégories du personnel
INSERT INTO categoriepersonnel (nom, description) VALUES
('Ouvrier', 'Exécute des tâches manuelles ou techniques spécifiques'),
('Employé', 'Tâches administratives, commerciales ou de support'),
('Technicien', 'Expertise technique, supervision d''équipe'),
('Agent de Maîtrise', 'Encadrement intermédiaire'),
('Cadre', 'Fonctions de gestion et responsabilité stratégique'),
('Dirigeant', 'Niveau le plus élevé de responsabilité');

-- Types de contrat
INSERT INTO typecontrat (libelle) VALUES
('CDI'), ('CDD'), ('Stage'), ('Apprentissage'), ('Intérim'), ('Consultation');

-- Statuts de contrat
INSERT INTO statutcontrat (nom) VALUES
('En cours'), ('Période essai'), ('Terminé'), ('Résilié'), ('Suspendu');

-- Types de documents
INSERT INTO typedocument (libelle, estobligatoire) VALUES
('CIN', TRUE),
('Diplôme', TRUE),
('CV', TRUE),
('Certificat de travail', FALSE),
('Attestation de résidence', FALSE),
('Casier judiciaire', TRUE),
('Photo d''identité', TRUE),
('Acte de naissance', TRUE),
('Certificat médical', FALSE);

-- Types de congés
INSERT INTO typeconge (libelle, estremunere, description) VALUES
('Congé payé', TRUE, 'Congé annuel rémunéré - 2.5 jours/mois'),
('Congé sans solde', FALSE, 'Absence autorisée sans rémunération'),
('Congé maladie', TRUE, 'Absence pour raison médicale avec certificat'),
('Congé maternité', TRUE, 'Congé pour naissance d''enfant'),
('Congé paternité', TRUE, 'Congé pour naissance d''enfant (père)'),
('Congé mariage', TRUE, 'Congé exceptionnel pour mariage'),
('Congé décès', TRUE, 'Congé exceptionnel pour décès d''un proche'),
('Congé formation', TRUE, 'Formation professionnelle'),
('Congé sabbatique', FALSE, 'Longue période pour projet personnel');

-- Statuts des demandes
INSERT INTO statutdemande (nom) VALUES
('En attente'), ('Validée'), ('Refusée'), ('Annulée');

-- Types de pointage
INSERT INTO typepointage (libelle) VALUES
('Entrée'), ('Sortie'), ('Début pause'), ('Fin pause');

-- Types d'absence
INSERT INTO typeabsence (libelle, impactesalaire) VALUES
('Absence justifiée', FALSE),
('Absence non justifiée', TRUE),
('Retard', TRUE),
('Maladie sans certificat', TRUE);

-- Types d'éléments de paie
INSERT INTO typeelementpaie (libelle, estgain, estsoumiscotisation) VALUES
('Salaire de base', TRUE, TRUE),
('Prime de rendement', TRUE, TRUE),
('Prime d''ancienneté', TRUE, TRUE),
('Heures supplémentaires', TRUE, TRUE),
('Indemnité transport', TRUE, FALSE),
('Indemnité logement', TRUE, FALSE),
('Avance sur salaire', FALSE, FALSE),
('CNAPS', FALSE, TRUE),
('OSTIE', FALSE, TRUE),
('IRSA', FALSE, FALSE),
('Retenue absence', FALSE, FALSE);

-- Statuts de bulletin
INSERT INTO statutbulletin (nom) VALUES
('Brouillon'), ('Validé'), ('Payé'), ('Annulé');

-- Types d'alertes
INSERT INTO typealerte (libelle, description) VALUES
('Congé en attente', 'Demande de congé nécessitant validation'),
('Document expiré', 'Document RH arrivant à expiration'),
('Fin de contrat', 'Contrat arrivant à échéance'),
('Absence répétée', 'Employé avec absences fréquentes'),
('Bulletin non validé', 'Bulletin de paie en attente de validation');

-- ===============================================================
-- FIN DU SCRIPT
-- ===============================================================