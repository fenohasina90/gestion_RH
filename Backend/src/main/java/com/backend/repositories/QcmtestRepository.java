package com.backend.repositories;

import com.backend.entities.Qcmtest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcmtestRepository extends JpaRepository<Qcmtest, Integer> {
    
    List<Qcmtest> findByIdprofil_Id(Integer profilId);
    
    @Query("SELECT DISTINCT qt FROM Qcmtest qt " +
           "JOIN qt.idprofil p " +
           "JOIN Annonce a ON a.idprofil.id = p.id " +
           "JOIN Candidat c ON c.idannonce.id = a.id " +
           "WHERE c.idcomptecandidat.id = :candidatId")
    List<Qcmtest> findQcmTestsByCandidatApplications(@Param("candidatId") Integer candidatId);
}
