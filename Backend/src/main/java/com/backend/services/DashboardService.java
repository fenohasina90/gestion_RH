package com.backend.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getRhSummary(Integer mois, Integer annee) {
        LocalDate now = LocalDate.now();
        int m = (mois != null) ? mois : now.getMonthValue();
        int y = (annee != null) ? annee : now.getYear();

        Map<String, Object> result = new HashMap<>();

        Integer totalEmployes = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employe",
                Integer.class
        );
        result.put("totalEmployes", totalEmployes);

        List<Map<String, Object>> effectifParDepartement = jdbcTemplate.queryForList(
                "SELECT COALESCE(d.nom,'Non affecté') AS label, COUNT(e.id) AS value " +
                        "FROM employe e " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "GROUP BY d.nom ORDER BY d.nom"
        );
        result.put("effectifParDepartement", effectifParDepartement);

        List<Map<String, Object>> effectifParCategorie = jdbcTemplate.queryForList(
                "SELECT COALESCE(c.nom,'Non défini') AS label, COUNT(e.id) AS value " +
                        "FROM employe e " +
                        "LEFT JOIN categoriepersonnel c ON c.id = e.idcategorie " +
                        "GROUP BY c.nom ORDER BY c.nom"
        );
        result.put("effectifParCategorie", effectifParCategorie);

        List<Map<String, Object>> effectifParGenre = jdbcTemplate.queryForList(
                "SELECT COALESCE(g.libelle, 'Non renseigné') AS label, COUNT(e.id) AS value " +
                        "FROM employe e " +
                        "LEFT JOIN genre g ON g.id = e.idgenre " +
                        "GROUP BY g.libelle " +
                        "ORDER BY g.libelle"
        );
        result.put("effectifParGenre", effectifParGenre);

        List<Map<String, Object>> effectifParTypeContrat = jdbcTemplate.queryForList(
                "SELECT tc.libelle AS label, COUNT(c.id) AS value " +
                        "FROM contrat c " +
                        "JOIN typecontrat tc ON tc.id = c.idtypecontrat " +
                        "JOIN statutcontrat sc ON sc.id = c.idstatut " +
                        "WHERE lower(sc.nom) = 'en cours' " +
                        "GROUP BY tc.libelle ORDER BY tc.libelle"
        );
        result.put("effectifParTypeContrat", effectifParTypeContrat);

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

        List<Map<String, Object>> turnoverParDepartement = jdbcTemplate.queryForList(
                "SELECT COALESCE(d.nom,'Non affecté') AS departement, " +
                        "COUNT(*) FILTER (WHERE EXTRACT(YEAR FROM e.datedembauche) = ?) AS embauches, " +
                        "COUNT(*) FILTER (WHERE c.datefin IS NOT NULL AND EXTRACT(YEAR FROM c.datefin) = ?) AS departures, " +
                        "COUNT(DISTINCT e.id) AS effectif, " +
                        "CASE WHEN COUNT(DISTINCT e.id) = 0 THEN 0 " +
                        "     ELSE ROUND( (COUNT(*) FILTER (WHERE c.datefin IS NOT NULL AND EXTRACT(YEAR FROM c.datefin) = ?) * 100.0) / COUNT(DISTINCT e.id), 2) END AS taux_departements " +
                        "FROM employe e " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "LEFT JOIN contrat c ON c.idemploye = e.id " +
                        "GROUP BY d.nom " +
                        "ORDER BY d.nom",
                y, y, y
        );
        result.put("turnoverParDepartement", turnoverParDepartement);

        Map<String, Object> absenteisme = new HashMap<>();
        Map<String, Object> absRow = jdbcTemplate.queryForMap(
                "SELECT " +
                        "COALESCE(SUM(CASE WHEN df.estabsent THEN 8 ELSE 0 END), 0) AS heures_absence, " +
                        "COUNT(*) FILTER (WHERE df.estabsent) AS nb_absences, " +
                        "COUNT(DISTINCT ft.idemploye) FILTER (WHERE df.estabsent) AS employes_touches " +
                        "FROM feuilletemps ft " +
                        "JOIN detailfeuilletemps df ON df.idfeuilletemps = ft.id " +
                        "WHERE ft.mois = ? AND ft.annee = ?",
                m, y
        );
        absenteisme.put("heuresAbsence", absRow.get("heures_absence"));
        absenteisme.put("nbAbsences", absRow.get("nb_absences"));
        absenteisme.put("employesTouches", absRow.get("employes_touches"));
        result.put("absenteisme", absenteisme);

        List<Map<String, Object>> absenteismeParDepartement = jdbcTemplate.queryForList(
                "SELECT COALESCE(d.nom,'Non affecté') AS departement, " +
                        "COALESCE(SUM(CASE WHEN df.estabsent THEN 8 ELSE 0 END), 0) AS heures_absence, " +
                        "COUNT(*) FILTER (WHERE df.estabsent) AS nb_absences, " +
                        "COUNT(DISTINCT ft.idemploye) FILTER (WHERE df.estabsent) AS employes_touches, " +
                        "COALESCE(SUM(ft.jourstravailles),0) AS jours_trav, " +
                        "COALESCE(SUM(ft.absences),0) AS jours_abs, " +
                        "CASE WHEN (COALESCE(SUM(ft.jourstravailles),0) + COALESCE(SUM(ft.absences),0)) = 0 THEN 0 " +
                        "     ELSE ROUND(COALESCE(SUM(ft.absences),0) * 100.0 / (COALESCE(SUM(ft.jourstravailles),0) + COALESCE(SUM(ft.absences),0)), 2) END AS taux_absence " +
                        "FROM feuilletemps ft " +
                        "JOIN employe e ON e.id = ft.idemploye " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "JOIN detailfeuilletemps df ON df.idfeuilletemps = ft.id " +
                        "WHERE ft.mois = ? AND ft.annee = ? " +
                        "GROUP BY d.nom " +
                        "ORDER BY d.nom",
                m, y
        );
        result.put("absenteismeParDepartement", absenteismeParDepartement);

        Double ancienneteMoyenne = jdbcTemplate.queryForObject(
                "SELECT COALESCE(AVG(EXTRACT(YEAR FROM age(current_date, datedembauche))),0) FROM employe WHERE datedembauche IS NOT NULL",
                Double.class
        );
        result.put("ancienneteMoyenne", ancienneteMoyenne);

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

        List<Map<String, Object>> congesFormation = jdbcTemplate.queryForList(
                "SELECT e.matricule, e.nom, e.prenom, d.datedebut, d.datefin, " +
                        "(d.datefin - d.datedebut + 1) AS jours, tc.libelle AS typeconge " +
                        "FROM demandeconge d " +
                        "JOIN employe e ON e.id = d.idemploye " +
                        "JOIN typeconge tc ON tc.id = d.idtypeconge " +
                        "JOIN statutdemande sd ON sd.id = d.idstatut " +
                        "WHERE EXTRACT(YEAR FROM d.datedebut) = ? " +
                        "AND lower(tc.libelle) LIKE '%formation%' " +
                        "AND (lower(sd.nom) = 'Validé manager' OR lower(sd.nom) = 'Validé manager')",
                y
        );
        result.put("congesFormation", congesFormation);

        return result;
    }

    public List<Map<String, Object>> getPerformanceEmployes(Integer mois, Integer annee) {
        LocalDate now = LocalDate.now();
        int m = (mois != null) ? mois : now.getMonthValue();
        int y = (annee != null) ? annee : now.getYear();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT e.id, e.matricule, e.nom, e.prenom, COALESCE(d.nom,'Non affecté') AS departement, " +
                        "COALESCE(SUM(ft.jourstravailles),0) AS jours_trav, " +
                        "COALESCE(SUM(ft.heuressupplementaires),0) AS heures_sup, " +
                        "COALESCE(SUM(ft.absences),0) AS jours_abs, " +
                        "COALESCE(SUM(ft.retards),0) AS retards " +
                        "FROM employe e " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "LEFT JOIN feuilletemps ft ON ft.idemploye = e.id AND ft.mois = ? AND ft.annee = ? " +
                        "GROUP BY e.id, e.matricule, e.nom, e.prenom, d.nom " +
                        "ORDER BY d.nom, e.nom, e.prenom",
                m, y
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>(row);

            double joursTrav = toDouble(row.get("jours_trav"));
            double joursAbs = toDouble(row.get("jours_abs"));
            double retards = toDouble(row.get("retards"));
            double heuresSup = toDouble(row.get("heures_sup"));

            double scorePonctualite = 100.0 - (joursAbs * 5.0) - (retards * 2.0);
            if (scorePonctualite < 0) scorePonctualite = 0;

            double scoreProductiviteBase = joursTrav + (heuresSup * 0.5);
            double scoreProductivite = (scoreProductiviteBase / (22.0 + 20.0 * 0.5)) * 100.0;
            if (scoreProductivite > 100) scoreProductivite = 100;

            double scoreGlobal = (0.6 * scorePonctualite) + (0.4 * scoreProductivite);

            item.put("scorePonctualite", scorePonctualite);
            item.put("scoreProductivite", scoreProductivite);
            item.put("scoreGlobal", scoreGlobal);

            result.add(item);
        }

        return result;
    }

    public List<Map<String, Object>> getPerformanceDepartements(Integer mois, Integer annee) {
        List<Map<String, Object>> employes = getPerformanceEmployes(mois, annee);

        Map<String, Map<String, Object>> agg = new HashMap<>();

        if (employes != null) {
            for (Map<String, Object> e : employes) {
                String dept = (String) e.getOrDefault("departement", "Non affecté");
                Map<String, Object> d = agg.computeIfAbsent(dept, k -> {
                    Map<String, Object> m2 = new HashMap<>();
                    m2.put("departement", k);
                    m2.put("nbEmployes", 0);
                    m2.put("sumPonctualite", 0.0);
                    m2.put("sumProductivite", 0.0);
                    m2.put("sumGlobal", 0.0);
                    return m2;
                });

                int nbEmp = (int) d.get("nbEmployes") + 1;
                double sumP = toDouble(d.get("sumPonctualite")) + toDouble(e.get("scorePonctualite"));
                double sumProd = toDouble(d.get("sumProductivite")) + toDouble(e.get("scoreProductivite"));
                double sumG = toDouble(d.get("sumGlobal")) + toDouble(e.get("scoreGlobal"));

                d.put("nbEmployes", nbEmp);
                d.put("sumPonctualite", sumP);
                d.put("sumProductivite", sumProd);
                d.put("sumGlobal", sumG);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> d : agg.values()) {
            int nbEmp = (int) d.get("nbEmployes");
            double avgP = nbEmp > 0 ? toDouble(d.get("sumPonctualite")) / nbEmp : 0.0;
            double avgProd = nbEmp > 0 ? toDouble(d.get("sumProductivite")) / nbEmp : 0.0;
            double avgG = nbEmp > 0 ? toDouble(d.get("sumGlobal")) / nbEmp : 0.0;

            Map<String, Object> out = new HashMap<>();
            out.put("departement", d.get("departement"));
            out.put("nbEmployes", nbEmp);
            out.put("scorePonctualiteMoyen", avgP);
            out.put("scoreProductiviteMoyen", avgProd);
            out.put("scoreGlobalMoyen", avgG);

            result.add(out);
        }

        return result;
    }

    private double toDouble(Object v) {
        if (v == null) return 0.0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try {
            return Double.parseDouble(v.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
