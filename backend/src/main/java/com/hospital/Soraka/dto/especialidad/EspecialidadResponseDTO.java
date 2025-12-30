package com.hospital.Soraka.dto.especialidad;

public class EspecialidadResponseDTO {

    // ATRIBUTOS
    private Long id;
    private String nombre;

    // CONSTRUCTORES
    public EspecialidadResponseDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}