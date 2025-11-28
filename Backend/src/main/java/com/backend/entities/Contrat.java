package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "contrat")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idemploye")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employe idemploye;

    @Column(name = "datedebut")
    private LocalDate datedebut;

    @Column(name = "datefin")
    private LocalDate datefin;

    @Column(name = "nombremois")
    private Integer nombremois;

    @Column(name = "periodessai")
    private Integer periodessai;

    // Référence au type de contrat (table typecontrat)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypecontrat")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Typecontrat idtypecontrat;

    // Référence au statut du contrat (table statutcontrat)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idstatut")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Statutcontrat idstatut;

    // Champs supplémentaires utilisés par l'UI actuelle (conservation pour compatibilité)
    @Column(name = "typecontrat", length = 50)
    private String typecontrat;

    @Column(name = "poste", length = 100)
    private String poste;

    @Column(name = "salaire")
    private Double salaire;

    // Champs transient pour l'affichage
    @Transient
    private Candidat candidat;

    @Transient
    private Integer duration;

    @Transient
    private LocalDate startDate;

    @Transient
    private LocalDate endDate;
}