package com.backend.Controllers;

import com.backend.entities.Diplome;
import com.backend.services.DiplomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diplomes")
@CrossOrigin(origins = "*")
public class DiplomeController {
    
    @Autowired
    private DiplomeService diplomeService;
    
    @GetMapping
    public List<Diplome> getAllDiplomes() {
        return diplomeService.getAllDiplomes();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Diplome> getDiplomeById(@PathVariable Integer id) {
        Optional<Diplome> diplome = diplomeService.getDiplomeById(id);
        return diplome.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Diplome createDiplome(@RequestBody Diplome diplome) {
        return diplomeService.saveDiplome(diplome);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Diplome> updateDiplome(@PathVariable Integer id, @RequestBody Diplome diplome) {
        Optional<Diplome> existingDiplome = diplomeService.getDiplomeById(id);
        if (existingDiplome.isPresent()) {
            diplome.setId(id);
            return ResponseEntity.ok(diplomeService.saveDiplome(diplome));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiplome(@PathVariable Integer id) {
        if (diplomeService.getDiplomeById(id).isPresent()) {
            diplomeService.deleteDiplome(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
