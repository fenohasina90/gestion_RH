package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "v_conges_valides")
public class VCongeValide {
    @Id
    @Column(name = "iddemande")
    private Integer iddemande;

    @Column(name = "idemploye")
    private Integer idemploye;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "idtypeconge")
    private Integer idtypeconge;

    @Column(name = "typeconge")
    private String typeconge;

    @Column(name = "datedemande")
    private Instant datedemande;

    @Column(name = "datedebut")
    private LocalDate datedebut;

    @Column(name = "datefin")
    private LocalDate datefin;

    @Column(name = "nombrejours")
    private java.math.BigDecimal nombrejours;

    @Column(name = "statut")
    private String statut;
}
