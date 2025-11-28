package com.backend.Controllers;

import com.backend.entities.Categoriepersonnel;
import com.backend.services.CategoriepersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoriepersonnelController {

    @Autowired
    private CategoriepersonnelService categoriepersonnelService;

    @GetMapping
    public List<Categoriepersonnel> getAll() {
        return categoriepersonnelService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoriepersonnel> getById(@PathVariable Integer id) {
        Categoriepersonnel c = categoriepersonnelService.getById(id);
        if (c != null) return ResponseEntity.ok(c);
        return ResponseEntity.notFound().build();
    }
}
