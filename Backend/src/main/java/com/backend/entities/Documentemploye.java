package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "documentemploye")
public class Documentemploye {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employe idemploye;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypedocument")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Typedocument idtypedocument;

    @Column(name = "nomfichier", nullable = false)
    private String nomfichier;

    @Column(name = "chemin", length = Integer.MAX_VALUE)
    private String chemin;

    @Column(name = "dateupload")
    private Instant dateupload;

    @Column(name = "datexpiration")
    private LocalDate datexpiration;

    @Column(name = "commentaire", length = Integer.MAX_VALUE)
    private String commentaire;

    @Column(name = "contenujson", columnDefinition = "jsonb")
    private String contenujson;
}