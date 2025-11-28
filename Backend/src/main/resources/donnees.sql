-- =========================
-- DONNÉES DE TEST
-- =========================

-- Départements
INSERT INTO Departement (nom) VALUES
                                  ('Informatique'),
                                  ('Ressources Humaines'),
                                  ('Comptabilité'),
                                  ('Marketing');

-- Profils
INSERT INTO Profil (nom) VALUES
                             ('Développeur Java'),
                             ('Comptable'),
                             ('Chargé RH'),
                             ('Community Manager');

-- Critères
INSERT INTO Critere (nom) VALUES
                              ('Expérience (années)'),
                              ('Diplôme requis'),
                              ('Compétence technique'),
                              ('Langue étrangère');

-- Critères pour profils
INSERT INTO CritereProfil (idProfil,idCritere,valeurDouble,valeurVarchar,valeurBool) VALUES
                                                                                         (1,1,2,NULL,NULL), -- Dév Java, 2 ans d'exp
                                                                                         (1,2,NULL,'Licence Informatique',NULL),
                                                                                         (1,3,NULL,'Java',NULL),
                                                                                         (2,2,NULL,'Licence Comptabilité',NULL),
                                                                                         (3,2,NULL,'Master RH',NULL),
                                                                                         (4,3,NULL,'Réseaux sociaux',NULL);

-- Annonces
INSERT INTO Annonce (description,dateDebut,dateFin,nomPoste,idDepartement,idProfil) VALUES
                                                                                        ('Recrutement développeur Java junior', '2025-09-01', '2025-09-30', 'Développeur Java', 1, 1),
                                                                                        ('Besoin urgent d’un comptable confirmé', '2025-08-15', '2025-09-15', 'Comptable', 3, 2);

-- Statuts candidats
INSERT INTO StatutCandidat (nom) VALUES
                                     ('En attente'),
                                     ('Entretien'),
                                     ('Accepté'),
                                     ('Refusé');

-- Candidats
INSERT INTO Candidat (nom,prenom,dateNaissance,adresse,cv,idAnnonce,idStatut) VALUES
                                                                                  ('Rakoto','Hery','1995-04-12','Antananarivo','Expérience Java 2 ans',1,1),
                                                                                  ('Rasoanaivo','Lova','1993-06-25','Fianarantsoa','Comptable senior',2,2),
                                                                                  ('Randrianarisoa','Tiana','1998-01-10','Toamasina','Stage en RH',2,4);

-- Employés
INSERT INTO Employe (nom,prenom,adresse,idDept) VALUES
                                                    ('Rabe','Soa','Antananarivo',1),
                                                    ('Rakotobe','Feno','Mahajanga',3);

-- Utilisateurs
INSERT INTO Utilisateurs (email,motDePasse,idEmploye) VALUES
                                                          ('soa.rabe@entreprise.mg','pass123',1),
                                                          ('feno.rakotobe@entreprise.mg','pass456',2);

-- Candidat -> Employé
INSERT INTO CandidatEmploye (idCandidat,idEmploye) VALUES
                                                       (1,1),
                                                       (2,2);

-- Diplômes
INSERT INTO Diplome (nom) VALUES
                              ('Licence Informatique'),
                              ('Master RH'),
                              ('Licence Comptabilité');

-- Tests QCM
INSERT INTO QcmTest (nom,idProfil) VALUES
                                       ('Test Java niveau junior',1),
                                       ('Test Comptabilité générale',2);

-- Lier tests aux annonces
INSERT INTO TestAnnonce (idTest,idAnnonce) VALUES
                                               (1,1),
                                               (2,2);

-- Questions Java
INSERT INTO QcmQuestion (idTest,numero,question,points) VALUES
                                                            (1,1,'Qu’est-ce qu’une classe en Java ?',2),
                                                            (1,2,'À quoi sert le mot clé static ?',1);

-- Choix pour les questions
INSERT INTO QcmChoix (idQuestion,texte,estCorrect) VALUES
                                                       (1,'Un modèle définissant objets et méthodes',TRUE),
                                                       (1,'Un fichier de configuration XML',FALSE),
                                                       (2,'Permet d’utiliser une méthode sans instance',TRUE),
                                                       (2,'Permet d’indiquer un commentaire',FALSE);

-- Réponses candidates
INSERT INTO QcmReponse (idCandidat,idTest,idQuestion,idChoix,pointsObtenus) VALUES
                                                                                (1,1,1,1,2),
                                                                                (1,1,2,3,1);

-- Résultats
INSERT INTO Resultat (note,appreciation) VALUES
                                             (3,'Bon niveau'),
                                             (1,'Insuffisant');

-- Statuts entretien
INSERT INTO StatutEntretien (nom) VALUES
                                      ('Planifié'),
                                      ('En cours'),
                                      ('Terminé');

-- Entretiens
INSERT INTO Entretien (idCandidat,dateHeure,idStatut,idResultat) VALUES
                                                                     (1,'2025-09-20 10:00:00',1,1),
                                                                     (2,'2025-09-18 14:00:00',3,2);

-- Historique entretiens
INSERT INTO HistoriqueEntretien (idEntretien,idStatut,dateChangement) VALUES
                                                                          (1,1,'2025-09-15 09:00:00'),
                                                                          (2,3,'2025-09-18 15:30:00');

-- Historique candidatures
INSERT INTO HistoriqueCandidature (idCandidat,idStatut,dateChangement) VALUES
                                                                           (1,1,'2025-09-05 08:00:00'),
                                                                           (2,2,'2025-09-10 11:00:00'),
                                                                           (3,4,'2025-09-12 13:00:00');

-- Contrats
INSERT INTO Contrat (idEmploye,dateDebut,nombreMois,typeContrat) VALUES
                                                                     (1,'2025-10-01',12,'CDI'),
                                                                     (2,'2025-09-01',6,'CDD');

-- Candidature Critères
INSERT INTO CandidatureCritere (idCandidat,idAnnonce,idCritere,valeurDouble,valeurVarchar,valeurBool) VALUES
                                                                                                          (1,1,1,2,NULL,NULL), -- 2 ans exp Java
                                                                                                          (2,2,2,NULL,'Licence Comptabilité',NULL),
                                                                                                          (3,2,2,NULL,'Stage RH',NULL);
INSERT INTO TypeChamp (libelle) VALUES
                                    ('text'),       -- champ texte libre
                                    ('number'),     -- champ numérique
                                    ('radio'),      -- choix unique
                                    ('select'),     -- liste déroulante
                                    ('checkbox'),   -- cases à cocher
                                    ('date'),       -- champ de date
                                    ('textarea');   -- zone de texte longue
INSERT INTO province (nom) VALUES
                               ('Antananarivo'),
                               ('Fianarantsoa'),
                               ('Toamasina'),
                               ('Toliara'),
                               ('Mahajanga'),
                               ('Antsiranana');
