package com.backend.repositories;

import com.backend.entities.Historiqueposte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoriqueposteRepository extends JpaRepository<Historiqueposte, Integer> {
    @Query("select h from Historiqueposte h where h.idemploye.id = :empId order by h.datedebut desc")
    List<Historiqueposte> findByEmployeIdOrderByDatedebutDesc(@Param("empId") Integer empId);
}
