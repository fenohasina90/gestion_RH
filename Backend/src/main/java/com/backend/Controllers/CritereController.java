package com.backend.Controllers;

import com.backend.entities.Critere;
import com.backend.services.CritereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/criteres")
@CrossOrigin(origins = "*")
public class CritereController {
    
    @Autowired
    private CritereService critereService;
    
    @GetMapping
    public List<Critere> getAllCriteres() {
        return critereService.getAllCriteres();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Critere> getCritereById(@PathVariable Integer id) {
        Optional<Critere> critere = critereService.getCritereById(id);
        return critere.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Critere createCritere(@RequestBody Critere critere) {
        return critereService.saveCritere(critere);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Critere> updateCritere(@PathVariable Integer id, @RequestBody Critere critere) {
        Optional<Critere> existingCritere = critereService.getCritereById(id);
        if (existingCritere.isPresent()) {
            critere.setId(id);
            return ResponseEntity.ok(critereService.saveCritere(critere));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCritere(@PathVariable Integer id) {
        if (critereService.getCritereById(id).isPresent()) {
            critereService.deleteCritere(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
