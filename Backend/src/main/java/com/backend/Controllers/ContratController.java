package com.backend.Controllers;

import com.backend.entities.Contrat;
import com.backend.entities.Historiquecontrat;
import com.backend.repositories.HistoriquecontratRepository;
import com.backend.services.ContratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrats")
@CrossOrigin(origins = "*")
public class ContratController {

    @Autowired
    private ContratService contratService;

    @Autowired
    private HistoriquecontratRepository historiquecontratRepository;

    @PostMapping("/generate")
    public ResponseEntity<?> generateContract(@RequestBody ContractRequest request) {
        try {
            System.out.println("=== DEBUT CONTROLLER ===");
            System.out.println("Received contract request: " + request);
            System.out.println("CandidatId: " + request.getCandidatId());
            System.out.println("StartDate: " + request.getStartDate());
            System.out.println("Duration: " + request.getDuration());
            System.out.println("Poste: " + request.getPoste());
            
            if (request.getCandidatId() == null) {
                System.out.println("ERREUR: CandidatId est null");
                return ResponseEntity.badRequest().body("CandidatId ne peut pas être null");
            }
            
            if (request.getStartDate() == null) {
                System.out.println("ERREUR: StartDate est null");
                return ResponseEntity.badRequest().body("StartDate ne peut pas être null");
            }
            
            if (request.getDuration() == null) {
                System.out.println("ERREUR: Duration est null");
                return ResponseEntity.badRequest().body("Duration ne peut pas être null");
            }
            
            Contrat contrat = contratService.generateContract(
                request.getCandidatId(),
                request.getStartDate(),
                request.getDuration(),
                request.getPoste()
            );
            System.out.println("=== CONTRAT GENERE AVEC SUCCES ===");
            return ResponseEntity.ok(contrat);
        } catch (Exception e) {
            System.out.println("=== ERREUR DANS CONTROLLER ===");
            System.out.println("Message d'erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrat> getContratById(@PathVariable Integer id) {
        Optional<Contrat> contrat = contratService.findById(id);
        return contrat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/historiques")
    public ResponseEntity<List<Historiquecontrat>> getHistoriqueByContrat(@PathVariable Integer id) {
        List<Historiquecontrat> list = historiquecontratRepository.findByContratId(id);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/confirm-hiring")
    public ResponseEntity<Void> confirmHiring(@PathVariable Integer id) {
        try {
            contratService.confirmHiring(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Contrat>> listContracts() {
        List<Contrat> contrats = contratService.findAll();
        return ResponseEntity.ok(contrats);
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> listContractTypes() {
        return ResponseEntity.ok(contratService.listTypes());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Contrat>> filterContracts(
            @RequestParam(required = false) Integer departementId,
            @RequestParam(required = false) String typeContrat,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        List<Contrat> contrats = contratService.filterContrats(departementId, typeContrat, status, search);
        return ResponseEntity.ok(contrats);
    }

    // Classe interne pour la requête de génération de contrat
    public static class ContractRequest {
        private Integer candidatId;
        private LocalDate startDate;
        private Integer duration;
        private String poste;

        // Getters et setters
        public Integer getCandidatId() {
            return candidatId;
        }

        public void setCandidatId(Integer candidatId) {
            this.candidatId = candidatId;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public String getPoste() {
            return poste;
        }

        public void setPoste(String poste) {
            this.poste = poste;
        }
    }
}
