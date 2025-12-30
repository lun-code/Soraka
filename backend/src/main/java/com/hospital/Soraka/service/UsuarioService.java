package com.hospital.Soraka.service;

import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.exception.EmailYaEnUsoException;
import com.hospital.Soraka.exception.UsuarioNotFoundException;
import com.hospital.Soraka.repository.UsuarioRepository;
import com.hospital.Soraka.dto.usuario.UsuarioPatchDTO;
import com.hospital.Soraka.dto.usuario.UsuarioPostDTO;
import com.hospital.Soraka.dto.usuario.UsuarioResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(u -> new UsuarioResponseDTO(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getRol(),
                        u.isActivo(),
                        u.getFechaRegistro()
                ))
                .toList();
    }

    public UsuarioResponseDTO getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaRegistro()
        );
    }

    public UsuarioResponseDTO createUsuario(UsuarioPostDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new EmailYaEnUsoException("Email ya existente");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setRol(usuarioDTO.getRol());

        Usuario saved = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                saved.getId(),
                saved.getNombre(),
                saved.getEmail(),
                saved.getRol(),
                saved.isActivo(),
                saved.getFechaRegistro()
        );
    }

    public void deleteUsuario(Long id) {
        if(!usuarioRepository.existsById(id)){
            throw new UsuarioNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    // PATCH
    public UsuarioResponseDTO patchUsuario(Long id, UsuarioPatchDTO usuarioDTO) {

        Usuario existente = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        if (usuarioDTO.getNombre() != null) {
            existente.setNombre(usuarioDTO.getNombre());
        }

        if (usuarioDTO.getRol() != null) {
            existente.setRol(usuarioDTO.getRol());
        }

        if (usuarioDTO.getIsActivo() != null) {
            existente.setActivo(usuarioDTO.getIsActivo());
        }

        if (usuarioDTO.getEmail() != null && !existente.getEmail().equals(usuarioDTO.getEmail())) { // Si el email no es null y no es igual
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) { // Si existe un usuario con el email introducido
                throw new EmailYaEnUsoException("El email ya está en uso");
            }
            existente.setEmail(usuarioDTO.getEmail());
        }

        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        Usuario usuarioUpdated = usuarioRepository.save(existente);

        return new UsuarioResponseDTO(
                usuarioUpdated.getId(),
                usuarioUpdated.getNombre(),
                usuarioUpdated.getEmail(),
                usuarioUpdated.getRol(),
                usuarioUpdated.isActivo(),
                usuarioUpdated.getFechaRegistro()
        );
    }
}