package com.backend.Controllers;

import com.backend.dto.CreatePointageRequest;
import com.backend.entities.Pointage;
import com.backend.services.PointageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/pointages")
@CrossOrigin
public class PointageController {
    private final PointageService pointageService;

    public PointageController(PointageService pointageService) {
        this.pointageService = pointageService;
    }

    // GET /api/pointages/today
    @GetMapping("/today")
    public ResponseEntity<List<Pointage>> getToday() {
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(pointageService.getPointagesOfDay(today));
    }

    // GET /api/pointages/employee/{id}/date/{date}
    @GetMapping("/employee/{id}/date/{date}")
    public ResponseEntity<List<Pointage>> getByEmployeeAndDate(
            @PathVariable("id") Integer empId,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(pointageService.getPointagesOfEmployeeOn(date, empId));
    }

    // GET /api/pointages/employee/matricule/{matricule}/date/{date}
    @GetMapping("/employee/matricule/{matricule}/date/{date}")
    public ResponseEntity<List<Pointage>> getByMatriculeAndDate(
            @PathVariable("matricule") String matricule,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(pointageService.getPointagesOfEmployeeOnByMatricule(date, matricule));
    }

    // POST /api/pointages
    @PostMapping
    public ResponseEntity<Pointage> create(@RequestBody CreatePointageRequest req) {
        LocalDate date = LocalDate.parse(req.date);
        LocalTime time = LocalTime.parse(req.time);
        Pointage created = pointageService.createManual(req.employeId, date, time, req.typeId, req.commentaire);
        return ResponseEntity.ok(created);
    }

    // PATCH /api/pointages/{id}/invalidate
    @PatchMapping("/{id}/invalidate")
    public ResponseEntity<Void> invalidate(@PathVariable("id") Integer id) {
        pointageService.invalidate(id);
        return ResponseEntity.noContent().build();
    }
}
