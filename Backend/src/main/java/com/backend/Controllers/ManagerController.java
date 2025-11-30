package com.backend.Controllers;

import com.backend.services.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin(origins = "*")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String matricule = body.get("matricule");
        String motdepasse = body.get("motdepasse");
        Map<String, Object> result = managerService.loginManager(matricule, motdepasse);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/demandes-conge")
    public ResponseEntity<?> getDemandesEquipe(@RequestParam("idmanager") Integer idManager,
                                               @RequestParam(value = "statut", required = false) String statut,
                                               @RequestParam(value = "annee", required = false) Integer annee,
                                               @RequestParam(value = "idtypeconge", required = false) Integer idTypeConge) {
        return ResponseEntity.ok(managerService.getDemandesEquipe(idManager, statut, annee, idTypeConge));
    }

    @PostMapping("/demandes-conge/{id}/valider")
    public ResponseEntity<?> validerDemande(@PathVariable("id") Integer idDemande,
                                            @RequestParam("idmanager") Integer idManager) {
        Map<String, Object> result = managerService.validerDemandeManager(idManager, idDemande);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/demandes-conge/{id}/refuser")
    public ResponseEntity<?> refuserDemande(@PathVariable("id") Integer idDemande,
                                            @RequestParam("idmanager") Integer idManager) {
        Map<String, Object> result = managerService.refuserDemandeManager(idManager, idDemande);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam("idmanager") Integer idManager,
                                          @RequestParam(value = "mois", required = false) Integer mois,
                                          @RequestParam(value = "annee", required = false) Integer annee) {
      Map<String, Object> result = managerService.getDashboard(idManager, mois, annee);
      return ResponseEntity.ok(result);
    }
}
