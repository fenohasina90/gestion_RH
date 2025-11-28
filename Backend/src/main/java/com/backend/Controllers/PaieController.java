package com.backend.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paie")
public class PaieController {

    private final JdbcTemplate jdbcTemplate;

    public PaieController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/bulletin")
    public ResponseEntity<?> getPayslip(
            @RequestParam String matricule,
            @RequestParam Integer mois,
            @RequestParam Integer annee
    ) {
        String sql = "SELECT * FROM v_bulletin_paie_complet WHERE matricule = ? AND mois = ? AND annee = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, matricule, mois, annee);
        if (rows.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rows.get(0));
    }

    @GetMapping("/etat")
    public ResponseEntity<?> getEtatPaie(
            @RequestParam Integer mois,
            @RequestParam Integer annee
    ) {
        String sql = "SELECT * FROM v_bulletin_paie_complet WHERE mois = ? AND annee = ? ORDER BY departement, nom_complet";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, mois, annee);
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/etat/export")
    public ResponseEntity<String> exportEtatPaieCsv(
            @RequestParam Integer mois,
            @RequestParam Integer annee
    ) {
        String sql = "SELECT v.*, e.datedembauche, e.nombreenfants " +
                "FROM v_bulletin_paie_complet v " +
                "JOIN employe e ON e.id = v.employe_id " +
                "WHERE v.mois = ? AND v.annee = ? " +
                "ORDER BY v.departement, v.nom_complet";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, mois, annee);

        String[] headers = new String[]{
                "DATE", "NBRE", "NUM_MATR", "NUM_CNAPS", "NOM_PRENOMS", "DATE_EMBAUCHE",
                "ABSENCE_MOIS", "CAT", "FONCTION", "SALAIRE_BASE", "RETENUES_ABSENCES",
                "SALAIRE_BASE_MOIS", "INDEMNITE", "RAPPEL", "AUTRES", "HEURES_SUP_MAJ",
                "SALAIRE_BRUT", "CNAPS_1", "CNAPS_8", "OSTIE_1", "OSTIE_5",
                "REVENU_IMPOSABLE", "IMPOT_DU", "ENFANT_CHARGE", "MONTANT_ENFANT",
                "IGR_NET", "AUTRES_RETENUES", "TOTAL_RETENUES", "SALAIRE_NET",
                "AVANCE", "NET_A_PAYER", "AUTRES_INDEMNITE", "NET_DU_MOIS"
        };

        StringBuilder sb = new StringBuilder();
        // En-tête CSV
        for (int i = 0; i < headers.length; i++) {
            if (i > 0) sb.append(';');
            sb.append(headers[i]);
        }
        sb.append('\n');

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Map<String, Object> row : rows) {
            String date = today.format(dateFormatter);
            String nbre = "1";
            String matricule = stringVal(row.get("matricule"));
            String numCnaps = stringVal(row.get("numero_cnaps"));
            String nomPrenoms = stringVal(row.get("nom_complet"));
            String dateEmb = row.get("datedembauche") != null ? row.get("datedembauche").toString() : "";
            String absMois = stringVal(row.get("jours_absences_non_justifiees"));
            String cat = stringVal(row.get("categorie"));
            String fonction = stringVal(row.get("departement"));

            double salaireBase = doubleVal(row.get("salaire_base"));
            double dedRetards = doubleVal(row.get("deduction_retards"));
            double dedAbs = doubleVal(row.get("deduction_absences"));
            double retenuesAbs = dedRetards + dedAbs;
            double salaireBaseMois = salaireBase - retenuesAbs;

            double indemnite = doubleVal(row.get("indemnite_transport"))
                    + doubleVal(row.get("indemnite_logement"));

            double heuresSupMaj = doubleVal(row.get("montant_heures_sup"));

            double salaireBrut = doubleVal(row.get("salaire_brut"));

            double cnapsSal = doubleVal(row.get("cnaps_salarie"));
            double cnapsEmp = doubleVal(row.get("cnaps_employeur"));
            double ostieSal = doubleVal(row.get("ostie_salarie"));
            double ostieEmp = doubleVal(row.get("ostie_employeur"));

            // Approximation pour une répartition 1% / 8% à partir du total employeur si nécessaire
            double cnaps1 = cnapsSal; // part salarié 1%
            double cnaps8 = cnapsEmp; // part employeur (affiché en 8% dans le modèle)
            double ostie1 = ostieSal;
            double ostie5 = ostieEmp;

            double revenuImposable = doubleVal(row.get("salaire_imposable"));
            double impotDu = doubleVal(row.get("irsa"));

            int enfants = intVal(row.get("nombreenfants"));
            double montantEnfant = 0.0; // à ajuster si barème spécifique
            double igrNet = impotDu - montantEnfant;

            double autresRetenues = doubleVal(row.get("autres_retenues"));
            double totalRetenues = doubleVal(row.get("total_retenues"));
            double salaireNet = doubleVal(row.get("salaire_net"));

            double avance = doubleVal(row.get("avance_sur_salaire"));
            double netAPayer = salaireNet - avance;
            double autresIndemnite = 0.0; // placeholder
            double netDuMois = netAPayer + autresIndemnite;

            Object[] values = new Object[]{
                    date, nbre, matricule, numCnaps, nomPrenoms, dateEmb,
                    absMois, cat, fonction, salaireBase, retenuesAbs,
                    salaireBaseMois, indemnite, 0.0, 0.0, heuresSupMaj,
                    salaireBrut, cnaps1, cnaps8, ostie1, ostie5,
                    revenuImposable, impotDu, enfants, montantEnfant,
                    igrNet, autresRetenues, totalRetenues, salaireNet,
                    avance, netAPayer, autresIndemnite, netDuMois
            };

            for (int i = 0; i < values.length; i++) {
                if (i > 0) sb.append(';');
                sb.append(escapeCsv(values[i]));
            }
            sb.append('\n');
        }

        HttpHeaders headersResp = new HttpHeaders();
        headersResp.setContentType(new MediaType("text", "csv"));
        headersResp.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=etat_paie_" + mois + "_" + annee + ".csv");

        return ResponseEntity
                .ok()
                .headers(headersResp)
                .body(sb.toString());
    }

    private static String stringVal(Object o) {
        return o == null ? "" : o.toString();
    }

    private static double doubleVal(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(o.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static int intVal(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String escapeCsv(Object v) {
        if (v == null) return "";
        String s = v.toString();
        if (s.contains(";") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
}
