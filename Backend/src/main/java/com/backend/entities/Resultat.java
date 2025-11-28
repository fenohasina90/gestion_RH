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
@Table(name = "resultat")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Resultat {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "note")
    private Integer note;

    @Column(name = "appreciation", length = 200)
    private String appreciation;

}