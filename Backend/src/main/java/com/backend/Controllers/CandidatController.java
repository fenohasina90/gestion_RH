package com.backend.Controllers;

import com.backend.entities.*;
import com.backend.services.CandidatService;
import com.backend.services.CandidaturecritereService;
import com.backend.services.ComptecandidatService;
import com.backend.services.AnnonceService;
import com.backend.services.ProvinceService;
import com.backend.services.DiplomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidats")
@CrossOrigin(origins = "*")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private CandidaturecritereService candidaturecritereService;

    @Autowired
    private ComptecandidatService comptecandidatService;

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private DiplomeService diplomeService;

    @GetMapping
    public List<Candidat> getAllCandidats() {
        return candidatService.getAllCandidats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidat> getCandidatById(@PathVariable Integer id) {
        Candidat candidat = candidatService.getCandidatById(id);
        if (candidat != null) {
            return ResponseEntity.ok(candidat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/annonce/{annonceId}")
    public ResponseEntity<List<Candidat>> getCandidatsByAnnonce(@PathVariable Integer annonceId) {
        List<Candidat> candidats = candidatService.getCandidatsByAnnonce(annonceId);
        return ResponseEntity.ok(candidats);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCandidat(@RequestBody Map<String, Object> candidatureData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extraire les données du candidat
            String nom = (String) candidatureData.get("nom");
            String prenom = (String) candidatureData.get("prenom");
            String dateNaissanceStr = (String) candidatureData.get("datenaissance");
            String adresse = (String) candidatureData.get("adresse");
            String cv = (String) candidatureData.get("cv");
            // salaire peut arriver comme Number (Double/Integer) ou String
            Double salaire = null;
            Object salaireObj = candidatureData.get("salaire");
            if (salaireObj instanceof Number) {
                salaire = ((Number) salaireObj).doubleValue();
            } else if (salaireObj instanceof String) {
                String s = (String) salaireObj;
                if (s != null && !s.isEmpty()) {
                    try {
                        salaire = Double.parseDouble(s);
                    } catch (NumberFormatException ignored) {}
                }
            }
            Integer annonceId = (Integer) candidatureData.get("idannonce");
            Integer compteId = (Integer) candidatureData.get("idcomptecandidat");
            Integer provinceId = null;
            Object provinceObj = candidatureData.get("idprovince");
            if (provinceObj instanceof Number) {
                provinceId = ((Number) provinceObj).intValue();
            } else if (provinceObj instanceof String) {
                try { provinceId = Integer.parseInt((String) provinceObj); } catch (NumberFormatException ignored) {}
            }

            Integer diplomeId = null;
            Object diplomeObj = candidatureData.get("iddiplome");
            if (diplomeObj instanceof Number) {
                diplomeId = ((Number) diplomeObj).intValue();
            } else if (diplomeObj instanceof String) {
                try { diplomeId = Integer.parseInt((String) diplomeObj); } catch (NumberFormatException ignored) {}
            }

            // Créer le candidat
            Candidat candidat = new Candidat();
            candidat.setNom(nom);
            candidat.setPrenom(prenom);
            if (dateNaissanceStr != null && !dateNaissanceStr.isEmpty()) {
                candidat.setDatenaissance(LocalDate.parse(dateNaissanceStr));
            }
            candidat.setAdresse(adresse);
            candidat.setCv(cv);
            if (salaire != null) {
                candidat.setSalaire(salaire);
            }

            // Associer l'annonce
            if (annonceId != null) {
                Annonce annonce = annonceService.getAnnonceById(annonceId);
                candidat.setIdannonce(annonce);
            }

            // Associer le compte candidat
            if (compteId != null) {
                Comptecandidat compte = comptecandidatService.findById(compteId);
                candidat.setIdcomptecandidat(compte);
            }

            // Associer la province
            if (provinceId != null) {
                provinceService.findById(provinceId).ifPresent(candidat::setIdprovince);
            }

            // Associer la province
            Candidat savedCandidat = candidatService.saveCandidat(candidat);
            if (provinceId != null) {
                provinceService.findById(provinceId).ifPresent(savedCandidat::setIdprovince);
            }
            // Associer le diplôme sélectionné
            if (diplomeId != null) {
                diplomeService.getDiplomeById(diplomeId).ifPresent(savedCandidat::setIddiplome);
            }
            // Resauvegarder si des associations ont été ajoutées après la création
            if (provinceId != null || diplomeId != null) {
                savedCandidat = candidatService.saveCandidat(savedCandidat);
            }

            // Traiter les critères
            Map<String, Object> criteresValues = new HashMap<>();
            for (Map.Entry<String, Object> entry : candidatureData.entrySet()) {
                if (entry.getKey().startsWith("critere_")) {
                    criteresValues.put(entry.getKey(), entry.getValue());
                }
            }

            if (!criteresValues.isEmpty() && savedCandidat.getIdannonce() != null) {
                candidaturecritereService.saveCandidatureCriteres(
                    savedCandidat, 
                    savedCandidat.getIdannonce(), 
                    criteresValues
                );
            }

            response.put("success", true);
            response.put("message", "Candidature soumise avec succès");
            response.put("candidat", savedCandidat);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la soumission: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidat> updateCandidat(@PathVariable Integer id, @RequestBody Candidat candidatDetails) {
        Candidat candidat = candidatService.getCandidatById(id);
        if (candidat != null) {
            candidat.setNom(candidatDetails.getNom());
            candidat.setPrenom(candidatDetails.getPrenom());
            candidat.setDatenaissance(candidatDetails.getDatenaissance());
            candidat.setAdresse(candidatDetails.getAdresse());
            candidat.setCv(candidatDetails.getCv());
            candidat.setSalaire(candidatDetails.getSalaire());
            candidat.setIdannonce(candidatDetails.getIdannonce());
            candidat.setIdstatut(candidatDetails.getIdstatut());
            candidat.setIdcomptecandidat(candidatDetails.getIdcomptecandidat());
            return ResponseEntity.ok(candidatService.saveCandidat(candidat));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidat(@PathVariable Integer id) {
        candidatService.deleteCandidat(id);
        return ResponseEntity.ok().build();
    }
}
