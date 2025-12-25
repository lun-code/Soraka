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
import java.util.Optional;

@Service
@Transactional
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<MedicoResponseDTO> listarMedicos() {
        return medicoRepository.findAll()
                .stream()
                .map(m -> new MedicoResponseDTO(
                        m.getId(),
                        m.getUsuario().getId(),
                        m.getUsuario().getNombre(),
                        m.getUsuario().getEmail(),
                        m.getEspecialidad().getId(),
                        m.getEspecialidad().getNombre()
                )).toList();
    }

    public MedicoResponseDTO buscarMedicoPorId(Long id){
        Medico m = medicoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        return new MedicoResponseDTO(
                m.getId(),
                m.getUsuario().getId(),
                m.getUsuario().getNombre(),
                m.getUsuario().getEmail(),
                m.getEspecialidad().getId(),
                m.getEspecialidad().getNombre()
        );
    }

    public MedicoResponseDTO registrarMedico(MedicoPostDTO medicoDTO) {

        Usuario usuario = usuarioRepository.findById(medicoDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Especialidad especialidad = especialidadRepository.findById(medicoDTO.getEspecialidadId())
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada"));

        if (medicoRepository.existsByUsuario(usuario)) {
            throw new IllegalArgumentException("El usuario ya está asignado a un médico");
        }

        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setEspecialidad(especialidad);

        Medico guardado = medicoRepository.save(medico);

        return new MedicoResponseDTO(
                guardado.getId(),
                guardado.getUsuario().getId(),
                guardado.getUsuario().getNombre(),
                guardado.getUsuario().getEmail(),
                guardado.getEspecialidad().getId(),
                guardado.getEspecialidad().getNombre()
        );
    }


    public void borrarMedico(Long id){
        if(!medicoRepository.existsById(id)){
            throw new EntityNotFoundException("El medico no existe");
        }
        medicoRepository.deleteById(id);
    }

    // PATCH
    public MedicoResponseDTO actualizarMedico(Long id, MedicoPatchDTO medicoDTO){

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

        return new MedicoResponseDTO(
                guardado.getId(),
                guardado.getUsuario().getId(),
                guardado.getUsuario().getNombre(),
                guardado.getUsuario().getEmail(),
                guardado.getEspecialidad().getId(),
                guardado.getEspecialidad().getNombre()
        );
    }
}