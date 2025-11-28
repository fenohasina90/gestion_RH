package com.backend.services;

import com.backend.entities.Critereprofil;
import com.backend.entities.Profil;
import com.backend.entities.Critere;
import com.backend.repositories.CritereprofilRepository;
import com.backend.repositories.ProfilRepository;
import com.backend.repositories.CritereRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CritereProfilService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CritereProfilService.class);
    
    @Autowired
    private CritereprofilRepository critereProfilRepository;
    
    @Autowired
    private ProfilRepository profilRepository;
    
    @Autowired
    private CritereRepository critereRepository;
    
    public List<Critereprofil> getCriteresByProfilId(Integer profilId) {
        return critereProfilRepository.findByProfilId(profilId);
    }
    
    @Transactional
    public void assignCriteresToProfil(Integer profilId, List<CritereAssignmentRequest> critereAssignments) {
        // Supprimer les anciens critères assignés
        critereProfilRepository.deleteByProfilId(profilId);
        
        // Récupérer le profil
        Optional<Profil> profilOpt = profilRepository.findById(profilId);
        if (profilOpt.isPresent()) {
            Profil profil = profilOpt.get();
            
            // Assigner les nouveaux critères
            for (CritereAssignmentRequest assignment : critereAssignments) {
                Optional<Critere> critereOpt = critereRepository.findById(assignment.getCritereId());
                if (critereOpt.isPresent()) {
                    Critereprofil critereProfil = new Critereprofil();
                    critereProfil.setIdprofil(profil);
                    critereProfil.setIdcritere(critereOpt.get());
                    critereProfil.setEstobligatoire(assignment.getObligatoire());

                    // Mapper la valeur selon le type de champ du critère
                    String type = null;
                    if (critereOpt.get().getIdtypechamp() != null && critereOpt.get().getIdtypechamp().getLibelle() != null) {
                        type = critereOpt.get().getIdtypechamp().getLibelle().toLowerCase();
                    }
                    String valeur = assignment.getValeur();
                    if (valeur != null) {
                        valeur = valeur.trim();
                        if (valeur.isEmpty()) {
                            // pas de valeur => ne rien setter
                            LOGGER.debug("Assign critere {} to profil {} sans valeur (optionnel)", assignment.getCritereId(), profilId);
                            critereProfilRepository.save(critereProfil);
                            continue;
                        }
                        try {
                            switch (type == null ? "" : type) {
                                case "number":
                                case "numeric":
                                case "double":
                                case "decimal":
                                case "nombre":
                                    critereProfil.setValeurdouble(new java.math.BigDecimal(valeur));
                                    break;
                                case "checkbox":
                                case "boolean":
                                case "bool":
                                case "booleen":
                                case "booléen":
                                    critereProfil.setValeurbool(Boolean.parseBoolean(valeur));
                                    break;
                                default:
                                    critereProfil.setValeurvarchar(valeur);
                            }
                        } catch (Exception ex) {
                            // En cas d'erreur de parsing, stocker en varchar par défaut
                            LOGGER.warn("Parsing valeur '{}' pour critere {} type '{}': {}. Stockage en varchar.", valeur, assignment.getCritereId(), type, ex.getMessage());
                            critereProfil.setValeurvarchar(valeur);
                        }
                    }
                    LOGGER.debug("Save Critereprofil: profil={} critere={} obligatoire={} vdouble={} vbool={} vvarchar={}",
                            profilId,
                            assignment.getCritereId(),
                            assignment.getObligatoire(),
                            critereProfil.getValeurdouble(),
                            critereProfil.getValeurbool(),
                            critereProfil.getValeurvarchar());
                    critereProfilRepository.save(critereProfil);
                }
            }
        }
    }

    public static class CritereAssignmentRequest {
        private Integer critereId;
        private Boolean obligatoire;
        private String valeur; // valeur libre envoyée par le front
        
        public CritereAssignmentRequest() {}
        
        public CritereAssignmentRequest(Integer critereId, Boolean obligatoire) {
            this.critereId = critereId;
            this.obligatoire = obligatoire;
        }
        
        public Integer getCritereId() {
            return critereId;
        }
        
        public void setCritereId(Integer critereId) {
            this.critereId = critereId;
        }
        
        public Boolean getObligatoire() {
            return obligatoire;
        }
        
        public void setObligatoire(Boolean obligatoire) {
            this.obligatoire = obligatoire;
        }

        public String getValeur() {
            return valeur;
        }

        public void setValeur(String valeur) {
            this.valeur = valeur;
        }
    }
    
    public Critereprofil saveCritereProfil(Critereprofil critereProfil) {
        return critereProfilRepository.save(critereProfil);
    }
    
    public void deleteCritereProfil(Integer id) {
        critereProfilRepository.deleteById(id);
    }
}
