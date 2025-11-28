package com.backend.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;

    public DashboardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/rh")
    public ResponseEntity<?> getRhSummary(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee
    ) {
        LocalDate now = LocalDate.now();
        int m = (mois != null) ? mois : now.getMonthValue();
        int y = (annee != null) ? annee : now.getYear();

        Map<String, Object> result = new HashMap<>();

        // Effectifs globaux
        Integer totalEmployes = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employe",
                Integer.class
        );
        result.put("totalEmployes", totalEmployes);

        // Effectifs par département
        List<Map<String, Object>> effectifParDepartement = jdbcTemplate.queryForList(
                "SELECT COALESCE(d.nom,'Non affecté') AS label, COUNT(e.id) AS value " +
                        "FROM employe e " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "GROUP BY d.nom ORDER BY d.nom"
        );
        result.put("effectifParDepartement", effectifParDepartement);

        // Effectifs par catégorie
        List<Map<String, Object>> effectifParCategorie = jdbcTemplate.queryForList(
                "SELECT COALESCE(c.nom,'Non défini') AS label, COUNT(e.id) AS value " +
                        "FROM employe e " +
                        "LEFT JOIN categoriepersonnel c ON c.id = e.idcategorie " +
                        "GROUP BY c.nom ORDER BY c.nom"
        );
        result.put("effectifParCategorie", effectifParCategorie);

        // Effectifs par genre (Homme / Femme / Autre)
        List<Map<String, Object>> effectifParGenre = jdbcTemplate.queryForList(
                "SELECT COALESCE(g.libelle, 'Non renseigné') AS label, COUNT(e.id) AS value " +
                "FROM employe e " +
                "LEFT JOIN genre g ON g.id = e.idgenre " +
                "GROUP BY g.libelle " +
                "ORDER BY g.libelle"
        );
        result.put("effectifParGenre", effectifParGenre);

        // Effectifs par type de contrat (contrats en cours)
        List<Map<String, Object>> effectifParTypeContrat = jdbcTemplate.queryForList(
                "SELECT tc.libelle AS label, COUNT(c.id) AS value " +
                        "FROM contrat c " +
                        "JOIN typecontrat tc ON tc.id = c.idtypecontrat " +
                        "JOIN statutcontrat sc ON sc.id = c.idstatut " +
                        "WHERE lower(sc.nom) = 'en cours' " +
                        "GROUP BY tc.libelle ORDER BY tc.libelle"
        );
        result.put("effectifParTypeContrat", effectifParTypeContrat);

        // Répartition par tranches d'âge
        List<Map<String, Object>> effectifParAge = jdbcTemplate.queryForList(
                "SELECT " +
                        "CASE " +
                        " WHEN age_ < 25 THEN '<25 ans' " +
                        " WHEN age_ BETWEEN 25 AND 34 THEN '25-34 ans' " +
                        " WHEN age_ BETWEEN 35 AND 44 THEN '35-44 ans' " +
                        " WHEN age_ BETWEEN 45 AND 54 THEN '45-54 ans' " +
                        " ELSE '55+ ans' END AS label, " +
                        "COUNT(*) AS value " +
                        "FROM (SELECT EXTRACT(YEAR FROM age(current_date, datenaissance))::int AS age_ " +
                        "      FROM employe WHERE datenaissance IS NOT NULL) AS sub " +
                        "GROUP BY label ORDER BY label"
        );
        result.put("effectifParAge", effectifParAge);

        // Turnover simple : embauches / départs sur l'année
        Integer embauches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employe WHERE EXTRACT(YEAR FROM datedembauche) = ?",
                Integer.class, y
        );
        Integer departContrats = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM contrat WHERE datefin IS NOT NULL AND EXTRACT(YEAR FROM datefin) = ?",
                Integer.class, y
        );
        Map<String, Object> turnover = new HashMap<>();
        turnover.put("embauches", embauches);
        turnover.put("departures", departContrats);
        double tauxTurnover = (totalEmployes != null && totalEmployes > 0)
                ? (departContrats * 100.0 / totalEmployes)
                : 0.0;
        turnover.put("taux", tauxTurnover);
        result.put("turnover", turnover);

        // Absentéisme sur le mois donné
        Map<String, Object> absenteisme = new HashMap<>();
        Double heuresAbsence = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(nombreheures),0) FROM absence " +
                        "WHERE EXTRACT(MONTH FROM datedebut) = ? AND EXTRACT(YEAR FROM datedebut) = ?",
                Double.class, m, y
        );
        Integer nbAbsences = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM absence " +
                        "WHERE EXTRACT(MONTH FROM datedebut) = ? AND EXTRACT(YEAR FROM datedebut) = ?",
                Integer.class, m, y
        );
        Integer employesAvecAbsence = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT idemploye) FROM absence " +
                        "WHERE EXTRACT(MONTH FROM datedebut) = ? AND EXTRACT(YEAR FROM datedebut) = ?",
                Integer.class, m, y
        );
        absenteisme.put("heuresAbsence", heuresAbsence);
        absenteisme.put("nbAbsences", nbAbsences);
        absenteisme.put("employesTouches", employesAvecAbsence);
        result.put("absenteisme", absenteisme);

        // Ancienneté moyenne
        Double ancienneteMoyenne = jdbcTemplate.queryForObject(
                "SELECT COALESCE(AVG(EXTRACT(YEAR FROM age(current_date, datedembauche))),0) FROM employe WHERE datedembauche IS NOT NULL",
                Double.class
        );
        result.put("ancienneteMoyenne", ancienneteMoyenne);

        // Alertes fin de contrat (30 jours à venir)
        List<Map<String, Object>> alertesFinContrat = jdbcTemplate.queryForList(
                "SELECT e.matricule, e.nom, e.prenom, c.datefin, tc.libelle AS typecontrat " +
                        "FROM contrat c " +
                        "JOIN employe e ON e.id = c.idemploye " +
                        "JOIN statutcontrat sc ON sc.id = c.idstatut " +
                        "LEFT JOIN typecontrat tc ON tc.id = c.idtypecontrat " +
                        "WHERE c.datefin IS NOT NULL " +
                        "AND c.datefin BETWEEN current_date AND current_date + INTERVAL '30 days' " +
                        "AND lower(sc.nom) <> 'terminé'",
                new Object[]{}
        );
        result.put("alertesFinContrat", alertesFinContrat);

        // Alertes congés non pris (solde > 0 pour l'année en cours)
        List<Map<String, Object>> alertesCongesNonPris = jdbcTemplate.queryForList(
                "SELECT e.matricule, e.nom, e.prenom, scg.annee, scg.joursrestants, tcg.libelle AS typeconge " +
                        "FROM soldeconge scg " +
                        "JOIN employe e ON e.id = scg.idemploye " +
                        "JOIN typeconge tcg ON tcg.id = scg.idtypeconge " +
                        "WHERE scg.annee = ? AND scg.joursrestants > 0 " +
                        "ORDER BY scg.joursrestants DESC",
                y
        );
        result.put("alertesCongesNonPris", alertesCongesNonPris);

        // "Budget" formation : total jours de congé formation sur l'année
        List<Map<String, Object>> congesFormation = jdbcTemplate.queryForList(
                "SELECT e.matricule, e.nom, e.prenom, d.datedebut, d.datefin, " +
                        "(d.datefin - d.datedebut + 1) AS jours, tc.libelle AS typeconge " +
                        "FROM demandeconge d " +
                        "JOIN employe e ON e.id = d.idemploye " +
                        "JOIN typeconge tc ON tc.id = d.idtypeconge " +
                        "JOIN statutdemande sd ON sd.id = d.idstatut " +
                        "WHERE EXTRACT(YEAR FROM d.datedebut) = ? " +
                        "AND lower(tc.libelle) LIKE '%formation%' " +
                        "AND (lower(sd.nom) = 'validée' OR lower(sd.nom) = 'validee')",
                y
        );
        result.put("congesFormation", congesFormation);

        return ResponseEntity.ok(result);
    }
}
