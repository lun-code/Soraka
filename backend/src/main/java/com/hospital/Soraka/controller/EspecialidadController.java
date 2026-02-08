package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.especialidad.EspecialidadPatchDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadPostDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadResponseDTO;
import com.hospital.Soraka.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gestiona las operaciones sobre Especialidades.
 *
 * <p>
 * Permite listar, consultar por ID, crear, modificar y eliminar especialidades.
 * La seguridad se aplica mediante {@code @PreAuthorize} para restringir la creación,
 * modificación y eliminación únicamente a administradores.
 */
@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "http://localhost:5173")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    /**
     * Obtiene todas las especialidades registradas.
     *
     * <p>
     * Este endpoint puede ser público para que pacientes puedan verlas al agendar citas.
     *
     * @return Lista de {@link EspecialidadResponseDTO} con todas las especialidades.
     */
    @GetMapping
    public List<EspecialidadResponseDTO> getEspecialidades() {
        return especialidadService.getEspecialidades();
    }

    /**
     * Obtiene una especialidad por su ID.
     *
     * <p>
     * Este endpoint puede ser público para que pacientes puedan ver los detalles.
     *
     * @param id Identificador de la especialidad.
     * @return {@link EspecialidadResponseDTO} con los datos de la especialidad.
     */
    @GetMapping("/{id}")
    public EspecialidadResponseDTO getEspecialidadById(@PathVariable Long id) {
        return especialidadService.getEspecialidadById(id);
    }

    /**
     * Crea una nueva especialidad médica.
     *
     * <p>
     * Solo accesible para administradores.
     *
     * @param especialidad DTO con los datos de la nueva especialidad.
     * @return {@link EspecialidadResponseDTO} con la especialidad creada.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public EspecialidadResponseDTO createEspecialidad(@Valid @RequestBody EspecialidadPostDTO especialidad) {
        return especialidadService.createEspecialidad(especialidad);
    }

    /**
     * Modifica parcialmente una especialidad existente.
     *
     * <p>
     * Solo accesible para administradores.
     *
     * @param id Identificador de la especialidad a modificar.
     * @param especialidad DTO con los campos a actualizar.
     * @return {@link EspecialidadResponseDTO} con la especialidad actualizada.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public EspecialidadResponseDTO patchEspecialidad(@PathVariable Long id,
                                                     @Valid @RequestBody EspecialidadPatchDTO especialidad) {
        return especialidadService.patchEspecialidad(id, especialidad);
    }

    /**
     * Elimina una especialidad existente.
     *
     * <p>
     * Solo accesible para administradores.
     *
     * @param id Identificador de la especialidad a eliminar.
     * @return {@link ResponseEntity} con estado 204 No Content si se elimina correctamente.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Long id) {
        especialidadService.deleteEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}