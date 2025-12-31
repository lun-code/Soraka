package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.usuario.*;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Devuelve todos los usuarios. Solo accesible por admins.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Devuelve un usuario por su id.
     * Solo accesible por admins o por el propio usuario (lectura de su perfil).
     */
    @PreAuthorize("hasAuthority('ADMIN') or #id == principal.id")
    public UsuarioResponseDTO getUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return buildResponse(usuario);
    }

    /**
     * Crear un usuario nuevo. Solo admins.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public UsuarioResponseDTO createUsuario(UsuarioPostDTO usuarioDTO){
        Usuario nuevo = new Usuario(usuarioDTO.getNombre(),
                usuarioDTO.getEmail(),
                usuarioDTO.getPassword(),
                usuarioDTO.getRol());
        Usuario guardado = usuarioRepository.save(nuevo);
        return buildResponse(guardado);
    }

    /**
     * Eliminar un usuario. Solo admins.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUsuario(Long id){
        if(!usuarioRepository.existsById(id)){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Actualizar usuario. Solo admins pueden modificar datos.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public UsuarioResponseDTO patchUsuario(Long id, UsuarioPatchDTO usuarioDTO){
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if(usuarioDTO.getNombre() != null) existente.setNombre(usuarioDTO.getNombre());
        if(usuarioDTO.getEmail() != null) existente.setEmail(usuarioDTO.getEmail());
        if(usuarioDTO.getRol() != null) existente.setRol(usuarioDTO.getRol());
        // Nunca permitir que el usuario cambie isActivo o fechaRegistro por sí mismo

        Usuario actualizado = usuarioRepository.save(existente);
        return buildResponse(actualizado);
    }

    private UsuarioResponseDTO buildResponse(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getRol(),
                u.isActivo(),
                u.getFechaRegistro()
        );
    }
}