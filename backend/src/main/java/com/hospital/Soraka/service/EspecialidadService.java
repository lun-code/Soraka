package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.especialidad.EspecialidadPatchDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadPostDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadResponseDTO;
import com.hospital.Soraka.entity.Especialidad;
import com.hospital.Soraka.repository.EspecialidadRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la gestión de especialidades médicas.
 * <p>
 * Incluye operaciones de listado, consulta por ID, creación, actualización parcial y eliminación.
 * El control fino de acceso se aplica usando anotaciones @PreAuthorize, de forma que solo
 * usuarios con los roles adecuados puedan modificar o eliminar especialidades.
 */
@Service
@Transactional
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    /**
     * Obtiene la lista completa de especialidades.
     * Accesible para cualquier usuario autenticado.
     *
     * @return Lista de EspecialidadResponseDTO con información de cada especialidad.
     */
    public List<EspecialidadResponseDTO> getEspecialidades() {
        return especialidadRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene una especialidad por su ID.
     * Accesible para cualquier usuario autenticado.
     *
     * @param id Id de la especialidad.
     * @return EspecialidadResponseDTO con información de la especialidad.
     * @throws EntityNotFoundException si la especialidad no existe.
     */
    public EspecialidadResponseDTO getEspecialidadById(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe esa especialidad."));
        return buildResponse(especialidad);
    }

    /**
     * Crea una nueva especialidad.
     * Accesible solo para administradores.
     *
     * @param especialidad DTO con los datos de la especialidad a crear.
     * @return EspecialidadResponseDTO con la especialidad creada.
     * @throws IllegalArgumentException si ya existe una especialidad con el mismo nombre.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public EspecialidadResponseDTO createEspecialidad(EspecialidadPostDTO especialidad) {

        if (especialidadRepository.existsByNombre(especialidad.getNombre())) {
            throw new IllegalArgumentException("La especialidad ya existe.");
        }

        Especialidad nuevaEspecialidad = new Especialidad(especialidad.getNombre());
        Especialidad guardada = especialidadRepository.save(nuevaEspecialidad);

        return buildResponse(guardada);
    }

    /**
     * Elimina una especialidad existente.
     * Accesible solo para administradores.
     *
     * @param id ID de la especialidad a eliminar.
     * @throws IllegalArgumentException si la especialidad no existe.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteEspecialidad(Long id) {

        if (!especialidadRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe esa especialidad.");
        }
        especialidadRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente una especialidad existente.
     * Accesible solo para administradores.
     *
     * @param id ID de la especialidad a actualizar.
     * @param especialidad DTO con los campos a modificar.
     * @return EspecialidadResponseDTO con la especialidad actualizada.
     * @throws EntityNotFoundException si la especialidad no existe.
     * @throws IllegalArgumentException si el nombre es inválido o ya existe otra especialidad con el mismo nombre.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public EspecialidadResponseDTO patchEspecialidad(Long id, EspecialidadPatchDTO especialidad) {

        Especialidad existente = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe esa especialidad."));

        if (especialidad.getNombre() != null) {

            if (especialidad.getNombre().isBlank()) {
                throw new IllegalArgumentException("La especialidad no puede estar vacía.");
            }

            if (especialidadRepository.existsByNombre(especialidad.getNombre()) &&
                    !existente.getNombre().equals(especialidad.getNombre())) {
                throw new IllegalArgumentException("Ya existe una especialidad con el mismo nombre.");
            }

            existente.setNombre(especialidad.getNombre());
        }

        Especialidad guardada = especialidadRepository.save(existente);

        return buildResponse(guardada);
    }

    /**
     * Construye un DTO de respuesta a partir de la entidad Especialidad.
     *
     * @param e Entidad Especialidad.
     * @return EspecialidadResponseDTO con la información relevante.
     */
    private EspecialidadResponseDTO buildResponse(Especialidad e) {
        return new EspecialidadResponseDTO(e.getId(), e.getNombre());
    }
}