package com.backend.Controllers;

import com.backend.entities.Comptecandidat;
import com.backend.services.ComptecandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comptecandidat")
@CrossOrigin(origins = "*")
public class ComptecandidatController {

    @Autowired
    private ComptecandidatService comptecandidatService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String motdepasse = credentials.get("motdepasse");

        Map<String, Object> response = new HashMap<>();

        if (email == null || motdepasse == null) {
            response.put("success", false);
            response.put("message", "Email et mot de passe requis");
            return ResponseEntity.badRequest().body(response);
        }

        Comptecandidat candidat = comptecandidatService.authenticate(email, motdepasse);

        if (candidat != null) {
            response.put("success", true);
            response.put("candidate", candidat);
            response.put("token", "candidate_token_" + candidat.getId()); // Token simple pour demo
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String motdepasse = userData.get("motdepasse");

        Map<String, Object> response = new HashMap<>();

        if (email == null || motdepasse == null) {
            response.put("success", false);
            response.put("message", "Email et mot de passe requis");
            return ResponseEntity.badRequest().body(response);
        }

        if (comptecandidatService.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Un compte avec cet email existe déjà");
            return ResponseEntity.ok(response);
        }

        Comptecandidat nouveauCandidat = comptecandidatService.register(email, motdepasse);

        if (nouveauCandidat != null) {
            response.put("success", true);
            response.put("candidate", nouveauCandidat);
            response.put("token", "candidate_token_" + nouveauCandidat.getId()); // Token simple pour demo
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Erreur lors de la création du compte");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        // Vérification simple du token pour demo
        if (token != null && token.startsWith("candidate_token_")) {
            response.put("success", true);
            response.put("valid", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("valid", false);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("candidate_token_")) {
            try {
                // Extraire l'ID du candidat depuis le token
                String candidatIdStr = token.replace("candidate_token_", "");
                Integer candidatId = Integer.parseInt(candidatIdStr);
                
                Comptecandidat candidat = comptecandidatService.findById(candidatId);
                
                if (candidat != null) {
                    response.put("success", true);
                    response.put("candidate", candidat);
                    return ResponseEntity.ok(response);
                }
            } catch (NumberFormatException e) {
                // Token malformé
            }
        }
        
        response.put("success", false);
        response.put("message", "Token invalide");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        // Pour une déconnexion simple, on retourne toujours succès
        // Dans un vrai système, on invaliderait le token côté serveur
        response.put("success", true);
        response.put("message", "Déconnexion réussie");
        
        return ResponseEntity.ok(response);
    }
}
