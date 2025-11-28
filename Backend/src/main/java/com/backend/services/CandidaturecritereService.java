package com.backend.services;

import com.backend.entities.*;
import com.backend.repositories.CandidaturecritereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CandidaturecritereService {

    @Autowired
    private CandidaturecritereRepository candidaturecritereRepository;

    public void saveCandidatureCriteres(Candidat candidat, Annonce annonce, Map<String, Object> criteresValues) {
        for (Map.Entry<String, Object> entry : criteresValues.entrySet()) {
            String critereIdStr = entry.getKey().replace("critere_", "");
            Object value = entry.getValue();
            
            try {
                Integer critereId = Integer.parseInt(critereIdStr);
                
                Candidaturecritere candidaturecritere = new Candidaturecritere();
                candidaturecritere.setIdcandidat(candidat);
                candidaturecritere.setIdannonce(annonce);
                
                // Créer l'objet Critere avec l'ID
                Critere critere = new Critere();
                critere.setId(critereId);
                candidaturecritere.setIdcritere(critere);
                
                // Déterminer le type de valeur et l'assigner à la bonne colonne
                if (value != null) {
                    if (value instanceof Boolean) {
                        candidaturecritere.setValeurbool((Boolean) value);
                    } else if (value instanceof Number) {
                        candidaturecritere.setValeurdouble(new BigDecimal(value.toString()));
                    } else {
                        candidaturecritere.setValeurvarchar(value.toString());
                    }
                }
                
                candidaturecritereRepository.save(candidaturecritere);
            } catch (NumberFormatException e) {
                // Ignorer les critères avec des IDs invalides
                continue;
            }
        }
    }
}
