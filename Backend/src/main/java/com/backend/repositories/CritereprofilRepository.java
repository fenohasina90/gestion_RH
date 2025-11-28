package com.backend.repositories;

import com.backend.entities.Critereprofil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CritereprofilRepository extends JpaRepository<Critereprofil, Integer> {
    @Query("SELECT cp FROM Critereprofil cp WHERE cp.idprofil.id = :profilId")
    List<Critereprofil> findByProfilId(@Param("profilId") Integer profilId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Critereprofil cp WHERE cp.idprofil.id = :profilId")
    int deleteByProfilId(@Param("profilId") Integer profilId);
}