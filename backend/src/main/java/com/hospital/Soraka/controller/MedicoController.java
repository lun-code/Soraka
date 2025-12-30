package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.medico.MedicoPatchDTO;
import com.hospital.Soraka.dto.medico.MedicoPostDTO;
import com.hospital.Soraka.dto.medico.MedicoResponseDTO;
import com.hospital.Soraka.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping("/medicos")
    public List<MedicoResponseDTO> getMedicos(){
        return medicoService.getMedicos();
    }

    @GetMapping("/medicos/{id}")
    public MedicoResponseDTO getMedicoById(@PathVariable Long id){
        return medicoService.getMedicoById(id);
    }

    @PostMapping("/medicos")
    public MedicoResponseDTO createMedico(@Valid @RequestBody MedicoPostDTO medico){
        return medicoService.createMedico(medico);
    }

    @DeleteMapping("/medicos/{id}")
    public void deleteMedico(@PathVariable Long id){
        medicoService.deleteMedico(id);
    }

    @PatchMapping("/medicos/{id}")
    public MedicoResponseDTO patchMedico(@Valid @RequestBody MedicoPatchDTO medico, @PathVariable Long id){
        return medicoService.patchMedico(id, medico);
    }
}