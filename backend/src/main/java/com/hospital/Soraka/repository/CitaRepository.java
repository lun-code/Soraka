package com.hospital.Soraka.repository;

import com.hospital.Soraka.entity.Cita;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita,Long> {

    Optional<Cita> findByMedicoAndFechaHora(Medico medico, LocalDateTime fechaHora);

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByEstadoAndFechaHoraAfter(EstadoCita estado, LocalDateTime fechaHora);

    List<Cita> findByFechaHoraBefore(LocalDateTime fechaHora);

    List<Cita> findByFechaHoraBeforeAndEstadoIn(LocalDateTime fechaHora, List<EstadoCita> confirmada);

    boolean existsByMedicoAndFechaHora(Medico medico, LocalDateTime fechaHora);
}