-- ===========================================================
-- Script recréé avec SERIAL PRIMARY KEY et contraintes
-- ===========================================================

CREATE TABLE departement (
                             id SERIAL PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL
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

CREATE TABLE candidat (
                          id SERIAL PRIMARY KEY,
                          nom VARCHAR(100) NOT NULL,
                          prenom VARCHAR(100) NOT NULL,
                          datenaissance DATE,
                          adresse VARCHAR(200),
                          cv TEXT,
                          idannonce INT REFERENCES annonce(id),
                          idstatut INT REFERENCES statutcandidat(id),
                          idcomptecandidat INT REFERENCES comptecandidat(id)
);
CREATE TABLE employe (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL,
                         prenom VARCHAR(100) NOT NULL,
                         adresse VARCHAR(200),
                         iddept INT REFERENCES departement(id)
);

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



CREATE TABLE contrat (
                         id SERIAL PRIMARY KEY,
                         idemploye INT REFERENCES employe(id),
                         datedebut DATE,
                         nombremois INT,
                         typecontrat VARCHAR(50)
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
                           idresultat INT REFERENCES resultat(id)
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
ALTER TABLE entretien
    ADD COLUMN idannonce INT REFERENCES annonce(id);
CREATE TABLE province (
    id SERIAL PRIMARY KEY,
    nom varchar
);
ALTER TABLE candidat
    ADD COLUMN idprovince INT REFERENCES province(id);