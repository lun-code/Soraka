package com.hospital.Soraka.repository;

import com.hospital.Soraka.entity.Cita;
import com.hospital.Soraka.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita,Long> {
    Optional<Cita> findByMedicoAndFechaHora(Medico medico, LocalDateTime fechaHora);
}