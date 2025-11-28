--ajout du colonne estObligatoire dans CritereProfil
ALTER TABLE CritereProfil
ADD COLUMN estObligatoire BOOLEAN DEFAULT TRUE;


-- Table des types d'annonce (CDI, Freelance, CDD, etc.)
CREATE TABLE TypeAnnonce (
                             id SERIAL PRIMARY KEY,
                             libelle VARCHAR(50) NOT NULL UNIQUE
);

ALTER TABLE Annonce
    ADD COLUMN idTypeAnnonce INT,
    ADD CONSTRAINT fk_annonce_type
        FOREIGN KEY (idTypeAnnonce) REFERENCES TypeAnnonce(id);

ALTER TABLE Annonce
    ADD COLUMN idprovince INTEGER;


CREATE TABLE TypeChamp (
                           id SERIAL PRIMARY KEY,
                           libelle VARCHAR(50) NOT NULL UNIQUE -- ex: "text", "number", "radio", "select", "checkbox"
);
ALTER TABLE Critere
    ADD COLUMN idTypeChamp INT REFERENCES TypeChamp(id);
ALTER TABLE qcmreponse
    ADD COLUMN datereponse date;
ALTER TABLE candidat
    ADD COLUMN salaire double precision;
ALTER TABLE contrat
    ADD COLUMN poste varchar;
ALTER TABLE contrat
    ADD COLUMN salaire double precision;

