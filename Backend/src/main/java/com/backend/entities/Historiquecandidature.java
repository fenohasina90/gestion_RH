package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "historiquecandidature")
public class Historiquecandidature {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcandidat")
    private Candidat idcandidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idstatut")
    private Statutcandidat idstatut;

    @Column(name = "datechangement")
    private Instant datechangement;

}