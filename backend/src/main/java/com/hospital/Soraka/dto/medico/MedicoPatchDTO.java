package com.hospital.Soraka.dto.medico;

public class MedicoPatchDTO {

    // ATRIBUTOS
    private Long usuarioId;
    private Long especialidadId;
    private String urlFoto;

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

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto){
        this.urlFoto = urlFoto;
    }
}