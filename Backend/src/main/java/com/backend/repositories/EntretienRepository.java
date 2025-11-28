package com.backend.repositories;

import com.backend.entities.Entretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntretienRepository extends JpaRepository<Entretien, Integer> {
    
    @Query("SELECT e FROM Entretien e WHERE e.idcandidat.id = ?1")
    List<Entretien> findByIdcandidat_Id(Integer candidatId);
    
    @Query("SELECT e FROM Entretien e WHERE e.idcandidat.id = ?1 AND e.idstatut.nom IN ('Planifie', 'En cours')")
    Optional<Entretien> findActiveByCandidatId(Integer candidatId);
    
    @Query("SELECT e FROM Entretien e WHERE e.idcandidat.id = ?1 AND e.idannonce.id = ?2 AND e.idstatut.nom IN ('Planifie', 'En cours')")
    Optional<Entretien> findActiveByCandidatAndAnnonce(Integer candidatId, Integer annonceId);
    
    @Query("SELECT e FROM Entretien e WHERE e.dateheure BETWEEN ?1 AND ?2")
    List<Entretien> findByDateheureBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    
    @Query("SELECT e FROM Entretien e WHERE e.idresultat.appreciation = ?1")
    List<Entretien> findByIdresultat_Appreciation(String appreciation);

    // Native filter: returns all entretiens (even if idresultat is NULL), with optional filters
    @Query(value = "SELECT e.* FROM entretien e " +
           "JOIN candidat c ON c.id = e.idcandidat " +
           "JOIN annonce a ON a.id = e.idannonce " +
           "JOIN statutentretien s ON s.id = e.idstatut " +
           "LEFT JOIN resultat r ON r.id = e.idresultat " +
           "WHERE (COALESCE(:candidateQuery, '') = '' OR (c.prenom || ' ' || c.nom) ILIKE CONCAT('%', :candidateQuery, '%')) " +
           "AND (COALESCE(:positionQuery, '') = '' OR a.nomposte ILIKE CONCAT('%', :positionQuery, '%')) " +
           "AND (COALESCE(:statusQuery, '') = '' OR s.nom = :statusQuery) " +
           "AND (COALESCE(:resultQuery, '') = '' OR (r.appreciation ILIKE CONCAT('%', :resultQuery, '%'))) " +
           "AND (COALESCE(:dateFrom, e.dateheure) <= e.dateheure) " +
           "AND (COALESCE(:dateTo, e.dateheure) >= e.dateheure) " +
           "ORDER BY e.dateheure DESC",
           nativeQuery = true)
    List<Entretien> findFilteredEntretiens(
            @Param("candidateQuery") String candidateQuery,
            @Param("positionQuery") String positionQuery,
            @Param("statusQuery") String statusQuery,
            @Param("resultQuery") String resultQuery,
            @Param("dateFrom") java.time.LocalDateTime dateFrom,
            @Param("dateTo") java.time.LocalDateTime dateTo
    );
}
