package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "critereprofil")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Critereprofil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idprofil")
    private Profil idprofil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcritere")
    private Critere idcritere;

    @Column(name = "valeurdouble", precision = 10, scale = 2)
    private BigDecimal valeurdouble;

    @Column(name = "valeurvarchar", length = 200)
    private String valeurvarchar;

    @Column(name = "valeurbool")
    private Boolean valeurbool;

    @Column(name = "estobligatoire")
    private Boolean estobligatoire;

}