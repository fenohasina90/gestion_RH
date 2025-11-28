package com.backend.services;

import com.backend.entities.Typechamp;
import com.backend.repositories.TypechampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypechampService {
    @Autowired
    private TypechampRepository typechampRepository;

    public List<Typechamp> findAll() {
        return typechampRepository.findAll();
    }

    public Optional<Typechamp> findById(Integer id) {
        return typechampRepository.findById(id);
    }

    public Typechamp save(Typechamp t) {
        return typechampRepository.save(t);
    }

    public void delete(Integer id) {
        typechampRepository.deleteById(id);
    }
}
