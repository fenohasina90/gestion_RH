package com.backend.Controllers;

import com.backend.services.EmployeSelfServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employe")
@CrossOrigin(origins = "*")
public class EmployeSelfServiceController {

    private final EmployeSelfServiceService selfService;

    public EmployeSelfServiceController(EmployeSelfServiceService selfService) {
        this.selfService = selfService;
    }

    // 1) Login employé par matricule + mot de passe statique
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String matricule = credentials.get("matricule");
        String motdepasse = credentials.get("motdepasse");
        Map<String, Object> result = selfService.loginEmploye(matricule, motdepasse);
        return ResponseEntity.ok(result);
    }

    // 2) Profil employé - lecture
    @GetMapping("/self/profil")
    public ResponseEntity<?> getProfil(@RequestParam("idemploye") Integer idEmploye) {
        Map<String, Object> profil = selfService.getProfilEmploye(idEmploye);
        if (profil == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profil);
    }

    // 2) Profil employé - mise à jour
    @PutMapping("/self/profil")
    public ResponseEntity<?> updateProfil(@RequestParam("idemploye") Integer idEmploye,
                                          @RequestBody Map<String, Object> data) {
        int updated = selfService.updateProfilEmploye(idEmploye, data);
        return ResponseEntity.ok(Map.of(
                "success", updated > 0,
                "updated", updated
        ));
    }

    // 3) Bulletins de paie
    @GetMapping("/self/bulletins")
    public ResponseEntity<?> getBulletins(@RequestParam("idemploye") Integer idEmploye,
                                          @RequestParam(value = "mois", required = false) Integer mois,
                                          @RequestParam(value = "annee", required = false) Integer annee) {
        List<Map<String, Object>> bulletins = selfService.getBulletins(idEmploye, mois, annee);
        return ResponseEntity.ok(bulletins);
    }

    // 4) Solde de congés
    @GetMapping("/self/conges/solde")
    public ResponseEntity<?> getSoldeConge(@RequestParam("idemploye") Integer idEmploye,
                                           @RequestParam(value = "annee", required = false) Integer annee) {
        List<Map<String, Object>> soldes = selfService.getSoldeConge(idEmploye, annee);
        return ResponseEntity.ok(soldes);
    }

    // 5) Liste des demandes de congé de l'employé
    @GetMapping("/self/demandes-conge")
    public ResponseEntity<?> getDemandesConge(@RequestParam("idemploye") Integer idEmploye) {
        List<Map<String, Object>> demandes = selfService.getDemandesConge(idEmploye);
        return ResponseEntity.ok(demandes);
    }

    // 6) Création d'une demande de congé
    @PostMapping("/self/demandes-conge")
    public ResponseEntity<?> creerDemandeConge(@RequestBody Map<String, Object> body) {
        Integer idEmploye = Integer.valueOf(body.get("idemploye").toString());
        Integer idTypeConge = Integer.valueOf(body.get("idtypeconge").toString());
        LocalDate dateDebut = LocalDate.parse(body.get("datedebut").toString());
        LocalDate dateFin = LocalDate.parse(body.get("datefin").toString());
        String motif = body.get("motif") != null ? body.get("motif").toString() : null;

        Map<String, Object> result = selfService.creerDemandeConge(idEmploye, idTypeConge, dateDebut, dateFin, motif);
        return ResponseEntity.ok(result);
    }

    // 7) Messagerie RH - liste des messages de l'employé
    @GetMapping("/self/messages")
    public ResponseEntity<?> getMessages(@RequestParam("idemploye") Integer idEmploye) {
        List<Map<String, Object>> messages = selfService.getMessagesEmploye(idEmploye);
        return ResponseEntity.ok(messages);
    }

    // 8) Messagerie RH - création d'un message
    @PostMapping("/self/messages")
    public ResponseEntity<?> creerMessage(@RequestBody Map<String, Object> body) {
        Integer idEmploye = Integer.valueOf(body.get("idemploye").toString());
        String sujet = body.get("sujet") != null ? body.get("sujet").toString() : null;
        String contenu = body.get("contenu") != null ? body.get("contenu").toString() : null;

        Map<String, Object> result = selfService.creerMessageEmploye(idEmploye, sujet, contenu);
        return ResponseEntity.ok(result);
    }
}
