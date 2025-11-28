package com.backend.Controllers;

import com.backend.entities.Province;
import com.backend.services.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/provinces")
@CrossOrigin(origins = "*")
public class ProvinceController {

    @Autowired
    private ProvinceService provinceService;

    @GetMapping
    public List<Province> getAll() {
        return provinceService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Province> getById(@PathVariable Integer id) {
        Optional<Province> p = provinceService.findById(id);
        return p.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
