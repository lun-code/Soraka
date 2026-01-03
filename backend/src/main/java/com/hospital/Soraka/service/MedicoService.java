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
     * Listado de todos los médicos. Visible para cualquier usuario autenticado.
     */
    public List<MedicoResponseDTO> getMedicos() {
        return medicoRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Consulta un médico por su ID.
     */
    public MedicoResponseDTO getMedicoById(Long id){
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        return buildResponse(medico);
    }

    /**
     * Crear un nuevo médico. Solo admins pueden.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
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
     * Elimina un médico por ID. Solo admins pueden.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteMedico(Long id){
        if(!medicoRepository.existsById(id)){
            throw new EntityNotFoundException("El medico no existe");
        }
        medicoRepository.deleteById(id);
    }

    /**
     * Modifica un médico existente. Solo admins pueden.
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

            medicoRepository.findByUsuario(usuario)
                    .filter(m -> !m.getId().equals(id))
                    .ifPresent(m -> {
                        throw new IllegalArgumentException("El usuario ya está asignado a otro médico");
                    });

            existente.setUsuario(usuario);
        }

        Medico guardado = medicoRepository.save(existente);
        return buildResponse(guardado);
    }

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