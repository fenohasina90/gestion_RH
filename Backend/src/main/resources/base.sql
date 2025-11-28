-- =========================
-- SCRIPT POSTGRESQL RECRUTEMENT (VERSION SIMPLIFIEE QCM)
-- =========================

-- Table Departement
CREATE TABLE Departement (
                             id SERIAL PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL
);

-- Table Profil
CREATE TABLE Profil (
                        id SERIAL PRIMARY KEY,
                        nom VARCHAR(100) NOT NULL
);

-- Table Critere
CREATE TABLE Critere (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL
);

-- Table CritereProfil
CREATE TABLE CritereProfil (
                               id SERIAL PRIMARY KEY,
                               idProfil INT REFERENCES Profil(id),
                               idCritere INT REFERENCES Critere(id),
                               valeurDouble NUMERIC(10,2),
                               valeurVarchar VARCHAR(200),
                               valeurBool BOOLEAN
);

-- Table Annonce
CREATE TABLE Annonce (
                         id SERIAL PRIMARY KEY,
                         description TEXT,
                         dateDebut DATE,
                         dateFin DATE,
                         nomPoste VARCHAR(100) NOT NULL,
                         idDepartement INT REFERENCES Departement(id),
                         idProfil INT REFERENCES Profil(id)
);

-- Table StatutCandidat
CREATE TABLE StatutCandidat (
                                id SERIAL PRIMARY KEY,
                                nom VARCHAR(50) NOT NULL
);

-- Table Candidat
CREATE TABLE Candidat (
                          id SERIAL PRIMARY KEY,
                          nom VARCHAR(100) NOT NULL,
                          prenom VARCHAR(100) NOT NULL,
                          dateNaissance DATE,
                          adresse VARCHAR(200),
                          cv TEXT,
                          idAnnonce INT REFERENCES Annonce(id),
                          idStatut INT REFERENCES StatutCandidat(id)
);

-- Table Employe
CREATE TABLE Employe (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL,
                         prenom VARCHAR(100) NOT NULL,
                         adresse VARCHAR(200),
                         idDept INT REFERENCES Departement(id)
);

-- Table Utilisateurs
CREATE TABLE Utilisateurs (
                              id SERIAL PRIMARY KEY,
                              email VARCHAR(150) UNIQUE NOT NULL,
                              motDePasse VARCHAR(200) NOT NULL,
                              idEmploye INT REFERENCES Employe(id)
);

-- Table CandidatEmploye
CREATE TABLE CandidatEmploye (
                                 id SERIAL PRIMARY KEY,
                                 idCandidat INT REFERENCES Candidat(id),
                                 idEmploye INT REFERENCES Employe(id)
);

-- Table Diplome
CREATE TABLE Diplome (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL
);

-- Table QcmTest
CREATE TABLE QcmTest (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(150),
                         idProfil INT REFERENCES Profil(id)
);

-- Table TestAnnonce
CREATE TABLE TestAnnonce (
                             id SERIAL PRIMARY KEY,
                             idTest INT NOT NULL REFERENCES QcmTest(id),
                             idAnnonce INT NOT NULL REFERENCES Annonce(id)
);

-- Table QcmQuestion
CREATE TABLE QcmQuestion (
                             id SERIAL PRIMARY KEY,
                             idTest INT NOT NULL REFERENCES QcmTest(id),
                             numero INT NOT NULL,
                             question TEXT NOT NULL,
                             points INT NOT NULL DEFAULT 1
);

-- Table QcmChoix
CREATE TABLE QcmChoix (
                          id SERIAL PRIMARY KEY,
                          idQuestion INT NOT NULL REFERENCES QcmQuestion(id),
                          texte VARCHAR(500) NOT NULL,
                          estCorrect BOOLEAN DEFAULT FALSE
);

-- Table QcmReponse
CREATE TABLE QcmReponse (
                            id SERIAL PRIMARY KEY,
                            idCandidat INT NOT NULL REFERENCES Candidat(id),
                            idTest INT NOT NULL REFERENCES QcmTest(id),
                            idQuestion INT NOT NULL REFERENCES QcmQuestion(id),
                            idChoix INT REFERENCES QcmChoix(id),
                            pointsObtenus INT DEFAULT 0
);

-- Table Resultat
CREATE TABLE Resultat (
                          id SERIAL PRIMARY KEY,
                          note INT,
                          appreciation VARCHAR(200)
);

-- Table StatutEntretien
CREATE TABLE StatutEntretien (
                                 id SERIAL PRIMARY KEY,
                                 nom VARCHAR(50) NOT NULL
);

-- Table Entretien
CREATE TABLE Entretien (
                           id SERIAL PRIMARY KEY,
                           idCandidat INT REFERENCES Candidat(id),
                           dateHeure TIMESTAMP,
                           idStatut INT REFERENCES StatutEntretien(id),
                           idResultat INT REFERENCES Resultat(id)
);

-- Table HistoriqueEntretien
CREATE TABLE HistoriqueEntretien (
                                     id SERIAL PRIMARY KEY,
                                     idEntretien INT REFERENCES Entretien(id),
                                     idStatut INT REFERENCES StatutEntretien(id),
                                     dateChangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table HistoriqueCandidature
CREATE TABLE HistoriqueCandidature (
                                       id SERIAL PRIMARY KEY,
                                       idCandidat INT REFERENCES Candidat(id),
                                       idStatut INT REFERENCES StatutCandidat(id),
                                       dateChangement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Contrat
CREATE TABLE Contrat (
                         id SERIAL PRIMARY KEY,
                         idEmploye INT REFERENCES Employe(id),
                         dateDebut DATE,
                         nombreMois INT,
                         typeContrat VARCHAR(50)
);

-- Table CandidatureCritere
CREATE TABLE CandidatureCritere (
                                    id SERIAL PRIMARY KEY,
                                    idCandidat INT REFERENCES Candidat(id),
                                    idAnnonce INT REFERENCES Annonce(id),
                                    idCritere INT REFERENCES Critere(id),
                                    valeurDouble NUMERIC(10,2),
                                    valeurVarchar VARCHAR(200),
                                    valeurBool BOOLEAN
);
ALTER SEQUENCE candidat_id_seq RESTART WITH 1;
