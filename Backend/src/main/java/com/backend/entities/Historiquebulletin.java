package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "historiquebulletin")
public class Historiquebulletin {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idbulletin")
    private Bulletinpaie idbulletin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idstatut")
    private Statutbulletin idstatut;

    @Column(name = "datechangement")
    private Instant datechangement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemploye")
    private Employe idemploye;

    @Column(name = "commentaire", length = Integer.MAX_VALUE)
    private String commentaire;

}