package com.backend.repositories;

import com.backend.entities.Statutcandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutcandidatRepository extends JpaRepository<Statutcandidat, Integer> {
    
    @Query("SELECT s FROM Statutcandidat s WHERE s.nom = ?1")
    Optional<Statutcandidat> findByNom(String nom);
}
