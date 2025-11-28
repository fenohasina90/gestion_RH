package com.backend.services;

import com.backend.entities.Province;
import com.backend.repositories.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProvinceService {
    @Autowired
    private ProvinceRepository provinceRepository;

    public List<Province> findAll() {
        return provinceRepository.findAll();
    }

    public Optional<Province> findById(Integer id) {
        return provinceRepository.findById(id);
    }
}
