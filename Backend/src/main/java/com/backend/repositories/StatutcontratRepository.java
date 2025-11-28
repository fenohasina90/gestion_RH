package com.backend.repositories;

import com.backend.entities.Statutcontrat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutcontratRepository extends JpaRepository<Statutcontrat, Integer> {
    Optional<Statutcontrat> findByNomIgnoreCase(String nom);
}
