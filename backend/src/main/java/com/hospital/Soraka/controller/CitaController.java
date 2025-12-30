package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.cita.CitaPatchDTO;
import com.hospital.Soraka.dto.cita.CitaPostDTO;
import com.hospital.Soraka.dto.cita.CitaResponseDTO;
import com.hospital.Soraka.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping("/citas")
    public List<CitaResponseDTO> getCitas(){
        return citaService.getCitas();
    }

    @GetMapping("/citas/{id}")
    public CitaResponseDTO getCitaById(@PathVariable Long id){
        return citaService.getCitaById(id);
    }

    @PostMapping("/citas")
    public CitaResponseDTO createCita(@Valid @RequestBody CitaPostDTO cita){
        return citaService.createCita(cita);
    }

    @DeleteMapping("/citas/{id}")
    public void deleteCitaById(@PathVariable Long id){
        citaService.deleteCita(id);
    }

    @PatchMapping("/citas/{id}")
    public CitaResponseDTO patchCita(@PathVariable Long id, @Valid @RequestBody CitaPatchDTO cita){
        return citaService.patchCita(id, cita);
    }
}