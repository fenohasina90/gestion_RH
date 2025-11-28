package com.backend.repositories;

import com.backend.entities.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Integer> {
    
    List<Employe> findByIddept_Id(Integer departementId);

    Optional<Employe> findByMatricule(String matricule);
}