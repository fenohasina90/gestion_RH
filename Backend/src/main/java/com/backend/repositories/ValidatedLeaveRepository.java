package com.backend.repositories;

import com.backend.entities.VCongeValide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ValidatedLeaveRepository extends JpaRepository<VCongeValide, Integer> {

    @Query(value = "SELECT * FROM v_conges_valides WHERE datefin >= :from AND datedebut <= :to", nativeQuery = true)
    List<VCongeValide> findInRange(@Param("from") LocalDate from,
                                   @Param("to") LocalDate to);
}
