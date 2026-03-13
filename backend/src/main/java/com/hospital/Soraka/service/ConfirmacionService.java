package com.hospital.Soraka.service;

import com.hospital.Soraka.entity.TokenConfirmacion;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.exception.Confirmacion.TokenExpiradoException;
import com.hospital.Soraka.exception.Confirmacion.TokenInvalidoException;
import com.hospital.Soraka.repository.TokenConfirmacionRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio encargado de la confirmación de cuentas de usuario mediante token.
 * <p>
 * Gestiona la validación del token enviado por correo electrónico y la
 * activación de la cuenta del usuario asociado.
 */
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

    /**
     * Confirma la cuenta de un usuario a partir del token de verificación.
     * <p>
     * Flujo:
     * <ol>
     *     <li>Busca el token en la base de datos; lanza {@link TokenInvalidoException} si no existe.</li>
     *     <li>Verifica que el token no haya expirado; lanza {@link TokenExpiradoException} si ha caducado.</li>
     *     <li>Activa la cuenta del usuario ({@code isActivo = true}).</li>
     *     <li>Elimina el token de confirmación para que no pueda reutilizarse.</li>
     * </ol>
     *
     * @param token Token de confirmación enviado por correo electrónico.
     * @throws TokenInvalidoException si el token no existe en la base de datos.
     * @throws TokenExpiradoException si el token ha superado su fecha de expiración.
     */
    public void confirmarCuenta(String token) {
        TokenConfirmacion tc = tokenRepo.findByToken(token)
                .orElseThrow(() -> new TokenInvalidoException("Token de confirmación inválido"));

        if (tc.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new TokenExpiradoException("El token de confirmación ha expirado");
        }

        Usuario usuario = tc.getUsuario();
        usuario.setActivo(true);

        usuarioRepo.save(usuario);
        tokenRepo.delete(tc);
    }
}