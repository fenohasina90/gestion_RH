package com.backend.Controllers;

import com.backend.entities.Typeconge;
import com.backend.entities.Typepointage;
import com.backend.repositories.TypecongeRepository;
import com.backend.repositories.TypepointageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LookupController {

    @Autowired
    private TypecongeRepository typecongeRepository;

    @Autowired
    private TypepointageRepository typepointageRepository;


    @GetMapping("/typeconges")
    public ResponseEntity<List<Typeconge>> getTypeconges() {
        return ResponseEntity.ok(typecongeRepository.findAll());
    }

    @GetMapping("/typepointages")
    public ResponseEntity<List<Typepointage>> getTypepointages() {
        return ResponseEntity.ok(typepointageRepository.findAll());
    }

}
