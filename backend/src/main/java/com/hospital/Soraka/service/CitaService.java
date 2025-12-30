package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.cita.CitaPatchDTO;
import com.hospital.Soraka.dto.cita.CitaPostDTO;
import com.hospital.Soraka.dto.cita.CitaResponseDTO;
import com.hospital.Soraka.entity.Cita;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.repository.CitaRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public List<CitaResponseDTO> getCitas() {
        return citaRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    public CitaResponseDTO getCitaById(Long id) {
        Cita existente = citaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cita no encontrada."));

        return buildResponse(existente);
    }

    public CitaResponseDTO createCita(CitaPostDTO cita) {

        Usuario paciente = usuarioRepository.findById(cita.getPacienteId()).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        Medico medico = medicoRepository.findById(cita.getMedicoId()).orElseThrow(() -> new EntityNotFoundException("Medico no encontrado."));

        if(citaRepository.findByMedicoAndFechaHora(medico, cita.getFechaHora()).isPresent()) {
            throw new IllegalArgumentException("El médico ya tiene otra cita en esa fecha y hora.");
        }

        Cita nuevaCita = new Cita(
                paciente,
                medico,
                cita.getFechaHora(),
                cita.getMotivo()
        );

        Cita guardada = citaRepository.save(nuevaCita);

        return buildResponse(guardada);
    }

    public void deleteCita(Long id) {
        if(!citaRepository.existsById(id)){
            throw new EntityNotFoundException("Cita no encontrada.");
        }
        citaRepository.deleteById(id);
    }

    public CitaResponseDTO patchCita(Long id, CitaPatchDTO cita) {

        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada."));

        if(cita.getFechaHora() != null) {
            citaRepository.findByMedicoAndFechaHora(existente.getMedico(), cita.getFechaHora())
                    .filter(c -> !c.getId().equals(existente.getId()))
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("El médico ya tiene otra cita en esa fecha y hora.");
                    });
            existente.setFechaHora(cita.getFechaHora());
        }

        if(cita.getEstado() != null){
            existente.setEstado(cita.getEstado());
        }

        if(cita.getMotivo() != null) {
            existente.setMotivo(cita.getMotivo());
        }

        Cita actualizada = citaRepository.save(existente);

        return buildResponse(actualizada);
    }

    private CitaResponseDTO buildResponse(Cita c) {
        return new CitaResponseDTO(
                c.getId(),
                c.getPaciente().getId(),
                c.getPaciente().getNombre(),
                c.getMedico().getId(),
                c.getMedico().getUsuario().getNombre(),
                c.getMedico().getEspecialidad().getNombre(),
                c.getFechaHora(),
                c.getEstado(),
                c.getMotivo()
        );
    }
}