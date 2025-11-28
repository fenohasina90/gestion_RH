package com.backend.services;

import com.backend.entities.Resultat;
import com.backend.repositories.ResultatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultatService {

    @Autowired
    private ResultatRepository resultatRepository;

    public List<Resultat> getAllResultats() {
        return resultatRepository.findAll();
    }
}
