package com.backend.repositories;

import com.backend.entities.Typeannonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeannonceRepository extends JpaRepository<Typeannonce, Integer> {
}