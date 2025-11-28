package com.backend.services;

import com.backend.entities.Critere;
import com.backend.repositories.CritereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CritereService {
    
    @Autowired
    private CritereRepository critereRepository;
    
    public List<Critere> getAllCriteres() {
        return critereRepository.findAll();
    }
    
    public Optional<Critere> getCritereById(Integer id) {
        return critereRepository.findById(id);
    }
    
    public Critere saveCritere(Critere critere) {
        return critereRepository.save(critere);
    }
    
    public void deleteCritere(Integer id) {
        critereRepository.deleteById(id);
    }
}
