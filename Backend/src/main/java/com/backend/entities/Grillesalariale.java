package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "grillesalariale")
public class Grillesalariale {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategorie")
    private Categoriepersonnel idcategorie;

    @Column(name = "niveauechelon", length = 50)
    private String niveauechelon;

    @Column(name = "salairebrut", nullable = false, precision = 12, scale = 2)
    private BigDecimal salairebrut;

    @Column(name = "dateeffet", nullable = false)
    private LocalDate dateeffet;

    @Column(name = "datefin")
    private LocalDate datefin;

}