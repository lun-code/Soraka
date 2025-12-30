package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.medico.MedicoPatchDTO;
import com.hospital.Soraka.dto.medico.MedicoPostDTO;
import com.hospital.Soraka.dto.medico.MedicoResponseDTO;
import com.hospital.Soraka.entity.Especialidad;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.repository.EspecialidadRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<MedicoResponseDTO> getMedicos() {
        return medicoRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    public MedicoResponseDTO getMedicoById(Long id){
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        return buildResponse(medico);
    }

    public MedicoResponseDTO createMedico(MedicoPostDTO medico) {

        Usuario usuario = usuarioRepository.findById(medico.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Especialidad especialidad = especialidadRepository.findById(medico.getEspecialidadId())
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada"));

        if (medicoRepository.existsByUsuario(usuario)) {
            throw new IllegalArgumentException("El usuario ya está asignado a un médico");
        }

        Medico nuevoMedico = new Medico(
                usuario,
                especialidad
        );

        Medico guardado = medicoRepository.save(nuevoMedico);

        return buildResponse(guardado);
    }


    public void deleteMedico(Long id){
        if(!medicoRepository.existsById(id)){
            throw new EntityNotFoundException("El medico no existe");
        }
        medicoRepository.deleteById(id);
    }

    // PATCH
    public MedicoResponseDTO patchMedico(Long id, MedicoPatchDTO medicoDTO){

        Medico existente = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        // Cambiar especialidad si viene
        if(medicoDTO.getEspecialidadId() != null){
            Especialidad especialidad = especialidadRepository.findById(medicoDTO.getEspecialidadId())
                    .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada"));

            existente.setEspecialidad(especialidad);
        }

        // Cambiar usuario si viene
        if(medicoDTO.getUsuarioId() != null){
            Usuario usuario = usuarioRepository.findById(medicoDTO.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            // Verificar que ese usuario no esté ya asignado a otro médico
            medicoRepository.findByUsuario(usuario) // Devuelve el médico que tenga el usuario asociado
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