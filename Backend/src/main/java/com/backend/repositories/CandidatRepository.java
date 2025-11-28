package com.backend.repositories;

import com.backend.entities.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Integer> {
    List<Candidat> findByIdannonce_Id(Integer annonceId);
    Optional<Candidat> findByNomAndPrenom(String nom, String prenom);
}
