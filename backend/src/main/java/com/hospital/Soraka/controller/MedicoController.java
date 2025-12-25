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
        return medicoService.listarMedicos();
    }

    @GetMapping("/medicos/{id}")
    public MedicoResponseDTO getMedico(@PathVariable Long id){
        return medicoService.buscarMedicoPorId(id);
    }

    @PostMapping("/medicos")
    public MedicoResponseDTO createMedico(@Valid @RequestBody MedicoPostDTO medicoDTO){
        return medicoService.registrarMedico(medicoDTO);
    }

    @PatchMapping("/medicos/{id}")
    public MedicoResponseDTO updateMedico(@Valid @RequestBody MedicoPatchDTO medicoDTO, @PathVariable Long id){
        return medicoService.actualizarMedico(id, medicoDTO);
    }
// as
    @DeleteMapping("/medicos/{id}")
    public void deleteMedico(@PathVariable Long id){
        medicoService.borrarMedico(id);
    }
}