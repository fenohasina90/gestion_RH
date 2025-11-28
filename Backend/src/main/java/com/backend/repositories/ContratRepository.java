package com.backend.repositories;

import com.backend.entities.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Integer> {

    @Query(value = "SELECT DISTINCT c.typecontrat FROM contrat c WHERE c.typecontrat IS NOT NULL ORDER BY c.typecontrat", nativeQuery = true)
    java.util.List<String> findDistinctTypes();

    @Query(value = "SELECT c.* FROM contrat c " +
            "JOIN employe e ON e.id = c.idemploye " +
            "WHERE (COALESCE(:departementId, e.iddept) = e.iddept) " +
            "AND (COALESCE(:typeContrat, c.typecontrat) = c.typecontrat) " +
            "AND (COALESCE(:search, '') = '' OR (LOWER(e.prenom || ' ' || e.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.poste) LIKE LOWER(CONCAT('%', :search, '%')))) " +
            "AND (COALESCE(:status, '') = '' OR (" +
            "    (:status = 'non_commence' AND CURRENT_DATE < c.datedebut) OR " +
            "    (:status = 'en_cours' AND CURRENT_DATE >= c.datedebut AND CURRENT_DATE <= (c.datedebut + (c.nombremois || ' months')::interval)) OR " +
            "    (:status = 'termine' AND CURRENT_DATE > (c.datedebut + (c.nombremois || ' months')::interval))" +
            ")) " +
            "ORDER BY c.datedebut DESC",
            nativeQuery = true)
    java.util.List<Contrat> findFilteredContrats(
            @Param("departementId") Integer departementId,
            @Param("typeContrat") String typeContrat,
            @Param("status") String status,
            @Param("search") String search
    );

    @Query(value = "SELECT c.* FROM contrat c " +
            "JOIN employe e ON e.id = c.idemploye " +
            "WHERE (:nom IS NULL OR LOWER(e.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :nom, '%'))) " +
            "AND (:departementId IS NULL OR e.iddept = :departementId) " +
            "AND (:poste IS NULL OR LOWER(c.poste) LIKE LOWER(CONCAT('%', :poste, '%'))) " +
            "AND (:typeContrat IS NULL OR c.typecontrat = :typeContrat) " +
            "AND (:salaireMin IS NULL OR c.salaire >= :salaireMin) " +
            "AND (:dateDebutFrom IS NULL OR c.datedebut >= :dateDebutFrom) " +
            "ORDER BY c.datedebut DESC",
            nativeQuery = true)
    java.util.List<Contrat> findFilteredEmployeeContracts(
            @Param("nom") String nom,
            @Param("departementId") Integer departementId,
            @Param("poste") String poste,
            @Param("typeContrat") String typeContrat,
            @Param("salaireMin") Double salaireMin,
            @Param("dateDebutFrom") java.time.LocalDate dateDebutFrom
    );

    @Query(value = "SELECT c.* FROM contrat c WHERE c.idemploye = :empId ORDER BY c.datedebut DESC", nativeQuery = true)
    java.util.List<Contrat> findByEmployeIdOrderByDatedebutDesc(@Param("empId") Integer empId);
}
