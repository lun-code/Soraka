package com.hospital.Soraka.dto.cita;

import com.hospital.Soraka.enums.EstadoCita;

import java.time.LocalDateTime;

public class CitaResponseDTO {

    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private Long medicoId;
    private String medicoNombre;
    private String medicoEspecialidad;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private String motivo;

    public CitaResponseDTO(
            Long id,
            Long pacienteId,
            String pacienteNombre,
            Long medicoId,
            String medicoNombre,
            String medicoEspecialidad,
            LocalDateTime fechaHora,
            EstadoCita estado,
            String motivo
    ) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.medicoId = medicoId;
        this.medicoNombre = medicoNombre;
        this.medicoEspecialidad = medicoEspecialidad;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.motivo = motivo;
    }

    public Long getId() {
        return id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public String getMedicoNombre() {
        return medicoNombre;
    }

    public String getMedicoEspecialidad() {return medicoEspecialidad;}

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }
}