package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "soldeconge")
public class Soldeconge {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypeconge")
    private Typeconge idtypeconge;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "joursacquis", precision = 6, scale = 2)
    private BigDecimal joursacquis;

    @Column(name = "jourspris", precision = 6, scale = 2)
    private BigDecimal jourspris;

    @Column(name = "joursrestants", precision = 6, scale = 2)
    private BigDecimal joursrestants;

    @Column(name = "datecalcul")
    private Instant datecalcul;

}