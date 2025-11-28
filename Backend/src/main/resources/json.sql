INSERT INTO documentemploye (idemploye, idtypedocument, nomfichier, chemin, commentaire, contenujson)
VALUES
-- 1. CIN
(10, 1, 'CIN_10.json', '/uploads/employe10/CIN_10.pdf', 'Copie du CIN valide',
 '{
    "type": "CIN",
    "numero": "201012345678",
    "date_delivrance": "2019-04-12",
    "lieu_delivrance": "Antananarivo",
    "autorite": "Commune Urbaine d’Antananarivo"
 }'),

-- 2. Diplôme
(10, 2, 'Diplome_10.json', '/uploads/employe10/Diplome_10.pdf', 'Diplôme principal',
 '{
    "type": "Diplôme",
    "intitule": "Licence en Informatique",
    "etablissement": "Université d’Antananarivo",
    "annee_obtention": 2021,
    "mention": "Bien"
 }'),

-- 3. CV
(10, 3, 'CV_10.json', '/uploads/employe10/CV_10.pdf', 'Curriculum Vitae à jour',
 '{
    "type": "CV",
    "competences": ["PHP", "PostgreSQL", "Linux", "Java"],
    "langues": ["Français", "Anglais"],
    "experience": [
        {"poste": "Développeur junior", "entreprise": "Tech Mada", "annee": "2022-2023"}
    ]
 }'),

-- 4. Certificat de travail
(10, 4, 'Certificat_Travail_10.json', '/uploads/employe10/Certificat_Travail.pdf', 'Ancien employeur',
 '{
    "type": "Certificat de travail",
    "entreprise": "Tech Mada",
    "poste": "Développeur junior",
    "periode": {"debut": "2022-01-01", "fin": "2023-06-30"}
 }'),

-- 5. Casier judiciaire
(10, 6, 'Casier_Judiciaire_10.json', '/uploads/employe10/Casier_Judiciaire.pdf', 'Extrait récent',
 '{
    "type": "Casier judiciaire",
    "numero": "CJ-10256",
    "date_emission": "2025-01-15",
    "valide_jusquau": "2026-01-15"
 }'),

-- 6. Photo d’identité
(10, 7, 'Photo_Identite_10.json', '/uploads/employe10/photo.jpg', 'Photo officielle',
 '{
    "type": "Photo d’identité",
    "format": "JPG",
    "resolution": "600x600",
    "taille": "120 Ko"
 }'),

-- 7. Acte de naissance
(10, 8, 'Acte_Naissance_10.json', '/uploads/employe10/Acte_Naissance.pdf', 'Original certifié conforme',
 '{
    "type": "Acte de naissance",
    "numero": "AN-2020-45678",
    "lieu_naissance": "Antsirabe",
    "date_naissance": "2000-09-10"
 }');
