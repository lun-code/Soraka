package com.hospital.Soraka.dto.cita;

import com.hospital.Soraka.enums.EstadoCita;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CitaPatchDTO {

    // ATRIBUTOS
    private LocalDateTime fechaHora;

    private EstadoCita estado;

    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    private Long medicoId;

    // GETTERS Y SETTERS
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }
}