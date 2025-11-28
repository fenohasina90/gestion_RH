package com.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "entretien")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Entretien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idcandidat")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Candidat idcandidat;

    @Column(name = "dateheure")
    private LocalDateTime dateheure;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idstatut")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Statutentretien idstatut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idresultat")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Resultat idresultat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idannonce")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Annonce idannonce;

}