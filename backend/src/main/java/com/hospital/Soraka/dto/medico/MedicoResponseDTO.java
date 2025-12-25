package com.hospital.Soraka.dto.medico;

public class MedicoResponseDTO {

    // Medico
    private Long id;

    // Usuario
    private Long usuarioId;
    private String nombreUsuario;
    private String emailUsuario;

    // Especialidad
    private Long especialidadId;
    private String nombreEspecialidad;

    // Constructor
    public MedicoResponseDTO(
            Long id,
            Long usuarioId,
            String nombreUsuario,
            String emailUsuario,
            Long especialidadId,
            String nombreEspecialidad
    ) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.especialidadId = especialidadId;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getEmailUsuario() { return emailUsuario; }
    public Long getEspecialidadId() { return especialidadId; }
    public String getNombreEspecialidad() { return nombreEspecialidad; }
}