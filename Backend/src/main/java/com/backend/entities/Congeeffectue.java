package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "congeeffectue")
public class Congeeffectue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddemandeconge")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Demandeconge iddemandeconge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employe idemploye;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypeconge")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Typeconge idtypeconge;

    @Column(name = "datedebut", nullable = false)
    private LocalDate datedebut;

    @Column(name = "datefin", nullable = false)
    private LocalDate datefin;

    @Column(name = "nombrejourspris", precision = 5, scale = 2)
    private BigDecimal nombrejourspris;

}