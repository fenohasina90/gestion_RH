package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "absence")
public class Absence {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypeabsence")
    private Typeabsence idtypeabsence;

    @Column(name = "datedebut", nullable = false)
    private LocalDate datedebut;

    @Column(name = "datefin", nullable = false)
    private LocalDate datefin;

    @Column(name = "nombreheures", precision = 6, scale = 2)
    private BigDecimal nombreheures;

    @Column(name = "justificatif", length = Integer.MAX_VALUE)
    private String justificatif;

    @Column(name = "dateenregistrement")
    private Instant dateenregistrement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvalideur")
    private Employe idvalideur;

}