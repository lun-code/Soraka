package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.cita.*;
import com.hospital.Soraka.entity.Cita;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.EstadoCita;
import com.hospital.Soraka.exception.Cita.CitaNoCancelableException;
import com.hospital.Soraka.exception.Cita.CitaNoDisponibleException;
import com.hospital.Soraka.exception.Cita.CitaNotFoundException;
import com.hospital.Soraka.exception.Cita.CitaOcupadaException;
import com.hospital.Soraka.exception.Medico.MedicoNotFoundException;
import com.hospital.Soraka.exception.Usuario.UsuarioNotFoundException;
import com.hospital.Soraka.repository.CitaRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio encargado de la gestión de citas médicas.
 * <p>
 * Contiene toda la lógica relacionada con la creación, modificación,
 * reserva, cancelación y cierre automático de citas, así como
 * las validaciones de permisos y reglas de negocio.
 */
@Service
@Transactional
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /* =========================
       CONSULTAS
       ========================= */

    /**
     * Obtiene todas las citas asociadas a un paciente concreto.
     *
     * @param pacienteId id del paciente
     * @return lista de citas del paciente
     */
    public List<CitaResponseDTO> getCitasPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene todas las citas del sistema.
     *
     * @return lista completa de citas
     */
    public List<CitaResponseDTO> getTodasLasCitas() {
        return citaRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene una cita por su id verificando permisos de acceso.
     * <p>
     * Un usuario con rol PACIENTE solo puede acceder a sus propias citas.
     *
     * @param id id de la cita
     * @return cita encontrada
     * @throws CitaNotFoundException si la cita no existe
     * @throws AccessDeniedException si el usuario no tiene permisos
     */
    public CitaResponseDTO getCitaById(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        return buildResponse(cita);
    }

    /**
     * Lista todas las citas disponibles con fecha futura.
     *
     * @return lista de citas disponibles
     */
    public List<CitaResponseDTO> listarDisponibles() {
        return citaRepository.findByEstadoAndFechaHoraAfter(
                EstadoCita.DISPONIBLE,
                LocalDateTime.now()
        ).stream().map(this::buildResponse).toList();
    }

    /* =========================
       CREACIÓN
       ========================= */

    /**
     * Crea una nueva cita médica.
     * <p>
     * Un usuario con rol PACIENTE solo puede crear citas para sí mismo.
     *
     * @param dto datos de creación de la cita
     * @return cita creada
     */
    public CitaResponseDTO createCita(CitaPostDTO dto) {

        Usuario paciente = usuarioRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new MedicoNotFoundException("Medico no encontrado"));

        if (dto.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new CitaNoDisponibleException("No se pueden crear citas en el pasado");
        }

        if (citaRepository.findByMedicoAndFechaHora(medico, dto.getFechaHora()).isPresent()) {
            throw new CitaOcupadaException("El médico ya tiene otra cita en esa fecha y hora");
        }

        Cita nueva = new Cita(paciente, medico, dto.getFechaHora(), dto.getMotivo());
        return buildResponse(citaRepository.save(nueva));
    }

    /* =========================
       MODIFICACIÓN
       ========================= */

    /**
     * Modifica parcialmente una cita existente.
     * <p>
     * No permite modificar el estado directamente.
     *
     * @param id id de la cita
     * @param dto datos a modificar
     * @return cita modificada
     */
    public CitaResponseDTO patchCita(Long id, CitaPatchDTO dto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        if (dto.getFechaHora() != null) {

            if (dto.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new CitaNoDisponibleException("No se puede mover la cita al pasado");
            }

            citaRepository.findByMedicoAndFechaHora(cita.getMedico(), dto.getFechaHora())
                    .filter(c -> !c.getId().equals(cita.getId()))
                    .ifPresent(c -> {
                        throw new CitaOcupadaException("El médico ya tiene otra cita en esa fecha y hora");
                    });

            cita.setFechaHora(dto.getFechaHora());
        }

        if (dto.getMotivo() != null) {
            cita.setMotivo(dto.getMotivo());
        }

        return buildResponse(citaRepository.save(cita));
    }

    /* =========================
       ELIMINACIÓN
       ========================= */

    /**
     * Elimina una cita si el usuario tiene permisos.
     *
     * @param id id de la cita
     */
    public void deleteCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        citaRepository.delete(cita);
    }

    /* =========================
       RESERVA / CANCELACIÓN
       ========================= */

    /**
     * Reserva una cita disponible para un paciente.
     *
     * @param citaId id de la cita
     * @param paciente paciente que reserva
     * @param dto datos adicionales de la reserva
     */
    public void reservarCita(Long citaId, Usuario paciente, ReservarCitaDTO dto) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.DISPONIBLE) {
            throw new CitaNoDisponibleException("La cita no está disponible");
        }

        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new CitaNoDisponibleException("No se puede reservar una cita pasada");
        }

        cita.setPaciente(paciente);
        cita.setMotivo(dto.getMotivo());
        cita.setEstado(EstadoCita.CONFIRMADA);

        citaRepository.save(cita);
    }

    /**
     * Cancela una cita previamente confirmada.
     *
     * @param citaId id de la cita
     * @param paciente paciente que cancela
     */
    public void cancelarCita(Long citaId, Usuario paciente) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new CitaNoCancelableException("Solo se pueden cancelar citas confirmadas");
        }

        if (cita.getPaciente() == null ||
                !cita.getPaciente().getId().equals(paciente.getId())) {
            throw new AccessDeniedException("No puedes cancelar esta cita");
        }

        cita.setPaciente(null);
        cita.setMotivo(null);
        cita.setEstado(EstadoCita.DISPONIBLE);

        citaRepository.save(cita);
    }

    /* =========================
       TAREA PROGRAMADA
       ========================= */

    /**
     * Cierra automáticamente las citas pasadas.
     * <ul>
     *   <li>CONFIRMADA → REALIZADA</li>
     *   <li>DISPONIBLE → CADUCADA</li>
     * </ul>
     * Se ejecuta cada 10 minutos.
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void cerrarCitasPasadas() {
        List<Cita> citasPasadas =
                citaRepository.findByFechaHoraBeforeAndEstadoIn(
                        LocalDateTime.now(),
                        List.of(EstadoCita.CONFIRMADA, EstadoCita.DISPONIBLE)
                );

        for (Cita c : citasPasadas) {
            if (c.getEstado() == EstadoCita.CONFIRMADA) {
                c.setEstado(EstadoCita.REALIZADA);
            } else {
                c.setEstado(EstadoCita.CADUCADA);
            }
        }

        citaRepository.saveAll(citasPasadas);
    }

    /**
     * Construye el DTO de respuesta a partir de la entidad Cita.
     *
     * @param c entidad cita
     * @return DTO de respuesta
     */
    private CitaResponseDTO buildResponse(Cita c) {
        return new CitaResponseDTO(
                c.getId(),
                c.getPaciente() != null ? c.getPaciente().getId() : null,
                c.getPaciente() != null ? c.getPaciente().getNombre() : null,
                c.getMedico().getId(),
                c.getMedico().getUsuario().getNombre(),
                c.getMedico().getEspecialidad().getNombre(),
                c.getFechaHora(),
                c.getEstado(),
                c.getMotivo()
        );
    }
}