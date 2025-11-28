package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "salairebase")
public class Salairebase {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @Column(name = "salairebrut", nullable = false, precision = 12, scale = 2)
    private BigDecimal salairebrut;

    @Column(name = "dateeffet", nullable = false)
    private LocalDate dateeffet;

    @Column(name = "datefin")
    private LocalDate datefin;

    @Column(name = "motif", length = 200)
    private String motif;

}