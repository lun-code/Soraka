package com.hospital.Soraka.dto.medico;

import jakarta.validation.constraints.NotNull;

public class MedicoPostDTO {

    // Atributos
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La especialidad es obligatoria")
    private Long especialidadId;

    @NotNull(message = "La foto es obligatoria")
    private String urlFoto;

    // Getters y setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getEspecialidadId() {
        return especialidadId;
    }

    public String getUrlFoto() { return urlFoto; }
}