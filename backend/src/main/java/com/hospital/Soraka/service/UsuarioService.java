package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.usuario.*;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.Rol;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio encargado de la gestion de usuarios del sistema.
 * <p>
 * Proporciona operaciones de consulta, creacion, actualizacion
 * y eliminacion de usuarios, aplicando las restricciones de
 * seguridad segun el rol del usuario autenticado.
 * </p>
 * <p>
 * Adicionalmente, valida reglas de negocio como:
 * - Unicidad del email
 * - Restricciones al cambio de rol para usuarios MEDICO
 * </p>
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
     * <p>
     * Solo accesible por administradores.
     * </p>
     *
     * @return lista de usuarios en forma de {@link UsuarioResponseDTO}
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene un usuario por su identificador.
     * <p>
     * Acceso permitido a administradores o al propio usuario
     * para consulta de su perfil.
     * </p>
     *
     * @param id identificador del usuario
     * @return usuario en forma de {@link UsuarioResponseDTO}
     * @throws EntityNotFoundException si no existe un usuario con el ID indicado
     */
    @PreAuthorize("hasAuthority('ADMIN') or #id == principal.id")
    public UsuarioResponseDTO getUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return buildResponse(usuario);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * La contrasena se almacena cifrada usando el {@link PasswordEncoder}.
     * Se valida que el email no este en uso.
     * </p>
     *
     * @param usuarioDTO datos para la creacion del usuario
     * @return usuario creado en forma de {@link UsuarioResponseDTO}
     * @throws IllegalArgumentException si el email ya esta en uso
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public UsuarioResponseDTO createUsuario(UsuarioPostDTO usuarioDTO){
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya esta en uso");
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
     * Elimina un usuario del sistema a partir de su ID.
     * <p>
     * Solo accesible por administradores.
     * </p>
     *
     * @param id identificador del usuario a eliminar
     * @throws EntityNotFoundException si el usuario no existe
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUsuario(Long id){
        if(!usuarioRepository.existsById(id)){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente los datos de un usuario existente.
     * <p>
     * Solo se modifican los campos presentes en el DTO.
     * - Validación de unicidad del email.
     * - Restricción: no se puede cambiar el rol de un usuario MEDICO
     *   si tiene entidad Medico asociada.
     * </p>
     *
     * @param id identificador del usuario a modificar
     * @param usuarioDTO datos parciales a actualizar
     * @return usuario actualizado en forma de {@link UsuarioResponseDTO}
     * @throws EntityNotFoundException si el usuario no existe
     * @throws IllegalArgumentException si el email ya esta en uso o si se intenta cambiar el rol de un MEDICO con entidad asociada
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public UsuarioResponseDTO patchUsuario(Long id, UsuarioPatchDTO usuarioDTO){
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Validación de email
        if(usuarioDTO.getEmail() != null &&
                !usuarioDTO.getEmail().equals(existente.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya esta en uso");
        }

        if(usuarioDTO.getNombre() != null) existente.setNombre(usuarioDTO.getNombre());
        if(usuarioDTO.getEmail() != null) existente.setEmail(usuarioDTO.getEmail());

        // Validación de rol
        if(usuarioDTO.getRol() != null && !usuarioDTO.getRol().equals(existente.getRol())) {
            // Si el usuario es MEDICO, verificar que no tenga entidad Medico asociada
            if(existente.getRol() == Rol.MEDICO && medicoRepository.existsByUsuario(existente)) {
                throw new IllegalArgumentException("No se puede cambiar el rol de un usuario MEDICO con entidad Medico asociada");
            }
            existente.setRol(usuarioDTO.getRol());
        }

        Usuario actualizado = usuarioRepository.save(existente);
        return buildResponse(actualizado);
    }

    /**
     * Construye el DTO de respuesta a partir de una entidad {@link Usuario}.
     *
     * @param u entidad usuario
     * @return DTO con los datos publicos del usuario
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