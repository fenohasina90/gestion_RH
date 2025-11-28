package com.backend.repositories;

import com.backend.entities.Feuilletemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeuilletempRepository extends JpaRepository<Feuilletemp, Integer> {
    Optional<Feuilletemp> findByIdemploye_IdAndMoisAndAnnee(Integer employeId, Integer mois, Integer annee);
}
