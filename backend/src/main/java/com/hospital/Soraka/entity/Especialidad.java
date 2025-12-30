package com.hospital.Soraka.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "especialidades")
public class Especialidad {

    // ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Column(nullable = false, unique = true)
    private String nombre;

    @JsonIgnore
    @OneToMany(mappedBy = "especialidad", fetch = FetchType.LAZY)
    private List<Medico> medicos =  new ArrayList<>();

    // CONSTRUCTORES
    public Especialidad(String nombre) {
        this.nombre = nombre;
    }

    public Especialidad() {}

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
