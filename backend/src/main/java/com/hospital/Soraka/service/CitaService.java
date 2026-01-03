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
     * Este método se utiliza principalmente en expresiones {@link PreAuthorize}
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

        return cita.getPaciente() != null
                && cita.getPaciente().getId().equals(pacienteId);
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
     * Lista todas las citas disponibles (estado DISPONIBLE) a partir de la fecha y hora actual.
     * <p>
     * Se puede acceder a este método por pacientes, médicos o administradores.
     *
     * @return lista de {@link CitaResponseDTO} correspondientes a citas disponibles
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
     * Genera automáticamente citas disponibles para un médico dentro de un rango de fechas.
     * <p>
     * Cada cita se crea con:
     * <ul>
     *     <li>Paciente = null</li>
     *     <li>Estado = DISPONIBLE</li>
     * </ul>
     * Se evita crear citas duplicadas si ya existe una cita del mismo médico en la misma fecha y hora.
     *
     * <p>
     * Este método se puede ejecutar de forma programada usando {@link Scheduled} para automatizar
     * la generación de citas en la aplicación.
     */
    @Scheduled(cron = "0 0 0 * * *") // Todos los días a medianoche
    public void generarCitasDisponibles() {

        List<Medico> medicos = medicoRepository.findAll();
        LocalDate hoy = LocalDate.now();

        for (Medico medico : medicos) {

            LocalDate fecha = hoy; // hoy incluido
            LocalDate fechaFin = hoy.plusDays(7); // 1 semana vista

            while (!fecha.isAfter(fechaFin)) {

                LocalDateTime hora = fecha.atTime(8, 0);
                LocalDateTime fin = fecha.atTime(15, 0);

                while (hora.isBefore(fin)) {

                    if (!citaRepository.existsByMedicoAndFechaHora(medico, hora)) {
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
     * Reserva una cita disponible para un paciente.
     * <p>
     * Cambia el estado de la cita a CONFIRMADA y asigna el paciente y el motivo de la reserva.
     * <p>
     * Valida que la cita exista y que esté en estado DISPONIBLE antes de reservarla.
     *
     * @param citaId identificador de la cita a reservar
     * @param paciente paciente que reserva la cita
     * @param dto contiene el motivo de la cita
     * @throws RuntimeException si la cita no existe
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
     * Cancela una cita previamente confirmada para un paciente.
     * <p>
     * Solo puede cancelar su propia cita y la cita debe estar en estado CONFIRMADA.
     * Tras cancelar, la cita vuelve a estar DISPONIBLE y sin paciente ni motivo asignado.
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
     * Marca automáticamente como REALIZADAS todas las citas confirmadas
     * cuya fecha y hora ya ha pasado.
     * <p>
     * Este método se puede ejecutar de manera programada para mantener actualizado
     * el estado de las citas en el sistema.
     */
    @Scheduled(cron = "0 */10 * * * *") // cada 10 minutos
    public void cerrarCitasPasadas() {

        List<Cita> citasPasadas = citaRepository.findByFechaHoraBefore(LocalDateTime.now());

        for (Cita c : citasPasadas) {
            if (c.getEstado() == EstadoCita.CONFIRMADA) {
                c.setEstado(EstadoCita.REALIZADA);
            } else if (c.getEstado() == EstadoCita.DISPONIBLE) {
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