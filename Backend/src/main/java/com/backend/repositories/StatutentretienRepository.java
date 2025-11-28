package com.backend.repositories;

import com.backend.entities.Statutentretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutentretienRepository extends JpaRepository<Statutentretien, Integer> {
    
    @Query("SELECT s FROM Statutentretien s WHERE s.nom = ?1")
    Optional<Statutentretien> findByNom(String nom);
}
