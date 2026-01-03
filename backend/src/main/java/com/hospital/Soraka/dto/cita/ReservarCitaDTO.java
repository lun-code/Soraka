package com.hospital.Soraka.dto.cita;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReservarCitaDTO {

    // ATRIBUTOS

    @NotBlank
    @Size(max = 255)
    private String motivo;

    // GETTERS Y SETTERS

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}