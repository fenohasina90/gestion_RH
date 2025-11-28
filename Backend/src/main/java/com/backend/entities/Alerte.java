package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "alerte")
public class Alerte {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypealerte")
    private Typealerte idtypealerte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @Column(name = "titre", nullable = false, length = 200)
    private String titre;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @Column(name = "estlu")
    private Boolean estlu;

    @Column(name = "dategeneration")
    private Instant dategeneration;

    @Column(name = "dateecheance")
    private LocalDate dateecheance;

}