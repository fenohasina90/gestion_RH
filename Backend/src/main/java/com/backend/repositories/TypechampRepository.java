package com.backend.repositories;

import com.backend.entities.Typechamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypechampRepository extends JpaRepository<Typechamp, Integer> {
}
