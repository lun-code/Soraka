package com.hospital.Soraka.controller;

import com.hospital.Soraka.service.UsuarioService;
import com.hospital.Soraka.dto.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene la lista de todos los usuarios.
     * Solo accesible para administradores.
     *
     * @return Lista de UsuarioResponseDTO con información de cada usuario.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioService.getUsuarios();
    }

    /**
     * Obtiene un usuario específico por su ID.
     * Acceso permitido a administradores o al propio usuario.
     *
     * @param id ID del usuario a consultar.
     * @return UsuarioResponseDTO con la información del usuario.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or #id == principal.id")
    public UsuarioResponseDTO getUsuarioById(@PathVariable Long id){
        return usuarioService.getUsuarioById(id);
    }

    /**
     * Elimina un usuario por su ID.
     * Solo accesible para administradores.
     *
     * @param id ID del usuario a eliminar.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id){
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * Modifica un usuario existente.
     * Solo accesible para administradores.
     *
     * @param usuarioDTO DTO con los campos a actualizar.
     * @param id ID del usuario a modificar.
     * @return UsuarioResponseDTO con los datos actualizados del usuario.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UsuarioResponseDTO patchUsuario(@Valid @RequestBody UsuarioPatchDTO usuarioDTO, @PathVariable Long id){
        return usuarioService.patchUsuario(id, usuarioDTO);
    }
}