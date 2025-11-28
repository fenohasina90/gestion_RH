package com.backend.repositories;

import com.backend.entities.Demandeconge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface DemandecongeRepository extends JpaRepository<Demandeconge, Integer> {

    @Query("select count(d) from Demandeconge d " +
            "where d.idemploye.id = :empId " +
            "and d.idtypeconge.id = :typeId " +
            "and d.datedemande >= coalesce(:from, d.datedemande) " +
            "and d.datedemande <= coalesce(:to, d.datedemande) " +
            "and (d.idstatut.nom in :pendingNames)")
    long countPendingByEmpTypeAndDate(
            @Param("empId") Integer empId,
            @Param("typeId") Integer typeId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("pendingNames") java.util.List<String> pendingNames
    );

    @Query("select d from Demandeconge d " +
            "where (:empId is null or d.idemploye.id = :empId) " +
            "and (:typeId is null or d.idtypeconge.id = :typeId) " +
            "and d.datedemande >= coalesce(:from, d.datedemande) " +
            "and d.datedemande <= coalesce(:to, d.datedemande) " +
            "and (d.idstatut.nom in :pendingNames) " +
            "order by d.datedemande desc")
    java.util.List<com.backend.entities.Demandeconge> findPending(
            @Param("empId") Integer empId,
            @Param("typeId") Integer typeId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("pendingNames") java.util.List<String> pendingNames
    );
}
