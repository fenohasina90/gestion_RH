package com.backend.services;

import com.backend.entities.Diplome;
import com.backend.repositories.DiplomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiplomeService {
    
    @Autowired
    private DiplomeRepository diplomeRepository;
    
    public List<Diplome> getAllDiplomes() {
        return diplomeRepository.findAll();
    }
    
    public Optional<Diplome> getDiplomeById(Integer id) {
        return diplomeRepository.findById(id);
    }
    
    public Diplome saveDiplome(Diplome diplome) {
        return diplomeRepository.save(diplome);
    }
    
    public void deleteDiplome(Integer id) {
        diplomeRepository.deleteById(id);
    }
}
