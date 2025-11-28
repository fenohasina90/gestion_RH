package com.backend.services;

import com.backend.entities.Candidat;
import com.backend.entities.Entretien;
import com.backend.entities.Statutentretien;
import com.backend.entities.Annonce;
import com.backend.entities.Resultat;
import com.backend.repositories.CandidatRepository;
import com.backend.repositories.EntretienRepository;
import com.backend.repositories.StatutentretienRepository;
import com.backend.repositories.AnnonceRepository;
import com.backend.repositories.ResultatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EntretienService {

    @Autowired
    private EntretienRepository entretienRepository;

    @Autowired
    private StatutentretienRepository statutentretienRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private ResultatRepository resultatRepository;

    // Create a new interview
    public Entretien createEntretien(Integer candidatId, Integer annonceId, LocalDateTime dateHeure) {
        Optional<Candidat> candidat = candidatRepository.findById(candidatId);
        if (candidat.isEmpty()) {
            throw new RuntimeException("Candidat not found");
        }

        Optional<Annonce> annonce = annonceRepository.findById(annonceId);
        if (annonce.isEmpty()) {
            throw new RuntimeException("Annonce not found");
        }

        // Check if candidate already has an active interview for this specific announcement
        Optional<Entretien> existingEntretien = entretienRepository.findActiveByCandidatAndAnnonce(candidatId, annonceId);
        if (existingEntretien.isPresent()) {
            throw new RuntimeException("Le candidat a déjà un entretien planifié ou en cours pour cette annonce");
        }

        // Get "Planifie" status
        Optional<Statutentretien> planifieStatus = statutentretienRepository.findByNom("Planifie");
        if (planifieStatus.isEmpty()) {
            throw new RuntimeException("Statut 'Planifie' not found");
        }

        Entretien entretien = new Entretien();
        entretien.setIdcandidat(candidat.get());
        entretien.setIdannonce(annonce.get());
        entretien.setDateheure(dateHeure);
        entretien.setIdstatut(planifieStatus.get());
        // idresultat remains null as requested

        return entretienRepository.save(entretien);
    }

    // Get all interviews for a candidate
    public List<Entretien> getEntretiensByCandidat(Integer candidatId) {
        return entretienRepository.findByIdcandidat_Id(candidatId);
    }

    // Get active interview for a candidate
    public Optional<Entretien> getActiveEntretienByCandidat(Integer candidatId) {
        return entretienRepository.findActiveByCandidatId(candidatId);
    }

    // Get all interviews
    public List<Entretien> getAllEntretiens() {
        return entretienRepository.findAll();
    }

    // Get interview by ID
    public Optional<Entretien> getEntretienById(Integer id) {
        return entretienRepository.findById(id);
    }

    // Get interviews by date
    public List<Entretien> getEntretiensByDate(String date) {
        return entretienRepository.findByDateheureBetween(
            java.time.LocalDate.parse(date).atStartOfDay(),
            java.time.LocalDate.parse(date).atTime(23, 59, 59)
        );
    }

    // Rate an interview (set result and status to "Termine")
    public Entretien rateInterview(Integer entretienId, Integer resultatId) {
        Optional<Entretien> entretienOpt = entretienRepository.findById(entretienId);
        if (entretienOpt.isEmpty()) {
            throw new RuntimeException("Entretien not found with id: " + entretienId);
        }

        Optional<Resultat> resultatOpt = resultatRepository.findById(resultatId);
        if (resultatOpt.isEmpty()) {
            throw new RuntimeException("Resultat not found with id: " + resultatId);
        }

        Optional<Statutentretien> termineStatus = statutentretienRepository.findByNom("Termine");
        if (termineStatus.isEmpty()) {
            throw new RuntimeException("Statut 'Termine' not found");
        }

        Entretien entretien = entretienOpt.get();
        entretien.setIdresultat(resultatOpt.get());
        entretien.setIdstatut(termineStatus.get());

        return entretienRepository.save(entretien);
    }

    // Get interviews with "Bon niveau" result
    public List<Entretien> getEntretiensWithBonNiveau() {
        return entretienRepository.findByIdresultat_Appreciation("Bon niveau");
    }

    // Filtered search across entretiens (includes all, even when idresultat is null)
    public List<Entretien> getFilteredEntretiens(
            String candidateQuery,
            String positionQuery,
            String statusQuery,
            String resultQuery,
            String dateFrom,
            String dateTo
    ) {
        LocalDateTime from = null;
        LocalDateTime to = null;
        try {
            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                from = LocalDate.parse(dateFrom).atStartOfDay();
            }
            if (dateTo != null && !dateTo.trim().isEmpty()) {
                to = LocalDate.parse(dateTo).atTime(23, 59, 59);
            }
        } catch (Exception e) {
            System.err.println("[EntretienService] Erreur parsing dates: " + e.getMessage());
        }

        return entretienRepository.findFilteredEntretiens(
                (candidateQuery != null && candidateQuery.trim().isEmpty()) ? null : candidateQuery,
                (positionQuery != null && positionQuery.trim().isEmpty()) ? null : positionQuery,
                (statusQuery != null && statusQuery.trim().isEmpty()) ? null : statusQuery,
                (resultQuery != null && resultQuery.trim().isEmpty()) ? null : resultQuery,
                from,
                to
        );
    }
}
