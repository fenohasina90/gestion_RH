package com.backend.Controllers;

import com.backend.dto.TimesheetDto;
import com.backend.services.PointageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feuilletemps")
@CrossOrigin
public class FeuilletempsController {
    private final PointageService pointageService;

    public FeuilletempsController(PointageService pointageService) {
        this.pointageService = pointageService;
    }

    // GET /api/feuilletemps/employee/{id}?mois=&annee=
    @GetMapping("/employee/{id}")
    public ResponseEntity<TimesheetDto> getEmployeeMonthly(
            @PathVariable("id") Integer employeId,
            @RequestParam(value = "mois", required = false) Integer mois,
            @RequestParam(value = "annee", required = false) Integer annee
    ){
        TimesheetDto dto = pointageService.getMonthlyTimesheet(employeId, mois, annee);
        return ResponseEntity.ok(dto);
    }
}
