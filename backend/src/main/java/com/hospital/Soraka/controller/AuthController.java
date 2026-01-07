package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.login.LoginRequestDTO;
import com.hospital.Soraka.dto.login.LoginResponseDTO;
import com.hospital.Soraka.dto.usuario.UsuarioPostDTO;
import com.hospital.Soraka.dto.usuario.UsuarioResponseDTO;
import com.hospital.Soraka.service.ConfirmacionService;
import com.hospital.Soraka.service.UsuarioService;
import com.hospital.Soraka.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de la autenticación y registro de usuarios.
 * <p>
 * Este controlador expone endpoints para:
 * <ul>
 *     <li>Login de usuarios y emisión de tokens JWT.</li>
 *     <li>Registro de nuevos usuarios (solo ADMIN).</li>
 *     <li>Confirmación de cuenta mediante token enviado por email.</li>
 * </ul>
 *
 * Se integra con Spring Security y JWT para proteger los endpoints según el rol del usuario
 * y el estado de la cuenta.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final ConfirmacionService confirmacionService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param authenticationManager Bean de Spring Security para autenticar credenciales.
     * @param jwtService Servicio para generar y validar tokens JWT.
     * @param usuarioService Servicio que maneja la creación de usuarios.
     * @param confirmacionService Servicio que maneja la confirmación de cuentas por email.
     */
    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioService usuarioService,
                          ConfirmacionService confirmacionService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.confirmacionService = confirmacionService;
    }

    /**
     * Endpoint para login de usuarios.
     * <p>
     * Recibe un DTO con email y contraseña, autentica al usuario usando Spring Security
     * y devuelve un JWT si la autenticación es exitosa.
     *
     * @param dto DTO con email y contraseña del usuario.
     * @return LoginResponseDTO que contiene el token JWT.
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas
     * o la cuenta no está activada (isActivo = false).
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(token);
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * <p>
     * Solo accesible para usuarios con rol ADMIN.
     * Delegará la creación del usuario al {@link UsuarioService}, que validará
     * que el email sea único y persistirá el usuario en la base de datos.
     * <p>
     * La cuenta se crea inactiva (isActivo = false) y se debe activar
     * mediante el token enviado por email.
     *
     * @param dto DTO con los datos del usuario a registrar.
     * @return UsuarioResponseDTO con los datos del usuario creado.
     * @throws com.hospital.Soraka.exception.Usuario.EmailYaEnUsoException si el email ya está registrado.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register")
    public UsuarioResponseDTO register(@RequestBody UsuarioPostDTO dto) {
        return usuarioService.createUsuario(dto);
    }

    /**
     * Endpoint público para confirmar la cuenta de un usuario mediante token.
     * <p>
     * El usuario hace clic en el enlace enviado por email, que incluye el token único.
     * El servicio {@link ConfirmacionService} valida el token y activa la cuenta
     * (isActivo = true) si el token es válido y no ha expirado.
     *
     * @param token Token de confirmación enviado por email.
     * @return ResponseEntity con mensaje de éxito si la cuenta se activó correctamente.
     * @throws RuntimeException si el token no existe o ha expirado.
     */
    @GetMapping("/confirmar")
    public ResponseEntity<String> confirmar(@RequestParam String token) {
        confirmacionService.confirmarCuenta(token);
        return ResponseEntity.ok("Cuenta activada correctamente");
    }
}