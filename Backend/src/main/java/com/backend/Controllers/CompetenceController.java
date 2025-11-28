package com.backend.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/competences")
@CrossOrigin(origins = "*")
public class CompetenceController {

    private final JdbcTemplate jdbcTemplate;

    public CompetenceController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Liste des compétences avec leur catégorie.
     */
    @GetMapping
    public ResponseEntity<?> getAllCompetences() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT c.id, c.code, c.libelle, c.description, " +
                        "cc.id AS idcategorie, cc.libelle AS categorie " +
                        "FROM competence c " +
                        "LEFT JOIN categoriecompetence cc ON cc.id = c.idcategorie " +
                        "ORDER BY cc.libelle, c.libelle"
        );
        return ResponseEntity.ok(rows);
    }

    /**
     * Liste des niveaux de compétences.
     */
    @GetMapping("/niveaux")
    public ResponseEntity<?> getAllNiveaux() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, code, libelle, ordre FROM niveaucompetence ORDER BY ordre"
        );
        return ResponseEntity.ok(rows);
    }

    /**
     * Compétences d'un employé (niveau actuel, libellés, etc.).
     */
    @GetMapping("/employe")
    public ResponseEntity<?> getCompetencesEmploye(@RequestParam("idemploye") Integer idEmploye) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
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
        return ResponseEntity.ok(rows);
    }

    /**
     * Compétences attendues pour un profil.
     */
    @GetMapping("/profil")
    public ResponseEntity<?> getCompetencesProfil(@RequestParam("idprofil") Integer idProfil) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
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
        return ResponseEntity.ok(rows);
    }

    /**
     * Matching compétences employé / profil : liste les compétences du profil avec
     * niveau attendu, niveau actuel de l'employé, écart et statut (OK / A renforcer / Manquante).
     */
    @GetMapping("/matching")
    public ResponseEntity<?> getMatchingEmployeProfil(@RequestParam("idemploye") Integer idEmploye,
                                                      @RequestParam("idprofil") Integer idProfil) {
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

        // Post-traitement pour calculer l'écart et un statut lisible
        for (Map<String, Object> row : rows) {
            int ordreCible = toInt(row.get("ordre_niveau_cible"));
            int ordreActuel = toInt(row.get("ordre_niveau_actuel"));
            int ecart = ordreActuel - ordreCible; // positif si au-dessus du niveau cible

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
        return ResponseEntity.ok(result);
    }

    /**
     * Suggestions de formations pour combler les écarts entre un employé et un profil.
     * On cible les compétences où l'employé est en dessous du niveau attendu,
     * et on propose les formations liées à ces compétences.
     */
    @GetMapping("/suggestions-formation")
    public ResponseEntity<?> getSuggestionsFormation(@RequestParam("idemploye") Integer idEmploye,
                                                     @RequestParam("idprofil") Integer idProfil) {
        // Sous-requête de matching similaire à /matching, filtrée sur les écarts négatifs
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
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
                        // employé en dessous du niveau cible (ou pas de niveau) et formation visant au moins le niveau cible
                        "AND (COALESCE(nec.ordre, 0) < COALESCE(npc.ordre, 0)) " +
                        "ORDER BY f.code, c.libelle",
                idEmploye, idProfil
        );

        return ResponseEntity.ok(rows);
    }

    /**
     * Attribuer automatiquement les compétences ciblées par une formation
     * à un employé qui a terminé cette formation.
     *
     * Pour chaque ligne de formationcompetence liée à la formation, on crée ou met
     * à jour la ligne employecompetence correspondante avec le niveau cible.
     */
    @PostMapping("/attribuer-apres-formation")
    public ResponseEntity<?> attribuerApresFormation(@RequestParam("idemploye") Integer idEmploye,
                                                     @RequestParam("idformation") Integer idFormation) {

        // Vérifier qu'il existe au moins une compétence ciblée par cette formation
        Integer nbCibles = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM formationcompetence WHERE idformation = ?",
                Integer.class,
                idFormation
        );
        if (nbCibles == null || nbCibles == 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("updated", 0);
            resp.put("message", "Aucune compétence cible pour cette formation");
            return ResponseEntity.ok(resp);
        }

        // Attribuer / mettre à jour les compétences via INSERT ... ON CONFLICT
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
        return ResponseEntity.ok(resp);
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
