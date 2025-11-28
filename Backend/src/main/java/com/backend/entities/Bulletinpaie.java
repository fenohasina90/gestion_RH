package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "bulletinpaie")
public class Bulletinpaie {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @Column(name = "mois", nullable = false)
    private Integer mois;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "salairebrut", nullable = false, precision = 12, scale = 2)
    private BigDecimal salairebrut;

    @Column(name = "totalcotisations", precision = 12, scale = 2)
    private BigDecimal totalcotisations;

    @Column(name = "totalgains", precision = 12, scale = 2)
    private BigDecimal totalgains;

    @Column(name = "totalretenues", precision = 12, scale = 2)
    private BigDecimal totalretenues;

    @Column(name = "salaireimposable", nullable = false, precision = 12, scale = 2)
    private BigDecimal salaireimposable;

    @Column(name = "impot", precision = 12, scale = 2)
    private BigDecimal impot;

    @Column(name = "netapayer", nullable = false, precision = 12, scale = 2)
    private BigDecimal netapayer;

    @Column(name = "dategeneration")
    private Instant dategeneration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idstatut")
    private Statutbulletin idstatut;

    @Column(name = "cheminpdf", length = Integer.MAX_VALUE)
    private String cheminpdf;

}