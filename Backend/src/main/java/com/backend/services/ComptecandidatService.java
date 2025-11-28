package com.backend.services;

import com.backend.entities.Comptecandidat;
import com.backend.repositories.ComptecandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ComptecandidatService {

    @Autowired
    private ComptecandidatRepository comptecandidatRepository;

    public Optional<Comptecandidat> findByEmail(String email) {
        return comptecandidatRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return comptecandidatRepository.existsByEmail(email);
    }

    public Comptecandidat save(Comptecandidat comptecandidat) {
        return comptecandidatRepository.save(comptecandidat);
    }

    public Comptecandidat authenticate(String email, String motdepasse) {
        Optional<Comptecandidat> compte = findByEmail(email);
        if (compte.isPresent() && compte.get().getMotdepasse().equals(motdepasse)) {
            return compte.get();
        }
        return null;
    }

    public Comptecandidat register(String email, String motdepasse) {
        if (existsByEmail(email)) {
            return null; // Email déjà utilisé
        }

        Comptecandidat nouveauCompte = new Comptecandidat();
        nouveauCompte.setEmail(email);
        nouveauCompte.setMotdepasse(motdepasse);
        
        return save(nouveauCompte);
    }

    public Comptecandidat findById(Integer id) {
        Optional<Comptecandidat> compte = comptecandidatRepository.findById(id);
        return compte.orElse(null);
    }
}
