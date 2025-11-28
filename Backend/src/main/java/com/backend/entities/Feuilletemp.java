package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "feuilletemps")
public class Feuilletemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @Column(name = "mois", nullable = false)
    private Integer mois;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "jourstravailles", precision = 5, scale = 2)
    private BigDecimal jourstravailles;

    @Column(name = "heuressupplementaires", precision = 6, scale = 2)
    private BigDecimal heuressupplementaires;

    @Column(name = "absences", precision = 5, scale = 2)
    private BigDecimal absences;

    @Column(name = "retards")
    private Integer retards;

    @Column(name = "datecloture")
    private Instant datecloture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvalideur")
    private Employe idvalideur;

}