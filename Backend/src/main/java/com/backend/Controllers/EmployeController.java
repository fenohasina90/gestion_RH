package com.backend.Controllers;

import com.backend.entities.Employe;
import com.backend.entities.Contrat;
import com.backend.services.EmployeService;
import com.backend.repositories.ContratRepository;
import com.backend.repositories.TypecontratRepository;
import com.backend.repositories.StatutcontratRepository;
import com.backend.repositories.HistoriqueposteRepository;
import com.backend.repositories.DocumentemployeRepository;
import com.backend.entities.Documentemploye;
import com.backend.entities.Historiqueposte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employes")
@CrossOrigin(origins = "*")
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private TypecontratRepository typecontratRepository;

    @Autowired
    private StatutcontratRepository statutcontratRepository;

    @Autowired
    private HistoriqueposteRepository historiqueposteRepository;

    @Autowired
    private DocumentemployeRepository documentemployeRepository;

    @GetMapping
    public ResponseEntity<List<Contrat>> getAllEmployees() {
        // Récupérer tous les contrats (qui contiennent les infos employés)
        List<Contrat> contrats = employeService.getFilteredEmployeeContracts(null, null, null, null, null, null);
        return ResponseEntity.ok(contrats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employe> getEmployeById(@PathVariable Integer id) {
        Employe employe = employeService.getEmployeById(id);
        if (employe != null) {
            return ResponseEntity.ok(employe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/contrats")
    public ResponseEntity<List<Contrat>> getContratsByEmploye(@PathVariable Integer id) {
        List<Contrat> list = contratRepository.findByEmployeIdOrderByDatedebutDesc(id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/historiquepostes")
    public ResponseEntity<List<Historiqueposte>> getHistoriquesPoste(@PathVariable Integer id) {
        List<Historiqueposte> list = historiqueposteRepository.findByEmployeIdOrderByDatedebutDesc(id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<Documentemploye>> getDocumentsByEmploye(@PathVariable Integer id) {
        List<Documentemploye> list = documentemployeRepository.findByEmployeIdOrderByDateuploadDesc(id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/documents/{docId}")
    public ResponseEntity<Documentemploye> getDocumentById(@PathVariable Integer docId) {
        return documentemployeRepository.findById(docId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Contrat>> getFilteredEmployees(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Integer departementId,
            @RequestParam(required = false) String poste,
            @RequestParam(required = false) String typeContrat,
            @RequestParam(required = false) Double salaireMin,
            @RequestParam(required = false) String dateDebutFrom) {
        
        LocalDate dateFrom = null;
        if (dateDebutFrom != null && !dateDebutFrom.isEmpty()) {
            try {
                dateFrom = LocalDate.parse(dateDebutFrom);
            } catch (Exception e) {
                // Ignorer les dates mal formatées
            }
        }
        
        List<Contrat> contrats = employeService.getFilteredEmployeeContracts(
            nom, departementId, poste, typeContrat, salaireMin, dateFrom
        );
        return ResponseEntity.ok(contrats);
    }

    @GetMapping("/departement/{departementId}")
    public ResponseEntity<List<Employe>> getEmployesByDepartement(@PathVariable Integer departementId) {
        List<Employe> employes = employeService.getEmployesByDepartement(departementId);
        return ResponseEntity.ok(employes);
    }

    @PostMapping
    public ResponseEntity<Employe> createEmploye(@RequestBody Employe employe) {
        Employe savedEmploye = employeService.saveEmploye(employe);
        return ResponseEntity.ok(savedEmploye);
    }

    // DTO pour créer un contrat pour un employé existant
    public static class NewContratRequest {
        public String poste;
        public Double salaire;
        public String typecontratLibelle; // libellé du type de contrat (ex: CDI)
        public Integer typecontratId;     // si fourni, prioritaire
        public LocalDate datedebut;
        public LocalDate datefin;         // optionnel si nombremois présent
        public Integer nombremois;
        public Integer periodessai;
        public Integer statutId;
        public String statutNom;          // si fourni, utilisé pour rechercher le statut
    }

    @PostMapping("/{id}/contrat")
    public ResponseEntity<Contrat> createContratForEmploye(@PathVariable Integer id, @RequestBody NewContratRequest req) {
        Employe employe = employeService.getEmployeById(id);
        if (employe == null) return ResponseEntity.notFound().build();

        Contrat c = new Contrat();
        c.setIdemploye(employe);
        c.setPoste(req.poste);
        c.setSalaire(req.salaire);
        c.setDatedebut(req.datedebut);
        c.setNombremois(req.nombremois);
        c.setPeriodessai(req.periodessai);
        // support champs legacy pour UI
        c.setTypecontrat(req.typecontratLibelle);

        // datefin calculée si non fournie et si nombremois présent
        if (req.datefin != null) {
            c.setDatefin(req.datefin);
        } else if (req.datedebut != null && req.nombremois != null) {
            c.setDatefin(req.datedebut.plusMonths(req.nombremois));
        }

        // Lier Typecontrat par id ou par libellé
        if (req.typecontratId != null) {
            typecontratRepository.findById(req.typecontratId).ifPresent(c::setIdtypecontrat);
        } else if (req.typecontratLibelle != null && !req.typecontratLibelle.isBlank()) {
            // simple recherche par libellé exact (ajouter repo method si besoin)
            typecontratRepository.findAll().stream()
                    .filter(t -> req.typecontratLibelle.equalsIgnoreCase(t.getLibelle()))
                    .findFirst()
                    .ifPresent(c::setIdtypecontrat);
        }

        // Lier Statutcontrat par id ou par nom, sinon tenter 'Actif'
        if (req.statutId != null) {
            statutcontratRepository.findById(req.statutId).ifPresent(c::setIdstatut);
        } else if (req.statutNom != null && !req.statutNom.isBlank()) {
            statutcontratRepository.findByNomIgnoreCase(req.statutNom).ifPresent(c::setIdstatut);
        } else {
            statutcontratRepository.findByNomIgnoreCase("Actif").ifPresent(c::setIdstatut);
        }

        Contrat saved = contratRepository.save(c);

        // Créer un historique de poste à l'embauche
        try {
            Historiqueposte hist = new Historiqueposte();
            hist.setIdemploye(employe);
            hist.setPosteoccupe(req.poste);
            // Départements et catégorie depuis l'employé si disponibles
            if (employe.getIddept() != null) {
                hist.setIddepartement(employe.getIddept());
            }
            if (employe.getIdcategorie() != null) {
                hist.setIdcategorie(employe.getIdcategorie());
            }
            // Date début = date de début du contrat
            hist.setDatedebut(saved.getDatedebut());
            // Date fin = date de fin du contrat si calculée (en fonction de la durée)
            hist.setDatefin(saved.getDatefin());
            // Optionnel: motif
            hist.setMotif("Embauche");
            historiqueposteRepository.save(hist);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employe> updateEmploye(@PathVariable Integer id, @RequestBody Employe employe) {
        Employe existingEmploye = employeService.getEmployeById(id);
        if (existingEmploye != null) {
            employe.setId(id);
            Employe updatedEmploye = employeService.saveEmploye(employe);
            return ResponseEntity.ok(updatedEmploye);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmploye(@PathVariable Integer id) {
        Employe existingEmploye = employeService.getEmployeById(id);
        if (existingEmploye != null) {
            employeService.deleteEmploye(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Employe> uploadPhoto(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        Employe employe = employeService.getEmployeById(id);
        if (employe == null) {
            return ResponseEntity.notFound().build();
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            // Create uploads directory if not exists
            Path uploadDir = Paths.get("uploads", "employees");
            Files.createDirectories(uploadDir);

            // Normalize filename
            String original = file.getOriginalFilename();
            String filename = (original == null || original.isBlank()) ? ("emp_" + id + ".bin") : (id + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_"));
            Path target = uploadDir.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Store relative path for frontend to access if served statically or via controller
            String relativePath = "/" + uploadDir.toString().replace('\\','/') + "/" + filename;
            employe.setPhoto(relativePath);
            Employe saved = employeService.saveEmploye(employe);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
