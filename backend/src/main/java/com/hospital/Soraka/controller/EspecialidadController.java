package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.especialidad.EspecialidadPatchDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadPostDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadResponseDTO;
import com.hospital.Soraka.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping("/especialidades")
    public List<EspecialidadResponseDTO> getEspecialidades(){
        return especialidadService.getEspecialidades();
    }

    @GetMapping("/especialidades/{id}")
    public EspecialidadResponseDTO getEspecialidadById(@PathVariable Long id){
        return especialidadService.getEspecialidadById(id);
    }

    @PostMapping("/especialidades")
    public EspecialidadResponseDTO createEspecialidad(@Valid @RequestBody EspecialidadPostDTO especialidad){
        return especialidadService.createEspecialidad(especialidad);
    }

    @PatchMapping("/especialidades/{id}")
    public EspecialidadResponseDTO patchEspecialidad(@PathVariable Long id, @Valid @RequestBody EspecialidadPatchDTO especialidad){
        return especialidadService.patchEspecialidad(id, especialidad);
    }

    @DeleteMapping("/especialidad/{id}")
    public void deleteEspecialidad(@PathVariable Long id){
        especialidadService.deleteEspecialidad(id);
    }
}