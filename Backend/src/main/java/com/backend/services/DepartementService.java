package com.backend.services;

import com.backend.entities.Departement;
import com.backend.repositories.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartementService {

    @Autowired
    private DepartementRepository departementRepository;

    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    public Departement getDepartementById(Integer id) {
        return departementRepository.findById(id).orElse(null);
    }
}
