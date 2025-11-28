package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "droitconge")
public class Droitconge {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategorie")
    private Categoriepersonnel idcategorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypeconge")
    private Typeconge idtypeconge;

    @Column(name = "joursparannee", precision = 5, scale = 2)
    private BigDecimal joursparannee;

    @Column(name = "joursparanciennete", precision = 5, scale = 2)
    private BigDecimal joursparanciennete;

    @Column(name = "anneesanciennete")
    private Integer anneesanciennete;

    @Column(name = "dureevalidite")
    private Integer dureevalidite;

}