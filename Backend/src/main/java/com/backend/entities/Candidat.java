package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "candidat")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Candidat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "datenaissance")
    private LocalDate datenaissance;

    @Column(name = "adresse", length = 200)
    private String adresse;

    @Column(name = "cv", length = Integer.MAX_VALUE)
    private String cv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idannonce")
    private Annonce idannonce;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idstatut")
    private Statutcandidat idstatut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcomptecandidat")
    private Comptecandidat idcomptecandidat;

    @Column(name="salaire")
    private Double salaire;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="idprovince")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Province idprovince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddiplome")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Diplome iddiplome;
}