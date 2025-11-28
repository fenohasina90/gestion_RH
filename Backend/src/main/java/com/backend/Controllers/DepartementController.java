package com.backend.Controllers;

import com.backend.entities.Departement;
import com.backend.services.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
@CrossOrigin(origins = "*")
public class DepartementController {
    
    @Autowired
    private DepartementService departementService;
    
    @GetMapping
    public List<Departement> getAllDepartements() {
        return departementService.getAllDepartements();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable Integer id) {
        Departement departement = departementService.getDepartementById(id);
        if (departement != null) {
            return ResponseEntity.ok(departement);
        }
        return ResponseEntity.notFound().build();
    }
}
