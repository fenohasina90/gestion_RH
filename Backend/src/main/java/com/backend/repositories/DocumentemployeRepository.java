package com.backend.repositories;

import com.backend.entities.Documentemploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentemployeRepository extends JpaRepository<Documentemploye, Integer> {
    @Query("select d from Documentemploye d where d.idemploye.id = :empId order by d.dateupload desc")
    List<Documentemploye> findByEmployeIdOrderByDateuploadDesc(@Param("empId") Integer empId);
}
