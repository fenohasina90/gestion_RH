package com.backend.services;

import com.backend.entities.Typeannonce;
import com.backend.repositories.TypeannonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeannonceService {
    
    @Autowired
    private TypeannonceRepository typeannonceRepository;
    
    public List<Typeannonce> getAllTypeannonces() {
        return typeannonceRepository.findAll();
    }
    
    public Optional<Typeannonce> getTypeannonceById(Integer id) {
        return typeannonceRepository.findById(id);
    }
    
    public Typeannonce createTypeannonce(Typeannonce typeannonce) {
        return typeannonceRepository.save(typeannonce);
    }
    
    public Typeannonce updateTypeannonce(Integer id, Typeannonce typeannonceDetails) {
        return typeannonceRepository.findById(id)
            .map(typeannonce -> {
                typeannonce.setLibelle(typeannonceDetails.getLibelle());
                return typeannonceRepository.save(typeannonce);
            })
            .orElseThrow(() -> new RuntimeException("Typeannonce not found with id " + id));
    }
    
    public void deleteTypeannonce(Integer id) {
        typeannonceRepository.deleteById(id);
    }
}
