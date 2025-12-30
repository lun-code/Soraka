package com.hospital.Soraka.dto.especialidad;

import jakarta.validation.constraints.NotBlank;

public class EspecialidadPostDTO {

    // ATRIBUTOS
    @NotBlank(message = "El nombre de la especialidad es obligatorio.")
    private String nombre;

    // GETTERS Y SETTERS
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}