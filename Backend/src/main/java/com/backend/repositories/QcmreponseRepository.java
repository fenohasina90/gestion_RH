package com.backend.repositories;

import com.backend.entities.Qcmreponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QcmreponseRepository extends JpaRepository<Qcmreponse, Integer> {
    
    List<Qcmreponse> findByIdcandidat_IdAndIdtest_Id(Integer candidatId, Integer testId);
    
    Optional<Qcmreponse> findByIdcandidat_IdAndIdquestion_Id(Integer candidatId, Integer questionId);
    
    @Query("SELECT SUM(r.pointsobtenus) FROM Qcmreponse r WHERE r.idcandidat.id = :candidatId AND r.idtest.id = :testId")
    Integer getTotalScoreByCandidatAndTest(@Param("candidatId") Integer candidatId, @Param("testId") Integer testId);
    
    @Query("SELECT COUNT(r) FROM Qcmreponse r WHERE r.idcandidat.id = :candidatId AND r.idtest.id = :testId")
    Long countAnsweredQuestionsByCandidatAndTest(@Param("candidatId") Integer candidatId, @Param("testId") Integer testId);
    
    boolean existsByIdcandidat_IdAndIdtest_Id(Integer candidatId, Integer testId);
}
