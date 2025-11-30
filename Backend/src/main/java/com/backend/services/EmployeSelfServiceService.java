package com.backend.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeSelfServiceService {

    private final JdbcTemplate jdbcTemplate;

    public EmployeSelfServiceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1) Login employé par matricule + mot de passe statique
    public Map<String, Object> loginEmploye(String matricule, String motdepasse) {
        Map<String, Object> response = new HashMap<>();

        if (!"emp123".equals(motdepasse)) {
            response.put("success", false);
            response.put("message", "Matricule ou mot de passe incorrect");
            return response;
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, matricule, nom, prenom FROM employe WHERE matricule = ?",
                matricule
        );

        if (rows.isEmpty()) {
            response.put("success", false);
            response.put("message", "Matricule ou mot de passe incorrect");
            return response;
        }

        Map<String, Object> emp = rows.get(0);
        response.put("success", true);
        response.put("message", "Connexion employé réussie");
        response.put("employe", emp);
        return response;
    }

    // 2) Profil employé - lecture
    public Map<String, Object> getProfilEmploye(Integer idEmploye) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT e.id, e.matricule, e.nom, e.prenom, g.libelle AS genre, e.adresse, e.telephone, e.email, e.situationfamiliale, e.nombreenfants " +
                        "FROM employe e " +
                        "LEFT JOIN genre g ON g.id = e.idgenre " +
                        "WHERE e.id = ?",
                idEmploye
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    // 2) Profil employé - mise à jour des champs autorisés
    public int updateProfilEmploye(Integer idEmploye, Map<String, Object> data) {
        String sql = "UPDATE employe SET idgenre = ?, adresse = ?, telephone = ?, email = ?, situationfamiliale = ?, nombreenfants = ? " +
                "WHERE id = ?";

        Object genreVal = data.get("genre");
        Object adresse = data.get("adresse");
        Object telephone = data.get("telephone");
        Object email = data.get("email");
        Object situationfamiliale = data.get("situationfamiliale");
        Integer nombreenfants = null;
        if (data.get("nombreenfants") != null) {
            nombreenfants = Integer.valueOf(data.get("nombreenfants").toString());
        }

        Integer idGenre = null;
        if (genreVal != null) {
            String gStr = genreVal.toString().trim();
            // on accepte M/F/A ou Homme/Femme/Autre (insensible à la casse)
            List<Map<String, Object>> gRows = jdbcTemplate.queryForList(
                    "SELECT id FROM genre WHERE lower(code) = lower(?) OR lower(libelle) = lower(?) LIMIT 1",
                    gStr, gStr
            );
            if (!gRows.isEmpty()) {
                Object v = gRows.get(0).get("id");
                if (v != null) {
                    idGenre = Integer.valueOf(v.toString());
                }
            }
        }

        return jdbcTemplate.update(sql,
                idGenre,
                adresse,
                telephone,
                email,
                situationfamiliale,
                nombreenfants,
                idEmploye
        );
    }

    // 3) Bulletins de paie depuis la vue v_bulletin_paie_complet
    public List<Map<String, Object>> getBulletins(Integer idEmploye, Integer mois, Integer annee) {
        if (mois != null && annee != null) {
            return jdbcTemplate.queryForList(
                    "SELECT * FROM v_bulletin_paie_complet WHERE employe_id = ? AND mois = ? AND annee = ? " +
                            "ORDER BY annee DESC, mois DESC",
                    idEmploye, mois, annee
            );
        } else {
            return jdbcTemplate.queryForList(
                    "SELECT * FROM v_bulletin_paie_complet WHERE employe_id = ? ORDER BY annee DESC, mois DESC",
                    idEmploye
            );
        }
    }

    // 4) Solde de congés par employé / annee
    public List<Map<String, Object>> getSoldeConge(Integer idEmploye, Integer annee) {
        if (annee != null) {
            return jdbcTemplate.queryForList(
                    "SELECT sc.id, sc.idtypeconge, tc.libelle AS typeconge, sc.annee, sc.joursacquis, sc.jourspris, sc.joursrestants " +
                            "FROM soldeconge sc " +
                            "JOIN typeconge tc ON tc.id = sc.idtypeconge " +
                            "WHERE sc.idemploye = ? AND sc.annee = ?",
                    idEmploye, annee
            );
        } else {
            return jdbcTemplate.queryForList(
                    "SELECT sc.id, sc.idtypeconge, tc.libelle AS typeconge, sc.annee, sc.joursacquis, sc.jourspris, sc.joursrestants " +
                            "FROM soldeconge sc " +
                            "JOIN typeconge tc ON tc.id = sc.idtypeconge " +
                            "WHERE sc.idemploye = ? ORDER BY sc.annee DESC",
                    idEmploye
            );
        }
    }

    // 5) Liste des demandes de congé pour un employé
    public List<Map<String, Object>> getDemandesConge(Integer idEmploye) {
        return jdbcTemplate.queryForList(
                "SELECT d.id, d.idtypeconge, tc.libelle AS typeconge, d.datedebut, d.datefin, d.nombrejoursouvres, " +
                        "d.motif, d.datedemande, d.idvalideur, d.idstatut, sd.nom AS statut " +
                        "FROM demandeconge d " +
                        "JOIN typeconge tc ON tc.id = d.idtypeconge " +
                        "JOIN statutdemande sd ON sd.id = d.idstatut " +
                        "WHERE d.idemploye = ? " +
                        "ORDER BY d.datedemande DESC",
                idEmploye
        );
    }

    // 6) Création d'une demande de congé
    public Map<String, Object> creerDemandeConge(Integer idEmploye,
                                                 Integer idTypeConge,
                                                 LocalDate dateDebut,
                                                 LocalDate dateFin,
                                                 String motif) {
        Map<String, Object> response = new HashMap<>();

        if (dateDebut == null || dateFin == null || dateFin.isBefore(dateDebut)) {
            response.put("success", false);
            response.put("message", "Période invalide");
            return response;
        }

        long jours = dateFin.toEpochDay() - dateDebut.toEpochDay() + 1;
        double nombreJoursOuvres = (double) jours; // simplification: tous les jours ouvrés

        Integer idStatutEnAttente = jdbcTemplate.queryForObject(
                "SELECT id FROM statutdemande WHERE nom = 'En attente' LIMIT 1",
                Integer.class
        );

        String sql = "INSERT INTO demandeconge (idemploye, idtypeconge, datedebut, datefin, nombrejoursouvres, motif, idvalideur, idstatut) " +
                "VALUES (?, ?, ?, ?, ?, ?, NULL, ?) RETURNING id";

        Integer idDemande = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                idEmploye,
                idTypeConge,
                Date.valueOf(dateDebut),
                Date.valueOf(dateFin),
                nombreJoursOuvres,
                motif,
                idStatutEnAttente
        );

        // Historique de la demande
        jdbcTemplate.update(
                "INSERT INTO historiquedemande (iddemandeconge, idstatut, idemploye, commentaire) VALUES (?, ?, ?, ?)",
                idDemande,
                idStatutEnAttente,
                idEmploye,
                "Demande créée par l'employé"
        );

        response.put("success", true);
        response.put("message", "Demande de congé créée");
        response.put("iddemande", idDemande);
        return response;
    }

    // 7) Messagerie RH - liste des messages d'un employé
    public List<Map<String, Object>> getMessagesEmploye(Integer idEmploye) {
        return jdbcTemplate.queryForList(
                "SELECT id, idemploye, sujet, contenu, reponse, date_envoi, date_reponse, lu " +
                        "FROM message_rh WHERE idemploye = ? ORDER BY date_envoi DESC",
                idEmploye
        );
    }

    // 8) Messagerie RH - création d'un nouveau message par l'employé
    public Map<String, Object> creerMessageEmploye(Integer idEmploye, String sujet, String contenu) {
        Map<String, Object> response = new HashMap<>();

        if (sujet == null || sujet.trim().isEmpty() || contenu == null || contenu.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Sujet et contenu sont obligatoires");
            return response;
        }

        Integer idMessage = jdbcTemplate.queryForObject(
                "INSERT INTO message_rh (idemploye, sujet, contenu) VALUES (?, ?, ?) RETURNING id",
                Integer.class,
                idEmploye,
                sujet.trim(),
                contenu.trim()
        );

        response.put("success", true);
        response.put("message", "Message envoyé à la RH");
        response.put("idmessage", idMessage);
        return response;
    }
}
