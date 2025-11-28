package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "testannonce")
public class Testannonce {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idtest", nullable = false)
    private Qcmtest idtest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idannonce", nullable = false)
    private Annonce idannonce;

}