package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.usuario.*;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.Rol;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de dominio encargado de la gestión de usuarios del sistema.
 *
 * <p>
 * Contiene operaciones de consulta, creación, actualización parcial y eliminación.
 * La seguridad basada en roles debe aplicarse desde el controller mediante {@code @PreAuthorize}.
 * Este service valida únicamente la existencia de entidades y reglas de negocio:
 * - Unicidad de email
 * - Restricciones al cambio de rol para usuarios MEDICO con entidad asociada
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene el listado completo de usuarios registrados.
     *
     * <p>
     * Solo un administrador debería llamar a este método desde el controller.
     *
     * @return lista de {@link UsuarioResponseDTO} con los datos de cada usuario.
     */
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * <p>
     * Solo administradores o el propio usuario deberían llamar a este método desde el controller.
     *
     * @param id identificador del usuario
     * @return {@link UsuarioResponseDTO} con los datos del usuario
     * @throws EntityNotFoundException si no existe un usuario con el ID indicado
     */
    public UsuarioResponseDTO getUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return buildResponse(usuario);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * <p>
     * La contraseña se almacena cifrada usando {@link PasswordEncoder}.
     * Solo un administrador debería llamar a este método desde el controller.
     *
     * @param usuarioDTO DTO con los datos para la creación del usuario
     * @return {@link UsuarioResponseDTO} con el usuario creado
     * @throws IllegalArgumentException si el email ya está en uso
     */
    public UsuarioResponseDTO createUsuario(UsuarioPostDTO usuarioDTO){
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Usuario nuevo = new Usuario(
                usuarioDTO.getNombre(),
                usuarioDTO.getEmail(),
                passwordEncoder.encode(usuarioDTO.getPassword()),
                usuarioDTO.getRol());
        Usuario guardado = usuarioRepository.save(nuevo);
        return buildResponse(guardado);
    }

    /**
     * Elimina un usuario a partir de su ID.
     *
     * <p>
     * Solo un administrador debería llamar a este método desde el controller.
     *
     * @param id identificador del usuario a eliminar
     * @throws EntityNotFoundException si el usuario no existe
     */
    public void deleteUsuario(Long id){
        if(!usuarioRepository.existsById(id)){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente los datos de un usuario existente.
     *
     * <p>
     * Solo un administrador debería llamar a este método desde el controller.
     * Se aplican validaciones de negocio:
     * - Unicidad de email
     * - No se puede cambiar el rol de un usuario MEDICO con entidad asociada
     *
     * @param id identificador del usuario a modificar
     * @param usuarioDTO DTO con los campos parciales a actualizar
     * @return {@link UsuarioResponseDTO} con los datos actualizados
     * @throws EntityNotFoundException si el usuario no existe
     * @throws IllegalArgumentException si el email ya está en uso o se intenta cambiar el rol de un MEDICO con entidad asociada
     */
    public UsuarioResponseDTO patchUsuario(Long id, UsuarioPatchDTO usuarioDTO){
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if(usuarioDTO.getEmail() != null &&
                !usuarioDTO.getEmail().equals(existente.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        if(usuarioDTO.getNombre() != null) existente.setNombre(usuarioDTO.getNombre());
        if(usuarioDTO.getEmail() != null) existente.setEmail(usuarioDTO.getEmail());

        if(usuarioDTO.getRol() != null && !usuarioDTO.getRol().equals(existente.getRol())) {
            if(existente.getRol() == Rol.MEDICO && medicoRepository.existsByUsuario(existente)) {
                throw new IllegalArgumentException("No se puede cambiar el rol de un usuario MEDICO con entidad Medico asociada");
            }
            existente.setRol(usuarioDTO.getRol());
        }

        Usuario actualizado = usuarioRepository.save(existente);
        return buildResponse(actualizado);
    }

    /**
     * Construye un DTO de respuesta a partir de la entidad {@link Usuario}.
     *
     * @param u entidad usuario
     * @return {@link UsuarioResponseDTO} con los datos públicos del usuario
     */
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