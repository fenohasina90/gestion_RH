package com.backend.repositories;

import com.backend.entities.Profil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilRepository extends JpaRepository<Profil, Integer> {
}