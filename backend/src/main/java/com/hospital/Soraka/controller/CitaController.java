package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.cita.CitaPatchDTO;
import com.hospital.Soraka.dto.cita.CitaPostDTO;
import com.hospital.Soraka.dto.cita.CitaResponseDTO;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de citas médicas.
 * <p>
 * Este controlador expone los endpoints HTTP relacionados con las citas y
 * delega toda la lógica de negocio y seguridad en {@link CitaService}.
 * <p>
 * La autorización fina (quién puede ver, crear, modificar o eliminar citas)
 * se gestiona mediante {@code @PreAuthorize} en la capa de servicio.
 */
@RestController
public class CitaController {

    @Autowired
    private CitaService citaService;

    /**
     * Obtiene el listado de citas del usuario autenticado.
     * <p>
     * - Si el usuario es PACIENTE, devuelve únicamente sus propias citas.
     * - Si el usuario es MEDICO o ADMIN, la autorización se valida en el service
     *   y se permite el acceso según la lógica definida allí.
     *
     * @param authentication objeto de autenticación de Spring Security, del cual se obtiene el usuario autenticado.
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
     * La validación de permisos se realiza en el service:
     * - Médicos pueden acceder a cualquier cita.
     * - Pacientes solo pueden acceder a sus propias citas.
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
     * - Médicos y administradores pueden crear citas para cualquier paciente.
     * - Pacientes solo pueden crear citas para sí mismos.
     * <p>
     * Las reglas de autorización se validan en la capa de servicio.
     *
     * @param cita DTO con los datos necesarios para crear la cita.
     * @return {@link CitaResponseDTO} de la cita creada.
     */
    @PostMapping("/citas")
    public CitaResponseDTO createCita(@Valid @RequestBody CitaPostDTO cita) {
        return citaService.createCita(cita);
    }

    /**
     * Elimina una cita existente por su identificador.
     * <p>
     * - Médicos y administradores pueden eliminar cualquier cita.
     * - Pacientes solo pueden eliminar sus propias citas.
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
     * Permite actualizar campos como fecha, estado o motivo según la
     * lógica definida en el service.
     * <p>
     * - Médicos pueden modificar cualquier cita.
     * - Pacientes solo pueden modificar sus propias citas.
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