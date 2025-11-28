package com.backend.repositories;

import com.backend.entities.Qcmchoix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcmchoixRepository extends JpaRepository<Qcmchoix, Integer> {
    
    List<Qcmchoix> findByIdquestion_Id(Integer questionId);
    
    List<Qcmchoix> findByIdquestion_IdAndEstcorrectTrue(Integer questionId);
}
