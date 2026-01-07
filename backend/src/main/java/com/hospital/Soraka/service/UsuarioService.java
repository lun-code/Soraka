package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.usuario.*;
import com.hospital.Soraka.entity.TokenConfirmacion;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.Rol;
import com.hospital.Soraka.exception.Usuario.CambioRolMedicoNoPermitidoException;
import com.hospital.Soraka.exception.Usuario.EmailYaEnUsoException;
import com.hospital.Soraka.exception.Usuario.UsuarioNotFoundException;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.TokenConfirmacionRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    TokenConfirmacionRepository tokenConfirmacionRepository;

    @Autowired
    EmailService emailService;

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
     * @throws UsuarioNotFoundException si no existe un usuario con el ID indicado
     */
    public UsuarioResponseDTO getUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        return buildResponse(usuario);
    }

    /**
     * Crea un nuevo usuario en el sistema y envía un correo de confirmación.
     *
     * <p>El flujo completo es el siguiente:</p>
     * <ol>
     *     <li>Se valida que el email no esté en uso; si lo está, lanza {@link EmailYaEnUsoException}.</li>
     *     <li>Se crea la entidad {@link Usuario} con la contraseña cifrada mediante {@link PasswordEncoder}.</li>
     *     <li>Se establece explícitamente {@code isActivo = false}, dejando la cuenta inactiva hasta confirmar el email.</li>
     *     <li>Se guarda el usuario en la base de datos.</li>
     *     <li>Se genera un token único {@link java.util.UUID} para la confirmación de la cuenta.</li>
     *     <li>Se crea y persiste un {@link com.hospital.Soraka.entity.TokenConfirmacion} asociado al usuario, con fecha de expiración de 24 horas.</li>
     *     <li>Se envía un email de confirmación al usuario mediante {@link EmailService#enviarEmailConfirmacion(String, String)}.</li>
     * </ol>
     *
     * <p>Este método <b>solo debe ser invocado por un administrador</b> desde el controller.</p>
     *
     * @param usuarioDTO DTO con los datos necesarios para crear el usuario.
     * @return {@link UsuarioResponseDTO} con la información del usuario recién creado.
     * @throws EmailYaEnUsoException si el email proporcionado ya está registrado en el sistema.
     */
    public UsuarioResponseDTO createUsuario(UsuarioPostDTO usuarioDTO){
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new EmailYaEnUsoException("El email ya está en uso");
        }

        Usuario nuevo = new Usuario(
                usuarioDTO.getNombre(),
                usuarioDTO.getEmail(),
                passwordEncoder.encode(usuarioDTO.getPassword()),
                usuarioDTO.getRol()
        );

        nuevo.setActivo(false); // <- Explícito

        Usuario guardado = usuarioRepository.save(nuevo);

        String token = UUID.randomUUID().toString();

        TokenConfirmacion confirmacion = new TokenConfirmacion();
        confirmacion.setToken(token);
        confirmacion.setUsuario(nuevo);
        confirmacion.setFechaExpiracion(LocalDateTime.now().plusHours(24));

        tokenConfirmacionRepository.save(confirmacion);

        emailService.enviarEmailConfirmacion(nuevo.getEmail(), token);

        return buildResponse(guardado);
    }

    /**
     * Elimina un usuario a partir de su ID.
     *
     * <p>
     * Solo un administrador debería llamar a este método desde el controller.
     *
     * @param id identificador del usuario a eliminar
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    public void deleteUsuario(Long id){
        if(!usuarioRepository.existsById(id)){
            throw new UsuarioNotFoundException("Usuario no encontrado");
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
     * @throws UsuarioNotFoundException si el usuario no existe
     * @throws EmailYaEnUsoException si el email ya está en uso
     * @throws CambioRolMedicoNoPermitidoException se intenta cambiar el rol de un Usuario con entidad Médico asociada
     */
    public UsuarioResponseDTO patchUsuario(Long id, UsuarioPatchDTO usuarioDTO){
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        if(usuarioDTO.getEmail() != null &&
                !usuarioDTO.getEmail().equals(existente.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new EmailYaEnUsoException("El email ya está en uso");
        }

        if(usuarioDTO.getNombre() != null) existente.setNombre(usuarioDTO.getNombre());
        if(usuarioDTO.getEmail() != null) existente.setEmail(usuarioDTO.getEmail());

        if(usuarioDTO.getRol() != null && !usuarioDTO.getRol().equals(existente.getRol())) {
            if(existente.getRol() == Rol.MEDICO && medicoRepository.existsByUsuario(existente)) {
                throw new CambioRolMedicoNoPermitidoException("No se puede cambiar el rol de un usuario MEDICO con entidad Medico asociada");
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