package com.hospital.Soraka.dto.cita;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CitaPostDTO {

    // ATRIBUTOS
    @NotNull(message = "El paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El medico es obligatorio")
    private Long medicoId;

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;


    // GETTERS Y SETTERS
    public Long getPacienteId() {
        return pacienteId;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
