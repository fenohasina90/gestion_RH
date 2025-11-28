package com.backend.Controllers;

import com.backend.entities.Typecontrat;
import com.backend.services.TypecontratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/typecontrats")
@CrossOrigin(origins = "*")
public class TypecontratController {

    @Autowired
    private TypecontratService typecontratService;

    @GetMapping
    public List<Typecontrat> getAll() {
        return typecontratService.getAll();
    }

    @GetMapping("/{id}")
    public Typecontrat getById(@PathVariable Integer id) {
        return typecontratService.getById(id);
    }
}
