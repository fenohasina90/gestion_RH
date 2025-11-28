package com.backend.Controllers;

import com.backend.entities.Typechamp;
import com.backend.services.TypechampService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/typechamps")
@CrossOrigin(origins = "*")
public class TypechampController {

    @Autowired
    private TypechampService typechampService;

    @GetMapping
    public List<Typechamp> getAll() {
        return typechampService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Typechamp> getById(@PathVariable Integer id) {
        Optional<Typechamp> t = typechampService.findById(id);
        return t.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
