package com.backend.repositories;

import com.backend.entities.Statutdemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutdemandeRepository extends JpaRepository<Statutdemande, Integer> {
    Optional<Statutdemande> findByNomIgnoreCase(String nom);
}
