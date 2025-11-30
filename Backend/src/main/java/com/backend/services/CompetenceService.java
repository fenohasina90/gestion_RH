package com.backend.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompetenceService {

    private final JdbcTemplate jdbcTemplate;

    public CompetenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getAllCompetences() {
        return jdbcTemplate.queryForList(
                "SELECT c.id, c.code, c.libelle, c.description, " +
                        "cc.id AS idcategorie, cc.libelle AS categorie " +
                        "FROM competence c " +
                        "LEFT JOIN categoriecompetence cc ON cc.id = c.idcategorie " +
                        "ORDER BY cc.libelle, c.libelle"
        );
    }

    public List<Map<String, Object>> getAllNiveaux() {
        return jdbcTemplate.queryForList(
                "SELECT id, code, libelle, ordre FROM niveaucompetence ORDER BY ordre"
        );
    }

    public List<Map<String, Object>> getCompetencesEmploye(Integer idEmploye) {
        return jdbcTemplate.queryForList(
                "SELECT ec.id, ec.idemploye, c.id AS idcompetence, c.code, c.libelle, c.description, " +
                        "nc.id AS idniveau, nc.code AS code_niveau, nc.libelle AS libelle_niveau, nc.ordre AS ordre_niveau, " +
                        "ec.source, ec.dateevaluation, ec.commentaire " +
                        "FROM employecompetence ec " +
                        "JOIN competence c ON c.id = ec.idcompetence " +
                        "LEFT JOIN niveaucompetence nc ON nc.id = ec.idniveau_actuel " +
                        "WHERE ec.idemploye = ? " +
                        "ORDER BY c.libelle",
                idEmploye
        );
    }

    public List<Map<String, Object>> getCompetencesProfil(Integer idProfil) {
        return jdbcTemplate.queryForList(
                "SELECT pc.id, pc.idprofil, c.id AS idcompetence, c.code, c.libelle, c.description, " +
                        "nc.id AS idniveau_cible, nc.code AS code_niveau_cible, nc.libelle AS libelle_niveau_cible, nc.ordre AS ordre_niveau_cible, " +
                        "pc.poids, pc.estobligatoire " +
                        "FROM profilcompetence pc " +
                        "JOIN competence c ON c.id = pc.idcompetence " +
                        "LEFT JOIN niveaucompetence nc ON nc.id = pc.idniveau_cible " +
                        "WHERE pc.idprofil = ? " +
                        "ORDER BY pc.estobligatoire DESC, c.libelle",
                idProfil
        );
    }

    public Map<String, Object> getMatchingEmployeProfil(Integer idEmploye, Integer idProfil) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT " +
                        "c.id AS idcompetence, c.code, c.libelle, c.description, " +
                        "npc.id AS idniveau_cible, npc.code AS code_niveau_cible, npc.libelle AS libelle_niveau_cible, npc.ordre AS ordre_niveau_cible, " +
                        "nec.id AS idniveau_actuel, nec.code AS code_niveau_actuel, nec.libelle AS libelle_niveau_actuel, nec.ordre AS ordre_niveau_actuel, " +
                        "pc.poids, pc.estobligatoire " +
                        "FROM profilcompetence pc " +
                        "JOIN competence c ON c.id = pc.idcompetence " +
                        "LEFT JOIN niveaucompetence npc ON npc.id = pc.idniveau_cible " +
                        "LEFT JOIN employecompetence ec ON ec.idemploye = ? AND ec.idcompetence = c.id " +
                        "LEFT JOIN niveaucompetence nec ON nec.id = ec.idniveau_actuel " +
                        "WHERE pc.idprofil = ? " +
                        "ORDER BY pc.estobligatoire DESC, c.libelle",
                idEmploye, idProfil
        );

        for (Map<String, Object> row : rows) {
            int ordreCible = toInt(row.get("ordre_niveau_cible"));
            int ordreActuel = toInt(row.get("ordre_niveau_actuel"));
            int ecart = ordreActuel - ordreCible;

            String statut;
            if (ordreActuel == 0 && ordreCible > 0) {
                statut = "Manquante";
            } else if (ecart >= 0) {
                statut = "OK";
            } else if (ecart == -1) {
                statut = "A renforcer";
            } else {
                statut = "Critique";
            }

            row.put("ecart", ecart);
            row.put("statut", statut);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("competences", rows);
        return result;
    }

    public List<Map<String, Object>> getSuggestionsFormation(Integer idEmploye, Integer idProfil) {
        return jdbcTemplate.queryForList(
                "SELECT DISTINCT " +
                        "f.id AS idformation, f.code AS code_formation, f.libelle AS libelle_formation, " +
                        "f.description AS description_formation, f.duree_heures, f.fournisseur, f.cout, " +
                        "c.id AS idcompetence, c.code AS code_competence, c.libelle AS libelle_competence, " +
                        "npc.code AS code_niveau_cible, npc.libelle AS libelle_niveau_cible, npc.ordre AS ordre_niveau_cible, " +
                        "nec.code AS code_niveau_actuel, nec.libelle AS libelle_niveau_actuel, nec.ordre AS ordre_niveau_actuel " +
                        "FROM profilcompetence pc " +
                        "JOIN competence c ON c.id = pc.idcompetence " +
                        "LEFT JOIN niveaucompetence npc ON npc.id = pc.idniveau_cible " +
                        "LEFT JOIN employecompetence ec ON ec.idemploye = ? AND ec.idcompetence = c.id " +
                        "LEFT JOIN niveaucompetence nec ON nec.id = ec.idniveau_actuel " +
                        "JOIN formationcompetence fc ON fc.idcompetence = c.id " +
                        "JOIN formation f ON f.id = fc.idformation " +
                        "WHERE pc.idprofil = ? " +
                        "AND (COALESCE(nec.ordre, 0) < COALESCE(npc.ordre, 0)) " +
                        "ORDER BY f.code, c.libelle",
                idEmploye, idProfil
        );
    }

    public Map<String, Object> attribuerApresFormation(Integer idEmploye, Integer idFormation) {
        Integer nbCibles = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM formationcompetence WHERE idformation = ?",
                Integer.class,
                idFormation
        );
        if (nbCibles == null || nbCibles == 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("updated", 0);
            resp.put("message", "Aucune compétence cible pour cette formation");
            return resp;
        }

        int updated = jdbcTemplate.update(
                "INSERT INTO employecompetence (idemploye, idcompetence, idniveau_actuel, source, dateevaluation, commentaire) " +
                        "SELECT ?, fc.idcompetence, fc.idniveau_cible, 'formation', CURRENT_DATE, " +
                        "       'Compétence mise à jour après formation ' || f.code " +
                        "FROM formationcompetence fc " +
                        "JOIN formation f ON f.id = fc.idformation " +
                        "WHERE fc.idformation = ? " +
                        "ON CONFLICT (idemploye, idcompetence) DO UPDATE SET " +
                        "  idniveau_actuel = EXCLUDED.idniveau_actuel, " +
                        "  source = EXCLUDED.source, " +
                        "  dateevaluation = EXCLUDED.dateevaluation, " +
                        "  commentaire = EXCLUDED.commentaire",
                idEmploye, idFormation
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("updated", updated);
        resp.put("message", "Compétences attribuées / mises à jour après formation");
        return resp;
    }

    private int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            return Integer.parseInt(v.toString());
        } catch (Exception e) {
            return 0;
        }
    }
}
