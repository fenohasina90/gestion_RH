package com.backend.repositories;

import com.backend.entities.Congeeffectue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CongeeffectueRepository extends JpaRepository<Congeeffectue, Integer> {

    @Query(value = "select c.* from congeeffectue c " +
            "where (:empId is null or c.idemploye = :empId) " +
            "and (:typeId is null or c.idtypeconge = :typeId) " +
            "and (:annee is null or extract(year from c.datedebut) = :annee)",
            nativeQuery = true)
    List<Congeeffectue> findFiltered(
            @Param("empId") Integer empId,
            @Param("typeId") Integer typeId,
            @Param("annee") Integer annee
    );
}
