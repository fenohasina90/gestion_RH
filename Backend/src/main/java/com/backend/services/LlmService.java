package com.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    @Value("${MISTRAL_API_KEY:}")
    private String mistralApiKey;

    @Value("${MISTRAL_MODEL:mistral-small-latest}")
    private String mistralModel;

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String systemPrompt, String userPrompt) {
        if (mistralApiKey == null || mistralApiKey.isBlank()) {
            return "[LLM désactivé : clé Mistral manquante. Configurez MISTRAL_API_KEY dans l'environnement.]";
        }

        String url = "https://api.mistral.ai/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", mistralModel);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(mistralApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map response = restTemplate.postForObject(url, entity, Map.class);
        if (response == null) {
            return "Aucune réponse reçue du modèle.";
        }
        Object choicesObj = response.get("choices");
        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
            return "Réponse invalide du modèle.";
        }
        Object first = choices.get(0);
        if (!(first instanceof Map<?,?> firstMap)) {
            return "Réponse invalide du modèle.";
        }
        Object msgObj = firstMap.get("message");
        if (!(msgObj instanceof Map<?,?> msgMap)) {
            return "Réponse invalide du modèle.";
        }
        Object content = msgMap.get("content");
        return content != null ? content.toString() : "";
    }
}
