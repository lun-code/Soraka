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
    @GetMapping("/citas")
    public List<CitaResponseDTO> getCitas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return citaService.getCitas(usuario.getId());
    }

    /**
     * Obtiene el detalle de una cita concreta por su identificador.
     * <p>
     * Las reglas de acceso se validan en la capa de servicio:
     * <ul>
     *     <li>Médicos pueden acceder a cualquier cita.</li>
     *     <li>Pacientes solo pueden acceder a sus propias citas.</li>
     * </ul>
     *
     * @param id identificador de la cita.
     * @return {@link CitaResponseDTO} con la información de la cita.
     */
    @GetMapping("/citas/{id}")
    public CitaResponseDTO getCitaById(@PathVariable Long id) {
        return citaService.getCitaById(id);
    }

    /**
     * Crea una nueva cita médica.
     * <p>
     * Reglas generales:
     * <ul>
     *     <li>Médicos y administradores pueden crear citas para cualquier paciente.</li>
     *     <li>Pacientes solo pueden crear citas para sí mismos.</li>
     * </ul>
     * <p>
     * La validación de permisos y conflictos de agenda se realiza en el service.
     *
     * @param cita DTO con los datos necesarios para crear la cita.
     * @return {@link CitaResponseDTO} de la cita creada.
     */
    @PostMapping("/citas")
    public CitaResponseDTO createCita(@Valid @RequestBody CitaPostDTO cita) {
        return citaService.createCita(cita);
    }

    /**
     * Genera automáticamente citas disponibles para los médicos del sistema.
     * <p>
     * Este endpoint está pensado para uso administrativo o de mantenimiento
     * y normalmente se ejecuta de forma programada mediante tareas {@code @Scheduled}.
     *
     * @return respuesta HTTP 201 (CREATED) sin cuerpo.
     */
    @PostMapping("/citas/disponibles")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MEDICO')")
    public ResponseEntity<Void> generarCitasDisponibles() {

        citaService.generarCitasDisponibles();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Reserva una cita disponible para el paciente autenticado.
     * <p>
     * La cita debe estar en estado DISPONIBLE. Si otro paciente la reserva
     * simultáneamente, el sistema gestionará el conflicto mediante control
     * de concurrencia.
     *
     * @param id identificador de la cita a reservar.
     * @param motivo DTO que contiene el motivo de la consulta.
     * @param auth contexto de autenticación con el paciente autenticado.
     * @return respuesta HTTP 200 (OK) si la reserva se completa correctamente.
     */
    @PostMapping("/citas/{id}/reservar")
    @PreAuthorize("hasAuthority('PACIENTE')")
    public ResponseEntity<Void> reservarCita(@PathVariable Long id, @RequestBody @Valid ReservarCitaDTO motivo, Authentication auth) {

        Usuario paciente = (Usuario) auth.getPrincipal();
        citaService.reservarCita(id, paciente, motivo);

        return ResponseEntity.ok().build();
    }

    /**
     * Elimina una cita existente por su identificador.
     * <p>
     * Reglas de acceso:
     * <ul>
     *     <li>Médicos y administradores pueden eliminar cualquier cita.</li>
     *     <li>Pacientes solo pueden eliminar sus propias citas.</li>
     * </ul>
     *
     * @param id identificador de la cita a eliminar.
     */
    @DeleteMapping("/citas/{id}")
    public void deleteCitaById(@PathVariable Long id) {
        citaService.deleteCita(id);
    }

    /**
     * Modifica parcialmente una cita existente.
     * <p>
     * Permite actualizar campos como fecha, estado o motivo según
     * la lógica definida en la capa de servicio.
     * <p>
     * Reglas de acceso:
     * <ul>
     *     <li>Médicos pueden modificar cualquier cita.</li>
     *     <li>Pacientes solo pueden modificar sus propias citas.</li>
     * </ul>
     *
     * @param id   identificador de la cita a modificar.
     * @param cita DTO con los campos a actualizar.
     * @return {@link CitaResponseDTO} con la información actualizada de la cita.
     */
    @PatchMapping("/citas/{id}")
    public CitaResponseDTO patchCita(@PathVariable Long id,
                                     @Valid @RequestBody CitaPatchDTO cita) {
        return citaService.patchCita(id, cita);
    }
}