package com.backend.Controllers;

import com.backend.entities.Resultat;
import com.backend.services.ResultatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultats")
@CrossOrigin(origins = "*")
public class ResultatController {

    @Autowired
    private ResultatService resultatService;

    @GetMapping
    public ResponseEntity<List<Resultat>> getAllResultats() {
        List<Resultat> resultats = resultatService.getAllResultats();
        return ResponseEntity.ok(resultats);
    }
}
