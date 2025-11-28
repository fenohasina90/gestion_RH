package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "qcmreponse")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Qcmreponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcandidat", nullable = false)
    private Candidat idcandidat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idtest", nullable = false)
    private Qcmtest idtest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idquestion", nullable = false)
    private Qcmquestion idquestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idchoix")
    private Qcmchoix idchoix;

    @Column(name = "pointsobtenus")
    private Integer pointsobtenus;

    @Column(name = "datereponse")
    private java.time.LocalDateTime datereponse;

}