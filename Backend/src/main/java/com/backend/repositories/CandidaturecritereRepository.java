package com.backend.repositories;

import com.backend.entities.Candidaturecritere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidaturecritereRepository extends JpaRepository<Candidaturecritere, Integer> {
}
