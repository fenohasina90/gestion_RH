package com.backend.Controllers;

import com.backend.services.CompetenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/competences")
@CrossOrigin(origins = "*")
public class CompetenceController {

    private final CompetenceService competenceService;

    public CompetenceController(CompetenceService competenceService) {
        this.competenceService = competenceService;
    }

    /**
     * Liste des compétences avec leur catégorie.
     */
    @GetMapping
    public ResponseEntity<?> getAllCompetences() {
        List<Map<String, Object>> rows = competenceService.getAllCompetences();
        return ResponseEntity.ok(rows);
    }

    /**
     * Liste des niveaux de compétences.
     */
    @GetMapping("/niveaux")
    public ResponseEntity<?> getAllNiveaux() {
        List<Map<String, Object>> rows = competenceService.getAllNiveaux();
        return ResponseEntity.ok(rows);
    }

    /**
     * Compétences d'un employé (niveau actuel, libellés, etc.).
     */
    @GetMapping("/employe")
    public ResponseEntity<?> getCompetencesEmploye(@RequestParam("idemploye") Integer idEmploye) {
        List<Map<String, Object>> rows = competenceService.getCompetencesEmploye(idEmploye);
        return ResponseEntity.ok(rows);
    }

    /**
     * Compétences attendues pour un profil.
     */
    @GetMapping("/profil")
    public ResponseEntity<?> getCompetencesProfil(@RequestParam("idprofil") Integer idProfil) {
        List<Map<String, Object>> rows = competenceService.getCompetencesProfil(idProfil);
        return ResponseEntity.ok(rows);
    }

    /**
     * Matching compétences employé / profil : liste les compétences du profil avec
     * niveau attendu, niveau actuel de l'employé, écart et statut (OK / A renforcer / Manquante).
     */
    @GetMapping("/matching")
    public ResponseEntity<?> getMatchingEmployeProfil(@RequestParam("idemploye") Integer idEmploye,
                                                      @RequestParam("idprofil") Integer idProfil) {
        Map<String, Object> result = competenceService.getMatchingEmployeProfil(idEmploye, idProfil);
        return ResponseEntity.ok(result);
    }

    /**
     * Suggestions de formations pour combler les écarts entre un employé et un profil.
     * On cible les compétences où l'employé est en dessous du niveau attendu,
     * et on propose les formations liées à ces compétences.
     */
    @GetMapping("/suggestions-formation")
    public ResponseEntity<?> getSuggestionsFormation(@RequestParam("idemploye") Integer idEmploye,
                                                     @RequestParam("idprofil") Integer idProfil) {
        List<Map<String, Object>> rows = competenceService.getSuggestionsFormation(idEmploye, idProfil);
        return ResponseEntity.ok(rows);
    }

    /**
     * Attribuer automatiquement les compétences ciblées par une formation
     * à un employé qui a terminé cette formation.
     *
     * Pour chaque ligne de formationcompetence liée à la formation, on crée ou met
     * à jour la ligne employecompetence correspondante avec le niveau cible.
     */
    @PostMapping("/attribuer-apres-formation")
    public ResponseEntity<?> attribuerApresFormation(@RequestParam("idemploye") Integer idEmploye,
                                                     @RequestParam("idformation") Integer idFormation) {
        Map<String, Object> resp = competenceService.attribuerApresFormation(idEmploye, idFormation);
        return ResponseEntity.ok(resp);
    }
}
