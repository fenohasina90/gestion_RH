package com.backend.repositories;

import com.backend.entities.Historiquecontrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoriquecontratRepository extends JpaRepository<Historiquecontrat, Integer> {
    @Query("select h from Historiquecontrat h where h.idcontrat.id = :contratId order by h.datechangement desc")
    List<Historiquecontrat> findByContratId(@Param("contratId") Integer contratId);
}
