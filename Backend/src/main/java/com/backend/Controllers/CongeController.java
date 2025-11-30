package com.backend.Controllers;

import com.backend.entities.Soldeconge;
import com.backend.repositories.SoldecongeRepository;
import com.backend.repositories.DemandecongeRepository;
import com.backend.repositories.CongeeffectueRepository;
import com.backend.repositories.StatutdemandeRepository;
import com.backend.repositories.ValidatedLeaveRepository;
import com.backend.repositories.JourferieRepository;
import com.backend.entities.Congeeffectue;
import com.backend.entities.Demandeconge;
import com.backend.entities.Soldeconge;
import com.backend.entities.Statutdemande;
import com.backend.entities.VCongeValide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api/conges")
@CrossOrigin(origins = "*")
public class CongeController {

    @Autowired
    private SoldecongeRepository soldecongeRepository;

    @Autowired
    private DemandecongeRepository demandecongeRepository;

    @Autowired
    private CongeeffectueRepository congeeffectueRepository;

    @Autowired
    private StatutdemandeRepository statutdemandeRepository;

    @Autowired
    private ValidatedLeaveRepository validatedLeaveRepository;

    @Autowired
    private JourferieRepository jourferieRepository;

    public static class SoldeDTO {
        public Integer employeId;
        public String nom;
        public String prenom;
        public String departement;
        public Integer typeId;
        public String type;
        public Integer annee;
        public Double joursAcquis;
        public Double joursPris;
        public Double joursRestants;
        public Long demandesEnAttente;
    }

    @GetMapping("/solde")
    public ResponseEntity<List<SoldeDTO>> getSoldeConges(
            @RequestParam(required = false) String employe,
            @RequestParam(required = false) Integer departementId,
            @RequestParam(required = false) Integer typeCongeId,
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false, defaultValue = "nom") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        List<Soldeconge> list = soldecongeRepository.findFiltered(
                (employe != null && !employe.isBlank()) ? employe : null,
                departementId,
                typeCongeId,
                annee
        );

        List<String> pendingNames = Arrays.asList("En attente", "Pending", "ATTENTE");

        Instant from;
        Instant to;
        if (annee != null) {
            LocalDate start = LocalDate.of(annee, 1, 1);
            LocalDate end = LocalDate.of(annee, 12, 31);
            from = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
            to = end.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant();
        } else {
            // Use very wide bounds instead of null to avoid DB type inference issues
            from = LocalDate.of(1970,1,1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            to = LocalDate.of(3000,12,31).atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant();
        }

        List<SoldeDTO> dtos = new ArrayList<>();
        for (Soldeconge s : list) {
            SoldeDTO dto = new SoldeDTO();
            dto.employeId = s.getIdemploye() != null ? s.getIdemploye().getId() : null;
            dto.nom = s.getIdemploye() != null ? s.getIdemploye().getNom() : null;
            dto.prenom = s.getIdemploye() != null ? s.getIdemploye().getPrenom() : null;
            dto.departement = (s.getIdemploye() != null && s.getIdemploye().getIddept() != null) ? s.getIdemploye().getIddept().getNom() : null;
            dto.typeId = s.getIdtypeconge() != null ? s.getIdtypeconge().getId() : null;
            dto.type = s.getIdtypeconge() != null ? s.getIdtypeconge().getLibelle() : null;
            dto.annee = s.getAnnee();
            dto.joursAcquis = s.getJoursacquis() != null ? s.getJoursacquis().doubleValue() : null;
            dto.joursPris = s.getJourspris() != null ? s.getJourspris().doubleValue() : null;
            dto.joursRestants = s.getJoursrestants() != null ? s.getJoursrestants().doubleValue() : null;

            if (dto.employeId != null && dto.typeId != null) {
                long pending = demandecongeRepository.countPendingByEmpTypeAndDate(
                        dto.employeId, dto.typeId, from, to, pendingNames
                );
                dto.demandesEnAttente = pending;
            } else {
                dto.demandesEnAttente = 0L;
            }
            dtos.add(dto);
        }

        Comparator<SoldeDTO> cmp;
        if ("solde".equalsIgnoreCase(sortBy)) {
            cmp = Comparator.comparing((SoldeDTO d) -> d.joursRestants == null ? -Double.MAX_VALUE : d.joursRestants);
        } else {
            cmp = Comparator.comparing((SoldeDTO d) -> (d.nom == null ? "" : d.nom) + " " + (d.prenom == null ? "" : d.prenom), String.CASE_INSENSITIVE_ORDER);
        }
        if ("desc".equalsIgnoreCase(order)) cmp = cmp.reversed();
        dtos = dtos.stream().sorted(cmp).toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/periodes")
    public ResponseEntity<List<Map<String,Object>>> getPeriodes(
            @RequestParam(required = false) Integer empId,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) Integer annee
    ) {
        List<Congeeffectue> list = congeeffectueRepository.findFiltered(empId, typeId, annee);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Congeeffectue c : list) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("datedebut", c.getDatedebut());
            m.put("datefin", c.getDatefin());
            m.put("nombrejourspris", c.getNombrejourspris());
            // Employe
            if (c.getIdemploye() != null) {
                m.put("employeId", c.getIdemploye().getId());
                m.put("employeNom", c.getIdemploye().getNom());
                m.put("employePrenom", c.getIdemploye().getPrenom());
            }
            // Type congé
            if (c.getIdtypeconge() != null) {
                m.put("typeId", c.getIdtypeconge().getId());
                m.put("typeLibelle", c.getIdtypeconge().getLibelle());
            }
            out.add(m);
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/calendar")
    public ResponseEntity<Map<String, Object>> getCalendar(
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        // month is 1-12
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Fetch validated leaves intersecting the month
        List<VCongeValide> rows = validatedLeaveRepository.findInRange(start, end);

        // Fetch holidays
        var feries = new HashSet<LocalDate>(jourferieRepository.findByDatejourBetween(start, end)
                .stream().map(j -> j.getDatejour()).toList());

        Map<LocalDate, List<String>> dayToNames = new HashMap<>();

        for (VCongeValide r : rows) {
            String nom = r.getNom() != null ? r.getNom() : "";
            String prenom = r.getPrenom() != null ? r.getPrenom() : "";
            String fullName = (nom + " " + prenom).trim();
            LocalDate d1 = r.getDatedebut();
            LocalDate d2 = r.getDatefin();
            if (d1 == null || d2 == null) continue;
            LocalDate cur = d1.isBefore(start) ? start : d1;
            LocalDate stop = d2.isAfter(end) ? end : d2;
            while (!cur.isAfter(stop)) {
                var dow = cur.getDayOfWeek();
                boolean weekend = (dow.getValue() == 6 || dow.getValue() == 7); // 6=Saturday,7=Sunday
                if (!weekend && !feries.contains(cur)) {
                    dayToNames.computeIfAbsent(cur, k -> new ArrayList<>()).add(fullName);
                }
                cur = cur.plusDays(1);
            }
        }

        // Build response: list of days with count and names
        List<Map<String,Object>> days = new ArrayList<>();
        LocalDate cur = start;
        while (!cur.isAfter(end)) {
            List<String> names = dayToNames.getOrDefault(cur, Collections.emptyList());
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("date", cur.toString());
            m.put("count", names.size());
            m.put("names", names);
            m.put("holiday", feries.contains(cur));
            m.put("weekend", cur.getDayOfWeek().getValue() >= 6);
            days.add(m);
            cur = cur.plusDays(1);
        }

        Map<String,Object> out = new LinkedHashMap<>();
        out.put("year", year);
        out.put("month", month);
        out.put("days", days);
        return ResponseEntity.ok(out);
    }

    @PostMapping("/demandes/{id}/approve")
    public ResponseEntity<?> approveDemande(@PathVariable Integer id) {
        Optional<Demandeconge> opt = demandecongeRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Demandeconge d = opt.get();

        // Resolve statut Validée (id=2 fallback to name search)
        Statutdemande statut = null;
        Optional<Statutdemande> byId = statutdemandeRepository.findById(2);
        if (byId.isPresent()) statut = byId.get();
        else statut = statutdemandeRepository.findByNomIgnoreCase("Validé RH").orElse(null);
        if (statut != null) d.setIdstatut(statut);

        // Compute year and days taken
        Integer year = d.getDatedebut() != null ? d.getDatedebut().getYear() : (d.getDatefin() != null ? d.getDatefin().getYear() : LocalDate.now().getYear());
        java.math.BigDecimal joursPris = d.getNombrejoursouvres();
        if (joursPris == null && d.getDatedebut() != null && d.getDatefin() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(d.getDatedebut(), d.getDatefin()) + 1;
            joursPris = new java.math.BigDecimal(days);
        }
        if (joursPris == null) joursPris = java.math.BigDecimal.ZERO;

        // Update solde (increment jourspris, decrement joursrestants) if a row exists
        if (d.getIdemploye() != null && d.getIdtypeconge() != null && year != null) {
            Soldeconge solde = soldecongeRepository.findOneByEmpTypeAndYear(d.getIdemploye().getId(), d.getIdtypeconge().getId(), year);
            if (solde != null) {
                java.math.BigDecimal pris = solde.getJourspris() == null ? java.math.BigDecimal.ZERO : solde.getJourspris();
                java.math.BigDecimal restants = solde.getJoursrestants() == null ? java.math.BigDecimal.ZERO : solde.getJoursrestants();
                solde.setJourspris(pris.add(joursPris));
                solde.setJoursrestants(restants.subtract(joursPris));
                solde.setDatecalcul(Instant.now());
                soldecongeRepository.save(solde);
            }
        }

        // Create congeeffectue record
        Congeeffectue ce = new Congeeffectue();
        ce.setIddemandeconge(d);
        ce.setIdemploye(d.getIdemploye());
        ce.setIdtypeconge(d.getIdtypeconge());
        ce.setDatedebut(d.getDatedebut());
        ce.setDatefin(d.getDatefin());
        ce.setNombrejourspris(joursPris);
        congeeffectueRepository.save(ce);

        // Save demande with updated statut
        demandecongeRepository.save(d);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/demandes/{id}/reject")
    public ResponseEntity<?> rejectDemande(@PathVariable Integer id) {
        Optional<Demandeconge> opt = demandecongeRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Demandeconge d = opt.get();

        // Resolve statut Rejetée (id=3 fallback to name search)
        Statutdemande statut = statutdemandeRepository.findById(3).orElseGet(() ->
                statutdemandeRepository.findByNomIgnoreCase("Refusé RH").orElse(null)
        );
        if (statut != null) d.setIdstatut(statut);
        demandecongeRepository.save(d);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/demandes/pending")
    public ResponseEntity<List<Map<String,Object>>> getPendingDemandes(
            @RequestParam(required = false) Integer empId,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) Integer annee
    ) {
        List<String> pendingNames = Arrays.asList("En attente RH", "Pending", "ATTENTE");

        Instant from;
        Instant to;
        if (annee != null) {
            LocalDate start = LocalDate.of(annee, 1, 1);
            LocalDate end = LocalDate.of(annee, 12, 31);
            from = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
            to = end.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant();
        } else {
            from = LocalDate.of(1970,1,1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            to = LocalDate.of(3000,12,31).atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant();
        }

        List<Demandeconge> list = demandecongeRepository.findPending(empId, typeId, from, to, pendingNames);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Demandeconge d : list) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", d.getId());
            m.put("datedemande", d.getDatedemande());
            m.put("datedebut", d.getDatedebut());
            m.put("datefin", d.getDatefin());
            m.put("motif", d.getMotif());
            if (d.getIdemploye() != null) {
                m.put("employeId", d.getIdemploye().getId());
                m.put("employeNom", d.getIdemploye().getNom());
                m.put("employePrenom", d.getIdemploye().getPrenom());
            }
            if (d.getIdtypeconge() != null) {
                m.put("typeId", d.getIdtypeconge().getId());
                m.put("typeLibelle", d.getIdtypeconge().getLibelle());
            }
            if (d.getIdstatut() != null) {
                m.put("statut", d.getIdstatut().getNom());
            }
            out.add(m);
        }
        return ResponseEntity.ok(out);
    }
}
