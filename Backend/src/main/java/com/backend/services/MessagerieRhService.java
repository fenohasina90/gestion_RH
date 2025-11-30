package com.backend.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessagerieRhService {

    private final JdbcTemplate jdbcTemplate;

    public MessagerieRhService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getAllMessages() {
        return jdbcTemplate.queryForList(
                "SELECT m.id, m.idemploye, e.matricule, e.nom, e.prenom, d.nom AS departement, " +
                        "m.sujet, m.contenu, m.reponse, m.date_envoi, m.date_reponse, m.lu " +
                        "FROM message_rh m " +
                        "JOIN employe e ON e.id = m.idemploye " +
                        "LEFT JOIN departement d ON d.id = e.iddept " +
                        "ORDER BY m.date_envoi DESC"
        );
    }

    public Map<String, Object> repondreMessage(Integer idMessage, String reponse) {
        Map<String, Object> resp = new HashMap<>();
        if (reponse == null || reponse.trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "La réponse ne peut pas être vide");
            return resp;
        }

        int updated = jdbcTemplate.update(
                "UPDATE message_rh SET reponse = ?, date_reponse = ?, lu = TRUE WHERE id = ?",
                reponse.trim(),
                LocalDateTime.now(),
                idMessage
        );

        resp.put("success", updated > 0);
        resp.put("updated", updated);
        resp.put("message", updated > 0 ? "Réponse enregistrée" : "Aucun message mis à jour");
        return resp;
    }
}
