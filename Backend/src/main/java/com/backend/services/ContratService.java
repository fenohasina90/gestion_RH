package com.backend.services;

import com.backend.entities.*;
import com.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Transactional
    public Contrat generateContract(Integer candidatId, LocalDate startDate, Integer duration, String poste) {
        System.out.println("=== Début generateContract ===");
        System.out.println("candidatId: " + candidatId);
        System.out.println("startDate: " + startDate);
        System.out.println("duration: " + duration);
        System.out.println("poste: " + poste);
        
        try {
            // Récupérer le candidat
            Optional<Candidat> candidatOpt = candidatRepository.findById(candidatId);
            if (!candidatOpt.isPresent()) {
                System.out.println("Erreur: Candidat non trouvé avec ID: " + candidatId);
                throw new RuntimeException("Candidat non trouvé avec ID: " + candidatId);
            }
            
            Candidat candidat = candidatOpt.get();
            System.out.println("Candidat trouvé: " + candidat.getNom() + " " + candidat.getPrenom());
            
            // Créer un nouvel employé à partir du candidat
            Employe employe = new Employe();
            employe.setNom(candidat.getNom());
            employe.setPrenom(candidat.getPrenom());
            employe.setAdresse(candidat.getAdresse());
            // Vous pouvez définir le département selon vos besoins
            System.out.println("Sauvegarde de l'employé...");
            employe = employeRepository.save(employe);
            System.out.println("Employé sauvegardé avec ID: " + employe.getId());
            
            // Créer le contrat
            Contrat contrat = new Contrat();
            contrat.setIdemploye(employe);
            contrat.setDatedebut(startDate);
            contrat.setNombremois(duration);
            contrat.setTypecontrat("Contrat d'essai");
            contrat.setPoste(poste);
            contrat.setSalaire(candidat.getSalaire());
            
            System.out.println("Sauvegarde du contrat...");
            contrat = contratRepository.save(contrat);
            System.out.println("Contrat sauvegardé avec ID: " + contrat.getId());
            
            // Ajouter les informations transient pour l'affichage
            contrat.setCandidat(candidat);
            contrat.setDuration(duration);
            contrat.setStartDate(startDate);
            contrat.setEndDate(startDate.plusMonths(duration));
            
            System.out.println("Candidat ajouté au contrat: " + candidat.getNom() + " " + candidat.getPrenom());
            if (candidat.getIdcomptecandidat() != null) {
                System.out.println("Email candidat: " + candidat.getIdcomptecandidat().getEmail());
            }
            
            System.out.println("=== Fin generateContract - Succès ===");
            return contrat;
            
        } catch (Exception e) {
            System.out.println("=== Erreur dans generateContract ===");
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<Contrat> findById(Integer id) {
        Optional<Contrat> contratOpt = contratRepository.findById(id);
        if (contratOpt.isPresent()) {
            Contrat contrat = contratOpt.get();
            System.out.println("Contrat trouvé avec ID: " + id);
            
            // Enrichir avec les données transient
            if (contrat.getIdemploye() != null) {
                System.out.println("Employé trouvé: " + contrat.getIdemploye().getNom());
                
                // Essayer de retrouver le candidat original via son nom/prénom
                // (Ceci est une solution temporaire - idéalement il faudrait une relation directe)
                Optional<Candidat> candidatOriginal = candidatRepository.findByNomAndPrenom(
                    contrat.getIdemploye().getNom(), 
                    contrat.getIdemploye().getPrenom()
                );
                
                if (candidatOriginal.isPresent()) {
                    System.out.println("Candidat original retrouvé avec email: " + 
                        (candidatOriginal.get().getIdcomptecandidat() != null ? 
                         candidatOriginal.get().getIdcomptecandidat().getEmail() : "pas d'email"));
                    contrat.setCandidat(candidatOriginal.get());
                } else {
                    // Créer un candidat fictif à partir de l'employé pour l'affichage
                    Candidat candidat = new Candidat();
                    candidat.setNom(contrat.getIdemploye().getNom());
                    candidat.setPrenom(contrat.getIdemploye().getPrenom());
                    candidat.setAdresse(contrat.getIdemploye().getAdresse());
                    contrat.setCandidat(candidat);
                    System.out.println("Candidat fictif créé (pas d'email disponible)");
                }
                
                contrat.setDuration(contrat.getNombremois());
                contrat.setStartDate(contrat.getDatedebut());
                if (contrat.getDatedebut() != null && contrat.getNombremois() != null) {
                    contrat.setEndDate(contrat.getDatedebut().plusMonths(contrat.getNombremois()));
                }
            }
        }
        return contratOpt;
    }

    public List<Contrat> findAll() {
        List<Contrat> list = contratRepository.findAll();
        List<Contrat> enriched = new ArrayList<>();
        for (Contrat contrat : list) {
            try {
                // Enrichir avec données dérivées
                if (contrat.getNombremois() != null) {
                    contrat.setDuration(contrat.getNombremois());
                }
                if (contrat.getDatedebut() != null) {
                    contrat.setStartDate(contrat.getDatedebut());
                    if (contrat.getNombremois() != null) {
                        contrat.setEndDate(contrat.getDatedebut().plusMonths(contrat.getNombremois()));
                    }
                }
                // Optionally attach a lightweight candidat from employe (if needed for front)
                if (contrat.getIdemploye() != null && contrat.getCandidat() == null) {
                    Candidat c = new Candidat();
                    c.setNom(contrat.getIdemploye().getNom());
                    c.setPrenom(contrat.getIdemploye().getPrenom());
                    c.setAdresse(contrat.getIdemploye().getAdresse());
                    contrat.setCandidat(c);
                }
            } catch (Exception ignored) {}
            enriched.add(contrat);
        }
        return enriched;
    }

    public List<String> listTypes() {
        return contratRepository.findDistinctTypes();
    }

    public List<Contrat> filterContrats(Integer departementId, String typeContrat, String status, String search) {
        List<Contrat> list = contratRepository.findFilteredContrats(
                departementId,
                (typeContrat != null && typeContrat.trim().isEmpty()) ? null : typeContrat,
                (status != null && status.trim().isEmpty()) ? null : status,
                (search != null && search.trim().isEmpty()) ? null : search
        );
        // enrich derived fields similar to findAll
        for (Contrat contrat : list) {
            try {
                if (contrat.getNombremois() != null) {
                    contrat.setDuration(contrat.getNombremois());
                }
                if (contrat.getDatedebut() != null) {
                    contrat.setStartDate(contrat.getDatedebut());
                    if (contrat.getNombremois() != null) {
                        contrat.setEndDate(contrat.getDatedebut().plusMonths(contrat.getNombremois()));
                    }
                }
                if (contrat.getIdemploye() != null && contrat.getCandidat() == null) {
                    Candidat c = new Candidat();
                    c.setNom(contrat.getIdemploye().getNom());
                    c.setPrenom(contrat.getIdemploye().getPrenom());
                    c.setAdresse(contrat.getIdemploye().getAdresse());
                    contrat.setCandidat(c);
                }
            } catch (Exception ignored) {}
        }
        return list;
    }

    @Transactional
    public void confirmHiring(Integer contratId) {
        Optional<Contrat> contratOpt = contratRepository.findById(contratId);
        if (contratOpt.isPresent()) {
            Contrat contrat = contratOpt.get();
            // Ici vous pouvez ajouter une logique supplémentaire
            // comme changer le statut du contrat, envoyer des notifications, etc.
            
            // Pour l'instant, nous gardons le contrat tel quel
            // Le candidat est déjà devenu employé lors de la génération du contrat
        }
    }
}
