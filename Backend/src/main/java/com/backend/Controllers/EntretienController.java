package com.backend.Controllers;

import com.backend.entities.Entretien;
import com.backend.services.EntretienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entretiens")
@CrossOrigin(origins = "*")
public class EntretienController {

    @Autowired
    private EntretienService entretienService;

    @PostMapping("/create")
    public ResponseEntity<?> createEntretien(@RequestBody Map<String, Object> request) {
        try {
            Integer candidatId = (Integer) request.get("candidatId");
            Integer annonceId = (Integer) request.get("annonceId");
            String dateStr = (String) request.get("date");
            String timeStr = (String) request.get("time");
            
            // Combine date and time
            String dateTimeStr = dateStr + "T" + timeStr;
            LocalDateTime dateHeure = LocalDateTime.parse(dateTimeStr);
            
            Entretien entretien = entretienService.createEntretien(candidatId, annonceId, dateHeure);
            return ResponseEntity.ok(entretien);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<List<Entretien>> getEntretiensByCandidat(@PathVariable Integer candidatId) {
        List<Entretien> entretiens = entretienService.getEntretiensByCandidat(candidatId);
        return ResponseEntity.ok(entretiens);
    }

    @GetMapping("/candidat/{candidatId}/active")
    public ResponseEntity<?> getActiveEntretienByCandidat(@PathVariable Integer candidatId) {
        return entretienService.getActiveEntretienByCandidat(candidatId)
                .map(entretien -> ResponseEntity.ok(entretien))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Entretien>> getAllEntretiens() {
        List<Entretien> entretiens = entretienService.getAllEntretiens();
        return ResponseEntity.ok(entretiens);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Entretien>> getEntretiensByDate(@PathVariable String date) {
        List<Entretien> entretiens = entretienService.getEntretiensByDate(date);
        return ResponseEntity.ok(entretiens);
    }

    @PutMapping("/{id}/rate")
    public ResponseEntity<Entretien> rateInterview(@PathVariable Integer id, @RequestBody RateInterviewRequest request) {
        try {
            Entretien entretien = entretienService.rateInterview(id, request.getResultatId());
            return ResponseEntity.ok(entretien);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class RateInterviewRequest {
        private Integer resultatId;

        public Integer getResultatId() {
            return resultatId;
        }

        public void setResultatId(Integer resultatId) {
            this.resultatId = resultatId;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEntretienById(@PathVariable Integer id) {
        return entretienService.getEntretienById(id)
                .map(entretien -> ResponseEntity.ok(entretien))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bon-niveau")
    public ResponseEntity<List<Entretien>> getEntretiensWithBonNiveau() {
        List<Entretien> entretiens = entretienService.getEntretiensWithBonNiveau();
        return ResponseEntity.ok(entretiens);
    }

    // Results endpoint: returns all interviews regardless of result, with optional backend filters
    @GetMapping("/results")
    public ResponseEntity<List<Entretien>> getInterviewResults(
            @RequestParam(required = false) String candidate,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo
    ) {
        List<Entretien> results = entretienService.getFilteredEntretiens(candidate, position, status, result, dateFrom, dateTo);
        return ResponseEntity.ok(results);
    }
}
