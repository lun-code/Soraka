package com.hospital.Soraka.repository;

import com.hospital.Soraka.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EspecialidadRepository extends JpaRepository<Especialidad,Long> {
    boolean existsByNombre(String nombre);
}