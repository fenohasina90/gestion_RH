package com.backend.Controllers;

import com.backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/rh")
    public ResponseEntity<?> getRhSummary(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee
    ) {
        Map<String, Object> result = dashboardService.getRhSummary(mois, annee);
        return ResponseEntity.ok(result);
    }

    /**
     * Performance par employé (ponctualité + productivité) basée sur feuilletemps.
     */
    @GetMapping("/performance/employes")
    public ResponseEntity<?> getPerformanceEmployes(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee
    ) {
        List<Map<String, Object>> result = dashboardService.getPerformanceEmployes(mois, annee);
        return ResponseEntity.ok(result);
    }

    /**
     * Performance agrégée par département (moyenne des scores des employés).
     */
    @GetMapping("/performance/departements")
    public ResponseEntity<?> getPerformanceDepartements(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee
    ) {
        List<Map<String, Object>> result = dashboardService.getPerformanceDepartements(mois, annee);
        return ResponseEntity.ok(result);
    }
}
