package com.hospital.Soraka.dto.medico;

public class MedicoPatchDTO {

    // ATRIBUTOS
    private Long usuarioId;
    private Long especialidadId;


    // GETTERS Y SETTERS
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(Long especialidadId) {
        this.especialidadId = especialidadId;
    }
}