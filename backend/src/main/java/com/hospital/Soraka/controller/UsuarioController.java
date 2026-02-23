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
 * Controlador REST que gestiona operaciones sobre los usuarios del sistema.
 *
 * <p>
 * Permite consultar, modificar y eliminar usuarios según los permisos definidos.
 * Las operaciones sensibles como creación, actualización y eliminación están
 * restringidas a usuarios con roles específicos, típicamente ADMIN.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene la lista completa de usuarios.
     * <p>
     * Solo accesible para administradores.
     *
     * @return Lista de {@link UsuarioResponseDTO} con información completa de cada usuario.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioService.getUsuarios();
    }

    /**
     * Obtiene la lista de usuarios públicos (rol PACIENTE) para uso externo.
     *
     * @return Lista de {@link UsuarioPublicoDTO} con información limitada de cada usuario.
     */
    @GetMapping("/publico")
    public List<UsuarioPublicoDTO> getUsuariosPublico() {
        return usuarioService.getUsuariosPublico();
    }

    /**
     * Obtiene un usuario específico por su ID.
     * <p>
     * Accesible para administradores o el propio usuario.
     *
     * @param id ID del usuario a consultar.
     * @return {@link UsuarioResponseDTO} con información completa del usuario.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or #id == principal.id")
    public UsuarioResponseDTO getUsuarioById(@PathVariable Long id){
        return usuarioService.getUsuarioById(id);
    }

    /**
     * Elimina un usuario por su ID.
     * <p>
     * Solo accesible para administradores.
     *
     * @param id ID del usuario a eliminar.
     * @return {@link ResponseEntity} con estado 204 No Content si la eliminación es exitosa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id){
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza parcialmente un usuario existente.
     * <p>
     * Solo accesible para administradores.
     * Aplica validaciones de negocio definidas en {@link com.hospital.Soraka.service.UsuarioService}.
     *
     * @param usuarioDTO DTO con los campos parciales a actualizar.
     * @param id ID del usuario a modificar.
     * @return {@link UsuarioResponseDTO} con los datos actualizados del usuario.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UsuarioResponseDTO patchUsuario(@Valid @RequestBody UsuarioPatchDTO usuarioDTO, @PathVariable Long id){
        return usuarioService.patchUsuario(id, usuarioDTO);
    }
}