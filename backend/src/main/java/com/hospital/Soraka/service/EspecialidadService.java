package com.hospital.Soraka.service;

import com.hospital.Soraka.dto.especialidad.EspecialidadPatchDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadPostDTO;
import com.hospital.Soraka.dto.especialidad.EspecialidadResponseDTO;
import com.hospital.Soraka.entity.Especialidad;
import com.hospital.Soraka.exception.Especialidad.EspecialidadExisteException;
import com.hospital.Soraka.exception.Especialidad.EspecialidadInvalidaException;
import com.hospital.Soraka.exception.Especialidad.EspecialidadNotFoundException;
import com.hospital.Soraka.repository.EspecialidadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la gestión de especialidades médicas.
 *
 * <p>
 * Contiene operaciones de listado, consulta por ID, creación, actualización parcial y eliminación.
 * La seguridad basada en roles debe aplicarse en el controller mediante {@code @PreAuthorize}.
 * Este service valida únicamente la existencia de entidades y reglas de negocio.
 */
@Service
@Transactional
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    /**
     * Obtiene la lista completa de especialidades.
     *
     * @return Lista de {@link EspecialidadResponseDTO} con información de cada especialidad.
     */
    public List<EspecialidadResponseDTO> getEspecialidades() {
        return especialidadRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    /**
     * Obtiene una especialidad por su ID.
     *
     * @param id Id de la especialidad.
     * @return {@link EspecialidadResponseDTO} con información de la especialidad.
     * @throws EspecialidadNotFoundException si la especialidad no existe.
     */
    public EspecialidadResponseDTO getEspecialidadById(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new EspecialidadNotFoundException("No existe esa especialidad."));
        return buildResponse(especialidad);
    }

    /**
     * Crea una nueva especialidad médica.
     *
     * <p>
     * Solo un usuario con rol ADMIN debería llamar a este método desde el controller.
     *
     * @param especialidad DTO con los datos de la especialidad a crear.
     * @return {@link EspecialidadResponseDTO} con la especialidad creada.
     * @throws EspecialidadExisteException si ya existe una especialidad con el mismo nombre.
     */
    public EspecialidadResponseDTO createEspecialidad(EspecialidadPostDTO especialidad) {

        if (especialidadRepository.existsByNombre(especialidad.getNombre())) {
            throw new EspecialidadExisteException("La especialidad ya existe.");
        }

        Especialidad nuevaEspecialidad = new Especialidad(especialidad.getNombre());
        Especialidad guardada = especialidadRepository.save(nuevaEspecialidad);

        return buildResponse(guardada);
    }

    /**
     * Elimina una especialidad existente por su ID.
     *
     * <p>
     * Solo un usuario con rol ADMIN debería llamar a este método desde el controller.
     *
     * @param id ID de la especialidad a eliminar.
     * @throws EspecialidadNotFoundException si la especialidad no existe.
     */
    public void deleteEspecialidad(Long id) {

        if (!especialidadRepository.existsById(id)) {
            throw new EspecialidadNotFoundException("No existe esa especialidad.");
        }
        especialidadRepository.deleteById(id);
    }

    /**
     * Actualiza parcialmente una especialidad existente.
     *
     * <p>
     * Solo un usuario con rol ADMIN debería llamar a este método desde el controller.
     *
     * @param id ID de la especialidad a actualizar.
     * @param especialidad DTO con los campos a modificar.
     * @return {@link EspecialidadResponseDTO} con la especialidad actualizada.
     * @throws EspecialidadNotFoundException si la especialidad no existe.
     * @throws EspecialidadInvalidaException si el nombre es inválido o
     * @throws EspecialidadExisteException si ya existe otra especialidad con el mismo nombre.
     */
    public EspecialidadResponseDTO patchEspecialidad(Long id, EspecialidadPatchDTO especialidad) {

        Especialidad existente = especialidadRepository.findById(id)
                .orElseThrow(() -> new EspecialidadNotFoundException("No existe esa especialidad."));

        if (especialidad.getNombre() != null) {

            if (especialidad.getNombre().isBlank()) {
                throw new EspecialidadInvalidaException("La especialidad no puede estar vacía.");
            }

            if (especialidadRepository.existsByNombre(especialidad.getNombre()) &&
                    !existente.getNombre().equals(especialidad.getNombre())) {
                throw new EspecialidadExisteException("Ya existe una especialidad con el mismo nombre.");
            }

            existente.setNombre(especialidad.getNombre());
        }

        Especialidad guardada = especialidadRepository.save(existente);

        return buildResponse(guardada);
    }

    /**
     * Construye un DTO de respuesta a partir de la entidad {@link Especialidad}.
     *
     * @param e Entidad Especialidad.
     * @return {@link EspecialidadResponseDTO} con la información relevante.
     */
    private EspecialidadResponseDTO buildResponse(Especialidad e) {
        return new EspecialidadResponseDTO(e.getId(), e.getNombre());
    }
}