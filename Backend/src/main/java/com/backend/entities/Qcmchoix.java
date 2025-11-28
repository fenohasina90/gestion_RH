package com.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "qcmchoix")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Qcmchoix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idquestion", nullable = false)
    private Qcmquestion idquestion;

    @Column(name = "texte", nullable = false, length = 500)
    private String texte;

    @Column(name = "estcorrect")
    private Boolean estcorrect;

}