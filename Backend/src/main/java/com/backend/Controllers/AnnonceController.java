package com.backend.Controllers;

import com.backend.entities.Annonce;
import com.backend.services.AnnonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annonces")
@CrossOrigin(origins = "*")
public class AnnonceController {
    
    @Autowired
    private AnnonceService annonceService;
    
    @GetMapping
    public List<Annonce> getAllAnnonces() {
        return annonceService.getAllAnnonces();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Integer id) {
        Annonce annonce = annonceService.getAnnonceById(id);
        if (annonce != null) {
            return ResponseEntity.ok(annonce);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public Annonce createAnnonce(@RequestBody Annonce annonce) {
        return annonceService.createAnnonce(annonce);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Integer id, @RequestBody Annonce annonceDetails) {
        try {
            Annonce updatedAnnonce = annonceService.updateAnnonce(id, annonceDetails);
            return ResponseEntity.ok(updatedAnnonce);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnonce(@PathVariable Integer id) {
        annonceService.deleteAnnonce(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/profil/{profilId}")
    public List<Annonce> getAnnoncesByProfil(@PathVariable Integer profilId) {
        return annonceService.getAnnoncesByProfil(profilId);
    }
    
    @GetMapping("/departement/{departementId}")
    public List<Annonce> getAnnoncesByDepartement(@PathVariable Integer departementId) {
        return annonceService.getAnnoncesByDepartement(departementId);
    }
    
    @GetMapping("/{id}/candidatures/count")
    public ResponseEntity<Integer> getCandidaturesCount(@PathVariable Integer id) {
        int count = annonceService.getCandidaturesCount(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/filter")
    public List<Annonce> getFilteredAnnonces(
            @RequestParam(required = false) String nomposte,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer departementId,
            @RequestParam(required = false) Integer typeAnnonceId,
            @RequestParam(required = false) Integer profilId,
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = false) Integer diplomeId,
            @RequestParam(required = false) String dateDebutFrom,
            @RequestParam(required = false) String dateDebutTo,
            @RequestParam(required = false) String dateFinFrom,
            @RequestParam(required = false) String dateFinTo,
            @RequestParam(required = false) String datePublicationFrom,
            @RequestParam(required = false) String datePublicationTo) {
        return annonceService.getFilteredAnnonces(
            nomposte, description, departementId, typeAnnonceId, profilId, provinceId,
            diplomeId,
            dateDebutFrom, dateDebutTo, dateFinFrom, dateFinTo,
            datePublicationFrom, datePublicationTo
        );
    }
}
