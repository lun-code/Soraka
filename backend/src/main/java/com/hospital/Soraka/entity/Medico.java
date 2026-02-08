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

    @Column(name = "foto_url")
    private String urlFoto;

    // CONSTRUCTORES

    public Medico(Usuario usuario, Especialidad especialidad, String urlFoto) {
        this.usuario = usuario;
        this.especialidad = especialidad;
        this.urlFoto = urlFoto;
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

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}