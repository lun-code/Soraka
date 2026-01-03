package com.hospital.Soraka.controller;

import com.hospital.Soraka.service.UsuarioService;
import com.hospital.Soraka.dto.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que maneja operaciones sobre los usuarios del sistema.
 * Permite consultar, modificar y eliminar usuarios según los permisos definidos.
 * <p>
 * Las operaciones sensibles como creación, actualización y eliminación están
 * restringidas a usuarios con los roles adecuados (ej. ADMIN).
 */
@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene la lista de todos los usuarios.
     * Solo accesible para roles autorizados según la configuración de seguridad.
     *
     * @return Lista de UsuarioResponseDTO con información de cada usuario.
     */
    @GetMapping("/usuarios")
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioService.getUsuarios();
    }

    /**
     * Obtiene un usuario específico por su ID.
     * Pacientes pueden consultar sus propios datos, médicos y admins pueden consultar cualquier usuario.
     *
     * @param id ID del usuario a consultar.
     * @return UsuarioResponseDTO con la información del usuario.
     */
    @GetMapping("/usuarios/{id}")
    public UsuarioResponseDTO getUsuarioById(@PathVariable Long id){
        return usuarioService.getUsuarioById(id);
    }

    /**
     * Elimina un usuario por su ID.
     * Solo usuarios con permisos administrativos pueden realizar esta operación.
     *
     * @param id ID del usuario a eliminar.
     */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id){
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * Modifica un usuario existente.
     * Solo usuarios con permisos administrativos pueden modificar datos de otros usuarios.
     *
     * @param usuarioDTO DTO con los campos a actualizar.
     * @param id ID del usuario a modificar.
     * @return UsuarioResponseDTO con los datos actualizados del usuario.
     */
    @PatchMapping("/usuarios/{id}")
    public UsuarioResponseDTO patchUsuario(@Valid @RequestBody UsuarioPatchDTO usuarioDTO, @PathVariable Long id){
        return usuarioService.patchUsuario(id, usuarioDTO);
    }
}