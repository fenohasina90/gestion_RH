package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "employe")
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "adresse", length = 200)
    private String adresse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddept")
    private Departement iddept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategorie")
    private Categoriepersonnel idcategorie;

    @Column(name = "datenaissance")
    private LocalDate datenaissance;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "photo", length = Integer.MAX_VALUE)
    private String photo;

    @Column(name = "cin", length = 50)
    private String cin;

    @Column(name = "datedembauche")
    private LocalDate datedembauche;

    @Column(name = "lieunaissance", length = 150)
    private String lieunaissance;

    @Column(name = "nationalite", length = 50)
    private String nationalite;

    @Column(name = "situationfamiliale", length = 50)
    private String situationfamiliale;

    @Column(name = "nombreenfants")
    private Integer nombreenfants;

    @Column(name = "numerocnaps", length = 50)
    private String numerocnaps;

    @Column(name = "numeroostie", length = 50)
    private String numeroostie;

    @Column(name = "matricule", length = 50)
    private String matricule;

}