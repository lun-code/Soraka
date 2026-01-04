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
 * La seguridad se aplica mediante {@link PreAuthorize} para validar roles:
 * <ul>
 *     <li>PACIENTE: solo puede acceder a sus propias citas</li>
 *     <li>MEDICO / ADMIN: acceso amplio</li>
 * </ul>
 *
 * <p>
 * La validación de propiedad del recurso se delega al {@link CitaService}.
 */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    /**
     * Obtiene todas las citas del paciente autenticado.
     *
     * @param authentication contexto de seguridad con el usuario autenticado
     * @return lista de {@link CitaResponseDTO} del paciente
     */
    @GetMapping("/mis-citas")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public List<CitaResponseDTO> getCitasPorPaciente(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return citaService.getCitasPorPaciente(usuario.getId());
    }

    /**
     * Obtiene el detalle de una cita concreta por su ID.
     *
     * @param id identificador de la cita
     * @param authentication contexto de seguridad con el usuario autenticado
     * @return {@link CitaResponseDTO} de la cita
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public CitaResponseDTO getCitaById(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return citaService.getCitaById(id, usuario);
    }

    /**
     * Crea una nueva cita médica.
     *
     * @param cita DTO con los datos de la cita
     * @param authentication contexto de seguridad con el usuario autenticado
     * @return {@link CitaResponseDTO} de la cita creada
     */
    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN') or hasAuthority('PACIENTE')")
    public CitaResponseDTO createCita(@RequestBody @Valid CitaPostDTO cita, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return citaService.createCita(cita); // Service valida propiedad si paciente
    }

    /**
     * Modifica parcialmente una cita existente.
     *
     * @param id identificador de la cita
     * @param cita DTO con campos a actualizar
     * @param authentication contexto de seguridad con el usuario autenticado
     * @return {@link CitaResponseDTO} actualizado
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public CitaResponseDTO patchCita(@PathVariable Long id,
                                     @RequestBody CitaPatchDTO cita,
                                     Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return citaService.patchCita(id, cita, usuario);
    }

    /**
     * Elimina una cita existente.
     *
     * @param id identificador de la cita
     * @param authentication contexto de seguridad con el usuario autenticado
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public void deleteCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        citaService.deleteCita(id, usuario);
    }

    /**
     * Lista todas las citas DISPONIBLES a partir de la fecha actual.
     *
     * @param authentication contexto de seguridad con el usuario autenticado
     * @return lista de {@link CitaResponseDTO} disponibles
     */
    @GetMapping("/disponibles")
    @PreAuthorize("hasAuthority('PACIENTE') or hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public List<CitaResponseDTO> listarDisponibles(Authentication authentication) {
        return citaService.listarDisponibles();
    }

    /**
     * Reserva una cita DISPONIBLE para el paciente autenticado.
     *
     * @param citaId identificador de la cita a reservar
     * @param dto contiene el motivo de la cita
     * @param authentication contexto de seguridad con el usuario autenticado
     */
    @PostMapping("/{id}/reservar")
    @PreAuthorize("hasAuthority('PACIENTE')")
    public void reservarCita(@PathVariable("id") Long citaId,
                             @RequestBody @Valid ReservarCitaDTO dto,
                             Authentication authentication) {
        Usuario paciente = (Usuario) authentication.getPrincipal();
        citaService.reservarCita(citaId, paciente, dto);
    }

    /**
     * Cancela una cita confirmada del paciente autenticado.
     *
     * @param citaId identificador de la cita a cancelar
     * @param authentication contexto de seguridad con el usuario autenticado
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('PACIENTE')")
    public void cancelarCita(@PathVariable("id") Long citaId, Authentication authentication) {
        Usuario paciente = (Usuario) authentication.getPrincipal();
        citaService.cancelarCita(citaId, paciente);
    }
}