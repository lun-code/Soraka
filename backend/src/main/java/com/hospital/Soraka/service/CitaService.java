package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.cita.*;
import com.hospital.Soraka.entity.Cita;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.EstadoCita;
import com.hospital.Soraka.repository.CitaRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * La seguridad se aplica a nivel de método mediante {@link PreAuthorize},
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

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Comprueba si una cita pertenece a un paciente concreto.
     *
     * @param citaId identificador de la cita
     * @param pacienteId identificador del paciente
     * @return {@code true} si la cita pertenece al paciente, {@code false} en caso contrario
     * @throws EntityNotFoundException si la cita no existe
     */
    public boolean perteneceAlPaciente(Long citaId, Long pacienteId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
        return cita.getPaciente() != null && cita.getPaciente().getId().equals(pacienteId);
    }

    /**
     * Obtiene las citas de un paciente específico.
     *
     * @param pacienteId identificador del paciente
     * @return lista de {@link CitaResponseDTO} del paciente
     */
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN') or #pacienteId == principal.id")
    public List<CitaResponseDTO> getCitasPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene todas las citas del sistema.
     * <p>
     * Solo accesible por médicos o administradores.
     *
     * @return lista de {@link CitaResponseDTO} de todas las citas
     */
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public List<CitaResponseDTO> getTodasLasCitas() {
        return citaRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene una cita por su ID.
     *
     * @param id identificador de la cita
     * @return {@link CitaResponseDTO} de la cita
     * @throws EntityNotFoundException si la cita no existe
     */
    @PreAuthorize("hasAuthority('MEDICO') or @citaService.perteneceAlPaciente(#id, principal.id)")
    public CitaResponseDTO getCitaById(Long id) {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
        return buildResponse(existente);
    }

    /**
     * Lista todas las citas disponibles a partir de la fecha actual.
     * <p>
     * Se puede acceder por pacientes, médicos y administradores.
     *
     * @return lista de {@link CitaResponseDTO} disponibles
     */
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public List<CitaResponseDTO> listarDisponibles() {
        return citaRepository.findByEstadoAndFechaHoraAfter(
                EstadoCita.DISPONIBLE,
                LocalDateTime.now()
        ).stream().map(this::buildResponse).toList();
    }

    /**
     * Crea una nueva cita médica.
     *
     * @param cita DTO con los datos de la cita
     * @return {@link CitaResponseDTO} de la cita creada
     * @throws EntityNotFoundException si paciente o médico no existen
     * @throws IllegalArgumentException si el médico ya tiene otra cita en ese horario
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
        return buildResponse(citaRepository.save(nuevaCita));
    }

    /**
     * Elimina una cita existente.
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
     *
     * @param id identificador de la cita
     * @param cita DTO con campos a actualizar
     * @return {@link CitaResponseDTO} con información actualizada
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

        if (cita.getEstado() != null) existente.setEstado(cita.getEstado());
        if (cita.getMotivo() != null) existente.setMotivo(cita.getMotivo());

        return buildResponse(citaRepository.save(existente));
    }

    /**
     * Genera automáticamente citas DISPONIBLES para los médicos.
     * Cada cita se crea con paciente = null y estado = DISPONIBLE.
     */
    @Scheduled(cron = "0 0 0 * * *") // Todos los días a medianoche
    public void generarCitasDisponibles() {
        List<Medico> medicos = medicoRepository.findAll();
        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        for (Medico medico : medicos) {
            LocalDate fecha = hoy.plusDays(1);
            LocalDate fechaFin = hoy.plusDays(7);

            while (!fecha.isAfter(fechaFin)) {
                LocalDateTime hora = fecha.atTime(8, 0);
                LocalDateTime fin = fecha.atTime(15, 0);

                while (hora.isBefore(fin)) {
                    if (hora.isAfter(ahora) && !citaRepository.existsByMedicoAndFechaHora(medico, hora)) {

                        Cita cita = new Cita();
                        cita.setMedico(medico);
                        cita.setFechaHora(hora);
                        cita.setEstado(EstadoCita.DISPONIBLE);
                        citaRepository.save(cita);
                    }
                    hora = hora.plusMinutes(30);
                }
                fecha = fecha.plusDays(1);
            }
        }
    }

    /**
     * Reserva una cita DISPONIBLE para un paciente.
     *
     * @param citaId identificador de la cita a reservar
     * @param paciente paciente que realiza la reserva
     * @param dto contiene el motivo de la cita
     * @throws EntityNotFoundException si la cita no existe
     * @throws IllegalStateException si la cita no está disponible
     */
    @PreAuthorize("hasAuthority('PACIENTE')")
    public void reservarCita(Long citaId, Usuario paciente, ReservarCitaDTO dto) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.DISPONIBLE) {
            throw new IllegalStateException("La cita no está disponible");
        }

        cita.setPaciente(paciente);
        cita.setMotivo(dto.getMotivo());
        cita.setEstado(EstadoCita.CONFIRMADA);

        citaRepository.save(cita);
    }

    /**
     * Cancela una cita confirmada para un paciente.
     *
     * @param citaId identificador de la cita a cancelar
     * @param paciente paciente que solicita la cancelación
     * @throws EntityNotFoundException si la cita no existe
     * @throws IllegalStateException si la cita no está confirmada
     * @throws SecurityException si el paciente no es propietario de la cita
     */
    @PreAuthorize("hasAuthority('PACIENTE')")
    public void cancelarCita(Long citaId, Usuario paciente) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new IllegalStateException("Solo se pueden cancelar citas confirmadas");
        }

        if (!cita.getPaciente().getId().equals(paciente.getId())) {
            throw new SecurityException("No puedes cancelar esta cita");
        }

        cita.setPaciente(null);
        cita.setMotivo(null);
        cita.setEstado(EstadoCita.DISPONIBLE);

        citaRepository.save(cita);
    }

    /**
     * Marca como REALIZADAS las citas confirmadas pasadas
     * y como CADUCADAS las DISPONIBLES pasadas.
     */
    @Scheduled(cron = "0 */10 * * * *") // cada 10 minutos
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
     * Construye un {@link CitaResponseDTO} a partir de una entidad {@link Cita}.
     *
     * @param c entidad cita
     * @return DTO de respuesta de cita
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