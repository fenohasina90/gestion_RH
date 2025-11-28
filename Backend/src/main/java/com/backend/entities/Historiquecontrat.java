package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "historiquecontrat")
public class Historiquecontrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontrat")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Contrat idcontrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idstatut")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Statutcontrat idstatut;

    @Column(name = "datechangement")
    private Instant datechangement;

    @Column(name = "commentaire", length = Integer.MAX_VALUE)
    private String commentaire;

}