package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pointage")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pointage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @Column(name = "dateheure", nullable = false)
    private Instant dateheure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypepointage")
    private Typepointage idtypepointage;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "commentaire", length = Integer.MAX_VALUE)
    private String commentaire;

    @Column(name = "estvalide")
    private Boolean estvalide = Boolean.TRUE;

}