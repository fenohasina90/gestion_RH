package com.backend.services;

import com.backend.entities.Candidat;
import com.backend.repositories.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatService {

    @Autowired
    private CandidatRepository candidatRepository;

    public List<Candidat> getAllCandidats() {
        return candidatRepository.findAll();
    }

    public Candidat getCandidatById(Integer id) {
        Optional<Candidat> candidat = candidatRepository.findById(id);
        return candidat.orElse(null);
    }

    public List<Candidat> getCandidatsByAnnonce(Integer annonceId) {
        return candidatRepository.findByIdannonce_Id(annonceId);
    }

    public Candidat saveCandidat(Candidat candidat) {
        return candidatRepository.save(candidat);
    }

    public void deleteCandidat(Integer id) {
        candidatRepository.deleteById(id);
    }
}
