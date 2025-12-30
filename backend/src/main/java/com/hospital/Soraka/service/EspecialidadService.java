package com.hospital.Soraka.service;


import com.hospital.Soraka.dto.especialidad.EspecialidadPatchDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadPostDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadResponseDTO;
import com.hospital.Soraka.entity.Especialidad;

import com.hospital.Soraka.repository.EspecialidadRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<EspecialidadResponseDTO> getEspecialidades() {
        return especialidadRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    public EspecialidadResponseDTO getEspecialidadById(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe esa especialidad."));
        return buildResponse(especialidad);
    }

    public EspecialidadResponseDTO createEspecialidad(EspecialidadPostDTO especialidad) {

        if(especialidadRepository.existsByNombre(especialidad.getNombre())) {
            throw new IllegalArgumentException("La especialidad ya existe.");
        }

        Especialidad nuevaEspecialidad = new Especialidad(
                especialidad.getNombre()
        );

        Especialidad guardada =  especialidadRepository.save(nuevaEspecialidad);

        return buildResponse(guardada);
    }

    public void deleteEspecialidad(Long id) {

        if(!especialidadRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe esa especialidad.");
        }
        especialidadRepository.deleteById(id);
    }

    public EspecialidadResponseDTO patchEspecialidad(Long id, EspecialidadPatchDTO especialidad) {

        Especialidad existente = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe esa especialidad."));

        if(especialidad.getNombre() != null) {

            if(especialidad.getNombre().isBlank()){
                throw new IllegalArgumentException("La especialidad no puede estar vacio.");
            }

            if(especialidadRepository.existsByNombre(especialidad.getNombre()) && !existente.getNombre().equals(especialidad.getNombre())) {
                throw new IllegalArgumentException("Ya existe una especialidad con el mismo nombre.");
            }

            existente.setNombre(especialidad.getNombre());
        }

        Especialidad guardada =  especialidadRepository.save(existente);

        return buildResponse(guardada);
    }

    private EspecialidadResponseDTO buildResponse(Especialidad e) {
        return new EspecialidadResponseDTO(
                e.getId(),
                e.getNombre()
        );
    }
}