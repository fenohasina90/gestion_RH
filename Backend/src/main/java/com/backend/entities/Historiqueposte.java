package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "historiqueposte")
public class Historiqueposte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employe idemploye;

    @Column(name = "posteoccupe", length = 150)
    private String posteoccupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategorie")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categoriepersonnel idcategorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddepartement")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Departement iddepartement;

    @Column(name = "datedebut", nullable = false)
    private LocalDate datedebut;

    @Column(name = "datefin")
    private LocalDate datefin;

    @Column(name = "motif", length = 200)
    private String motif;

}