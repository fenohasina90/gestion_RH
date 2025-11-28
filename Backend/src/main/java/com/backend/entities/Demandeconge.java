package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "demandeconge")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Demandeconge {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypeconge")
    private Typeconge idtypeconge;

    @Column(name = "datedebut", nullable = false)
    private LocalDate datedebut;

    @Column(name = "datefin", nullable = false)
    private LocalDate datefin;

    @Column(name = "nombrejoursouvres", precision = 5, scale = 2)
    private BigDecimal nombrejoursouvres;

    @Column(name = "motif", length = Integer.MAX_VALUE)
    private String motif;

    @Column(name = "datedemande")
    private Instant datedemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvalideur")
    private Employe idvalideur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idstatut")
    private Statutdemande idstatut;

}