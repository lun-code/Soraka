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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Obtiene el listado completo de medicos.
     *
     * @return lista de medicos transformados a {@link MedicoResponseDTO}
     */
    public List<MedicoResponseDTO> getMedicos() {
        return medicoRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene un medico a partir de su identificador.
     *
     * @param id identificador del medico
     * @return datos del medico en forma de {@link MedicoResponseDTO}
     * @throws EntityNotFoundException si no existe un medico con el ID indicado
     */
    public MedicoResponseDTO getMedicoById(Long id){
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        return buildResponse(medico);
    }

    /**
     * Crea un nuevo medico en el sistema.
     *
     * @param medico datos necesarios para la creacion del medico
     * @return medico creado en forma de {@link MedicoResponseDTO}
     * @throws EntityNotFoundException si el usuario o la especialidad no existen
     * @throws IllegalArgumentException si el usuario no tiene rol MEDICO o ya esta asignado a otro medico
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public MedicoResponseDTO createMedico(MedicoPostDTO medico) {
        Usuario usuario = usuarioRepository.findById(medico.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Especialidad especialidad = especialidadRepository.findById(medico.getEspecialidadId())
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada"));

        if (usuario.getRol() != Rol.MEDICO) {
            throw new IllegalArgumentException("El usuario debe tener rol MEDICO para ser asignado como medico");
        }

        if (medicoRepository.existsByUsuario(usuario)) {
            throw new IllegalArgumentException("El usuario ya esta asignado a un medico");
        }

        Medico nuevoMedico = new Medico(usuario, especialidad);
        Medico guardado = medicoRepository.save(nuevoMedico);
        return buildResponse(guardado);
    }

    /**
     * Elimina un medico del sistema a partir de su ID.
     *
     * @param id identificador del medico a eliminar
     * @throws EntityNotFoundException si el medico no existe
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteMedico(Long id){
        if(!medicoRepository.existsById(id)){
            throw new EntityNotFoundException("El medico no existe");
        }
        medicoRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente un medico existente.
     *
     * @param id identificador del medico a modificar
     * @param medicoDTO datos parciales a actualizar
     * @return medico actualizado en forma de {@link MedicoResponseDTO}
     * @throws EntityNotFoundException si el medico, usuario o especialidad no existen
     * @throws IllegalArgumentException si el usuario no tiene rol MEDICO o ya esta asignado a otro medico
     */
    @PreAuthorize("hasAuthority('ADMIN')")
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
                throw new IllegalArgumentException("El usuario debe tener rol MEDICO para ser asignado como medico");
            }

            if (medicoRepository.existsByUsuario(usuario)
                    && !usuario.getId().equals(existente.getUsuario().getId())) {
                throw new IllegalArgumentException("El usuario ya esta asignado a otro medico");
            }

            existente.setUsuario(usuario);
        }

        Medico guardado = medicoRepository.save(existente);
        return buildResponse(guardado);
    }

    /**
     * Construye el DTO de respuesta a partir de una entidad {@link Medico}.
     *
     * @param m entidad medico
     * @return DTO con los datos del medico, usuario y especialidad
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