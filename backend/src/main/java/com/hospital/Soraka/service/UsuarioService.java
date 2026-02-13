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
 * <p>
 * Proporciona operaciones de:
 * <ul>
 *     <li>Listado de usuarios completos y públicos.</li>
 *     <li>Consulta individual por ID.</li>
 *     <li>Creación de nuevos usuarios con envío de correo de confirmación.</li>
 *     <li>Actualización parcial de usuarios existentes.</li>
 *     <li>Eliminación de usuarios.</li>
 * </ul>
 * <p>
 * La seguridad basada en roles debe aplicarse desde el controller mediante {@code @PreAuthorize}.
 * Este servicio valida únicamente la existencia de entidades y reglas de negocio:
 * <ul>
 *     <li>Unicidad de email.</li>
 *     <li>Restricciones al cambio de rol para usuarios MEDICO con entidad asociada.</li>
 * </ul>
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
     * Obtiene todos los usuarios registrados en el sistema.
     * <p>
     * Solo administradores deberían invocar este método desde el controller.
     *
     * @return Lista de {@link UsuarioResponseDTO} con los datos completos de cada usuario.
     */
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene todos los usuarios de rol PACIENTE para uso público.
     *
     * @return Lista de {@link UsuarioPublicoDTO} con información limitada de cada usuario.
     */
    public List<UsuarioPublicoDTO> getUsuariosPublico() {
        return usuarioRepository.findAllByRol(Rol.PACIENTE)
                .stream()
                .map(this::buildResponsePublico)
                .toList();
    }

    /**
     * Obtiene un usuario por su ID.
     * <p>
     * Solo administradores o el propio usuario deberían invocar este método desde el controller.
     *
     * @param id Identificador del usuario.
     * @return {@link UsuarioResponseDTO} con los datos completos del usuario.
     * @throws UsuarioNotFoundException si no existe un usuario con el ID indicado.
     */
    public UsuarioResponseDTO getUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        return buildResponse(usuario);
    }

    /**
     * Crea un nuevo usuario en el sistema y envía un correo de confirmación.
     * <p>
     * Flujo completo:
     * <ol>
     *     <li>Valida que el email no esté en uso; lanza {@link EmailYaEnUsoException} si corresponde.</li>
     *     <li>Crea la entidad {@link Usuario} con la contraseña cifrada mediante {@link PasswordEncoder}.</li>
     *     <li>Establece {@code isActivo = false} hasta confirmar el email.</li>
     *     <li>Guarda el usuario en la base de datos.</li>
     *     <li>Genera un token único {@link UUID} para la confirmación de cuenta.</li>
     *     <li>Crea y persiste un {@link TokenConfirmacion} asociado al usuario, con expiración de 24 horas.</li>
     *     <li>Envía un correo de confirmación mediante {@link EmailService#enviarEmailConfirmacion(String, String)}.</li>
     * </ol>
     * <p>
     * Solo administradores deberían invocar este método desde el controller.
     *
     * @param usuarioDTO DTO con los datos necesarios para crear el usuario.
     * @return {@link UsuarioResponseDTO} con los datos del usuario creado.
     * @throws EmailYaEnUsoException si el email proporcionado ya está registrado.
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

        nuevo.setActivo(false);

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
     * Elimina un usuario del sistema por su ID.
     * <p>
     * Solo administradores deberían invocar este método desde el controller.
     *
     * @param id Identificador del usuario a eliminar.
     * @throws UsuarioNotFoundException si el usuario no existe.
     */
    public void deleteUsuario(Long id){
        if(!usuarioRepository.existsById(id)){
            throw new UsuarioNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente los datos de un usuario existente.
     * <p>
     * Solo administradores deberían invocar este método desde el controller.
     * Valida reglas de negocio:
     * <ul>
     *     <li>Unicidad de email.</li>
     *     <li>No permite cambiar el rol de un usuario MEDICO con entidad asociada.</li>
     * </ul>
     *
     * @param id Identificador del usuario a modificar.
     * @param usuarioDTO DTO con los campos parciales a actualizar.
     * @return {@link UsuarioResponseDTO} con los datos actualizados del usuario.
     * @throws UsuarioNotFoundException si el usuario no existe.
     * @throws EmailYaEnUsoException si el email ya está en uso.
     * @throws CambioRolMedicoNoPermitidoException si se intenta cambiar el rol de un usuario MEDICO con entidad asociada.
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
     * Construye un DTO de respuesta completo a partir de la entidad {@link Usuario}.
     *
     * @param u Entidad {@link Usuario}.
     * @return {@link UsuarioResponseDTO} con los datos completos del usuario.
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

    /**
     * Construye un DTO de respuesta pública a partir de la entidad {@link Usuario}.
     *
     * @param u Entidad {@link Usuario}.
     * @return {@link UsuarioPublicoDTO} con los datos limitados para uso público.
     */
    private UsuarioPublicoDTO buildResponsePublico(Usuario u) {
        return new UsuarioPublicoDTO(
                u.getId()
        );
    }
}