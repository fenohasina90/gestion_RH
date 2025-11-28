package com.backend.Controllers;

import com.backend.entities.Typeannonce;
import com.backend.services.TypeannonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/typeannonces")
@CrossOrigin(origins = "*")
public class TypeAnnonceController {
    
    @Autowired
    private TypeannonceService typeannonceService;
    
    @GetMapping
    public List<Typeannonce> getAllTypeannonces() {
        return typeannonceService.getAllTypeannonces();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Typeannonce> getTypeannonceById(@PathVariable Integer id) {
        return typeannonceService.getTypeannonceById(id)
            .map(typeannonce -> ResponseEntity.ok().body(typeannonce))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Typeannonce createTypeannonce(@RequestBody Typeannonce typeannonce) {
        return typeannonceService.createTypeannonce(typeannonce);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Typeannonce> updateTypeannonce(@PathVariable Integer id, @RequestBody Typeannonce typeannonceDetails) {
        try {
            Typeannonce updatedTypeannonce = typeannonceService.updateTypeannonce(id, typeannonceDetails);
            return ResponseEntity.ok(updatedTypeannonce);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTypeannonce(@PathVariable Integer id) {
        typeannonceService.deleteTypeannonce(id);
        return ResponseEntity.ok().build();
    }
}
