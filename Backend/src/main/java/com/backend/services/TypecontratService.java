package com.backend.services;

import com.backend.entities.Typecontrat;
import com.backend.repositories.TypecontratRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypecontratService {

    @Autowired
    private TypecontratRepository typecontratRepository;

    public List<Typecontrat> getAll() {
        return typecontratRepository.findAll();
    }

    public Typecontrat getById(Integer id) {
        return typecontratRepository.findById(id).orElse(null);
    }
}
