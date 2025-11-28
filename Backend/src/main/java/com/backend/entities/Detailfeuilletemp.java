package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "detailfeuilletemps")
public class Detailfeuilletemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfeuilletemps")
    private Feuilletemp idfeuilletemps;

    @Column(name = "datejour", nullable = false)
    private LocalDate datejour;

    @Column(name = "heuresentree")
    private LocalTime heuresentree;

    @Column(name = "heuressortie")
    private LocalTime heuressortie;

    @Column(name = "heurestravaillees", precision = 5, scale = 2)
    private BigDecimal heurestravaillees;

    @Column(name = "heuressup", precision = 5, scale = 2)
    private BigDecimal heuressup;

    @Column(name = "estabsent")
    private Boolean estabsent;

    @Column(name = "commentaire", length = Integer.MAX_VALUE)
    private String commentaire;

}