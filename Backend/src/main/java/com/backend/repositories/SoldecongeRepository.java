package com.backend.repositories;

import com.backend.entities.Soldeconge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SoldecongeRepository extends JpaRepository<Soldeconge, Integer> {

    @Query(value = "select s.* from soldeconge s " +
            "left join employe e on e.id = s.idemploye " +
            "left join departement d on d.id = e.iddept " +
            "left join typeconge t on t.id = s.idtypeconge " +
            "where (:empName is null or ( (coalesce(e.nom,'') || ' ' || coalesce(e.prenom,'')) ILIKE concat('%', :empName, '%') )) " +
            "and (:deptId is null or d.id = :deptId) " +
            "and (:typeId is null or t.id = :typeId) " +
            "and (:annee is null or s.annee = :annee)",
            nativeQuery = true)
    List<Soldeconge> findFiltered(
            @Param("empName") String empName,
            @Param("deptId") Integer deptId,
            @Param("typeId") Integer typeId,
            @Param("annee") Integer annee
    );

    @Query(value = "select s.* from soldeconge s where s.idemploye = :empId and s.idtypeconge = :typeId and s.annee = :annee limit 1", nativeQuery = true)
    Soldeconge findOneByEmpTypeAndYear(@Param("empId") Integer empId,
                                       @Param("typeId") Integer typeId,
                                       @Param("annee") Integer annee);
}
