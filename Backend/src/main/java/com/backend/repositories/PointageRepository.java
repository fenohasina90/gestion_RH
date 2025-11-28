package com.backend.repositories;

import com.backend.entities.Pointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PointageRepository extends JpaRepository<Pointage, Integer> {
    @Query("SELECT p FROM Pointage p WHERE p.estvalide = TRUE AND p.dateheure BETWEEN :start AND :end")
    List<Pointage> findValidBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT p FROM Pointage p WHERE p.estvalide = TRUE AND p.idemploye.id = :empId AND p.dateheure BETWEEN :start AND :end ORDER BY p.dateheure ASC")
    List<Pointage> findValidByEmployeBetween(@Param("empId") Integer empId, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT p FROM Pointage p WHERE p.estvalide = TRUE AND p.idemploye.id = :empId AND p.dateheure >= :after ORDER BY p.dateheure DESC")
    List<Pointage> findRecentValidAfter(@Param("empId") Integer empId, @Param("after") Instant after);
}
