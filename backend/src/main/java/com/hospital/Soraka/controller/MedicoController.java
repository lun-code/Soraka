package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.medico.MedicoPatchDTO;
import com.hospital.Soraka.dto.medico.MedicoPostDTO;
import com.hospital.Soraka.dto.medico.MedicoPublicoDTO;
import com.hospital.Soraka.dto.medico.MedicoResponseDTO;
import com.hospital.Soraka.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de médicos en el hospital.
 *
 * <p>
 * Exposición de endpoints para:
 * <ul>
 *     <li>Listar médicos.</li>
 *     <li>Consultar un médico por ID.</li>
 *     <li>Crear nuevos médicos.</li>
 *     <li>Actualizar parcialmente médicos existentes.</li>
 *     <li>Eliminar médicos.</li>
 * </ul>
 *
 * <p>
 * Los endpoints que modifican datos (POST, PATCH, DELETE) están protegidos para usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    /**
     * Obtiene la lista completa de médicos registrados.
     * <p>
     * Accesible solo para usuarios con rol ADMIN.
     *
     * @return Lista de {@link MedicoResponseDTO} con la información completa de cada médico.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<MedicoResponseDTO> getMedicos() {
        return medicoService.getMedicos();
    }

    /**
     * Obtiene la lista de médicos con información pública.
     *
     * <p>
     * Accesible públicamente, sin necesidad de autenticación.
     *
     * @return Lista de {@link MedicoPublicoDTO} con datos limitados de cada médico.
     */
    @GetMapping("/publicos")
    public List<MedicoPublicoDTO> getMedicosPublicos() {
        return medicoService.getMedicosPublico();
    }

    /**
     * Obtiene un médico específico por su ID.
     *
     * <p>
     * Accesible para cualquier usuario autenticado.
     *
     * @param id Identificador del médico.
     * @return {@link MedicoResponseDTO} con los datos completos del médico.
     */
    @GetMapping("/{id}")
    public MedicoResponseDTO getMedicoById(@PathVariable Long id) {
        return medicoService.getMedicoById(id);
    }

    /**
     * Crea un nuevo médico en el sistema.
     *
     * <p>
     * Solo accesible para administradores.
     *
     * @param medico DTO con los datos necesarios para crear el médico.
     * @return {@link MedicoResponseDTO} con los datos del médico recién creado.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public MedicoResponseDTO createMedico(@Valid @RequestBody MedicoPostDTO medico) {
        return medicoService.createMedico(medico);
    }

    /**
     * Elimina un médico existente por su ID.
     *
     * <p>
     * Solo accesible para administradores.
     *
     * @param id Identificador del médico a eliminar.
     * @return {@link ResponseEntity} con estado 204 No Content si la eliminación es exitosa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteMedico(@PathVariable Long id) {
        medicoService.deleteMedico(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza parcialmente los datos de un médico existente.
     *
     * <p>
     * Solo accesible para administradores.
     *
     * @param id Identificador del médico a modificar.
     * @param medico DTO con los campos parciales a actualizar.
     * @return {@link MedicoResponseDTO} con los datos actualizados del médico.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public MedicoResponseDTO patchMedico(@PathVariable Long id,
                                         @Valid @RequestBody MedicoPatchDTO medico) {
        return medicoService.patchMedico(id, medico);
    }
}