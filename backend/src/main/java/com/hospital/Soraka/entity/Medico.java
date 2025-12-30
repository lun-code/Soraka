package com.hospital.Soraka.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medicos")
public class Medico {

    // ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

    // CONSTRUCTORES

    public Medico(Usuario usuario, Especialidad especialidad) {
        this.usuario = usuario;
        this.especialidad = especialidad;
    }

    public Medico() {}

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}