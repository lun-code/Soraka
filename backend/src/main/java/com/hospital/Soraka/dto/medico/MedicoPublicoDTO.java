package com.hospital.Soraka.dto.medico;

public class MedicoPublicoDTO {

    private Long id;
    private String nombre;
    private String especialidad;
    private String urlFoto;

    public MedicoPublicoDTO(
            Long id,
            String nombre,
            String especialidad,
            String urlFoto
    ) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.urlFoto = urlFoto;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
    public String getUrlFoto() { return urlFoto; }
}