package com.hospital.Soraka.dto.usuario;

import com.hospital.Soraka.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UsuarioPatchDTO {

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @Email(message = "Formato de email inválido")
    private String email;

    private Boolean isActivo;

    private Rol rol;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // getters y setters

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsActivo() {
        return isActivo;
    }

    public Rol getRol() {
        return rol;
    }

    public String getPassword() {
        return password;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsActivo(Boolean isActivo) {
        this.isActivo = isActivo;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}