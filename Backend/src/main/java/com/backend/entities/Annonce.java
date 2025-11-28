package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "annonce")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "datedebut")
    private LocalDate datedebut;

    @Column(name = "datefin")
    private LocalDate datefin;

    @Column(name = "nomposte", nullable = false, length = 100)
    private String nomposte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddepartement")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Departement iddepartement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idprofil")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Profil idprofil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypeannonce")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Typeannonce idtypeannonce;

    @Column(name = "datepublication")
    private LocalDate datepublication;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="idprovince")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Province idprovince;




}