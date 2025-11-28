package com.backend.repositories;

import com.backend.entities.Detailfeuilletemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DetailfeuilletempRepository extends JpaRepository<Detailfeuilletemp, Integer> {
    Optional<Detailfeuilletemp> findByIdfeuilletemps_IdAndDatejour(Integer feuilleId, LocalDate datejour);
    List<Detailfeuilletemp> findByIdfeuilletemps_Id(Integer feuilleId);
}
