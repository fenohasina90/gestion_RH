package com.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "parametrecotisation")
public class Parametrecotisation {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "taux", precision = 6, scale = 2)
    private BigDecimal taux;

    @Column(name = "plafondsalarial", precision = 12, scale = 2)
    private BigDecimal plafondsalarial;

    @Column(name = "dateeffet", nullable = false)
    private LocalDate dateeffet;

    @Column(name = "datefin")
    private LocalDate datefin;

}