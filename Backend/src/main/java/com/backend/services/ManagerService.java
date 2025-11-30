package com.backend.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerService {

    private final JdbcTemplate jdbcTemplate;
    private final DashboardService dashboardService;

    public ManagerService(JdbcTemplate jdbcTemplate, DashboardService dashboardService) {
        this.jdbcTemplate = jdbcTemplate;
        this.dashboardService = dashboardService;
    }

    public Map<String, Object> loginManager(String matricule, String motdepasse) {
        Map<String, Object> response = new HashMap<>();

        if (!"manager123".equals(motdepasse)) {
            response.put("success", false);
            response.put("message", "Matricule ou mot de passe incorrect");
            return response;
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT e.id, e.matricule, e.nom, e.prenom, e.iddept, d.nom AS departement, c.nom AS categorie " +
                        "FROM employe e " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "LEFT JOIN categoriepersonnel c ON c.id = e.idcategorie " +
                        "WHERE e.matricule = ? AND c.nom IN ('Cadre', 'Dirigeant')",
                matricule
        );

        if (rows.isEmpty()) {
            response.put("success", false);
            response.put("message", "Matricule ou mot de passe incorrect ou non autorisé");
            return response;
        }

        Map<String, Object> manager = rows.get(0);
        response.put("success", true);
        response.put("message", "Connexion manager réussie");
        response.put("manager", manager);
        return response;
    }

    public List<Map<String, Object>> getDemandesEquipe(Integer idManager, String statut, Integer annee, Integer idTypeConge) {
        List<Map<String, Object>> result = new ArrayList<>();

        Integer idDept = jdbcTemplate.queryForObject(
                "SELECT iddept FROM employe WHERE id = ?",
                Integer.class,
                idManager
        );
        if (idDept == null) {
            return result;
        }

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT d.id, d.datedemande, d.datedebut, d.datefin, d.nombrejoursouvres, d.motif, ");
        sql.append("e.id AS idemploye, e.nom, e.prenom, e.matricule, ");
        sql.append("tc.id AS idtypeconge, tc.libelle AS typeconge, sd.nom AS statut ");
        sql.append("FROM demandeconge d ");
        sql.append("JOIN employe e ON e.id = d.idemploye ");
        sql.append("JOIN typeconge tc ON tc.id = d.idtypeconge ");
        sql.append("JOIN statutdemande sd ON sd.id = d.idstatut ");
        sql.append("WHERE e.iddept = ? ");
        params.add(idDept);

        if (statut != null && !statut.isBlank()) {
            sql.append("AND sd.nom = ? ");
            params.add(statut);
        } else {
            sql.append("AND sd.nom IN ('En attente manager', 'Validé manager', 'Refusé manager', 'En attente RH') ");
        }

        if (annee != null) {
            sql.append("AND EXTRACT(YEAR FROM d.datedebut) = ? ");
            params.add(annee);
        }

        if (idTypeConge != null) {
            sql.append("AND d.idtypeconge = ? ");
            params.add(idTypeConge);
        }

        sql.append("ORDER BY d.datedemande DESC");

        result = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return result;
    }

    public Map<String, Object> validerDemandeManager(Integer idManager, Integer idDemande) {
        Map<String, Object> response = new HashMap<>();

        Integer idDeptManager = jdbcTemplate.queryForObject(
                "SELECT e.iddept FROM employe e " +
                        "JOIN categoriepersonnel c ON c.id = e.idcategorie " +
                        "WHERE e.id = ? AND c.nom IN ('Cadre', 'Dirigeant')",
                Integer.class,
                idManager
        );
        if (idDeptManager == null) {
            response.put("success", false);
            response.put("message", "Manager non autorisé");
            return response;
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT d.id, d.idemploye, d.idstatut, e.iddept " +
                        "FROM demandeconge d " +
                        "JOIN employe e ON e.id = d.idemploye " +
                        "WHERE d.id = ?",
                idDemande
        );
        if (rows.isEmpty()) {
            response.put("success", false);
            response.put("message", "Demande introuvable");
            return response;
        }

        Map<String, Object> row = rows.get(0);
        Integer idDeptDemande = row.get("iddept") != null ? Integer.valueOf(row.get("iddept").toString()) : null;
        Integer idStatutActuel = row.get("idstatut") != null ? Integer.valueOf(row.get("idstatut").toString()) : null;

        if (idDeptDemande == null || !idDeptManager.equals(idDeptDemande)) {
            response.put("success", false);
            response.put("message", "Demande hors périmètre du manager");
            return response;
        }

        Integer idEnAttenteManager = jdbcTemplate.queryForObject(
                "SELECT id FROM statutdemande WHERE nom = 'En attente manager' LIMIT 1",
                Integer.class
        );
        if (!idEnAttenteManager.equals(idStatutActuel)) {
            response.put("success", false);
            response.put("message", "La demande n'est pas en attente du manager");
            return response;
        }

        Integer idValideManager = jdbcTemplate.queryForObject(
                "SELECT id FROM statutdemande WHERE nom = 'Validé manager' LIMIT 1",
                Integer.class
        );
        Integer idEnAttenteRh = jdbcTemplate.queryForObject(
                "SELECT id FROM statutdemande WHERE nom = 'En attente RH' LIMIT 1",
                Integer.class
        );

        jdbcTemplate.update(
                "UPDATE demandeconge SET idstatut = ?, idvalideur = ? WHERE id = ?",
                idEnAttenteRh,
                idManager,
                idDemande
        );

        jdbcTemplate.update(
                "INSERT INTO historiquedemande (iddemandeconge, idstatut, idemploye, commentaire) VALUES (?, ?, ?, ?)",
                idDemande,
                idValideManager,
                idManager,
                "Demande validée par le manager"
        );

        jdbcTemplate.update(
                "INSERT INTO historiquedemande (iddemandeconge, idstatut, idemploye, commentaire) VALUES (?, ?, ?, ?)",
                idDemande,
                idEnAttenteRh,
                idManager,
                "Demande transmise à la RH"
        );

        response.put("success", true);
        response.put("message", "Demande validée et transmise à la RH");
        return response;
    }

    public Map<String, Object> refuserDemandeManager(Integer idManager, Integer idDemande) {
        Map<String, Object> response = new HashMap<>();

        Integer idDeptManager = jdbcTemplate.queryForObject(
                "SELECT e.iddept FROM employe e " +
                        "JOIN categoriepersonnel c ON c.id = e.idcategorie " +
                        "WHERE e.id = ? AND c.nom IN ('Cadre', 'Dirigeant')",
                Integer.class,
                idManager
        );
        if (idDeptManager == null) {
            response.put("success", false);
            response.put("message", "Manager non autorisé");
            return response;
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT d.id, d.idemploye, d.idstatut, e.iddept " +
                        "FROM demandeconge d " +
                        "JOIN employe e ON e.id = d.idemploye " +
                        "WHERE d.id = ?",
                idDemande
        );
        if (rows.isEmpty()) {
            response.put("success", false);
            response.put("message", "Demande introuvable");
            return response;
        }

        Map<String, Object> row = rows.get(0);
        Integer idDeptDemande = row.get("iddept") != null ? Integer.valueOf(row.get("iddept").toString()) : null;
        Integer idStatutActuel = row.get("idstatut") != null ? Integer.valueOf(row.get("idstatut").toString()) : null;

        if (idDeptDemande == null || !idDeptManager.equals(idDeptDemande)) {
            response.put("success", false);
            response.put("message", "Demande hors périmètre du manager");
            return response;
        }

        Integer idEnAttenteManager = jdbcTemplate.queryForObject(
                "SELECT id FROM statutdemande WHERE nom = 'En attente manager' LIMIT 1",
                Integer.class
        );
        if (!idEnAttenteManager.equals(idStatutActuel)) {
            response.put("success", false);
            response.put("message", "La demande n'est pas en attente du manager");
            return response;
        }

        Integer idRefuseManager = jdbcTemplate.queryForObject(
                "SELECT id FROM statutdemande WHERE nom = 'Refusé manager' LIMIT 1",
                Integer.class
        );

        jdbcTemplate.update(
                "UPDATE demandeconge SET idstatut = ?, idvalideur = ? WHERE id = ?",
                idRefuseManager,
                idManager,
                idDemande
        );

        jdbcTemplate.update(
                "INSERT INTO historiquedemande (iddemandeconge, idstatut, idemploye, commentaire) VALUES (?, ?, ?, ?)",
                idDemande,
                idRefuseManager,
                idManager,
                "Demande refusée par le manager"
        );

        response.put("success", true);
        response.put("message", "Demande refusée par le manager");
        return response;
    }

    public Map<String, Object> getDashboard(Integer idManager, Integer mois, Integer annee) {
        Map<String, Object> result = new HashMap<>();

        Integer idDept = jdbcTemplate.queryForObject(
                "SELECT e.iddept FROM employe e JOIN categoriepersonnel c ON c.id = e.idcategorie " +
                        "WHERE e.id = ? AND c.nom IN ('Cadre', 'Dirigeant')",
                Integer.class,
                idManager
        );
        if (idDept == null) {
            result.put("success", false);
            result.put("message", "Manager non autorisé ou sans département");
            return result;
        }

        // Récupérer le nom du département
        String deptName = jdbcTemplate.queryForObject(
                "SELECT nom FROM departement WHERE id = ?",
                String.class,
                idDept
        );

        // Performances des employés (on filtre la liste complète par département)
        List<Map<String, Object>> perfAll = dashboardService.getPerformanceEmployes(mois, annee);
        List<Map<String, Object>> perfDept = new ArrayList<>();
        if (perfAll != null) {
            for (Map<String, Object> row : perfAll) {
                Object dName = row.get("departement");
                if (dName != null && dName.toString().equals(deptName)) {
                    perfDept.add(row);
                }
            }
        }

        // Absenteisme du département (on réutilise le résumé RH et on filtre sur le département)
        Map<String, Object> rhSummary = dashboardService.getRhSummary(mois, annee);
        Map<String, Object> absDept = null;
        Object absListObj = rhSummary.get("absenteismeParDepartement");
        if (absListObj instanceof List<?>) {
            for (Object o : (List<?>) absListObj) {
                if (o instanceof Map<?,?> m) {
                    Object dName = m.get("departement");
                    if (dName != null && dName.toString().equals(deptName)) {
                        absDept = new HashMap<>();
                        for (Map.Entry<?,?> e : m.entrySet()) {
                            absDept.put(e.getKey().toString(), e.getValue());
                        }
                        break;
                    }
                }
            }
        }

        result.put("success", true);
        result.put("departement", deptName);
        result.put("performancesEmployes", perfDept);
        result.put("absenteisme", absDept);
        return result;
    }
}
