package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.medico.MedicoPatchDTO;
import com.hospital.Soraka.dto.medico.MedicoPostDTO;
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
 * Permite listar médicos, consultar uno específico, crear, actualizar y eliminar.
 * Los endpoints de modificación (POST, PATCH, DELETE) están protegidos para administradores.
 */
@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    /**
     * Obtiene la lista completa de médicos registrados.
     *
     * <p>
     * Este endpoint es accesible para cualquier usuario autenticado.
     *
     * @return Lista de {@link MedicoResponseDTO} con la información de cada médico.
     */
    @GetMapping
    public List<MedicoResponseDTO> getMedicos() {
        return medicoService.getMedicos();
    }

    /**
     * Obtiene un médico por su ID.
     *
     * <p>
     * Este endpoint es accesible para cualquier usuario autenticado.
     *
     * @param id Identificador del médico.
     * @return {@link MedicoResponseDTO} con los datos del médico.
     */
    @GetMapping("/{id}")
    public MedicoResponseDTO getMedicoById(@PathVariable Long id) {
        return medicoService.getMedicoById(id);
    }

    /**
     * Crea un nuevo médico.
     *
     * <p>
     * Solo accesible para administradores.
     *
     * @param medico DTO con los datos del médico a crear.
     * @return {@link MedicoResponseDTO} con los datos del médico creado.
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
     * @return {@link ResponseEntity} con estado 204 No Content si se elimina correctamente.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteMedico(@PathVariable Long id) {
        medicoService.deleteMedico(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza parcialmente un médico existente.
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