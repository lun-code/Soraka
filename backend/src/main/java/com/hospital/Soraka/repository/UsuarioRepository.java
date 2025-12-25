package com.hospital.Soraka.repository;

import com.hospital.Soraka.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    // Buscar usuario por email (necesario para login)
    Optional<Usuario> findByEmail(String email);

    // Comprobar si ya existe un email (para registro)
    boolean existsByEmail(String email);
}
