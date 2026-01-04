package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.medico.MedicoPatchDTO;
import com.hospital.Soraka.dto.medico.MedicoPostDTO;
import com.hospital.Soraka.dto.medico.MedicoResponseDTO;
import com.hospital.Soraka.entity.Especialidad;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.Rol;
import com.hospital.Soraka.repository.EspecialidadRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de dominio para la gestión de médicos.
 *
 * <p>
 * Contiene operaciones de listado, consulta, creación, actualización parcial y eliminación.
 * La seguridad basada en roles debe aplicarse en el controller mediante {@code @PreAuthorize}.
 * Este service valida únicamente la existencia de entidades y reglas de negocio.
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
     * @return lista de {@link MedicoResponseDTO} con la información de cada médico.
     */
    public List<MedicoResponseDTO> getMedicos() {
        return medicoRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene un médico a partir de su ID.
     *
     * @param id identificador del médico
     * @return {@link MedicoResponseDTO} con los datos del médico
     * @throws EntityNotFoundException si no existe un médico con el ID indicado
     */
    public MedicoResponseDTO getMedicoById(Long id){
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        return buildResponse(medico);
    }

    /**
     * Crea un nuevo médico en el sistema.
     *
     * <p>
     * Solo un usuario con rol ADMIN debería llamar a este método desde el controller.
     *
     * @param medico DTO con los datos necesarios para la creación del médico.
     * @return {@link MedicoResponseDTO} con el médico creado.
     * @throws EntityNotFoundException si el usuario o la especialidad no existen
     * @throws IllegalArgumentException si el usuario no tiene rol MEDICO o ya está asignado a otro médico
     */
    public MedicoResponseDTO createMedico(MedicoPostDTO medico) {
        Usuario usuario = usuarioRepository.findById(medico.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Especialidad especialidad = especialidadRepository.findById(medico.getEspecialidadId())
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada"));

        if (usuario.getRol() != Rol.MEDICO) {
            throw new IllegalArgumentException("El usuario debe tener rol MEDICO para ser asignado como médico");
        }

        if (medicoRepository.existsByUsuario(usuario)) {
            throw new IllegalArgumentException("El usuario ya está asignado a un médico");
        }

        Medico nuevoMedico = new Medico(usuario, especialidad);
        Medico guardado = medicoRepository.save(nuevoMedico);
        return buildResponse(guardado);
    }

    /**
     * Elimina un médico del sistema a partir de su ID.
     *
     * <p>
     * Solo un usuario con rol ADMIN debería llamar a este método desde el controller.
     *
     * @param id identificador del médico a eliminar
     * @throws EntityNotFoundException si el médico no existe
     */
    public void deleteMedico(Long id){
        if(!medicoRepository.existsById(id)){
            throw new EntityNotFoundException("El médico no existe");
        }
        medicoRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente un médico existente.
     *
     * <p>
     * Solo un usuario con rol ADMIN debería llamar a este método desde el controller.
     *
     * @param id identificador del médico a modificar
     * @param medicoDTO DTO con los campos parciales a actualizar
     * @return {@link MedicoResponseDTO} con el médico actualizado
     * @throws EntityNotFoundException si el médico, usuario o especialidad no existen
     * @throws IllegalArgumentException si el usuario no tiene rol MEDICO o ya está asignado a otro médico
     */
    public MedicoResponseDTO patchMedico(Long id, MedicoPatchDTO medicoDTO){
        Medico existente = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        if(medicoDTO.getEspecialidadId() != null){
            Especialidad especialidad = especialidadRepository.findById(medicoDTO.getEspecialidadId())
                    .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada"));
            existente.setEspecialidad(especialidad);
        }

        if(medicoDTO.getUsuarioId() != null){
            Usuario usuario = usuarioRepository.findById(medicoDTO.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            if (usuario.getRol() != Rol.MEDICO) {
                throw new IllegalArgumentException("El usuario debe tener rol MEDICO para ser asignado como médico");
            }

            if (medicoRepository.existsByUsuario(usuario)
                    && !usuario.getId().equals(existente.getUsuario().getId())) {
                throw new IllegalArgumentException("El usuario ya está asignado a otro médico");
            }

            existente.setUsuario(usuario);
        }

        Medico guardado = medicoRepository.save(existente);
        return buildResponse(guardado);
    }

    /**
     * Construye un DTO de respuesta a partir de la entidad {@link Medico}.
     *
     * @param m entidad médico
     * @return {@link MedicoResponseDTO} con los datos del médico, usuario y especialidad
     */
    private MedicoResponseDTO buildResponse(Medico m) {
        return new MedicoResponseDTO(
                m.getId(),
                m.getUsuario().getId(),
                m.getUsuario().getNombre(),
                m.getUsuario().getEmail(),
                m.getEspecialidad().getId(),
                m.getEspecialidad().getNombre()
        );
    }
}