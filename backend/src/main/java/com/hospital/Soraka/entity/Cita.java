package com.hospital.Soraka.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hospital.Soraka.enums.EstadoCita;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas", uniqueConstraints = {@UniqueConstraint(columnNames = {"medico_id", "fecha_hora"})})
public class Cita {

    // ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    @NotNull(message = "El paciente es obligatorio")
    private Usuario paciente;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    @NotNull(message = "El médico es obligatorio")
    private Medico medico;

    @Column(name = "fecha_hora", nullable = false)
    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private EstadoCita estado;

    @Column(length = 255)
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    // CONSTRUCTORES

    public Cita(Usuario paciente, Medico medico, LocalDateTime fechaHora, String motivo) {
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.estado = EstadoCita.PENDIENTE;
        this.motivo = motivo;
    }

    public Cita() {}

    // SETTERS Y GETTERS

    public Long getId() {
        return id;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
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
}
