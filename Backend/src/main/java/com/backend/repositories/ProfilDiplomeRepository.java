package com.backend.repositories;

import com.backend.entities.Profildiplome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilDiplomeRepository extends JpaRepository<Profildiplome, Integer> {
    
    @Query("SELECT pd FROM Profildiplome pd WHERE pd.idprofil.id = :profilId")
    List<Profildiplome> findByProfilId(@Param("profilId") Integer profilId);
    
    @Modifying
    @Query("DELETE FROM Profildiplome pd WHERE pd.idprofil.id = :profilId")
    void deleteByProfilId(@Param("profilId") Integer profilId);
}
