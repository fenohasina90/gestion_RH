package com.backend.Controllers;

import com.backend.services.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class RhChatController {

    private final JdbcTemplate jdbcTemplate;
    private final LlmService llmService;

    public RhChatController(JdbcTemplate jdbcTemplate, LlmService llmService) {
        this.jdbcTemplate = jdbcTemplate;
        this.llmService = llmService;
    }

    public record ChatRequest(String question, String matricule) {}

    @PostMapping("/rh")
    public ResponseEntity<Map<String, Object>> chatRh(@RequestBody ChatRequest req) {
        String question = req.question() != null ? req.question().trim() : "";
        String matricule = req.matricule() != null ? req.matricule().trim() : null;
        if (question.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Question vide"));
        }

        String lower = question.toLowerCase();

        if (lower.contains("bulletin") && lower.contains("paie")) {
            String detectedMat = null;
            Matcher m = Pattern.compile("\\b[A-Z]{2,}0*\\d+\\b").matcher(question);
            if (m.find()) {
                detectedMat = m.group();
            }
            if (detectedMat == null && matricule != null && !matricule.isBlank()) {
                detectedMat = matricule;
            }

            Integer annee = null;
            m = Pattern.compile("(20\\d{2})").matcher(question);
            if (m.find()) {
                annee = Integer.parseInt(m.group(1));
            }

            Integer mois = null;
            if (lower.contains("janvier")) mois = 1;
            else if (lower.contains("février") || lower.contains("fevrier")) mois = 2;
            else if (lower.contains("mars")) mois = 3;
            else if (lower.contains("avril")) mois = 4;
            else if (lower.contains("mai")) mois = 5;
            else if (lower.contains("juin")) mois = 6;
            else if (lower.contains("juillet")) mois = 7;
            else if (lower.contains("août") || lower.contains("aout")) mois = 8;
            else if (lower.contains("septembre")) mois = 9;
            else if (lower.contains("octobre")) mois = 10;
            else if (lower.contains("novembre")) mois = 11;
            else if (lower.contains("décembre") || lower.contains("decembre")) mois = 12;
            if (mois == null) {
                Matcher mNum = Pattern.compile("\\b(1[0-2]|0?[1-9])\\b").matcher(question);
                if (mNum.find()) {
                    mois = Integer.parseInt(mNum.group(1));
                }
            }

            Map<String, Object> out = new HashMap<>();
            if (detectedMat == null || mois == null || annee == null) {
                out.put("answer", "Pour consulter un bulletin de paie via le chatbot, indique le matricule (par exemple EMP001), le mois et l'année, par exemple : 'Donne-moi le bulletin de paie de EMP001 pour novembre 2025'.");
                return ResponseEntity.ok(out);
            }

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM v_bulletin_paie_complet WHERE matricule = ? AND mois = ? AND annee = ?",
                    detectedMat, mois, annee
            );
            if (rows.isEmpty()) {
                out.put("answer", "Aucun bulletin de paie trouvé pour le matricule " + detectedMat + " au " + mois + "/" + annee + ".");
                return ResponseEntity.ok(out);
            }

            Map<String, Object> b = rows.get(0);
            StringBuilder sb = new StringBuilder();
            sb.append("Bulletin de paie ").append(detectedMat).append(" - ")
                    .append(String.format("%02d", mois)).append("/").append(annee).append("\n");
            sb.append("Nom complet : ").append(b.getOrDefault("nom_complet", "")).append("\n");
            sb.append("Département : ").append(b.getOrDefault("departement", "")).append("\n");
            sb.append("Salaire brut : ").append(b.getOrDefault("salaire_brut", "")).append("\n");
            sb.append("Salaire net : ").append(b.getOrDefault("salaire_net", "")).append("\n");
            sb.append("CNAPS salarié : ").append(b.getOrDefault("cnaps_salarie", "")).append("\n");
            sb.append("OSTIE salarié : ").append(b.getOrDefault("ostie_salarie", "")).append("\n");
            sb.append("IRSA : ").append(b.getOrDefault("irsa", "")).append("\n");
            sb.append("Avances sur salaire : ").append(b.getOrDefault("avance_sur_salaire", "")).append("\n");

            out.put("answer", sb.toString());
            return ResponseEntity.ok(out);
        }

        if (lower.contains("etat") && lower.contains("paie")) {
            Integer annee = null;
            Matcher m = Pattern.compile("(20\\d{2})").matcher(question);
            if (m.find()) {
                annee = Integer.parseInt(m.group(1));
            }

            Integer mois = null;
            if (lower.contains("janvier")) mois = 1;
            else if (lower.contains("février") || lower.contains("fevrier")) mois = 2;
            else if (lower.contains("mars")) mois = 3;
            else if (lower.contains("avril")) mois = 4;
            else if (lower.contains("mai")) mois = 5;
            else if (lower.contains("juin")) mois = 6;
            else if (lower.contains("juillet")) mois = 7;
            else if (lower.contains("août") || lower.contains("aout")) mois = 8;
            else if (lower.contains("septembre")) mois = 9;
            else if (lower.contains("octobre")) mois = 10;
            else if (lower.contains("novembre")) mois = 11;
            else if (lower.contains("décembre") || lower.contains("decembre")) mois = 12;
            if (mois == null) {
                Matcher mNum = Pattern.compile("\\b(1[0-2]|0?[1-9])\\b").matcher(question);
                if (mNum.find()) {
                    mois = Integer.parseInt(mNum.group(1));
                }
            }

            Map<String, Object> out = new HashMap<>();
            if (mois == null || annee == null) {
                out.put("answer", "Pour consulter un état de paie via le chatbot, indique le mois et l'année, par exemple : 'Donne-moi l'état de paie pour novembre 2025'.");
                return ResponseEntity.ok(out);
            }

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM v_bulletin_paie_complet WHERE mois = ? AND annee = ? ORDER BY departement, nom_complet",
                    mois, annee
            );
            if (rows.isEmpty()) {
                out.put("answer", "Aucun bulletin trouvé pour l'état de paie du mois " + mois + "/" + annee + ".");
                return ResponseEntity.ok(out);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("État de paie pour ").append(String.format("%02d", mois)).append("/").append(annee).append("\n");
            sb.append("Nombre de bulletins : ").append(rows.size()).append("\n\n");
            int max = Math.min(rows.size(), 50);
            for (int i = 0; i < max; i++) {
                Map<String, Object> r = rows.get(i);
                sb.append("- ")
                        .append(r.getOrDefault("matricule", "")).append(" | ")
                        .append(r.getOrDefault("nom_complet", "")).append(" | ")
                        .append(r.getOrDefault("departement", "")).append(" | Salaire net=")
                        .append(r.getOrDefault("salaire_net", ""))
                        .append(" | Net à payer=")
                        .append(r.getOrDefault("net_a_payer", ""))
                        .append("\n");
            }
            if (rows.size() > max) {
                sb.append("... ").append(rows.size() - max).append(" autres lignes non affichées.\n");
            }

            out.put("answer", sb.toString());
            return ResponseEntity.ok(out);
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Contexte entreprise RH:\n")
                .append("- Tu es un assistant RH pour une entreprise à Madagascar.\n")
                .append("- Tu dois toujours répondre en français, de façon claire et professionnelle.\n")
                .append("- Ne jamais inventer des montants ou des règles si l'information n'est pas dans les données fournies.\n")
                .append("- Tu dois respecter la confidentialité : ne jamais donner des informations nominatives sur d'autres employés.\n");

        if (matricule != null && !matricule.isBlank()) {
            List<Map<String, Object>> empRows = jdbcTemplate.queryForList(
                    "SELECT e.id, e.matricule, e.nom, e.prenom, e.numerocnaps, e.numeroostie FROM employe e WHERE e.matricule = ?",
                    matricule
            );
            if (!empRows.isEmpty()) {
                Map<String, Object> emp = empRows.get(0);
                Integer empId = ((Number) emp.get("id")).intValue();
                contextBuilder.append("\nDonnées de l'employé:\n");
                contextBuilder.append("- Matricule: ").append(emp.get("matricule")).append("\n");
                contextBuilder.append("- Nom: ").append(emp.get("nom")).append(" ").append(emp.get("prenom")).append("\n");
                contextBuilder.append("- CNAPS: ").append(emp.get("numerocnaps")).append("\n");
                contextBuilder.append("- OSTIE: ").append(emp.get("numeroostie")).append("\n");

                List<Map<String, Object>> soldeRows = jdbcTemplate.queryForList(
                        "SELECT annee, joursacquis, jourspris, joursrestants FROM soldeconge WHERE idemploye = ? ORDER BY annee DESC LIMIT 3",
                        empId
                );
                if (!soldeRows.isEmpty()) {
                    contextBuilder.append("\nSolde de congés (par année):\n");
                    for (Map<String, Object> s : soldeRows) {
                        contextBuilder.append("- ")
                                .append(s.get("annee"))
                                .append(": acquis=").append(s.get("joursacquis"))
                                .append(", pris=").append(s.get("jourspris"))
                                .append(", restants=").append(s.get("joursrestants"))
                                .append("\n");
                    }
                }

                // Historique des congés personnels (congés effectués) sur plusieurs années
                List<Map<String, Object>> congesRows = jdbcTemplate.queryForList(
                        "SELECT c.datedebut, c.datefin, c.nombrejourspris, t.libelle AS type_conge " +
                                "FROM congeeffectue c LEFT JOIN typeconge t ON c.idtypeconge = t.id " +
                                "WHERE c.idemploye = ? ORDER BY c.datedebut DESC LIMIT 50",
                        empId
                );
                if (!congesRows.isEmpty()) {
                    contextBuilder.append("\nHistorique des congés de l'employé (récents):\n");
                    for (Map<String, Object> c : congesRows) {
                        contextBuilder.append("- ")
                                .append(c.get("datedebut"))
                                .append(" au ")
                                .append(c.get("datefin"))
                                .append(" : ")
                                .append(c.get("type_conge"))
                                .append(" (jours pris=")
                                .append(c.get("nombrejourspris"))
                                .append(")\n");
                    }
                }

                // Aucune récupération de bulletin de paie ici : la paie (bulletins, état) est gérée via PaieController
            } else {
                contextBuilder.append("\nAucun employé trouvé pour le matricule fourni. Réponds de manière générique.");
            }
        }

        // Liste globale des employés (pour recherche par nom, matricule, département)
        List<Map<String, Object>> allEmployees = jdbcTemplate.queryForList(
                "SELECT e.id, e.matricule, e.nom, e.prenom, COALESCE(d.nom, 'Sans département') AS departement " +
                        "FROM employe e " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "ORDER BY d.nom, e.nom, e.prenom"
        );
        if (!allEmployees.isEmpty()) {
            contextBuilder.append("\nListe des employés (avec matricule et département):\n");
            for (Map<String, Object> emp : allEmployees) {
                contextBuilder.append("- ")
                        .append(emp.get("matricule")).append(" | ")
                        .append(emp.get("nom")).append(" ").append(emp.get("prenom"))
                        .append(" | Département: ").append(emp.get("departement"))
                        .append("\n");
            }
        }

        // Congés validés récents (tous employés)
        List<Map<String, Object>> allLeaves = jdbcTemplate.queryForList(
                "SELECT idemploye, nom, prenom, typeconge, datedebut, datefin, nombrejours " +
                        "FROM v_conges_valides " +
                        "ORDER BY datedebut DESC " +
                        "LIMIT 300"
        );
        if (!allLeaves.isEmpty()) {
            contextBuilder.append("\nListe des congés validés récents (tous employés):\n");
            for (Map<String, Object> l : allLeaves) {
                contextBuilder.append("- ")
                        .append(l.get("nom")).append(" ").append(l.get("prenom"))
                        .append(" | Type: ").append(l.get("typeconge"))
                        .append(" | Du ").append(l.get("datedebut"))
                        .append(" au ").append(l.get("datefin"))
                        .append(" | Jours: ").append(l.get("nombrejours"))
                        .append("\n");
            }
        }

        // Statistiques globales anonymisées sur les congés (tous employés confondus)
        List<Map<String, Object>> globalLeaves = jdbcTemplate.queryForList(
                "SELECT EXTRACT(YEAR FROM datedebut) AS annee, EXTRACT(MONTH FROM datedebut) AS mois, COUNT(*) AS nb_conges " +
                        "FROM v_conges_valides " +
                        "GROUP BY EXTRACT(YEAR FROM datedebut), EXTRACT(MONTH FROM datedebut) " +
                        "ORDER BY annee DESC, mois DESC LIMIT 24"
        );
        if (!globalLeaves.isEmpty()) {
            contextBuilder.append("\nStatistiques globales des congés (tous employés, sans noms):\n");
            for (Map<String, Object> g : globalLeaves) {
                contextBuilder.append("- ")
                        .append("Année ").append(g.get("annee"))
                        .append(", mois ").append(g.get("mois"))
                        .append(" : congés validés=")
                        .append(g.get("nb_conges"))
                        .append("\n");
            }
        }

        // Statistiques par département (effectifs, congés agrégés)
        List<Map<String, Object>> deptHeadcount = jdbcTemplate.queryForList(
                "SELECT COALESCE(d.nom, 'Sans département') AS departement, COUNT(*) AS nb_employes " +
                        "FROM employe e " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "GROUP BY COALESCE(d.nom, 'Sans département')"
        );
        List<Map<String, Object>> deptLeaves = jdbcTemplate.queryForList(
                "SELECT COALESCE(d.nom, 'Sans département') AS departement, " +
                        "EXTRACT(YEAR FROM v.datedebut) AS annee, EXTRACT(MONTH FROM v.datedebut) AS mois, COUNT(*) AS nb_conges " +
                        "FROM v_conges_valides v " +
                        "JOIN employe e ON e.id = v.idemploye " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "GROUP BY COALESCE(d.nom, 'Sans département'), EXTRACT(YEAR FROM v.datedebut), EXTRACT(MONTH FROM v.datedebut) " +
                        "ORDER BY annee DESC, mois DESC, departement " +
                        "LIMIT 120"
        );
        if (!deptHeadcount.isEmpty() || !deptLeaves.isEmpty()) {
            contextBuilder.append("\nStatistiques par département (effectifs, congés):\n");
            if (!deptHeadcount.isEmpty()) {
                contextBuilder.append("- Effectifs par département:\n");
                for (Map<String, Object> r : deptHeadcount) {
                    contextBuilder.append("  * ")
                            .append(r.get("departement")).append(" : ")
                            .append(r.get("nb_employes")).append(" employés\n");
                }
            }
            if (!deptLeaves.isEmpty()) {
                contextBuilder.append("- Congés agrégés par département et période (récente):\n");
                for (Map<String, Object> r : deptLeaves) {
                    contextBuilder.append("  * ")
                            .append(r.get("departement")).append(" | ")
                            .append("Période ").append(r.get("mois")).append("/").append(r.get("annee"))
                            .append(" : congés validés=").append(r.get("nb_conges"))
                            .append("\n");
                }
            }
        }

        String systemPrompt = contextBuilder.toString();
        String userPrompt = "Question de l'utilisateur: " + question + "\n" +
                "Consignes :\n" +
                "- Tu as, dans le contexte ci-dessus, des données détaillées sur les employés (identité, matricule, département), les soldes et historiques de congés, et des statistiques de congés par département. Utilise-les pour répondre.\n" +
                "- Si la question porte sur un employé précis, identifie-le par son matricule, ou à défaut par la combinaison nom + prénom + département, en t'appuyant sur la liste des employés du contexte.\n" +
                "- Pour 'liste des employés' ou 'liste des employés d'un département', exploite la liste des employés présente dans le contexte et formate une liste lisible.\n" +
                "- Pour 'liste des congés à une date donnée' ou 'statistique de congés sur une période', utilise la liste des congés validés récents et/ou les statistiques de congés par période pour filtrer les congés correspondant à la date ou au mois demandé.\n" +
                "- Pour 'statistique générale pour chaque département', utilise les statistiques par département (effectifs, congés agrégés) et construis un résumé comparatif.\n" +
                "- Si une information précise manque dans les données ci-dessus, explique-le clairement sans inventer de chiffres ni de règles, et propose à l'utilisateur de reformuler avec un matricule, un nom ou une période plus précise si nécessaire.";

        String answer = llmService.chat(systemPrompt, userPrompt);
        Map<String, Object> out = new HashMap<>();
        out.put("answer", answer);
        return ResponseEntity.ok(out);
    }
}
