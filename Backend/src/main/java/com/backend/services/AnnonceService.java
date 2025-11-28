package com.backend.services;

import com.backend.entities.Annonce;
import com.backend.repositories.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AnnonceService {
    
    @Autowired
    private AnnonceRepository annonceRepository;
    
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }
    
    public Annonce getAnnonceById(Integer id) {
        Optional<Annonce> annonce = annonceRepository.findById(id);
        return annonce.orElse(null);
    }
    
    public Annonce createAnnonce(Annonce annonce) {
        return annonceRepository.save(annonce);
    }
    
    public Annonce updateAnnonce(Integer id, Annonce annonceDetails) {
        return annonceRepository.findById(id)
            .map(annonce -> {
                annonce.setNomposte(annonceDetails.getNomposte());
                annonce.setDescription(annonceDetails.getDescription());
                annonce.setDatedebut(annonceDetails.getDatedebut());
                annonce.setDatefin(annonceDetails.getDatefin());
                annonce.setIddepartement(annonceDetails.getIddepartement());
                annonce.setIdprofil(annonceDetails.getIdprofil());
                annonce.setIdtypeannonce(annonceDetails.getIdtypeannonce());
                return annonceRepository.save(annonce);
            })
            .orElseThrow(() -> new RuntimeException("Annonce not found with id " + id));
    }
    
    public void deleteAnnonce(Integer id) {
        annonceRepository.deleteById(id);
    }
    
    public List<Annonce> getAnnoncesByProfil(Integer profilId) {
        return annonceRepository.findByIdprofil_Id(profilId);
    }
    
    public List<Annonce> getAnnoncesByDepartement(Integer departementId) {
        return annonceRepository.findByIddepartement_Id(departementId);
    }
    
    public int getCandidaturesCount(Integer annonceId) {
        return annonceRepository.countCandidaturesByAnnonceId(annonceId);
    }
    
    public List<Annonce> getFilteredAnnonces(
            String nomposte, String description, Integer departementId, 
            Integer typeAnnonceId, Integer profilId, Integer provinceId,
            Integer diplomeId,
            String dateDebutFrom, String dateDebutTo, 
            String dateFinFrom, String dateFinTo,
            String datePublicationFrom, String datePublicationTo) {
        
        // Convertir les chaînes vides en null pour éviter les erreurs PostgreSQL
        if (nomposte != null && nomposte.trim().isEmpty()) nomposte = null;
        if (description != null && description.trim().isEmpty()) description = null;
        
        LocalDate dateDebutFromParsed = null;
        LocalDate dateDebutToParsed = null;
        LocalDate dateFinFromParsed = null;
        LocalDate dateFinToParsed = null;
        LocalDate datePublicationFromParsed = null;
        LocalDate datePublicationToParsed = null;
        
        try {
            if (dateDebutFrom != null && !dateDebutFrom.trim().isEmpty()) {
                dateDebutFromParsed = LocalDate.parse(dateDebutFrom);
            }
            if (dateDebutTo != null && !dateDebutTo.trim().isEmpty()) {
                dateDebutToParsed = LocalDate.parse(dateDebutTo);
            }
            if (dateFinFrom != null && !dateFinFrom.trim().isEmpty()) {
                dateFinFromParsed = LocalDate.parse(dateFinFrom);
            }
            if (dateFinTo != null && !dateFinTo.trim().isEmpty()) {
                dateFinToParsed = LocalDate.parse(dateFinTo);
            }
            if (datePublicationFrom != null && !datePublicationFrom.trim().isEmpty()) {
                datePublicationFromParsed = LocalDate.parse(datePublicationFrom);
            }
            if (datePublicationTo != null && !datePublicationTo.trim().isEmpty()) {
                datePublicationToParsed = LocalDate.parse(datePublicationTo);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing des dates: " + e.getMessage());
        }
        
        return annonceRepository.findFilteredAnnonces(
            nomposte, description, departementId, typeAnnonceId, profilId, provinceId,
            diplomeId,
            dateDebutFromParsed, dateDebutToParsed, 
            dateFinFromParsed, dateFinToParsed,
            datePublicationFromParsed, datePublicationToParsed
        );
    }
}
