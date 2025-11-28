package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "qcmquestion")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Qcmquestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idtest", nullable = false)
    private Qcmtest idtest;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "question", nullable = false, length = Integer.MAX_VALUE)
    private String question;

    @Column(name = "points", nullable = false)
    private Integer points;

}