package com.hospital.Soraka.repository;

import com.hospital.Soraka.entity.TokenConfirmacion;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hospital.Soraka.entity.Usuario;

import java.util.Optional;

public interface TokenConfirmacionRepository
        extends JpaRepository<TokenConfirmacion, Long> {

    Optional<TokenConfirmacion> findByToken(String token);

    Optional<TokenConfirmacion> findByUsuario(Usuario usuario);
}