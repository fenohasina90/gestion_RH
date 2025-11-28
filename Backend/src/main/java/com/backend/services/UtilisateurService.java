package com.backend.services;

import com.backend.entities.Utilisateur;
import com.backend.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur authenticate(String email, String motdepasse) {
        // Rechercher l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        
        // Vérifier si l'utilisateur existe et si le mot de passe correspond (sans hachage)
        if (utilisateur != null && utilisateur.getMotdepasse().equals(motdepasse)) {
            return utilisateur;
        }
        
        return null;
    }

    public Utilisateur createUser(Utilisateur utilisateur) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()) != null) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        
        // Sauvegarder l'utilisateur (mot de passe en clair comme demandé)
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
}
