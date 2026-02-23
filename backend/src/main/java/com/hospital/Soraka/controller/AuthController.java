package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.login.LoginRequestDTO;
import com.hospital.Soraka.dto.login.LoginResponseDTO;
import com.hospital.Soraka.dto.usuario.UsuarioPostDTO;
import com.hospital.Soraka.dto.usuario.UsuarioResponseDTO;
import com.hospital.Soraka.entity.Usuario;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST encargado de la autenticación y registro de usuarios.
 * <p>
 * Proporciona endpoints para:
 * <ul>
 *     <li>Iniciar sesión de usuarios y generar tokens JWT.</li>
 *     <li>Registrar nuevos usuarios (acceso exclusivo para ADMIN).</li>
 *     <li>Confirmar cuentas mediante tokens enviados por correo electrónico.</li>
 * </ul>
 * <p>
 * Integra Spring Security y JWT para proteger los endpoints según el rol del usuario
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
     * Constructor con inyección de dependencias para los servicios y componentes de seguridad.
     *
     * @param authenticationManager Componente de Spring Security para autenticar credenciales.
     * @param jwtService Servicio para la generación y validación de tokens JWT.
     * @param usuarioService Servicio encargado de la creación y manejo de usuarios.
     * @param confirmacionService Servicio encargado de la confirmación de cuentas por correo electrónico.
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
     * Endpoint para el inicio de sesión de usuarios.
     * <p>
     * Recibe un {@link LoginRequestDTO} con el email y la contraseña del usuario,
     * autentica mediante Spring Security y devuelve un token JWT en caso de éxito.
     *
     * @param dto DTO con email y contraseña del usuario.
     * @return {@link LoginResponseDTO} que contiene el token JWT generado.
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas
     * o la cuenta está desactivada (isActivo = false).
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", ((Usuario) userDetails).getNombre());
        claims.put("rol", ((Usuario) userDetails).getRol());
        claims.put("Email", ((Usuario) userDetails).getEmail());

        String token = jwtService.generateToken(userDetails, claims);

        return new LoginResponseDTO(token);
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * <p>
     * Solo accesible para usuarios con rol ADMIN. Delegará la creación del usuario
     * al {@link UsuarioService}, que validará la unicidad del email y persistirá
     * la información en la base de datos.
     * <p>
     * La cuenta se crea inicialmente inactiva (isActivo = false) y debe ser activada
     * mediante el token enviado por correo electrónico.
     *
     * @param dto DTO con los datos del usuario a registrar.
     * @return {@link UsuarioResponseDTO} con la información del usuario creado.
     * @throws com.hospital.Soraka.exception.Usuario.EmailYaEnUsoException si el email ya está registrado.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register")
    public UsuarioResponseDTO register(@RequestBody UsuarioPostDTO dto) {
        return usuarioService.createUsuario(dto);
    }

    /**
     * Endpoint público para confirmar la cuenta de un usuario mediante un token.
     * <p>
     * El usuario hace clic en un enlace enviado por correo electrónico, que incluye
     * un token único. {@link ConfirmacionService} valida el token y activa la cuenta
     * (isActivo = true) si el token es válido y no ha expirado.
     *
     * @param token Token de confirmación enviado por correo electrónico.
     * @return {@link ResponseEntity} con mensaje de éxito si la cuenta fue activada correctamente.
     * @throws RuntimeException si el token no existe o ha expirado.
     */
    @GetMapping("/confirmar")
    public ResponseEntity<String> confirmar(@RequestParam String token) {
        confirmacionService.confirmarCuenta(token);
        return ResponseEntity.ok("Cuenta activada correctamente");
    }
}