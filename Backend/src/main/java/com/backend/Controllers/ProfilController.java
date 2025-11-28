package com.backend.Controllers;

import com.backend.entities.Profil;
import com.backend.entities.Critere;
import com.backend.entities.Critereprofil;
import com.backend.entities.Diplome;
import com.backend.entities.Profildiplome;
import com.backend.services.ProfilService;
import com.backend.services.CritereService;
import com.backend.services.CritereProfilService;
import com.backend.services.DiplomeService;
import com.backend.services.ProfilDiplomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profils")
@CrossOrigin(origins = "*")
public class ProfilController {
    
    @Autowired
    private ProfilService profilService;
    
    @Autowired
    private CritereService critereService;
    
    @Autowired
    private CritereProfilService critereProfilService;
    
    @Autowired
    private DiplomeService diplomeService;
    
    @Autowired
    private ProfilDiplomeService profilDiplomeService;
    
    @GetMapping
    public List<Profil> getAllProfils() {
        return profilService.getAllProfils();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Profil> getProfilById(@PathVariable Integer id) {
        Optional<Profil> profil = profilService.getProfilById(id);
        return profil.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Profil createProfil(@RequestBody Profil profil) {
        return profilService.saveProfil(profil);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Profil> updateProfil(@PathVariable Integer id, @RequestBody Profil profil) {
        Optional<Profil> existingProfil = profilService.getProfilById(id);
        if (existingProfil.isPresent()) {
            profil.setId(id);
            return ResponseEntity.ok(profilService.saveProfil(profil));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfil(@PathVariable Integer id) {
        if (profilService.getProfilById(id).isPresent()) {
            profilService.deleteProfil(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/criteres")
    public List<Critereprofil> getCriteresByProfilId(@PathVariable Integer id) {
        return critereProfilService.getCriteresByProfilId(id);
    }
    
    @PostMapping("/{id}/criteres")
    public ResponseEntity<Void> assignCriteresToProfil(@PathVariable Integer id, @RequestBody Map<String, List<CritereProfilService.CritereAssignmentRequest>> request) {
        List<CritereProfilService.CritereAssignmentRequest> critereAssignments = request.get("critereAssignments");
        critereProfilService.assignCriteresToProfil(id, critereAssignments);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/diplomes")
    public List<Profildiplome> getDiplomesByProfilId(@PathVariable Integer id) {
        return profilDiplomeService.getDiplomesByProfilId(id);
    }
    
    @PostMapping("/{id}/diplomes")
    public ResponseEntity<Void> assignDiplomesToProfil(@PathVariable Integer id, @RequestBody Map<String, List<ProfilDiplomeService.DiplomeAssignmentRequest>> request) {
        List<ProfilDiplomeService.DiplomeAssignmentRequest> diplomeAssignments = request.get("diplomeAssignments");
        profilDiplomeService.assignDiplomesToProfil(id, diplomeAssignments);
        return ResponseEntity.ok().build();
    }
}
