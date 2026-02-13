package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.medico.MedicoPatchDTO;
import com.hospital.Soraka.dto.medico.MedicoPostDTO;
import com.hospital.Soraka.dto.medico.MedicoPublicoDTO;
import com.hospital.Soraka.dto.medico.MedicoResponseDTO;
import com.hospital.Soraka.entity.Especialidad;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.Rol;
import com.hospital.Soraka.exception.Especialidad.EspecialidadNotFoundException;
import com.hospital.Soraka.exception.Medico.MedicoExisteException;
import com.hospital.Soraka.exception.Medico.MedicoNotFoundException;
import com.hospital.Soraka.exception.Usuario.RolNoValidoException;
import com.hospital.Soraka.exception.Usuario.UsuarioNotFoundException;
import com.hospital.Soraka.repository.EspecialidadRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de dominio para la gestión de médicos.
 * <p>
 * Proporciona operaciones de:
 * <ul>
 *     <li>Listado de médicos.</li>
 *     <li>Consulta individual por ID.</li>
 *     <li>Creación de nuevos médicos.</li>
 *     <li>Actualización parcial de médicos existentes.</li>
 *     <li>Eliminación de médicos.</li>
 * </ul>
 * <p>
 * La seguridad basada en roles debe aplicarse en el controller mediante {@code @PreAuthorize}.
 * Este servicio valida únicamente la existencia de entidades y las reglas de negocio relacionadas.
 */
@Service
@Transactional
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    /**
     * Obtiene todos los médicos registrados en el sistema.
     *
     * @return Lista de {@link MedicoResponseDTO} con la información de cada médico.
     */
    public List<MedicoResponseDTO> getMedicos() {
        return medicoRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene todos los médicos para uso público, con información limitada.
     *
     * @return Lista de {@link MedicoPublicoDTO} con datos públicos de cada médico.
     */
    public List<MedicoPublicoDTO> getMedicosPublico() {
        return medicoRepository.findAll()
                .stream()
                .map(this::buildResponsePublico)
                .toList();
    }

    /**
     * Obtiene un médico a partir de su ID.
     *
     * @param id Identificador del médico.
     * @return {@link MedicoResponseDTO} con los datos completos del médico.
     * @throws MedicoNotFoundException si no existe un médico con el ID indicado.
     */
    public MedicoResponseDTO getMedicoById(Long id){
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new MedicoNotFoundException("Medico no encontrado"));

        return buildResponse(medico);
    }

    /**
     * Crea un nuevo médico en el sistema.
     * <p>
     * Solo usuarios con rol ADMIN deberían invocar este método desde el controller.
     *
     * @param medico DTO con los datos necesarios para la creación.
     * @return {@link MedicoResponseDTO} con los datos del médico creado.
     * @throws UsuarioNotFoundException si el usuario no existe.
     * @throws EspecialidadNotFoundException si la especialidad no existe.
     * @throws RolNoValidoException si el usuario no tiene rol MEDICO.
     * @throws MedicoExisteException si el usuario ya está asignado a otro médico.
     */
    public MedicoResponseDTO createMedico(MedicoPostDTO medico) {
        Usuario usuario = usuarioRepository.findById(medico.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Especialidad especialidad = especialidadRepository.findById(medico.getEspecialidadId())
                .orElseThrow(() -> new EspecialidadNotFoundException("Especialidad no encontrada"));

        if (usuario.getRol() != Rol.MEDICO) {
            throw new RolNoValidoException("El usuario debe tener rol MEDICO para ser asignado como médico");
        }

        if (medicoRepository.existsByUsuario(usuario)) {
            throw new MedicoExisteException("El usuario ya está asignado a un médico");
        }

        Medico nuevoMedico = new Medico(usuario, especialidad, medico.getUrlFoto() );
        Medico guardado = medicoRepository.save(nuevoMedico);
        return buildResponse(guardado);
    }

    /**
     * Elimina un médico del sistema a partir de su ID.
     * <p>
     * Solo usuarios con rol ADMIN deberían invocar este método desde el controller.
     *
     * @param id Identificador del médico a eliminar.
     * @throws MedicoNotFoundException si el médico no existe.
     */
    public void deleteMedico(Long id){
        if(!medicoRepository.existsById(id)){
            throw new MedicoNotFoundException("El médico no existe");
        }
        medicoRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente un médico existente.
     * <p>
     * Solo usuarios con rol ADMIN deberían invocar este método desde el controller.
     *
     * @param id Identificador del médico a modificar.
     * @param medicoDTO DTO con los campos parciales a actualizar.
     * @return {@link MedicoResponseDTO} con los datos actualizados del médico.
     * @throws MedicoNotFoundException si el médico no existe.
     * @throws EspecialidadNotFoundException si la especialidad indicada no existe.
     * @throws UsuarioNotFoundException si el usuario indicado no existe.
     * @throws RolNoValidoException si el usuario no tiene rol MEDICO.
     * @throws MedicoExisteException si el usuario ya está asignado a otro médico.
     */
    public MedicoResponseDTO patchMedico(Long id, MedicoPatchDTO medicoDTO){
        Medico existente = medicoRepository.findById(id)
                .orElseThrow(() -> new MedicoNotFoundException("Medico no encontrado"));

        if(medicoDTO.getEspecialidadId() != null){
            Especialidad especialidad = especialidadRepository.findById(medicoDTO.getEspecialidadId())
                    .orElseThrow(() -> new EspecialidadNotFoundException("Especialidad no encontrada"));
            existente.setEspecialidad(especialidad);
        }

        if(medicoDTO.getUsuarioId() != null){
            Usuario usuario = usuarioRepository.findById(medicoDTO.getUsuarioId())
                    .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

            if (usuario.getRol() != Rol.MEDICO) {
                throw new RolNoValidoException("El usuario debe tener rol MEDICO para ser asignado como médico");
            }

            if (medicoRepository.existsByUsuario(usuario)
                    && !usuario.getId().equals(existente.getUsuario().getId())) {
                throw new MedicoExisteException("El usuario ya está asignado a otro médico");
            }

            existente.setUsuario(usuario);
        }

        Medico guardado = medicoRepository.save(existente);
        return buildResponse(guardado);
    }

    /**
     * Construye un DTO de respuesta completo a partir de la entidad {@link Medico}.
     *
     * @param m Entidad {@link Medico}.
     * @return {@link MedicoResponseDTO} con los datos completos del médico, usuario y especialidad.
     */
    private MedicoResponseDTO buildResponse(Medico m) {
        return new MedicoResponseDTO(
                m.getId(),
                m.getUsuario().getId(),
                m.getUsuario().getNombre(),
                m.getUsuario().getEmail(),
                m.getEspecialidad().getId(),
                m.getEspecialidad().getNombre(),
                m.getUrlFoto()
        );
    }

    /**
     * Construye un DTO de respuesta pública a partir de la entidad {@link Medico}.
     *
     * @param m Entidad {@link Medico}.
     * @return {@link MedicoPublicoDTO} con los datos públicos del médico.
     */
    private MedicoPublicoDTO buildResponsePublico(Medico m) {
        return new MedicoPublicoDTO(
                m.getId(),
                m.getUsuario().getNombre(),
                m.getEspecialidad().getNombre(),
                m.getUrlFoto()
        );
    }
}