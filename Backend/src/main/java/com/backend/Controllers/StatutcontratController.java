package com.backend.Controllers;

import com.backend.entities.Statutcontrat;
import com.backend.services.StatutcontratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statutcontrats")
@CrossOrigin(origins = "*")
public class StatutcontratController {

    @Autowired
    private StatutcontratService statutcontratService;

    @GetMapping
    public ResponseEntity<List<Statutcontrat>> getAll() {
        return ResponseEntity.ok(statutcontratService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Statutcontrat> getById(@PathVariable Integer id) {
        return statutcontratService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
