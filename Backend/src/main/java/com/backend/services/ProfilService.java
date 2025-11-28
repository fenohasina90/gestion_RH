package com.backend.services;

import com.backend.entities.Profil;
import com.backend.repositories.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfilService {
    
    @Autowired
    private ProfilRepository profilRepository;
    
    public List<Profil> getAllProfils() {
        return profilRepository.findAll();
    }
    
    public Optional<Profil> getProfilById(Integer id) {
        return profilRepository.findById(id);
    }
    
    public Profil saveProfil(Profil profil) {
        return profilRepository.save(profil);
    }
    
    public void deleteProfil(Integer id) {
        profilRepository.deleteById(id);
    }
}
