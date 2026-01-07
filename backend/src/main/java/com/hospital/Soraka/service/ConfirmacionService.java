package com.hospital.Soraka.service;

import com.hospital.Soraka.entity.TokenConfirmacion;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.repository.TokenConfirmacionRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ConfirmacionService {

    private final TokenConfirmacionRepository tokenRepo;
    private final UsuarioRepository usuarioRepo;

    public ConfirmacionService(TokenConfirmacionRepository tokenRepo,
                               UsuarioRepository usuarioRepo) {
        this.tokenRepo = tokenRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public void confirmarCuenta(String token) {
        TokenConfirmacion tc = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (tc.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        Usuario usuario = tc.getUsuario();
        usuario.setActivo(true);

        usuarioRepo.save(usuario);
        tokenRepo.delete(tc);
    }
}