package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.medico.MedicoPatchDTO;
import com.hospital.Soraka.dto.medico.MedicoPostDTO;
import com.hospital.Soraka.dto.medico.MedicoResponseDTO;
import com.hospital.Soraka.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de médicos en el hospital.
 * <p>
 * Permite listar médicos, consultar uno específico, crear, actualizar y eliminar.
 * La mayoría de los endpoints de modificación deben ser accesibles solo por administradores.
 */
@RestController
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    /**
     * Obtiene la lista de todos los médicos.
     * Accesible para cualquier usuario autenticado.
     *
     * @return Lista de MedicoResponseDTO con información de cada médico.
     */
    @GetMapping("/medicos")
    public List<MedicoResponseDTO> getMedicos(){
        return medicoService.getMedicos();
    }

    /**
     * Obtiene un médico por su ID.
     * Accesible para cualquier usuario autenticado.
     *
     * @param id Id del médico.
     * @return MedicoResponseDTO con información del médico.
     */
    @GetMapping("/medicos/{id}")
    public MedicoResponseDTO getMedicoById(@PathVariable Long id){
        return medicoService.getMedicoById(id);
    }

    /**
     * Crea un nuevo médico.
     * Accesible solo para administradores.
     *
     * @param medico DTO con los datos del médico a crear.
     * @return MedicoResponseDTO con los datos del médico creado.
     */
    @PostMapping("/medicos")
    public MedicoResponseDTO createMedico(@Valid @RequestBody MedicoPostDTO medico){
        return medicoService.createMedico(medico);
    }

    /**
     * Elimina un médico existente.
     * Accesible solo para administradores.
     *
     * @param id Id del médico a eliminar.
     */
    @DeleteMapping("/medicos/{id}")
    public ResponseEntity<Void> deleteMedico(@PathVariable Long id){
        medicoService.deleteMedico(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * Actualiza parcialmente un médico existente.
     * Accesible solo para administradores.
     *
     * @param medico DTO con los campos a actualizar.
     * @param id ID del médico a modificar.
     * @return MedicoResponseDTO con los datos actualizados del médico.
     */
    @PatchMapping("/medicos/{id}")
    public MedicoResponseDTO patchMedico(@Valid @RequestBody MedicoPatchDTO medico, @PathVariable Long id){
        return medicoService.patchMedico(id, medico);
    }
}