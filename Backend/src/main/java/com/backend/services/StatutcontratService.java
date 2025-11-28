package com.backend.services;

import com.backend.entities.Statutcontrat;
import com.backend.repositories.StatutcontratRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutcontratService {

    @Autowired
    private StatutcontratRepository statutcontratRepository;

    public List<Statutcontrat> findAll() {
        return statutcontratRepository.findAll();
    }

    public Optional<Statutcontrat> findById(Integer id) {
        return statutcontratRepository.findById(id);
    }
}
