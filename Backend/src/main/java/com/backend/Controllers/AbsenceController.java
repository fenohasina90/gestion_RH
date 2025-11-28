package com.backend.Controllers;

import com.backend.dto.CreateAbsenceRequest;
import com.backend.services.PointageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/absences")
@CrossOrigin
public class AbsenceController {
    private final PointageService pointageService;

    public AbsenceController(PointageService pointageService) {
        this.pointageService = pointageService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateAbsenceRequest req) {
        LocalDate date = LocalDate.parse(req.date);
        pointageService.addAbsence(req.employeId, date, req.commentaire);
        return ResponseEntity.noContent().build();
    }
}
