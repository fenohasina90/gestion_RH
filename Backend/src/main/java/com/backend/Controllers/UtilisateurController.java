package com.backend.Controllers;

import com.backend.entities.Utilisateur;
import com.backend.entities.Employe;
import com.backend.services.UtilisateurService;
import com.backend.services.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private EmployeService employeService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String motdepasse = credentials.get("motdepasse");
        
        Utilisateur utilisateur = utilisateurService.authenticate(email, motdepasse);
        
        if (utilisateur != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Connexion réussie",
                "user", Map.of(
                    "id", utilisateur.getId(),
                    "email", utilisateur.getEmail()
                )
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Email ou mot de passe incorrect"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> userData) {
        try {
            String email = (String) userData.get("email");
            String motdepasse = (String) userData.get("motdepasse");
            Integer employeId = (Integer) userData.get("employeId");
            
            // Vérifier que l'employé existe
            Employe employe = employeService.getEmployeById(employeId);
            if (employe == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Employé non trouvé"
                ));
            }
            
            // Créer l'utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setMotdepasse(motdepasse);
            utilisateur.setIdemploye(employe);
            
            Utilisateur newUser = utilisateurService.createUser(utilisateur);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Inscription réussie",
                "user", Map.of(
                    "id", newUser.getId(),
                    "email", newUser.getEmail(),
                    "employe", Map.of(
                        "id", employe.getId(),
                        "nom", employe.getNom(),
                        "prenom", employe.getPrenom()
                    )
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erreur lors de l'inscription: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/employes")
    public ResponseEntity<List<Employe>> getAllEmployes() {
        List<Employe> employes = employeService.getAllEmployes();
        return ResponseEntity.ok(employes);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Déconnexion réussie"
        );
        
        return ResponseEntity.ok(response);
    }
}
