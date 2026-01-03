package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.especialidad.EspecialidadPatchDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadPostDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadResponseDTO;
import com.hospital.Soraka.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que maneja las operaciones sobre Especialidades.
 * Permite listar, consultar por id, crear, modificar y eliminar especialidades.
 *
 * <p>Se recomienda proteger las operaciones de creación, modificación y eliminación
 * mediante Spring Security para que solo personal autorizado (médicos o administradores)
 * pueda realizarlas.</p>
 */
@RestController
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    /**
     * Obtiene todas las especialidades registradas.
     * Este endpoint puede ser público para que pacientes puedan verlas al agendar citas.
     *
     * @return Lista de EspecialidadResponseDTO con todas las especialidades.
     */
    @GetMapping("/especialidades")
    public List<EspecialidadResponseDTO> getEspecialidades() {
        return especialidadService.getEspecialidades();
    }

    /**
     * Obtiene una especialidad por su ID.
     *
     * @param id Identificador de la especialidad.
     * @return EspecialidadResponseDTO con los datos de la especialidad.
     */
    @GetMapping("/especialidades/{id}")
    public EspecialidadResponseDTO getEspecialidadById(@PathVariable Long id) {
        return especialidadService.getEspecialidadById(id);
    }

    /**
     * Crea una nueva especialidad.
     * Solo personal autorizado debe poder acceder a este endpoint.
     *
     * @param especialidad DTO con los datos de la nueva especialidad.
     * @return EspecialidadResponseDTO con la especialidad creada.
     */
    @PostMapping("/especialidades")
    public EspecialidadResponseDTO createEspecialidad(@Valid @RequestBody EspecialidadPostDTO especialidad) {
        return especialidadService.createEspecialidad(especialidad);
    }

    /**
     * Modifica una especialidad existente.
     * Solo personal autorizado debe poder acceder a este endpoint.
     *
     * @param id Identificador de la especialidad a modificar.
     * @param especialidad DTO con los campos a actualizar.
     * @return EspecialidadResponseDTO con la especialidad actualizada.
     */
    @PatchMapping("/especialidades/{id}")
    public EspecialidadResponseDTO patchEspecialidad(@PathVariable Long id, @Valid @RequestBody EspecialidadPatchDTO especialidad) {
        return especialidadService.patchEspecialidad(id, especialidad);
    }

    /**
     * Elimina una especialidad existente.
     * Solo personal autorizado debe poder acceder a este endpoint.
     *
     * @param id Identificador de la especialidad a eliminar.
     */
    @DeleteMapping("/especialidades/{id}")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Long id) {
        especialidadService.deleteEspecialidad(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}