package com.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@Entity
@Table(name = "typeconge")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Typeconge {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "estremunere")
    private Boolean estremunere;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

}