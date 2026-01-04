package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.cita.*;
import com.hospital.Soraka.entity.Cita;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.EstadoCita;
import com.hospital.Soraka.enums.Rol;
import com.hospital.Soraka.repository.CitaRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de dominio encargado de la gestión de citas médicas.
 *
 * <p>
 * Contiene la lógica de negocio relacionada con:
 * <ul>
 *     <li>Creación, consulta, modificación y eliminación de citas</li>
 *     <li>Validación de disponibilidad de médicos</li>
 *     <li>Control de acceso basado en propiedad del recurso</li>
 * </ul>
 *
 * <p>
 * La seguridad basada en roles debe aplicarse en el controlador mediante {@code @PreAuthorize}.
 * El service valida únicamente la propiedad y reglas de negocio.
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
     * @return {@code true} si la cita pertenece al paciente
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
    public List<CitaResponseDTO> getCitasPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene todas las citas del sistema.
     *
     * @return lista de {@link CitaResponseDTO} de todas las citas
     */
    public List<CitaResponseDTO> getTodasLasCitas() {
        return citaRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene una cita por su ID validando que el usuario tenga permiso.
     *
     * <p>
     * Los pacientes solo pueden acceder a sus propias citas.
     * Médicos y administradores pueden acceder a cualquier cita.
     *
     * @param id identificador de la cita
     * @param usuario usuario que realiza la consulta
     * @return {@link CitaResponseDTO} de la cita
     * @throws EntityNotFoundException si la cita no existe
     * @throws AccessDeniedException si el usuario no tiene permiso
     */
    public CitaResponseDTO getCitaById(Long id, Usuario usuario) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (usuario.getRol() == Rol.PACIENTE &&
                !cita.getPaciente().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No puedes acceder a esta cita");
        }

        return buildResponse(cita);
    }

    /**
     * Lista todas las citas DISPONIBLES a partir de la fecha y hora actual.
     *
     * @return lista de {@link CitaResponseDTO} disponibles
     */
    public List<CitaResponseDTO> listarDisponibles() {
        return citaRepository.findByEstadoAndFechaHoraAfter(
                EstadoCita.DISPONIBLE,
                LocalDateTime.now()
        ).stream().map(this::buildResponse).toList();
    }

    /**
     * Crea una nueva cita médica.
     *
     * <p>
     * Valida existencia de paciente y médico, y que el médico no tenga otra cita en ese horario.
     *
     * @param cita DTO con los datos de la cita
     * @return {@link CitaResponseDTO} de la cita creada
     * @throws EntityNotFoundException si paciente o médico no existen
     * @throws IllegalArgumentException si el médico ya tiene otra cita en ese horario
     */
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
     * Elimina una cita existente validando propiedad del usuario.
     *
     * @param id identificador de la cita
     * @param usuario usuario que solicita la eliminación
     * @throws EntityNotFoundException si la cita no existe
     * @throws AccessDeniedException si el usuario no tiene permiso
     */
    public void deleteCita(Long id, Usuario usuario) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (usuario.getRol() == Rol.PACIENTE &&
                !cita.getPaciente().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No puedes eliminar esta cita");
        }

        citaRepository.delete(cita);
    }

    /**
     * Modifica parcialmente una cita existente validando propiedad del usuario.
     *
     * @param id identificador de la cita
     * @param citaPatch DTO con los campos a actualizar
     * @param usuario usuario que solicita la modificación
     * @return {@link CitaResponseDTO} actualizado
     * @throws EntityNotFoundException si la cita no existe
     * @throws AccessDeniedException si el usuario no tiene permiso
     * @throws IllegalArgumentException si el médico ya tiene otra cita en el horario indicado
     */
    public CitaResponseDTO patchCita(Long id, CitaPatchDTO citaPatch, Usuario usuario) {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (usuario.getRol() == Rol.PACIENTE &&
                !existente.getPaciente().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No puedes modificar esta cita");
        }

        if (citaPatch.getFechaHora() != null) {
            citaRepository.findByMedicoAndFechaHora(existente.getMedico(), citaPatch.getFechaHora())
                    .filter(c -> !c.getId().equals(existente.getId()))
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("El médico ya tiene otra cita en esa fecha y hora.");
                    });
            existente.setFechaHora(citaPatch.getFechaHora());
        }

        if (citaPatch.getEstado() != null) existente.setEstado(citaPatch.getEstado());
        if (citaPatch.getMotivo() != null) existente.setMotivo(citaPatch.getMotivo());

        return buildResponse(citaRepository.save(existente));
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
     * Cancela una cita confirmada de un paciente.
     *
     * @param citaId identificador de la cita a cancelar
     * @param paciente paciente que solicita la cancelación
     * @throws EntityNotFoundException si la cita no existe
     * @throws IllegalStateException si la cita no está confirmada
     * @throws AccessDeniedException si el paciente no es propietario
     */
    public void cancelarCita(Long citaId, Usuario paciente) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new IllegalStateException("Solo se pueden cancelar citas confirmadas");
        }

        if (!cita.getPaciente().getId().equals(paciente.getId())) {
            throw new AccessDeniedException("No puedes cancelar esta cita");
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