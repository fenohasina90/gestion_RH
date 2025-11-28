package com.backend.repositories;

import com.backend.entities.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    List<Annonce> findByIdprofil_Id(Integer profilId);
    List<Annonce> findByIddepartement_Id(Integer departementId);
    
    @Query("SELECT COUNT(c) FROM Candidat c WHERE c.idannonce.id = :annonceId")
    int countCandidaturesByAnnonceId(@Param("annonceId") Integer annonceId);
    
    @Query(value = "SELECT * FROM annonce a WHERE " +
           "a.nomposte ILIKE CONCAT('%', COALESCE(:nomposte, ''), '%') AND " +
           "a.description ILIKE CONCAT('%', COALESCE(:description, ''), '%') AND " +
           "a.iddepartement = COALESCE(:departementId, a.iddepartement) AND " +
           "a.idtypeannonce = COALESCE(:typeAnnonceId, a.idtypeannonce) AND " +
           "a.idprofil = COALESCE(:profilId, a.idprofil) AND " +
           "a.idprovince = COALESCE(:provinceId, a.idprovince) AND " +
           "( CAST(:dateDebutFrom AS date) IS NULL OR (a.datedebut IS NOT NULL AND a.datedebut >= CAST(:dateDebutFrom AS date)) ) AND " +
           "( CAST(:dateDebutTo AS date) IS NULL OR (a.datedebut IS NOT NULL AND a.datedebut <= CAST(:dateDebutTo AS date)) ) AND " +
           "( CAST(:dateFinFrom AS date) IS NULL OR a.datefin IS NULL OR a.datefin >= CAST(:dateFinFrom AS date) ) AND " +
           "( CAST(:dateFinTo AS date) IS NULL OR a.datefin IS NULL OR a.datefin <= CAST(:dateFinTo AS date) ) AND " +
           "( CAST(:datePublicationFrom AS date) IS NULL OR (a.datepublication IS NOT NULL AND a.datepublication >= CAST(:datePublicationFrom AS date)) ) AND " +
           "( CAST(:datePublicationTo AS date) IS NULL OR (a.datepublication IS NOT NULL AND a.datepublication <= CAST(:datePublicationTo AS date)) ) AND " +
           // Diplôme: comparaison basée sur les noms (BACC < Licence < Master < Doctorat)
           "( :diplomeId IS NULL OR " +
           "  COALESCE((SELECT MAX(CASE " +
           "            WHEN LOWER(TRIM(d.nom)) = 'bacc' THEN 1 " +
           "            WHEN LOWER(TRIM(d.nom)) = 'licence' THEN 2 " +
           "            WHEN LOWER(TRIM(d.nom)) = 'master' THEN 3 " +
           "            WHEN LOWER(TRIM(d.nom)) = 'doctorat' THEN 4 " +
           "            ELSE 0 END) " +
           "    FROM profildiplome pd " +
           "    JOIN diplome d ON d.id = pd.iddiplome " +
           "    WHERE pd.idprofil = a.idprofil), 0) >= " +
           "  COALESCE((SELECT CASE " +
           "            WHEN LOWER(TRIM(d2.nom)) = 'bacc' THEN 1 " +
           "            WHEN LOWER(TRIM(d2.nom)) = 'licence' THEN 2 " +
           "            WHEN LOWER(TRIM(d2.nom)) = 'master' THEN 3 " +
           "            WHEN LOWER(TRIM(d2.nom)) = 'doctorat' THEN 4 " +
           "            ELSE 0 END FROM diplome d2 WHERE d2.id = :diplomeId), 0) " +
           ")",
           nativeQuery = true)
    List<Annonce> findFilteredAnnonces(
            @Param("nomposte") String nomposte,
            @Param("description") String description,
            @Param("departementId") Integer departementId,
            @Param("typeAnnonceId") Integer typeAnnonceId,
            @Param("profilId") Integer profilId,
            @Param("provinceId") Integer provinceId,
            @Param("diplomeId") Integer diplomeId,
            @Param("dateDebutFrom") LocalDate dateDebutFrom,
            @Param("dateDebutTo") LocalDate dateDebutTo,
            @Param("dateFinFrom") LocalDate dateFinFrom,
            @Param("dateFinTo") LocalDate dateFinTo,
            @Param("datePublicationFrom") LocalDate datePublicationFrom,
            @Param("datePublicationTo") LocalDate datePublicationTo);
}