package com.backend.services;

import com.backend.entities.Diplome;
import com.backend.entities.Profil;
import com.backend.entities.Profildiplome;
import com.backend.repositories.DiplomeRepository;
import com.backend.repositories.ProfilRepository;
import com.backend.repositories.ProfilDiplomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfilDiplomeService {
    
    @Autowired
    private ProfilDiplomeRepository profilDiplomeRepository;
    
    @Autowired
    private ProfilRepository profilRepository;
    
    @Autowired
    private DiplomeRepository diplomeRepository;
    
    public List<Profildiplome> getDiplomesByProfilId(Integer profilId) {
        return profilDiplomeRepository.findByProfilId(profilId);
    }
    
    @Transactional
    public void assignDiplomesToProfil(Integer profilId, List<DiplomeAssignmentRequest> diplomeAssignments) {
        // Supprimer les anciens diplômes assignés
        profilDiplomeRepository.deleteByProfilId(profilId);
        
        // Récupérer le profil
        Profil profil = profilRepository.findById(profilId)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
        
        // Assigner les nouveaux diplômes
        for (DiplomeAssignmentRequest assignment : diplomeAssignments) {
            Diplome diplome = diplomeRepository.findById(assignment.getDiplomeId())
                    .orElseThrow(() -> new RuntimeException("Diplôme non trouvé"));
            
            Profildiplome profilDiplome = new Profildiplome();
            profilDiplome.setIdprofil(profil);
            profilDiplome.setIddiplome(diplome);
            
            profilDiplomeRepository.save(profilDiplome);
        }
    }
    
    public static class DiplomeAssignmentRequest {
        private Integer diplomeId;
        
        public Integer getDiplomeId() {
            return diplomeId;
        }
        
        public void setDiplomeId(Integer diplomeId) {
            this.diplomeId = diplomeId;
        }
    }
}
