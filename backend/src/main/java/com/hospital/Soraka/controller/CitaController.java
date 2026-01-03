package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.cita.*;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de citas médicas.
 * <p>
 * Este controlador expone los endpoints HTTP relacionados con las citas y
 * delega toda la lógica de negocio en {@link CitaService}.
 * <p>
 * La autorización de acceso se gestiona principalmente en la capa de servicio
 * mediante anotaciones {@link PreAuthorize}.
 */
@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    /**
     * Obtiene el listado de citas asociadas al usuario autenticado.
     * <p>
     * El comportamiento depende del rol del usuario:
     * <ul>
     *     <li>PACIENTE: obtiene únicamente sus propias citas.</li>
     *     <li>MEDICO o ADMIN: la autorización y el alcance se validan en el service.</li>
     * </ul>
     *
     * @param authentication contexto de seguridad que contiene el usuario autenticado.
     * @return lista de {@link CitaResponseDTO} correspondientes al usuario.
     */
    @GetMapping
    public List<CitaResponseDTO> getCitasPorPaciente(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return citaService.getCitasPorPaciente(usuario.getId());
    }

    /**
     * Obtiene el detalle de una cita concreta por su identificador.
     *
     * @param id identificador de la cita.
     * @return {@link CitaResponseDTO} con la información de la cita.
     */
    @GetMapping("/{id}")
    public CitaResponseDTO getCitaById(@PathVariable Long id) {
        return citaService.getCitaById(id);
    }

    /**
     * Lista todas las citas disponibles a partir de la fecha actual.
     *
     * @return lista de {@link CitaResponseDTO} disponibles.
     */
    @GetMapping("/disponibles")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public List<CitaResponseDTO> listarCitasDisponibles() {
        return citaService.listarDisponibles();
    }

    /**
     * Obtiene todas las citas del sistema.
     * <p>
     * Solo accesible por médicos o administradores.
     *
     * @return lista de {@link CitaResponseDTO} de todas las citas.
     */
    @GetMapping("/todas")
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public List<CitaResponseDTO> getTodasLasCitas() {
        return citaService.getTodasLasCitas();
    }

    /**
     * Crea una nueva cita médica.
     *
     * @param cita DTO con los datos necesarios para crear la cita.
     * @return {@link CitaResponseDTO} de la cita creada.
     */
    @PostMapping
    public CitaResponseDTO createCita(@Valid @RequestBody CitaPostDTO cita) {
        return citaService.createCita(cita);
    }

    /**
     * Genera automáticamente citas disponibles para los médicos del sistema.
     * <p>
     * Este endpoint está pensado para uso administrativo o de mantenimiento.
     *
     * @return respuesta HTTP 201 (CREATED) sin cuerpo.
     */
    @PostMapping("/disponibles")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDICO')")
    public ResponseEntity<Void> generarCitasDisponibles() {
        citaService.generarCitasDisponibles();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Reserva una cita disponible para el paciente autenticado.
     *
     * @param id identificador de la cita a reservar.
     * @param motivo DTO con el motivo de la cita.
     * @param auth contexto de autenticación con el paciente autenticado.
     * @return respuesta HTTP 200 (OK) si la reserva se completa correctamente.
     */
    @PostMapping("/{id}/reservar")
    @PreAuthorize("hasAuthority('PACIENTE')")
    public ResponseEntity<Void> reservarCita(@PathVariable Long id, @RequestBody @Valid ReservarCitaDTO motivo, Authentication auth) {
        Usuario paciente = (Usuario) auth.getPrincipal();
        citaService.reservarCita(id, paciente, motivo);
        return ResponseEntity.ok().build();
    }

    /**
     * Cancela una cita confirmada del paciente autenticado.
     *
     * @param id identificador de la cita a cancelar.
     * @param auth contexto de autenticación con el paciente autenticado.
     * @return respuesta HTTP 200 (OK) si la cancelación se realiza correctamente.
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('PACIENTE')")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id, Authentication auth) {
        Usuario paciente = (Usuario) auth.getPrincipal();
        citaService.cancelarCita(id, paciente);
        return ResponseEntity.ok().build(); // 200 Ok
    }

    /**
     * Elimina una cita existente por su identificador.
     *
     * @param id identificador de la cita a eliminar.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN') or @citaService.perteneceAlPaciente(#id, principal.id)")
    public ResponseEntity<Void> deleteCitaById(@PathVariable Long id) {
        citaService.deleteCita(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * Modifica parcialmente una cita existente.
     *
     * @param id   identificador de la cita a modificar.
     * @param cita DTO con los campos a actualizar.
     * @return {@link CitaResponseDTO} con la información actualizada de la cita.
     */
    @PatchMapping("/{id}")
    public CitaResponseDTO patchCita(@PathVariable Long id, @Valid @RequestBody CitaPatchDTO cita) {
        return citaService.patchCita(id, cita);
    }
}