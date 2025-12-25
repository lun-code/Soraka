package com.hospital.Soraka.dto.usuario;

import com.hospital.Soraka.enums.Rol;
import java.time.LocalDateTime;

public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private Boolean isActivo;
    private LocalDateTime fechaRegistro;

    // Constructor
    public UsuarioResponseDTO(Long id, String nombre, String email, Rol rol, Boolean activo, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.isActivo = activo;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }
    public Boolean getIsActivo() { return isActivo; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
