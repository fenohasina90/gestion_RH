package com.backend.repositories;

import com.backend.entities.Qcmquestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcmquestionRepository extends JpaRepository<Qcmquestion, Integer> {
    
    List<Qcmquestion> findByIdtest_IdOrderByNumero(Integer testId);
    
    @Query("SELECT COUNT(q) FROM Qcmquestion q WHERE q.idtest.id = :testId")
    Long countQuestionsByTestId(@Param("testId") Integer testId);
    
    @Query("SELECT SUM(q.points) FROM Qcmquestion q WHERE q.idtest.id = :testId")
    Integer getTotalPointsByTestId(@Param("testId") Integer testId);
}
