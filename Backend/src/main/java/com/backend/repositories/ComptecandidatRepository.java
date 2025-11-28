package com.backend.repositories;

import com.backend.entities.Comptecandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComptecandidatRepository extends JpaRepository<Comptecandidat, Integer> {
    Optional<Comptecandidat> findByEmail(String email);
    boolean existsByEmail(String email);
}
