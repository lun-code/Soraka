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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de dominio encargado de la gestión de citas médicas.
 * <p>
 * Contiene la lógica de negocio relacionada con:
 * <ul>
 *     <li>Creación, consulta, modificación y eliminación de citas</li>
 *     <li>Validación de disponibilidad de médicos</li>
 *     <li>Control de acceso basado en roles y propiedad del recurso</li>
 * </ul>
 *
 * <p>
 * La seguridad se aplica a nivel de métodoo mediante {@link PreAuthorize},
 * permitiendo:
 * <ul>
 *     <li>Médicos y administradores: acceso amplio</li>
 *     <li>Pacientes: acceso únicamente a sus propias citas</li>
 * </ul>
 *
 * <p>
 * Todas las operaciones se ejecutan dentro de una transacción.
 */
@Service
@Transactional
public class CitaService {

    /**
     * Repositorio de acceso a datos de citas.
     */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * Repositorio de usuarios (pacientes).
     */
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Repositorio de médicos.
     */
    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Comprueba si una cita pertenece a un paciente concreto.
     * <p>
     * Este métodoo se utiliza principalmente en expresiones {@link PreAuthorize}
     * para validar que el usuario autenticado es el propietario de la cita.
     *
     * @param citaId identificador de la cita
     * @param pacienteId identificador del paciente
     * @return {@code true} si la cita pertenece al paciente, {@code false} en caso contrario
     * @throws EntityNotFoundException si la cita no existe
     */
    public boolean perteneceAlPaciente(Long citaId, Long pacienteId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
        return cita.getPaciente().getId().equals(pacienteId);
    }

    /**
     * Obtiene el listado de citas de un paciente.
     * <p>
     * Reglas de acceso:
     * <ul>
     *     <li>MÉDICO o ADMIN: pueden consultar citas de cualquier paciente</li>
     *     <li>PACIENTE: solo puede consultar sus propias citas</li>
     * </ul>
     *
     * @param pacienteId identificador del paciente
     * @return lista de citas del paciente
     */
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN') or #pacienteId == principal.id")
    public List<CitaResponseDTO> getCitas(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene una cita concreta por su identificador.
     * <p>
     * Reglas de acceso:
     * <ul>
     *     <li>MÉDICO: puede acceder a cualquier cita</li>
     *     <li>PACIENTE: solo puede acceder a sus propias citas</li>
     * </ul>
     *
     * @param id identificador de la cita
     * @return datos de la cita
     * @throws EntityNotFoundException si la cita no existe
     */
    @PreAuthorize("hasAuthority('MEDICO') or @citaService.perteneceAlPaciente(#id, principal.id)")
    public CitaResponseDTO getCitaById(Long id) {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
        return buildResponse(existente);
    }

    /**
     * Crea una nueva cita médica.
     * <p>
     * Reglas de acceso:
     * <ul>
     *     <li>MÉDICO o ADMIN: pueden crear citas para cualquier paciente</li>
     *     <li>PACIENTE: solo puede crear citas para sí mismo</li>
     * </ul>
     *
     * <p>
     * Se valida que el médico no tenga otra cita en la misma fecha y hora.
     *
     * @param cita DTO con los datos necesarios para crear la cita
     * @return cita creada
     * @throws EntityNotFoundException si el paciente o el médico no existen
     * @throws IllegalArgumentException si el médico ya tiene una cita en ese horario
     */
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN') or #cita.pacienteId == principal.id")
    public CitaResponseDTO createCita(CitaPostDTO cita) {
        Usuario paciente = usuarioRepository.findById(cita.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Medico medico = medicoRepository.findById(cita.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado"));

        if (citaRepository.findByMedicoAndFechaHora(medico, cita.getFechaHora()).isPresent()) {
            throw new IllegalArgumentException("El médico ya tiene otra cita en esa fecha y hora.");
        }

        Cita nuevaCita = new Cita(paciente, medico, cita.getFechaHora(), cita.getMotivo());
        Cita guardada = citaRepository.save(nuevaCita);
        return buildResponse(guardada);
    }

    /**
     * Elimina una cita existente.
     * <p>
     * Reglas de acceso:
     * <ul>
     *     <li>MÉDICO o ADMIN: pueden eliminar cualquier cita</li>
     *     <li>PACIENTE: solo puede eliminar sus propias citas</li>
     * </ul>
     *
     * @param id identificador de la cita
     * @throws EntityNotFoundException si la cita no existe
     */
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN') or @citaService.perteneceAlPaciente(#id, principal.id)")
    public void deleteCita(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new EntityNotFoundException("Cita no encontrada");
        }
        citaRepository.deleteById(id);
    }

    /**
     * Modifica parcialmente una cita existente.
     * <p>
     * Reglas de acceso:
     * <ul>
     *     <li>MÉDICO: puede modificar cualquier cita</li>
     *     <li>PACIENTE: solo puede modificar sus propias citas</li>
     * </ul>
     *
     * <p>
     * Se valida la disponibilidad del médico si se cambia la fecha y hora.
     *
     * @param id   identificador de la cita
     * @param cita DTO con los campos a modificar
     * @return cita actualizada
     * @throws EntityNotFoundException si la cita no existe
     * @throws IllegalArgumentException si el médico ya tiene otra cita en ese horario
     */
    @PreAuthorize("hasAuthority('MEDICO') or @citaService.perteneceAlPaciente(#id, principal.id)")
    public CitaResponseDTO patchCita(Long id, CitaPatchDTO cita) {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getFechaHora() != null) {
            citaRepository.findByMedicoAndFechaHora(existente.getMedico(), cita.getFechaHora())
                    .filter(c -> !c.getId().equals(existente.getId()))
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("El médico ya tiene otra cita en esa fecha y hora.");
                    });
            existente.setFechaHora(cita.getFechaHora());
        }

        if (cita.getEstado() != null) {
            existente.setEstado(cita.getEstado());
        }

        if (cita.getMotivo() != null) {
            existente.setMotivo(cita.getMotivo());
        }

        Cita actualizada = citaRepository.save(existente);
        return buildResponse(actualizada);
    }

    /**
     * Construye un {@link CitaResponseDTO} a partir de una entidad {@link Cita}.
     *
     * @param c entidad cita
     * @return DTO de respuesta de cita
     */
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