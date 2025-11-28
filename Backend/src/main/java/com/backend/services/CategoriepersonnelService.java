package com.backend.services;

import com.backend.entities.Categoriepersonnel;
import com.backend.repositories.CategoriepersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriepersonnelService {

    @Autowired
    private CategoriepersonnelRepository categoriepersonnelRepository;

    public List<Categoriepersonnel> getAll() {
        return categoriepersonnelRepository.findAll();
    }

    public Categoriepersonnel getById(Integer id) {
        return categoriepersonnelRepository.findById(id).orElse(null);
    }
}
