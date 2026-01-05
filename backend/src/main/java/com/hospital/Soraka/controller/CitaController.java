package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.cita.*;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gestiona las operaciones sobre citas médicas.
 *
 * <p>
 * La seguridad por rol se aplica mediante {@link PreAuthorize}.
 * La validación de propiedad del recurso y reglas de negocio
 * se delega al {@link CitaService}.
 */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    /**
     * Obtiene las citas del paciente autenticado.
     *
     * @param authentication contexto de seguridad
     * @return lista de citas del paciente
     */
    @GetMapping("/mis-citas")
    @PreAuthorize("hasAuthority('PACIENTE')")
    public List<CitaResponseDTO> getMisCitas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return citaService.getCitasPorPaciente(usuario.getId());
    }

    /**
     * Obtiene el detalle de una cita por su ID.
     *
     * @param id identificador de la cita
     * @return cita encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public CitaResponseDTO getCitaById(@PathVariable Long id) {
        return citaService.getCitaById(id);
    }

    /**
     * Crea una nueva cita médica.
     *
     * @param cita datos de la cita
     * @return cita creada
     */
    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public CitaResponseDTO createCita(
            @RequestBody @Valid CitaPostDTO cita
    ) {
        return citaService.createCita(cita);
    }

    /**
     * Modifica parcialmente una cita existente.
     *
     * @param id identificador de la cita
     * @param cita datos a modificar
     * @return cita modificada
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public CitaResponseDTO patchCita(
            @PathVariable Long id,
            @RequestBody @Valid CitaPatchDTO cita
    ) {
        return citaService.patchCita(id, cita);
    }

    /**
     * Elimina una cita existente.
     *
     * @param id identificador de la cita
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public void deleteCita(@PathVariable Long id) {
        citaService.deleteCita(id);
    }

    /**
     * Lista todas las citas disponibles con fecha futura.
     *
     * @return lista de citas disponibles
     */
    @GetMapping("/disponibles")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public List<CitaResponseDTO> listarDisponibles() {
        return citaService.listarDisponibles();
    }

    /**
     * Reserva una cita disponible para el paciente autenticado.
     *
     * @param id id de la cita
     * @param dto datos de la reserva
     * @param authentication contexto de seguridad
     */
    @PostMapping("/{id}/reservar")
    @PreAuthorize("hasAuthority('PACIENTE')")
    public void reservarCita(
            @PathVariable Long id,
            @RequestBody @Valid ReservarCitaDTO dto,
            Authentication authentication
    ) {
        Usuario paciente = (Usuario) authentication.getPrincipal();
        citaService.reservarCita(id, paciente, dto);
    }

    /**
     * Cancela una cita confirmada del paciente autenticado.
     *
     * @param id id de la cita
     * @param authentication contexto de seguridad
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public void cancelarCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        citaService.cancelarCita(id, usuario);
    }
}