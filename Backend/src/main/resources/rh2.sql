-- ===========================================================
-- SYSTÈME DE GESTION RH COMPLET
-- Base de données PostgreSQL
-- Recrutement + Gestion Personnel + Congés + Présences + Paie
-- ===========================================================

-- ===========================================================
-- PARTIE 1 : STRUCTURE DE BASE (RECRUTEMENT)
-- ===========================================================

CREATE TABLE departement (
                             id SERIAL PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL
);

-- Jours fériés (pour exclusion dans les calculs de calendrier)
CREATE TABLE IF NOT EXISTS jourferie (
    id SERIAL PRIMARY KEY,
    datejour DATE NOT NULL UNIQUE,
    libelle VARCHAR(150)
);

CREATE TABLE profil (
                        id SERIAL PRIMARY KEY,
                        nom VARCHAR(100) NOT NULL
);

CREATE TABLE typeannonce (
                             id SERIAL PRIMARY KEY,
                             libelle VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE annonce (
                         id SERIAL PRIMARY KEY,
                         description TEXT,
                         datedebut DATE,
                         datefin DATE,
                         nomposte VARCHAR(100) NOT NULL,
                         iddepartement INT REFERENCES departement(id),
                         idprofil INT REFERENCES profil(id),
                         idtypeannonce INT REFERENCES typeannonce(id),
                         datepublication DATE
);

CREATE TABLE statutcandidat (
                                id SERIAL PRIMARY KEY,
                                nom VARCHAR(50) NOT NULL
);

CREATE TABLE comptecandidat (
                                id SERIAL PRIMARY KEY,
                                email VARCHAR(100) NOT NULL UNIQUE,
                                motdepasse VARCHAR(255) NOT NULL
);

CREATE TABLE province (
                          id SERIAL PRIMARY KEY,
                          nom VARCHAR(100)
);

CREATE TABLE candidat (
                          id SERIAL PRIMARY KEY,
                          nom VARCHAR(100) NOT NULL,
                          prenom VARCHAR(100) NOT NULL,
                          datenaissance DATE,
                          adresse VARCHAR(200),
                          cv TEXT,
                          idannonce INT REFERENCES annonce(id),
                          idstatut INT REFERENCES statutcandidat(id),
                          idcomptecandidat INT REFERENCES comptecandidat(id),
                          idprovince INT REFERENCES province(id)
);

CREATE TABLE categoriepersonnel (
                                    id SERIAL PRIMARY KEY,
                                    nom VARCHAR(100) NOT NULL UNIQUE,
                                    description TEXT
);

CREATE TABLE employe (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL,
                         prenom VARCHAR(100) NOT NULL,
                         adresse VARCHAR(200),
                         iddept INT REFERENCES departement(id),
                         idcategorie INT REFERENCES categoriepersonnel(id),
                         datenaissance DATE,
                         telephone VARCHAR(20),
                         email VARCHAR(150),
                         photo TEXT,
                         cin VARCHAR(50),
                         datedembauche DATE,
                         lieunaissance VARCHAR(150),
                         nationalite VARCHAR(50),
                         situationfamiliale VARCHAR(50),
                         nombreenfants INT DEFAULT 0,
                         numerocnaps VARCHAR(50) UNIQUE,
                         numeroostie VARCHAR(50) UNIQUE,
                         matricule VARCHAR(50) UNIQUE
);

CREATE TABLE genre (
    id SERIAL PRIMARY KEY,
    code CHAR(1) NOT NULL UNIQUE,     -- 'M', 'F', 'A'
    libelle VARCHAR(20) NOT NULL      -- 'Homme', 'Femme', 'Autre'
);

INSERT INTO genre (code, libelle) VALUES
    ('M', 'Homme'),
    ('F', 'Femme'),
    ('A', 'Autre');

ALTER TABLE employe ADD COLUMN idgenre INT REFERENCES genre(id) DEFAULT 3;

CREATE TABLE candidatemploye (
                                 id SERIAL PRIMARY KEY,
                                 idcandidat INT REFERENCES candidat(id),
                                 idemploye INT REFERENCES employe(id)
);

CREATE TABLE typechamp (
                           id SERIAL PRIMARY KEY,
                           libelle VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE critere (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL,
                         idtypechamp INT REFERENCES typechamp(id)
);

CREATE TABLE diplome (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL
);

CREATE TABLE candidaturecritere (
                                    id SERIAL PRIMARY KEY,
                                    idcandidat INT REFERENCES candidat(id),
                                    idannonce INT REFERENCES annonce(id),
                                    idcritere INT REFERENCES critere(id),
                                    valeurdouble NUMERIC(10,2),
                                    valeurvarchar VARCHAR(200),
                                    valeurbool BOOLEAN,
                                    iddiplome INT REFERENCES diplome(id)
);

CREATE TABLE critereprofil (
                               id SERIAL PRIMARY KEY,
                               idprofil INT REFERENCES profil(id),
                               idcritere INT REFERENCES critere(id),
                               valeurdouble NUMERIC(10,2),
                               valeurvarchar VARCHAR(200),
                               valeurbool BOOLEAN,
                               estobligatoire BOOLEAN DEFAULT TRUE
);

CREATE TABLE typecontrat (
                             id SERIAL PRIMARY KEY,
                             libelle VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE statutcontrat (
                               id SERIAL PRIMARY KEY,
                               nom VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE contrat (
                         id SERIAL PRIMARY KEY,
                         idemploye INT REFERENCES employe(id),
                         datedebut DATE,
                         datefin DATE,
                         nombremois INT,
                         periodessai INT,
                         idstatut INT REFERENCES statutcontrat(id),
                         idtypecontrat INT REFERENCES typecontrat(id)
);

CREATE TABLE historiquecontrat (
                                   id SERIAL PRIMARY KEY,
                                   idcontrat INT REFERENCES contrat(id),
                                   idstatut INT REFERENCES statutcontrat(id),
                                   datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   commentaire TEXT
);

CREATE TABLE historiqueposte (
                                 id SERIAL PRIMARY KEY,
                                 idemploye INT REFERENCES employe(id),
                                 posteoccupe VARCHAR(150),
                                 idcategorie INT REFERENCES categoriepersonnel(id),
                                 iddepartement INT REFERENCES departement(id),
                                 datedebut DATE NOT NULL,
                                 datefin DATE,
                                 motif VARCHAR(200)
);

CREATE TABLE utilisateurs (
                              id SERIAL PRIMARY KEY,
                              email VARCHAR(150) NOT NULL UNIQUE,
                              motdepasse VARCHAR(200) NOT NULL,
                              idemploye INT REFERENCES employe(id)
);

CREATE TABLE resultat (
                          id SERIAL PRIMARY KEY,
                          note INT,
                          appreciation VARCHAR(200)
);

CREATE TABLE statutentretien (
                                 id SERIAL PRIMARY KEY,
                                 nom VARCHAR(50) NOT NULL
);

CREATE TABLE entretien (
                           id SERIAL PRIMARY KEY,
                           idcandidat INT REFERENCES candidat(id),
                           dateheure TIMESTAMP,
                           idstatut INT REFERENCES statutentretien(id),
                           idresultat INT REFERENCES resultat(id),
                           idannonce INT REFERENCES annonce(id)
);

CREATE TABLE historiquecandidature (
                                       id SERIAL PRIMARY KEY,
                                       idcandidat INT REFERENCES candidat(id),
                                       idstatut INT REFERENCES statutcandidat(id),
                                       datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE historiqueentretien (
                                     id SERIAL PRIMARY KEY,
                                     identretien INT REFERENCES entretien(id),
                                     idstatut INT REFERENCES statutentretien(id),
                                     datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE profildiplome (
                               id SERIAL PRIMARY KEY,
                               idprofil INT NOT NULL REFERENCES profil(id),
                               iddiplome INT NOT NULL REFERENCES diplome(id),
                               CONSTRAINT uk_profil_diplome UNIQUE (idprofil, iddiplome)
);

CREATE TABLE qcmtest (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(150),
                         idprofil INT REFERENCES profil(id)
);

CREATE TABLE qcmquestion (
                             id SERIAL PRIMARY KEY,
                             idtest INT NOT NULL REFERENCES qcmtest(id),
                             numero INT NOT NULL,
                             question TEXT NOT NULL,
                             points INT NOT NULL DEFAULT 1
);

CREATE TABLE qcmchoix (
                          id SERIAL PRIMARY KEY,
                          idquestion INT NOT NULL REFERENCES qcmquestion(id),
                          texte VARCHAR(500) NOT NULL,
                          estcorrect BOOLEAN DEFAULT FALSE
);

CREATE TABLE qcmreponse (
                            id SERIAL PRIMARY KEY,
                            idcandidat INT NOT NULL REFERENCES candidat(id),
                            idtest INT NOT NULL REFERENCES qcmtest(id),
                            idquestion INT NOT NULL REFERENCES qcmquestion(id),
                            idchoix INT REFERENCES qcmchoix(id),
                            pointsobtenus INT DEFAULT 0,
                            datereponse DATE
);

CREATE TABLE testannonce (
                             id SERIAL PRIMARY KEY,
                             idtest INT NOT NULL REFERENCES qcmtest(id),
                             idannonce INT NOT NULL REFERENCES annonce(id)
);

-- ===========================================================
-- PARTIE 2 : GESTION DES DOCUMENTS RH
-- ===========================================================

CREATE TABLE typedocument (
                              id SERIAL PRIMARY KEY,
                              libelle VARCHAR(100) NOT NULL UNIQUE,
                              estobligatoire BOOLEAN DEFAULT FALSE
);

CREATE TABLE documentemploye (
                                 id SERIAL PRIMARY KEY,
                                 idemploye INT REFERENCES employe(id),
                                 idtypedocument INT REFERENCES typedocument(id),
                                 nomfichier VARCHAR(255) NOT NULL,
                                 chemin TEXT,
                                 dateupload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 datexpiration DATE,
                                 commentaire TEXT
);

-- ===========================================================
-- PARTIE 3 : GESTION DES CONGÉS
-- ===========================================================

CREATE TABLE typeconge (
                           id SERIAL PRIMARY KEY,
                           libelle VARCHAR(100) NOT NULL UNIQUE,
                           estremunere BOOLEAN DEFAULT TRUE,
                           description TEXT
);

CREATE TABLE droitconge (
                            id SERIAL PRIMARY KEY,
                            idcategorie INT REFERENCES categoriepersonnel(id),
                            idtypeconge INT REFERENCES typeconge(id),
                            joursparannee NUMERIC(5,2),
                            joursparanciennete NUMERIC(5,2),
                            anneesanciennete INT,
                            dureevalidite INT DEFAULT 3,
                            CONSTRAINT uk_categorie_typeconge UNIQUE (idcategorie, idtypeconge)
);

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

CREATE TABLE statutdemande (
                               id SERIAL PRIMARY KEY,
                               nom VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE demandeconge (
                              id SERIAL PRIMARY KEY,
                              idemploye INT REFERENCES employe(id),
                              idtypeconge INT REFERENCES typeconge(id),
                              datedebut DATE NOT NULL,
                              datefin DATE NOT NULL,
                              nombrejoursouvres NUMERIC(5,2),
                              motif TEXT,
                              datedemande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              idvalideur INT REFERENCES employe(id),
                              idstatut INT REFERENCES statutdemande(id)
);

CREATE TABLE historiquedemande (
                                   id SERIAL PRIMARY KEY,
                                   iddemandeconge INT REFERENCES demandeconge(id),
                                   idstatut INT REFERENCES statutdemande(id),
                                   idemploye INT REFERENCES employe(id),
                                   datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   commentaire TEXT
);

CREATE TABLE congeeffectue (
                               id SERIAL PRIMARY KEY,
                               iddemandeconge INT REFERENCES demandeconge(id),
                               idemploye INT REFERENCES employe(id),
                               idtypeconge INT REFERENCES typeconge(id),
                               datedebut DATE NOT NULL,
                               datefin DATE NOT NULL,
                               nombrejourspris NUMERIC(5,2)
);


-- Vue des congés validés (demandes au statut "Validée")
CREATE OR REPLACE VIEW v_conges_valides AS
SELECT
    d.id                 AS iddemande,
    e.id                 AS idemploye,
    e.nom                AS nom,
    e.prenom             AS prenom,
    tc.id                AS idtypeconge,
    tc.libelle           AS typeconge,
    d.datedemande        AS datedemande,
    d.datedebut          AS datedebut,
    d.datefin            AS datefin,
    d.nombrejoursouvres  AS nombrejours,
    sd.nom               AS statut
FROM demandeconge d
JOIN employe e       ON e.id = d.idemploye
JOIN typeconge tc    ON tc.id = d.idtypeconge
JOIN statutdemande sd ON sd.id = d.idstatut
WHERE lower(sd.nom) = 'validé manager';

-- ===========================================================
-- PARTIE 4 : GESTION DU TEMPS ET PRÉSENCES
-- ===========================================================

CREATE TABLE typepointage (
                              id SERIAL PRIMARY KEY,
                              libelle VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE pointage (
                          id SERIAL PRIMARY KEY,
                          idemploye INT REFERENCES employe(id),
                          dateheure TIMESTAMP NOT NULL,
                          idtypepointage INT REFERENCES typepointage(id),
                          latitude NUMERIC(10,8),
                          longitude NUMERIC(11,8),
                          commentaire TEXT,
                          estvalide BOOLEAN DEFAULT TRUE
);

CREATE TABLE typeabsence (
                             id SERIAL PRIMARY KEY,
                             libelle VARCHAR(100) NOT NULL UNIQUE,
                             impactesalaire BOOLEAN DEFAULT TRUE
);

CREATE TABLE absence (
                         id SERIAL PRIMARY KEY,
                         idemploye INT REFERENCES employe(id),
                         idtypeabsence INT REFERENCES typeabsence(id),
                         datedebut DATE NOT NULL,
                         datefin DATE NOT NULL,
                         nombreheures NUMERIC(6,2),
                         justificatif TEXT,
                         dateenregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         idvalideur INT REFERENCES employe(id)
);

CREATE TABLE feuilletemps (
                              id SERIAL PRIMARY KEY,
                              idemploye INT REFERENCES employe(id),
                              mois INT NOT NULL,
                              annee INT NOT NULL,
                              jourstravailles NUMERIC(5,2),
                              heuressupplementaires NUMERIC(6,2),
                              absences NUMERIC(5,2),
                              retards INT,
                              datecloture TIMESTAMP,
                              idvalideur INT REFERENCES employe(id),
                              CONSTRAINT uk_employe_mois_annee UNIQUE (idemploye, mois, annee)
);

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

-- ===========================================================
-- PARTIE 5 : GESTION DE LA PAIE
-- ===========================================================

CREATE TABLE typeelementpaie (
                                 id SERIAL PRIMARY KEY,
                                 libelle VARCHAR(100) NOT NULL UNIQUE,
                                 estgain BOOLEAN DEFAULT TRUE,
                                 estsoumiscotisation BOOLEAN DEFAULT TRUE
);

CREATE TABLE parametrecotisation (
                                     id SERIAL PRIMARY KEY,
                                     libelle VARCHAR(100) NOT NULL,
                                     taux NUMERIC(6,2),
                                     plafondsalarial NUMERIC(12,2),
                                     dateeffet DATE NOT NULL,
                                     datefin DATE
);

CREATE TABLE grillesalariale (
                                 id SERIAL PRIMARY KEY,
                                 idcategorie INT REFERENCES categoriepersonnel(id),
                                 niveauechelon VARCHAR(50),
                                 salairebrut NUMERIC(12,2) NOT NULL,
                                 dateeffet DATE NOT NULL,
                                 datefin DATE
);

CREATE TABLE salairebase (
                             id SERIAL PRIMARY KEY,
                             idemploye INT REFERENCES employe(id),
                             salairebrut NUMERIC(12,2) NOT NULL,
                             dateeffet DATE NOT NULL,
                             datefin DATE,
                             motif VARCHAR(200)
);

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

CREATE TABLE statutbulletin (
                                id SERIAL PRIMARY KEY,
                                nom VARCHAR(50) NOT NULL UNIQUE
);

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
                              impot NUMERIC(12,2) DEFAULT 0,
                              netapayer NUMERIC(12,2) NOT NULL,
                              dategeneration TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              idstatut INT REFERENCES statutbulletin(id),
                              cheminpdf TEXT,
                              CONSTRAINT uk_employe_mois_annee_paie UNIQUE (idemploye, mois, annee)
);

CREATE TABLE detailbulletin (
                                id SERIAL PRIMARY KEY,
                                idbulletin INT REFERENCES bulletinpaie(id),
                                idtypeelementpaie INT REFERENCES typeelementpaie(id),
                                libelle VARCHAR(200) NOT NULL,
                                base NUMERIC(12,2),
                                taux NUMERIC(6,2),
                                montant NUMERIC(12,2) NOT NULL,
                                estgain BOOLEAN DEFAULT TRUE
);

CREATE TABLE historiquebulletin (
                                    id SERIAL PRIMARY KEY,
                                    idbulletin INT REFERENCES bulletinpaie(id),
                                    idstatut INT REFERENCES statutbulletin(id),
                                    datechangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    idemploye INT REFERENCES employe(id),
                                    commentaire TEXT
);

-- ===========================================================
-- PARTIE 6 : ALERTES ET NOTIFICATIONS
-- ===========================================================

CREATE TABLE typealerte (
                            id SERIAL PRIMARY KEY,
                            libelle VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT
);

CREATE TABLE alerte (
                        id SERIAL PRIMARY KEY,
                        idtypealerte INT REFERENCES typealerte(id),
                        idemploye INT REFERENCES employe(id),
                        titre VARCHAR(200) NOT NULL,
                        message TEXT,
                        estlu BOOLEAN DEFAULT FALSE,
                        dategeneration TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        dateecheance DATE
);

-- ===========================================================
-- PARTIE 7 : INDEXES POUR PERFORMANCES
-- ===========================================================

CREATE INDEX idx_demandeconge_employe ON demandeconge(idemploye);
CREATE INDEX idx_demandeconge_statut ON demandeconge(idstatut);
CREATE INDEX idx_bulletinpaie_employe ON bulletinpaie(idemploye);
CREATE INDEX idx_bulletinpaie_periode ON bulletinpaie(annee, mois);
CREATE INDEX idx_pointage_employe ON pointage(idemploye);
CREATE INDEX idx_pointage_date ON pointage(dateheure);
CREATE INDEX idx_historiqueposte_employe ON historiqueposte(idemploye);
CREATE INDEX idx_soldeconge_employe ON soldeconge(idemploye);
CREATE INDEX idx_employe_matricule ON employe(matricule);
CREATE INDEX idx_employe_cnaps ON employe(numerocnaps);
CREATE INDEX idx_employe_ostie ON employe(numeroostie);

-- ===========================================================
-- PARTIE X : GESTION DES COMPÉTENCES
-- ===========================================================

-- Catégories de compétences (techniques, comportementales, management, ...)
CREATE TABLE categoriecompetence (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Référentiel des compétences de l'entreprise
CREATE TABLE competence (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(150) NOT NULL,
    description TEXT,
    idcategorie INT REFERENCES categoriecompetence(id)
);

-- Niveaux de compétence (Débutant, Intermédiaire, Avancé, Expert, ...)
CREATE TABLE niveaucompetence (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,     -- ex: N1, N2
    libelle VARCHAR(50) NOT NULL,         -- ex: Débutant, Avancé
    ordre INT NOT NULL                    -- ordre croissant de maîtrise
);

-- Compétences attendues par profil de poste
CREATE TABLE profilcompetence (
    id SERIAL PRIMARY KEY,
    idprofil INT NOT NULL REFERENCES profil(id),
    idcompetence INT NOT NULL REFERENCES competence(id),
    idniveau_cible INT REFERENCES niveaucompetence(id),
    poids NUMERIC(5,2) DEFAULT 1,         -- importance de la compétence dans le match
    estobligatoire BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_profil_comp UNIQUE (idprofil, idcompetence)
);

-- Compétences détenues / évaluées pour les employés
CREATE TABLE employecompetence (
    id SERIAL PRIMARY KEY,
    idemploye INT NOT NULL REFERENCES employe(id),
    idcompetence INT NOT NULL REFERENCES competence(id),
    idniveau_actuel INT REFERENCES niveaucompetence(id),
    source VARCHAR(50),                   -- 'manager', 'auto', 'test', ...
    dateevaluation DATE NOT NULL DEFAULT CURRENT_DATE,
    commentaire TEXT,
    CONSTRAINT uk_employe_comp UNIQUE (idemploye, idcompetence)
);

-- Formations (catalogue)
CREATE TABLE formation (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(150) NOT NULL,
    description TEXT,
    duree_heures NUMERIC(5,2),
    fournisseur VARCHAR(150),             -- organisme, interne / externe
    cout NUMERIC(12,2)
);

-- Lien formations <-> compétences ciblées
CREATE TABLE formationcompetence (
    id SERIAL PRIMARY KEY,
    idformation INT NOT NULL REFERENCES formation(id),
    idcompetence INT NOT NULL REFERENCES competence(id),
    idniveau_cible INT REFERENCES niveaucompetence(id),
    CONSTRAINT uk_formation_comp UNIQUE (idformation, idcompetence)
);

-- Index pour la gestion des compétences
CREATE INDEX idx_competence_code ON competence(code);
CREATE INDEX idx_employecompetence_employe ON employecompetence(idemploye);
CREATE INDEX idx_employecompetence_competence ON employecompetence(idcompetence);
CREATE INDEX idx_profilcompetence_profil ON profilcompetence(idprofil);
CREATE INDEX idx_profilcompetence_competence ON profilcompetence(idcompetence);
CREATE INDEX idx_formationcompetence_formation ON formationcompetence(idformation);
CREATE INDEX idx_formationcompetence_competence ON formationcompetence(idcompetence);

-- ===========================================================
-- PARTIE 7bis : MESSAGERIE RH SIMPLE
-- ===========================================================

CREATE TABLE IF NOT EXISTS message_rh (
    id SERIAL PRIMARY KEY,
    idemploye INT NOT NULL REFERENCES employe(id),
    sujet VARCHAR(150) NOT NULL,
    contenu TEXT NOT NULL,
    reponse TEXT,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_reponse TIMESTAMP,
    lu BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_message_rh_employe ON message_rh(idemploye);

-- ===========================================================
-- PARTIE 8 : VUE COMPLÈTE POUR BULLETIN DE PAIE
-- ===========================================================

CREATE OR REPLACE VIEW v_bulletin_paie_complet AS
SELECT
    e.id as idemploye,
    e.matricule,
    e.nom,
    e.prenom,
    e.cin,
    e.datenaissance,
    e.lieunaissance,
    e.adresse,
    e.telephone,
    e.email,
    e.numerocnaps,
    e.numeroostie,
    e.situationfamiliale,
    e.nombreenfants,
    e.datedembauche,
    cp.nom as categorie,
    d.nom as departement,
    bp.id as idbulletin,
    bp.mois,
    bp.annee,
    bp.salairebrut,
    bp.totalgains,
    bp.totalcotisations,
    bp.totalretenues,
    bp.salaireimposable,
    bp.impot,
    bp.netapayer,
    bp.dategeneration,
    sb.nom as statut_bulletin,
    ROUND(bp.totalgains * 0.13, 2) as cnaps_employeur,
    ROUND(bp.totalgains * 0.05, 2) as ostie_employeur,
    ROUND(bp.totalgains * 0.18, 2) as total_charges_patronales,
    ft.jourstravailles,
    ft.heuressupplementaires,
    ft.absences,
    ft.retards
FROM bulletinpaie bp
         JOIN employe e ON e.id = bp.idemploye
         LEFT JOIN categoriepersonnel cp ON cp.id = e.idcategorie
         LEFT JOIN departement d ON d.id = e.iddept
         JOIN statutbulletin sb ON sb.id = bp.idstatut
         LEFT JOIN feuilletemps ft ON ft.idemploye = e.id
    AND ft.mois = bp.mois
    AND ft.annee = bp.annee;

-- ===========================================================
-- PARTIE 9 : DONNÉES DE RÉFÉRENCE
-- ===========================================================

-- Catégories du personnel
INSERT INTO categoriepersonnel (nom, description) VALUES
                                                      ('Ouvrier', 'Exécute des tâches manuelles ou techniques spécifiques'),
                                                      ('Employé', 'Tâches administratives, commerciales ou de support'),
                                                      ('Technicien', 'Expertise technique, supervision d''équipe'),
                                                      ('Agent de Maîtrise', 'Encadrement intermédiaire'),
                                                      ('Cadre', 'Fonctions de gestion et responsabilité stratégique'),
                                                      ('Dirigeant', 'Niveau le plus élevé de responsabilité');

-- Catégories de compétences
INSERT INTO categoriecompetence (libelle, description) VALUES
    ('Technique', 'Compétences techniques et métiers (développement, infrastructure, etc.)'),
    ('Comportementale', 'Soft skills, communication, travail en équipe'),
    ('Management', 'Pilotage, leadership, gestion d''équipe');

-- Niveaux de compétences
INSERT INTO niveaucompetence (code, libelle, ordre) VALUES
    ('N1', 'Débutant', 1),
    ('N2', 'Intermédiaire', 2),
    ('N3', 'Avancé', 3),
    ('N4', 'Expert', 4);

-- Référentiel de compétences
INSERT INTO competence (code, libelle, description, idcategorie) VALUES
    ('JAVA_BACK', 'Développement Java backend', 'Maîtrise de Java, Spring, JPA',
        (SELECT id FROM categoriecompetence WHERE libelle = 'Technique')),
    ('FRONTEND_WEB', 'Développement Frontend Web', 'HTML/CSS/JS, Vue.js ou frameworks similaires',
        (SELECT id FROM categoriecompetence WHERE libelle = 'Technique')),
    ('GESTION_RH', 'Gestion RH', 'Processus RH, paie, congés, entretiens',
        (SELECT id FROM categoriecompetence WHERE libelle = 'Technique')),
    ('MANAGEMENT_EQUIPE', 'Management d''équipe', 'Animation, fixation d''objectifs, feedback',
        (SELECT id FROM categoriecompetence WHERE libelle = 'Management')),
    ('COMMUNICATION', 'Communication', 'Communication écrite et orale, relation client',
        (SELECT id FROM categoriecompetence WHERE libelle = 'Comportementale'));

-- Formations
INSERT INTO formation (code, libelle, description, duree_heures, fournisseur, cout) VALUES
    ('FORM_JAVA', 'Perfectionnement Java & Spring', 'Approfondissement des concepts Java et Spring Boot', 21, 'Interne', 0),
    ('FORM_FRONT', 'Frontend moderne avec Vue.js', 'Développement d''interfaces modernes avec Vue.js', 14, 'Interne', 0),
    ('FORM_RH', 'Gestion RH avancée', 'Processus RH, paie, congés et indicateurs', 14, 'Organisme externe', 500000),
    ('FORM_MGMT', 'Leadership et Management d''équipe', 'Développer le leadership et la gestion d''équipe', 21, 'Organisme externe', 750000);

-- Formations <-> compétences
INSERT INTO formationcompetence (idformation, idcompetence, idniveau_cible) VALUES
    ((SELECT id FROM formation WHERE code = 'FORM_JAVA'),
     (SELECT id FROM competence WHERE code = 'JAVA_BACK'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3')),
    ((SELECT id FROM formation WHERE code = 'FORM_FRONT'),
     (SELECT id FROM competence WHERE code = 'FRONTEND_WEB'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3')),
    ((SELECT id FROM formation WHERE code = 'FORM_RH'),
     (SELECT id FROM competence WHERE code = 'GESTION_RH'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3')),
    ((SELECT id FROM formation WHERE code = 'FORM_MGMT'),
     (SELECT id FROM competence WHERE code = 'MANAGEMENT_EQUIPE'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3'));

-- Compétences évaluées pour 4 employés (suppose que les employés 1..4 existent déjà)

-- Employé 1 : développeur backend
INSERT INTO employecompetence (idemploye, idcompetence, idniveau_actuel, source, commentaire)
VALUES
    (1,
     (SELECT id FROM competence WHERE code = 'JAVA_BACK'),
     (SELECT id FROM niveaucompetence WHERE code = 'N4'),
     'manager', 'Excellent niveau en Java backend'),
    (1,
     (SELECT id FROM competence WHERE code = 'FRONTEND_WEB'),
     (SELECT id FROM niveaucompetence WHERE code = 'N2'),
     'manager', 'Peut intervenir ponctuellement sur le frontend'),
    (1,
     (SELECT id FROM competence WHERE code = 'COMMUNICATION'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3'),
     'manager', 'Bon communicant avec l''équipe');

-- Employé 2 : profil frontend
INSERT INTO employecompetence (idemploye, idcompetence, idniveau_actuel, source, commentaire)
VALUES
    (2,
     (SELECT id FROM competence WHERE code = 'FRONTEND_WEB'),
     (SELECT id FROM niveaucompetence WHERE code = 'N4'),
     'manager', 'Très bon niveau en développement frontend'),
    (2,
     (SELECT id FROM competence WHERE code = 'JAVA_BACK'),
     (SELECT id FROM niveaucompetence WHERE code = 'N2'),
     'manager', 'Bases en Java backend'),
    (2,
     (SELECT id FROM competence WHERE code = 'COMMUNICATION'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3'),
     'manager', 'Bonne communication avec les clients internes');

-- Employé 3 : responsable RH
INSERT INTO employecompetence (idemploye, idcompetence, idniveau_actuel, source, commentaire)
VALUES
    (3,
     (SELECT id FROM competence WHERE code = 'GESTION_RH'),
     (SELECT id FROM niveaucompetence WHERE code = 'N4'),
     'manager', 'Très bon niveau en gestion RH'),
    (3,
     (SELECT id FROM competence WHERE code = 'MANAGEMENT_EQUIPE'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3'),
     'manager', 'Gère une petite équipe RH'),
    (3,
     (SELECT id FROM competence WHERE code = 'COMMUNICATION'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3'),
     'manager', 'Bonne communication transversale');

-- Employé 4 : manager technique
INSERT INTO employecompetence (idemploye, idcompetence, idniveau_actuel, source, commentaire)
VALUES
    (4,
     (SELECT id FROM competence WHERE code = 'JAVA_BACK'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3'),
     'manager', 'Solide en Java mais intervient moins dans le code'),
    (4,
     (SELECT id FROM competence WHERE code = 'MANAGEMENT_EQUIPE'),
     (SELECT id FROM niveaucompetence WHERE code = 'N4'),
     'manager', 'Manager expérimenté d''une équipe technique'),
    (4,
     (SELECT id FROM competence WHERE code = 'COMMUNICATION'),
     (SELECT id FROM niveaucompetence WHERE code = 'N3'),
     'manager', 'Capacité à expliquer des sujets techniques aux métiers');

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

-- Statuts des demandes (workflow Manager -> RH)

UPDATE statutdemande SET nom = 'En attente manager' WHERE id = 1;
UPDATE statutdemande SET nom = 'Validé manager' WHERE id = 2;
UPDATE statutdemande SET nom = 'Refusé manager' WHERE id = 3;
UPDATE statutdemande SET nom = 'En attente RH' WHERE id = 4;

INSERT INTO statutdemande (nom) VALUES   
    ('Validé RH'),
    ('Refusé RH'),
    ('Annulée');


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

-- Paramétrage des cotisations sociales (Madagascar)
INSERT INTO parametrecotisation (libelle, taux, plafondsalarial, dateeffet, datefin) VALUES
                                                                                         ('CNAPS - Part Employeur', 0.1300, NULL, '2025-01-01', NULL),
                                                                                         ('CNAPS - Part Salarié', 0.0100, NULL, '2025-01-01', NULL),
                                                                                         ('OSTIE - Part Employeur', 0.0500, NULL, '2025-01-01', NULL),
                                                                                         ('OSTIE - Part Salarié', 0.0100, NULL, '2025-01-01', NULL);

-- Départements
INSERT INTO departement (nom) VALUES
                                  ('Ressources Humaines'),
                                  ('Informatique'),
                                  ('Commercial'),
                                  ('Comptabilité'),
                                  ('Production');

-- Profils
INSERT INTO profil (nom) VALUES
                           ('Développeur'),
                           ('Comptable'),
                           ('Commercial'),
                           ('Ressources Humaines');

-- Compétences attendues par profil (profilcompetence)
-- Profil Développeur : Java backend fort, frontend correct, bonne communication
INSERT INTO profilcompetence (idprofil, idcompetence, idniveau_cible, poids, estobligatoire) VALUES
  ((SELECT id FROM profil WHERE nom='Développeur'),
   (SELECT id FROM competence WHERE code='JAVA_BACK'),
   (SELECT id FROM niveaucompetence WHERE code='N3'),
   1.5, TRUE),
  ((SELECT id FROM profil WHERE nom='Développeur'),
   (SELECT id FROM competence WHERE code='FRONTEND_WEB'),
   (SELECT id FROM niveaucompetence WHERE code='N2'),
   1.0, FALSE),
  ((SELECT id FROM profil WHERE nom='Développeur'),
   (SELECT id FROM competence WHERE code='COMMUNICATION'),
   (SELECT id FROM niveaucompetence WHERE code='N2'),
   1.0, TRUE);

-- Profil Ressources Humaines : forte compétence RH, management et communication
INSERT INTO profilcompetence (idprofil, idcompetence, idniveau_cible, poids, estobligatoire) VALUES
  ((SELECT id FROM profil WHERE nom='Ressources Humaines'),
   (SELECT id FROM competence WHERE code='GESTION_RH'),
   (SELECT id FROM niveaucompetence WHERE code='N3'),
   1.5, TRUE),
  ((SELECT id FROM profil WHERE nom='Ressources Humaines'),
   (SELECT id FROM competence WHERE code='MANAGEMENT_EQUIPE'),
   (SELECT id FROM niveaucompetence WHERE code='N2'),
   1.2, TRUE),
  ((SELECT id FROM profil WHERE nom='Ressources Humaines'),
   (SELECT id FROM competence WHERE code='COMMUNICATION'),
   (SELECT id FROM niveaucompetence WHERE code='N3'),
   1.0, TRUE);

-- Types d'annonce
INSERT INTO typeannonce (libelle) VALUES
                                     ('CDI'), ('CDD'), ('Stage');

-- Types de champ pour critères
INSERT INTO typechamp (libelle) VALUES
                                  ('text'), ('number'), ('email'), ('date'),
                                  ('textarea'), ('checkbox'), ('radio'), ('select'), ('diplome');

-- Critères génériques
INSERT INTO critere (nom, idtypechamp) VALUES
  ('Années d''étude', (SELECT id FROM typechamp WHERE libelle='number')),
  ('Email de contact', (SELECT id FROM typechamp WHERE libelle='email')),
  ('Date de disponibilité', (SELECT id FROM typechamp WHERE libelle='date')),
  ('Mobilité nationale', (SELECT id FROM typechamp WHERE libelle='checkbox')),
  ('Niveau d''étude', (SELECT id FROM typechamp WHERE libelle='diplome'));

-- Diplômes
INSERT INTO diplome (nom) VALUES ('Baccalauréat'), ('Licence'), ('Master'), ('Doctorat');

-- Contraintes diplôme par profil
INSERT INTO profildiplome (idprofil, iddiplome) VALUES
  ((SELECT id FROM profil WHERE nom='Développeur'), (SELECT id FROM diplome WHERE nom='Licence')),
  ((SELECT id FROM profil WHERE nom='Développeur'), (SELECT id FROM diplome WHERE nom='Master')),
  ((SELECT id FROM profil WHERE nom='Comptable'), (SELECT id FROM diplome WHERE nom='Licence')),
  ((SELECT id FROM profil WHERE nom='Ressources Humaines'), (SELECT id FROM diplome WHERE nom='Licence'));

-- Critères par profil
INSERT INTO critereprofil (idprofil, idcritere, valeurdouble, valeurvarchar, valeurbool, estobligatoire) VALUES
  ((SELECT id FROM profil WHERE nom='Développeur'), (SELECT id FROM critere WHERE nom='Années d''étude'), 2.00, NULL, NULL, TRUE),
  ((SELECT id FROM profil WHERE nom='Développeur'), (SELECT id FROM critere WHERE nom='Mobilité nationale'), NULL, NULL, TRUE, FALSE),
  ((SELECT id FROM profil WHERE nom='Comptable'), (SELECT id FROM critere WHERE nom='Années d''étude'), 1.00, NULL, NULL, TRUE),
  ((SELECT id FROM profil WHERE nom='Commercial'), (SELECT id FROM critere WHERE nom='Années d''étude'), 0.00, NULL, NULL, FALSE),
  ((SELECT id FROM profil WHERE nom='Ressources Humaines'), (SELECT id FROM critere WHERE nom='Années d''étude'), 1.00, NULL, NULL, FALSE);

-- Provinces
INSERT INTO province (nom) VALUES ('Antananarivo'), ('Toamasina'), ('Mahajanga');

-- Statuts candidats
INSERT INTO statutcandidat (nom) VALUES ('Nouveau'), ('Shortlisté'), ('Rejeté'), ('Embauché');

-- Comptes candidats
INSERT INTO comptecandidat (email, motdepasse) VALUES
  ('alice@example.com', 'hashedpwd1'),
  ('bob@example.com', 'hashedpwd2');

-- Annonces d'emploi
INSERT INTO annonce (description, datedebut, datefin, nomposte, iddepartement, idprofil, idtypeannonce, datepublication) VALUES
  ('Développement d''applications web', '2025-02-01', NULL, 'Développeur Web',
   (SELECT id FROM departement WHERE nom='Informatique'), (SELECT id FROM profil WHERE nom='Développeur'), (SELECT id FROM typeannonce WHERE libelle='CDI'), '2025-01-15'),
  ('Tenue de la comptabilité générale', '2025-03-01', '2025-09-01', 'Comptable',
   (SELECT id FROM departement WHERE nom='Comptabilité'), (SELECT id FROM profil WHERE nom='Comptable'), (SELECT id FROM typeannonce WHERE libelle='CDD'), '2025-01-20');

-- Candidats
INSERT INTO candidat (nom, prenom, datenaissance, adresse, cv, idannonce, idstatut, idcomptecandidat, idprovince) VALUES
  ('Randria', 'Alice', '1995-05-10', 'Antananarivo', 'cv_alice.pdf',
   (SELECT id FROM annonce WHERE nomposte='Développeur Web'), (SELECT id FROM statutcandidat WHERE nom='Nouveau'),
   (SELECT id FROM comptecandidat WHERE email='alice@example.com'), (SELECT id FROM province WHERE nom='Antananarivo')),
  ('Rakoto', 'Bob', '1992-08-21', 'Toamasina', 'cv_bob.pdf',
   (SELECT id FROM annonce WHERE nomposte='Comptable'), (SELECT id FROM statutcandidat WHERE nom='Shortlisté'),
   (SELECT id FROM comptecandidat WHERE email='bob@example.com'), (SELECT id FROM province WHERE nom='Toamasina'));

-- QCM pour Développeur
INSERT INTO qcmtest (nom, idprofil) VALUES ('Test Dev Web', (SELECT id FROM profil WHERE nom='Développeur'));
INSERT INTO qcmquestion (idtest, numero, question, points) VALUES
  ((SELECT id FROM qcmtest WHERE nom='Test Dev Web'), 1, 'Qu''est-ce que HTTP ?', 2),
  ((SELECT id FROM qcmtest WHERE nom='Test Dev Web'), 2, 'Que fait un JOIN en SQL ?', 3);
INSERT INTO qcmchoix (idquestion, texte, estcorrect) VALUES
  ((SELECT id FROM qcmquestion WHERE numero=1 AND idtest=(SELECT id FROM qcmtest WHERE nom='Test Dev Web')), 'Protocole de transfert', TRUE),
  ((SELECT id FROM qcmquestion WHERE numero=1 AND idtest=(SELECT id FROM qcmtest WHERE nom='Test Dev Web')), 'Un langage de programmation', FALSE),
  ((SELECT id FROM qcmquestion WHERE numero=2 AND idtest=(SELECT id FROM qcmtest WHERE nom='Test Dev Web')), 'Relier des tables', TRUE),
  ((SELECT id FROM qcmquestion WHERE numero=2 AND idtest=(SELECT id FROM qcmtest WHERE nom='Test Dev Web')), 'Créer une table', FALSE);
INSERT INTO testannonce (idtest, idannonce) VALUES
  ((SELECT id FROM qcmtest WHERE nom='Test Dev Web'), (SELECT id FROM annonce WHERE nomposte='Développeur Web'));
INSERT INTO qcmreponse (idcandidat, idtest, idquestion, idchoix, pointsobtenus, datereponse) VALUES
  ((SELECT id FROM candidat WHERE nom='Randria' AND prenom='Alice'),
   (SELECT id FROM qcmtest WHERE nom='Test Dev Web'),
   (SELECT id FROM qcmquestion WHERE numero=1 AND idtest=(SELECT id FROM qcmtest WHERE nom='Test Dev Web')),
   (SELECT id FROM qcmchoix WHERE idquestion=(SELECT id FROM qcmquestion WHERE numero=1 AND idtest=(SELECT id FROM qcmtest WHERE nom='Test Dev Web')) AND estcorrect=TRUE),
   2, '2025-01-25');

-- Employés de base
INSERT INTO employe (nom, prenom, adresse, iddept, idcategorie, datenaissance, telephone, email, photo, cin, datedembauche, lieunaissance, nationalite, situationfamiliale, nombreenfants, numerocnaps, numeroostie, matricule) VALUES
  ('Ando', 'Tahina', 'Antananarivo', (SELECT id FROM departement WHERE nom='Informatique'), (SELECT id FROM categoriepersonnel WHERE nom='Technicien'), '1990-03-12', '0340000001', 'ando.tahina@ex.com', NULL, '123456789001', '2024-02-01', 'Tana', 'MG', 'Célibataire', 0, 'CNAPS0001', 'OSTIE0001', 'EMP001'),
  ('Fara', 'Miora', 'Toamasina', (SELECT id FROM departement WHERE nom='Comptabilité'), (SELECT id FROM categoriepersonnel WHERE nom='Employé'), '1988-11-02', '0340046002', 'fara.miora@ex.com', NULL, '123456789002', '2023-06-15', 'Tamatave', 'MG', 'Marié(e)', 2, 'CNAPS0002', 'OSTIE0002', 'EMP002'),
  ('Rakoto', 'Balita', 'Mahajanga', (SELECT id FROM departement WHERE nom='Comptabilité'), (SELECT id FROM categoriepersonnel WHERE nom='Employé'), '1968-11-02', '04540002002', 'fara1.miora@ex.com', NULL, '123456789102', '2023-06-15', 'Tamatave', 'MG', 'Marié(e)', 2, 'CNAPS7002', 'OSTIE2002', 'EMP003'),
  ('Fara', 'Miora', 'Toamasina', (SELECT id FROM departement WHERE nom='Comptabilité'), (SELECT id FROM categoriepersonnel WHERE nom='Employé'), '1998-11-02', '0340910002', 'fara2.miora@ex.com', NULL, '123456734002', '2023-06-15', 'Tamatave', 'MG', 'Marié(e)', 2, 'CNAPS0902', 'OSTIE5002', 'EMP004'),
  ('Fara', 'Miora', 'Toamasina', (SELECT id FROM departement WHERE nom='Comptabilité'), (SELECT id FROM categoriepersonnel WHERE nom='Employé'), '2008-11-02', '03400013602', 'fara3.miora@ex.com', NULL, '1234564649002', '2023-06-15', 'Tamatave', 'MG', 'Marié(e)', 2, 'CNAPS0702', 'OSTIE0602', 'EMP005'),
  ('Fara', 'Miora', 'Toamasina', (SELECT id FROM departement WHERE nom='Comptabilité'), (SELECT id FROM categoriepersonnel WHERE nom='Employé'), '1978-11-02', '03400000462', 'far6.miora@ex.com', NULL, '123453189002', '2023-06-15', 'Tamatave', 'MG', 'Marié(e)', 2, 'CNAPS0402', 'OSTIE0102', 'EMP006'),
  ('Fara', 'Miora', 'Toamasina', (SELECT id FROM departement WHERE nom='Comptabilité'), (SELECT id FROM categoriepersonnel WHERE nom='Employé'), '1958-11-02', '0340000079', 'fara9.miora@ex.com', NULL, '123459789002', '2023-06-15', 'Tamatave', 'MG', 'Marié(e)', 2, 'CNAPS0202', 'OSTIE9002', 'EMP007');

-- Lier candidat embauché à employé
INSERT INTO candidatemploye (idcandidat, idemploye) VALUES
  ((SELECT id FROM candidat WHERE nom='Rakoto' AND prenom='Bob'), (SELECT id FROM employe WHERE matricule='EMP002'));

-- Utilisateurs (comptes internes)
INSERT INTO utilisateurs (email, motdepasse, idemploye) VALUES
  ('admin@company.com', 'hashed_admin_pwd', (SELECT id FROM employe WHERE matricule='EMP001')),
  ('fara.miora@ex.com', 'hashed_emp_pwd', (SELECT id FROM employe WHERE matricule='EMP002'));

-- Contrats
INSERT INTO contrat (idemploye, datedebut, datefin, nombremois, periodessai, idstatut, idtypecontrat) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), '2024-02-01', NULL, NULL, 3, (SELECT id FROM statutcontrat WHERE nom='En cours'), (SELECT id FROM typecontrat WHERE libelle='CDI')),
  ((SELECT id FROM employe WHERE matricule='EMP002'), '2023-06-15', '2025-06-14', 24, 2, (SELECT id FROM statutcontrat WHERE nom='En cours'), (SELECT id FROM typecontrat WHERE libelle='CDD'));
INSERT INTO historiquecontrat (idcontrat, idstatut, commentaire) VALUES
  ((SELECT id FROM contrat WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001')), (SELECT id FROM statutcontrat WHERE nom='En cours'), 'Contrat démarré'),
  ((SELECT id FROM contrat WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP002')), (SELECT id FROM statutcontrat WHERE nom='En cours'), 'Contrat en cours');

-- Historique des postes
INSERT INTO historiqueposte (idemploye, posteoccupe, idcategorie, iddepartement, datedebut, datefin, motif) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), 'Développeur Web', (SELECT id FROM categoriepersonnel WHERE nom='Technicien'), (SELECT id FROM departement WHERE nom='Informatique'), '2024-02-01', NULL, 'Recrutement initial'),
  ((SELECT id FROM employe WHERE matricule='EMP002'), 'Comptable', (SELECT id FROM categoriepersonnel WHERE nom='Employé'), (SELECT id FROM departement WHERE nom='Comptabilité'), '2023-06-15', NULL, 'Recrutement initial');

-- Résultats et statuts entretien
INSERT INTO resultat (note, appreciation) VALUES (15, 'Bon'), (10, 'Moyen');
INSERT INTO statutentretien (nom) VALUES ('Planifié'), ('Réalisé'), ('Annulé');
INSERT INTO entretien (idcandidat, dateheure, idstatut, idresultat, idannonce) VALUES
  ((SELECT id FROM candidat WHERE nom='Randria' AND prenom='Alice'), '2025-01-22 10:00:00', (SELECT id FROM statutentretien WHERE nom='Planifié'), NULL, (SELECT id FROM annonce WHERE nomposte='Développeur Web')),
  ((SELECT id FROM candidat WHERE nom='Rakoto' AND prenom='Bob'), '2025-01-18 09:00:00', (SELECT id FROM statutentretien WHERE nom='Réalisé'), (SELECT id FROM resultat ORDER BY id LIMIT 1), (SELECT id FROM annonce WHERE nomposte='Comptable'));
INSERT INTO historiquecandidature (idcandidat, idstatut) VALUES
  ((SELECT id FROM candidat WHERE nom='Rakoto' AND prenom='Bob'), (SELECT id FROM statutcandidat WHERE nom='Shortlisté')),
  ((SELECT id FROM candidat WHERE nom='Rakoto' AND prenom='Bob'), (SELECT id FROM statutcandidat WHERE nom='Embauché')),
  ((SELECT id FROM candidat WHERE nom='Randria' AND prenom='Alice'), (SELECT id FROM statutcandidat WHERE nom='Nouveau'));
INSERT INTO historiqueentretien (identretien, idstatut) VALUES
  ((SELECT id FROM entretien WHERE idcandidat=(SELECT id FROM candidat WHERE nom='Rakoto' AND prenom='Bob')), (SELECT id FROM statutentretien WHERE nom='Réalisé'));

-- Documents employés
INSERT INTO documentemploye (idemploye, idtypedocument, nomfichier, chemin, datexpiration, commentaire) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), (SELECT id FROM typedocument WHERE libelle='CIN'), 'cin_emp001.pdf', '/docs/emp001/cin.pdf', '2035-12-31', 'Recto verso'),
  ((SELECT id FROM employe WHERE matricule='EMP002'), (SELECT id FROM typedocument WHERE libelle='CV'), 'cv_emp002.pdf', '/docs/emp002/cv.pdf', NULL, 'Dernière version');

-- Droits de congés par catégorie
INSERT INTO droitconge (idcategorie, idtypeconge, joursparannee, joursparanciennete, anneesanciennete, dureevalidite) VALUES
  ((SELECT id FROM categoriepersonnel WHERE nom='Employé'), (SELECT id FROM typeconge WHERE libelle='Congé payé'), 30.00, 0.00, 0, 3),
  ((SELECT id FROM categoriepersonnel WHERE nom='Technicien'), (SELECT id FROM typeconge WHERE libelle='Congé payé'), 30.00, 0.00, 0, 3);

-- Soldes de congés
INSERT INTO soldeconge (idemploye, idtypeconge, annee, joursacquis, jourspris, joursrestants) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), (SELECT id FROM typeconge WHERE libelle='Congé payé'), 2025, 30.00, 2.00, 28.00),
  ((SELECT id FROM employe WHERE matricule='EMP002'), (SELECT id FROM typeconge WHERE libelle='Congé payé'), 2025, 30.00, 0.00, 30.00);

-- Demandes de congés et historique
INSERT INTO demandeconge (idemploye, idtypeconge, datedebut, datefin, nombrejoursouvres, motif, idvalideur, idstatut) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), (SELECT id FROM typeconge WHERE libelle='Congé payé'), '2025-03-10', '2025-03-12', 3.00, 'Vacances', (SELECT id FROM employe WHERE matricule='EMP002'), (SELECT id FROM statutdemande WHERE nom='En attente'));
INSERT INTO historiquedemande (iddemandeconge, idstatut, idemploye, commentaire) VALUES
  ((SELECT id FROM demandeconge WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001')), (SELECT id FROM statutdemande WHERE nom='En attente'), (SELECT id FROM employe WHERE matricule='EMP002'), 'Demande reçue');
INSERT INTO congeeffectue (iddemandeconge, idemploye, idtypeconge, datedebut, datefin, nombrejourspris) VALUES
  ((SELECT id FROM demandeconge WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001')), (SELECT id FROM employe WHERE matricule='EMP001'), (SELECT id FROM typeconge WHERE libelle='Congé payé'), '2025-03-10', '2025-03-12', 3.00);

-- Pointage et feuilles de temps
INSERT INTO pointage (idemploye, dateheure, idtypepointage, latitude, longitude, commentaire) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), '2025-01-21 08:00:00', (SELECT id FROM typepointage WHERE libelle='Entrée'), -18.8792, 47.5079, 'On time'),
  ((SELECT id FROM employe WHERE matricule='EMP001'), '2025-01-21 17:00:00', (SELECT id FROM typepointage WHERE libelle='Sortie'), -18.8792, 47.5079, '');
INSERT INTO feuilletemps (idemploye, mois, annee, jourstravailles, heuressupplementaires, absences, retards, datecloture, idvalideur) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), 1, 2025, 22.00, 2.00, 0.00, 1, '2025-01-31 18:00:00', (SELECT id FROM employe WHERE matricule='EMP002'));
INSERT INTO detailfeuilletemps (idfeuilletemps, datejour, heuresentree, heuressortie, heurestravaillees, heuressup, estabsent, commentaire) VALUES
  ((SELECT id FROM feuilletemps WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001') AND mois=1 AND annee=2025), '2025-01-21', '08:00', '17:00', 8.00, 0.00, FALSE, 'Journée standard');

-- Absence
INSERT INTO absence (idemploye, idtypeabsence, datedebut, datefin, nombreheures, justificatif, idvalideur) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP002'), (SELECT id FROM typeabsence WHERE libelle='Absence justifiée'), '2025-01-15', '2025-01-15', 4.00, 'RDV administratif', (SELECT id FROM employe WHERE matricule='EMP001'));

-- Grille salariale et salaire de base
INSERT INTO grillesalariale (idcategorie, niveauechelon, salairebrut, dateeffet) VALUES
  ((SELECT id FROM categoriepersonnel WHERE nom='Technicien'), 'T1', 1200000.00, '2024-01-01'),
  ((SELECT id FROM categoriepersonnel WHERE nom='Employé'), 'E1', 900000.00, '2024-01-01');
INSERT INTO salairebase (idemploye, salairebrut, dateeffet, motif) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), 1500000.00, '2024-02-01', 'Salaire d''entrée'),
  ((SELECT id FROM employe WHERE matricule='EMP002'), 1000000.00, '2023-06-15', 'Salaire d''entrée');

-- Éléments variables
INSERT INTO elementvariable (idemploye, idtypeelementpaie, montant, mois, annee, commentaire) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), (SELECT id FROM typeelementpaie WHERE libelle='Prime de rendement'), 200000.00, 1, 2025, 'Objectifs atteints'),
  ((SELECT id FROM employe WHERE matricule='EMP001'), (SELECT id FROM typeelementpaie WHERE libelle='Heures supplémentaires'), 80000.00, 1, 2025, 'HS Janvier');

-- Bulletins de paie et détails
INSERT INTO bulletinpaie (idemploye, mois, annee, salairebrut, totalcotisations, totalgains, totalretenues, salaireimposable, impot, netapayer, idstatut, cheminpdf) VALUES
  ((SELECT id FROM employe WHERE matricule='EMP001'), 1, 2025, 1500000.00, 0.00, 1780000.00, 0.00, 1780000.00, 100000.00, 1680000.00, (SELECT id FROM statutbulletin WHERE nom='Validé'), '/bulletins/2025/01/EMP001.pdf');
INSERT INTO detailbulletin (idbulletin, idtypeelementpaie, libelle, base, taux, montant, estgain) VALUES
  ((SELECT id FROM bulletinpaie WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001') AND mois=1 AND annee=2025), (SELECT id FROM typeelementpaie WHERE libelle='Salaire de base'), 'Salaire de base', 1500000.00, NULL, 1500000.00, TRUE),
  ((SELECT id FROM bulletinpaie WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001') AND mois=1 AND annee=2025), (SELECT id FROM typeelementpaie WHERE libelle='Prime de rendement'), 'Prime de rendement', NULL, NULL, 200000.00, TRUE),
  ((SELECT id FROM bulletinpaie WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001') AND mois=1 AND annee=2025), (SELECT id FROM typeelementpaie WHERE libelle='Heures supplémentaires'), 'Heures supplémentaires', NULL, NULL, 80000.00, TRUE),
  ((SELECT id FROM bulletinpaie WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001') AND mois=1 AND annee=2025), (SELECT id FROM typeelementpaie WHERE libelle='IRSA'), 'IRSA', 1780000.00, 5.00, 89000.00, FALSE);
INSERT INTO historiquebulletin (idbulletin, idstatut, idemploye, commentaire) VALUES
  ((SELECT id FROM bulletinpaie WHERE idemploye=(SELECT id FROM employe WHERE matricule='EMP001') AND mois=1 AND annee=2025), (SELECT id FROM statutbulletin WHERE nom='Validé'), (SELECT id FROM employe WHERE matricule='EMP002'), 'Validation Janvier 2025');

-- Alertes
INSERT INTO alerte (idtypealerte, idemploye, titre, message, estlu, dateecheance) VALUES
  ((SELECT id FROM typealerte WHERE libelle='Document expiré'), (SELECT id FROM employe WHERE matricule='EMP001'), 'CIN à renouveler', 'Le document CIN expire bientôt', FALSE, '2035-10-01');

-- ===========================================================
-- FIN DU SCRIPT DE STRUCTURE
-- ===========================================================

/*
BASE DE DONNÉES COMPLÈTE CRÉÉE AVEC SUCCÈS !

MODULES INCLUS :
✓ Recrutement (annonces, candidats, entretiens, QCM)
✓ Gestion du personnel (employés, contrats, catégories)
✓ Documents RH (CIN, diplômes, certificats)
✓ Congés (droits, demandes, soldes, historique)
✓ Temps et présences (pointage, feuilles de temps)
✓ Paie (bulletins, CNAPS, OSTIE, IRSA, éléments variables)
✓ Alertes et notifications
✓ Historisation complète (sans UPDATE)

PROCHAINE ÉTAPE :
Exécutez les scripts de simulation de données pour avoir des exemples concrets.
*/
ALTER TABLE Annonce
    ADD COLUMN idprovince INTEGER,
    ADD CONSTRAINT fk_annonce_province FOREIGN KEY (idprovince) REFERENCES province(id);
ALTER TABLE candidat
    ADD COLUMN iddiplome INTEGER,
    ADD CONSTRAINT fk_candidat_diplome FOREIGN KEY (iddiplome) REFERENCES diplome(id);
ALTER TABLE candidat
    ADD COLUMN salaire DOUBLE PRECISION;
ALTER TABLE contrat
    ADD COLUMN poste VARCHAR;
ALTER TABLE contrat
    ADD COLUMN salaire DOUBLE PRECISION;
ALTER TABLE contrat
    ADD COLUMN typecontrat VARCHAR;
ALTER TABLE documentemploye ADD COLUMN contenujson JSONB;
--ALTER TABLE pointage ADD COLUMN IF NOT EXISTS estvalide BOOLEAN DEFAULT TRUE;




INSERT INTO pointage (idemploye, dateheure, idtypepointage, latitude, longitude, commentaire, estvalide) VALUES
-- Pointages pour Ando (ID 1)
(1, '2025-11-15 08:00:00', 1, -18.879190, 47.507905, 'Arrivée bureau', true),
(1, '2025-11-15 12:00:00', 2, -18.879190, 47.507905, 'Déjeuner', true),
(1, '2025-11-15 13:00:00', 1, -18.879190, 47.507905, 'Retour déjeuner', true),
(1, '2025-11-15 17:30:00', 2, -18.879190, 47.507905, 'Fin journée', true),

-- Pointages pour Fara (ID 2)
(2, '2025-11-15 07:55:00', 1, -18.879200, 47.507910, 'Arrivée', true),
(2, '2025-11-15 12:05:00', 3, -18.879200, 47.507910, 'Pause déjeuner', true),
(2, '2025-11-15 13:10:00', 4, -18.879200, 47.507910, 'Fin pause', true),
(2, '2025-11-15 17:45:00', 2, -18.879200, 47.507910, 'Départ', true),

-- Pointages avec retard pour Fara
(2, '2025-11-16 08:45:00', 1, -18.879200, 47.507910, 'Retard - embouteillage', true),
(2, '2025-11-16 17:35:00', 2, -18.879200, 47.507910, 'Départ', true);



INSERT INTO absence (idemploye, idtypeabsence, datedebut, datefin, nombreheures, justificatif, idvalideur) VALUES
-- Absence maladie pour Ando
(1, 4, '2025-11-10', '2025-11-10', 8, 'Maladie légère', 2),

-- Absence justifiée pour Fara
(2, 1, '2025-11-12', '2025-11-12', 8, 'Rendez-vous médical', 1),

-- Retard pour Ando
(1, 3, '2025-11-08', '2025-11-08', 0.5, 'Problème transport', 2),

-- Absence longue pour Fara
(2, 1, '2025-11-18', '2025-11-19', 16, 'Formation professionnelle', 1);


INSERT INTO feuilletemps (idemploye, mois, annee, jourstravailles, heuressupplementaires, absences, retards, datecloture, idvalideur) VALUES
-- Feuille de temps Janvier 2024 pour Ando
(1, 11, 2025, 20.5, 4.5, 1.5, 2, '2025-11-05 10:00:00', 2),

-- Feuille de temps Janvier 2025 pour Fara
(2, 11, 2025, 19.0, 6.0, 3.0, 1, '2025-11-05 10:30:00', 1),

-- Feuille de temps Décembre 2023 pour Ando
(1, 10, 2025, 22.0, 2.0, 0.0, 0, '2025-11-05 09:00:00', 2),

-- Feuille de temps Décembre 2023 pour Fara
(2, 10, 2025, 21.5, 3.5, 0.5, 0, '2025-01-05 09:30:00', 1);