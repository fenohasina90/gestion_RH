package com.backend.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paie/parametres")
@CrossOrigin(origins = "*")
public class PaieParamController {

    private final JdbcTemplate jdbcTemplate;

    public PaieParamController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------- COTISATIONS (CNAPS / OSTIE) ----------

    @GetMapping("/cotisations")
    public ResponseEntity<?> getCotisations() {
        String sql = "SELECT * FROM parametrecotisation WHERE datefin IS NULL ORDER BY libelle";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/cotisations")
    public ResponseEntity<?> updateCotisation(@RequestBody Map<String, Object> body) {
        String libelle = (String) body.get("libelle");
        Number taux = (Number) body.get("taux");
        Number plafond = (Number) body.get("plafondsalarial");
        LocalDate dateEffet = LocalDate.now();

        // Clôturer l'ancien paramètre actif
        jdbcTemplate.update(
                "UPDATE parametrecotisation SET datefin = ? WHERE libelle = ? AND datefin IS NULL",
                dateEffet.minusDays(1), libelle
        );

        // Insérer le nouveau
        jdbcTemplate.update(
                "INSERT INTO parametrecotisation(libelle, taux, plafondsalarial, dateeffet, datefin) VALUES (?,?,?,?,NULL)",
                libelle,
                taux != null ? taux.doubleValue() : null,
                plafond != null ? plafond.doubleValue() : null,
                dateEffet
        );

        return getCotisations();
    }

    // ---------- IRSA (barème complet) ----------

    @GetMapping("/irsa")
    public ResponseEntity<?> getIrsaBrackets() {
        String sql = "SELECT * FROM parametre_irsa WHERE datefin IS NULL ORDER BY borne_min";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/irsa")
    public ResponseEntity<?> replaceIrsaBrackets(@RequestBody List<Map<String, Object>> brackets) {
        LocalDate today = LocalDate.now();

        // Clôturer les tranches actuelles
        jdbcTemplate.update("UPDATE parametre_irsa SET datefin = ? WHERE datefin IS NULL", today.minusDays(1));

        // Insérer les nouvelles tranches
        String insertSql = "INSERT INTO parametre_irsa(borne_min, borne_max, taux, dateeffet, datefin) VALUES (?,?,?,?,NULL)";
        for (Map<String, Object> b : brackets) {
            Number borneMin = (Number) b.get("borne_min");
            Number borneMax = (Number) b.get("borne_max");
            Number taux = (Number) b.get("taux");
            jdbcTemplate.update(
                    insertSql,
                    borneMin != null ? borneMin.doubleValue() : 0.0,
                    borneMax != null ? borneMax.doubleValue() : null,
                    taux != null ? taux.doubleValue() : 0.0,
                    today
            );
        }

        return getIrsaBrackets();
    }

    // ---------- HEURES SUP (coefficients) ----------

    @GetMapping("/heures-sup")
    public ResponseEntity<?> getHeuresSup() {
        String sql = "SELECT * FROM parametre_heures_sup WHERE datefin IS NULL ORDER BY code";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/heures-sup")
    public ResponseEntity<?> updateHeuresSup(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        String libelle = (String) body.get("libelle");
        Number coefficient = (Number) body.get("coefficient");
        LocalDate dateEffet = LocalDate.now();

        jdbcTemplate.update(
                "UPDATE parametre_heures_sup SET datefin = ? WHERE code = ? AND datefin IS NULL",
                dateEffet.minusDays(1), code
        );

        jdbcTemplate.update(
                "INSERT INTO parametre_heures_sup(code, libelle, coefficient, dateeffet, datefin) VALUES (?,?,?,?,NULL)",
                code,
                libelle,
                coefficient != null ? coefficient.doubleValue() : 1.30,
                dateEffet
        );

        return getHeuresSup();
    }

    // ---------- Types d'éléments et éléments variables ----------

    @GetMapping("/types-elements")
    public ResponseEntity<?> getTypesElements() {
        String sql = "SELECT id, libelle, estgain, estsoumiscotisation FROM typeelementpaie ORDER BY libelle";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/elements")
    public ResponseEntity<?> getElements(@RequestParam Integer idemploye,
                                         @RequestParam Integer mois,
                                         @RequestParam Integer annee) {
        String sql = "SELECT ev.id, ev.idemploye, ev.idtypeelementpaie, te.libelle, ev.montant, ev.mois, ev.annee, ev.commentaire, ev.dateenregistrement " +
                "FROM elementvariable ev JOIN typeelementpaie te ON te.id = ev.idtypeelementpaie " +
                "WHERE ev.idemploye = ? AND ev.mois = ? AND ev.annee = ? ORDER BY ev.dateenregistrement";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idemploye, mois, annee);
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/elements")
    public ResponseEntity<?> createElement(@RequestBody Map<String, Object> body) {
        Number idemploye = (Number) body.get("idemploye");
        Number idtype = (Number) body.get("idtypeelementpaie");
        Number montant = (Number) body.get("montant");
        Number mois = (Number) body.get("mois");
        Number annee = (Number) body.get("annee");
        String commentaire = (String) body.get("commentaire");

        jdbcTemplate.update(
                "INSERT INTO elementvariable(idemploye, idtypeelementpaie, montant, mois, annee, commentaire, dateenregistrement) " +
                        "VALUES (?,?,?,?,?,?, now())",
                idemploye != null ? idemploye.intValue() : null,
                idtype != null ? idtype.intValue() : null,
                montant != null ? montant.doubleValue() : 0.0,
                mois != null ? mois.intValue() : null,
                annee != null ? annee.intValue() : null,
                commentaire
        );

        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/elements/{id}")
    public ResponseEntity<?> deleteElement(@PathVariable Integer id) {
        jdbcTemplate.update("DELETE FROM elementvariable WHERE id = ?", id);
        Map<String, Object> result = new HashMap<>();
        result.put("status", "deleted");
        return ResponseEntity.ok(result);
    }
}
