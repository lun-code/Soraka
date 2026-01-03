package com.hospital.Soraka.repository;

import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico,Long> {

    boolean existsByUsuario(Usuario usuario);
}