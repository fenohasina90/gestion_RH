-- departement
INSERT INTO departement (nom) VALUES
                                  ('Informatique'),
                                  ('Ressources Humaines'),
                                  ('Comptabilite'),
                                  ('Marketing');

-- profil
INSERT INTO profil (nom) VALUES
                             ('Developpeur Java'),
                             ('Comptable'),
                             ('Charge RH'),
                             ('Community Manager');

-- diplome
INSERT INTO diplome (nom) VALUES
                              ('Licence Informatique'),
                              ('Master RH'),
                              ('Licence Comptabilite');

-- typeannonce
INSERT INTO typeannonce (libelle) VALUES
                                      ('CDD'),
                                      ('CDI');

-- typechamp
INSERT INTO typechamp (libelle) VALUES
                                    ('text'),
                                    ('number'),
                                    ('radio'),
                                    ('select'),
                                    ('checkbox'),
                                    ('date'),
                                    ('textarea');

-- annonce
INSERT INTO annonce (description, datedebut, datefin, nomposte, iddepartement, idprofil, idtypeannonce, datepublication) VALUES
                                                                                                                             ('Recrutement developpeur Java junior','2025-09-01','2025-09-30','Developpeur Java',1,1,1,'2025-09-01'),
                                                                                                                             ('Besoin urgent dun comptable confirme','2025-08-15','2025-09-15','Comptable',3,2,1,'2025-08-10'),
                                                                                                                             ('Recrutement Charge RH','2025-09-10','2025-09-25','Charge RH',2,3,2,'2025-09-05'),
                                                                                                                             ('CDI Community Manager','2025-09-18',NULL,'Community Manager',4,4,2,'2025-09-18');

-- statutcandidat
INSERT INTO statutcandidat (nom) VALUES
                                     ('En attente'),
                                     ('Entretien'),
                                     ('Accepte'),
                                     ('Refuse');

-- statutentretien
INSERT INTO statutentretien (nom) VALUES
                                      ('Planifie'),
                                      ('En cours'),
                                      ('Termine');

-- comptecandidat
INSERT INTO comptecandidat (email,motdepasse) VALUES
                                                  ('rakoto.hery@gmail.com','pass123'),
                                                  ('rasoanaivo.lova@gmail.com','pass123'),
                                                  ('randrianarisoa.tiana@gmail.com','pass123'),
                                                  ('rabe.josoa@gmail.com','pass123');

-- candidat
INSERT INTO candidat (nom,prenom,datenaissance,adresse,cv,idannonce,idstatut,idcomptecandidat) VALUES
                                                                                                   ('Rakoto','Hery','1995-04-12','Antananarivo','Experience Java 2 ans',1,1,1),
                                                                                                   ('Rasoanaivo','Lova','1993-06-25','Fianarantsoa','Comptable senior',2,2,2),
                                                                                                   ('Randrianarisoa','Tiana','1998-01-10','Toamasina','Stage en RH',3,1,3),
                                                                                                   ('Rabe','Josoa','1996-02-15','Antsirabe','Community Manager debutant',4,1,4);

-- employe
INSERT INTO employe (nom,prenom,adresse,iddept) VALUES
                                                    ('Rakotobe','Feno','Mahajanga',3),
                                                    ('Rabe','Soa','Antananarivo',1);

-- contrat
INSERT INTO contrat (idemploye,datedebut,nombremois,typecontrat) VALUES
                                                                     (1,'2025-10-01',12,'CDI'),
                                                                     (2,'2025-09-01',6,'CDD');

-- utilisateurs
INSERT INTO utilisateurs (email,motdepasse,idemploye) VALUES
                                                          ('feno.rakotobe@entreprise.mg','pass123',1),
                                                          ('soa.rabe@entreprise.mg','pass123',2);

-- candidatemploye
INSERT INTO candidatemploye (idcandidat,idemploye) VALUES
                                                       (1,1),
                                                       (2,2);

-- critere
INSERT INTO critere (nom,idtypechamp) VALUES
                                          ('Experience (annees)',2),
                                          ('Diplome requis',4),
                                          ('Competence technique',1),
                                          ('Langue etrangere',3),
                                          ('Full Stack',5);

-- critereprofil
INSERT INTO critereprofil (idprofil,idcritere,valeurdouble,valeurvarchar,valeurbool,estobligatoire) VALUES
                                                                                                        (1,1,2.0,NULL,NULL,true),
                                                                                                        (1,2,NULL,'Licence Informatique',NULL,true),
                                                                                                        (1,3,NULL,'Java',NULL,true),
                                                                                                        (2,2,NULL,'Licence Comptabilite',NULL,true),
                                                                                                        (3,2,NULL,'Master RH',NULL,false),
                                                                                                        (4,3,NULL,'Reseaux sociaux',NULL,true);

-- profildiplome
INSERT INTO profildiplome (idprofil,iddiplome) VALUES
                                                   (1,1),
                                                   (2,3),
                                                   (3,2);

-- candidaturecritere
INSERT INTO candidaturecritere (idcandidat,idannonce,idcritere,valeurdouble,valeurvarchar,valeurbool,iddiplome) VALUES
                                                                                                                    (1,1,1,2.0,NULL,NULL,1),
                                                                                                                    (2,2,2,NULL,'Licence Comptabilite',NULL,3),
                                                                                                                    (3,3,2,NULL,'Master RH',NULL,2),
                                                                                                                    (4,4,3,NULL,'Community management',NULL,NULL);

-- resultat
INSERT INTO resultat (note,appreciation) VALUES
                                             (3,'Bon niveau'),
                                             (1,'Insuffisant');

-- entretien (apres insertion des resultat)
INSERT INTO entretien (idcandidat,dateheure,idstatut,idresultat)
VALUES
    (1,'2025-09-20 10:00:00',1,1),
    (2,'2025-09-18 14:00:00',3,2);

-- historiquecandidature
INSERT INTO historiquecandidature (idcandidat,idstatut,datechangement) VALUES
                                                                           (1,1,'2025-09-05 08:00:00'),
                                                                           (2,2,'2025-09-10 11:00:00'),
                                                                           (3,4,'2025-09-12 13:00:00');

-- historiqueentretien
INSERT INTO historiqueentretien (identretien,idstatut,datechangement) VALUES
                                                                          (1,1,'2025-09-15 09:00:00'),
                                                                          (2,3,'2025-09-18 15:30:00');

-- qcmtest
INSERT INTO qcmtest (nom,idprofil) VALUES
                                       ('Test Java niveau junior',1),
                                       ('Test Comptabilite generale',2),
                                       ('Test RH recrutement',3);

-- qcmquestion
INSERT INTO qcmquestion (idtest,numero,question,points) VALUES
                                                            (1,1,'Qu est-ce qu une classe en Java ?',2),
                                                            (1,2,'A quoi sert le mot cle static ?',1),
                                                            (3,1,'Quel est le role principal d un service RH ?',2),
                                                            (3,2,'Quelle est la duree maximale d une periode d essai a Madagascar ?',1),
                                                            (3,3,'Quel document formalise l embauche ?',1);

-- qcmchoix
INSERT INTO qcmchoix (idquestion,texte,estcorrect) VALUES
                                                       (1,'Un modele definissant objets et methodes',true),
                                                       (1,'Un fichier XML',false),
                                                       (2,'Permet d utiliser une methode sans instance',true),
                                                       (2,'Permet d indiquer un commentaire',false),
                                                       (3,'Gerer les salaires uniquement',false),
                                                       (3,'Gerer le recrutement, la formation et le personnel',true),
                                                       (3,'S occuper des finances',false),
                                                       (4,'3 mois renouvelables',true),
                                                       (4,'12 mois fixes',false),
                                                       (4,'1 mois non renouvelable',false),
                                                       (5,'Le contrat de travail',true),
                                                       (5,'Un bulletin de paie',false),
                                                       (5,'Une note de service',false);

-- qcmreponse
INSERT INTO qcmreponse (idcandidat,idtest,idquestion,idchoix,pointsobtenus,datereponse) VALUES
                                                                                            (1,1,1,1,2,'2025-09-20'),
                                                                                            (1,1,2,3,1,'2025-09-20'),
                                                                                            (2,2,1,2,0,'2025-09-18');

-- testannonce
INSERT INTO testannonce (idtest,idannonce) VALUES
                                               (1,1),
                                               (2,2),
                                               (3,3);
