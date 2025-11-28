package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "detailbulletin")
public class Detailbulletin {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idbulletin")
    private Bulletinpaie idbulletin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypeelementpaie")
    private Typeelementpaie idtypeelementpaie;

    @Column(name = "libelle", nullable = false, length = 200)
    private String libelle;

    @Column(name = "base", precision = 12, scale = 2)
    private BigDecimal base;

    @Column(name = "taux", precision = 6, scale = 2)
    private BigDecimal taux;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "estgain")
    private Boolean estgain;

}