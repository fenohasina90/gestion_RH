package com.backend.repositories;

import com.backend.entities.Jourferie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JourferieRepository extends JpaRepository<Jourferie, Integer> {
    List<Jourferie> findByDatejourBetween(LocalDate from, LocalDate to);
}
