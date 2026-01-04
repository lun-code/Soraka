package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.login.LoginRequestDTO;
import com.hospital.Soraka.dto.login.LoginResponseDTO;
import com.hospital.Soraka.dto.usuario.UsuarioPostDTO;
import com.hospital.Soraka.dto.usuario.UsuarioResponseDTO;
import com.hospital.Soraka.exception.Usuario.EmailYaEnUsoException;
import com.hospital.Soraka.security.JwtService;
import com.hospital.Soraka.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de la autenticación y registro de usuarios.
 * <p>
 * Proporciona endpoints para:
 * <ul>
 *     <li>Login de usuarios y generación de tokens JWT para acceso a recursos protegidos.</li>
 *     <li>Registro de nuevos usuarios (solo accesible para administradores).</li>
 * </ul>
 */
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param authenticationManager Bean de Spring Security para autenticar credenciales.
     * @param jwtService Servicio para generar y validar tokens JWT.
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint para login de usuarios.
     * <p>
     * Recibe las credenciales del usuario (email y password), las autentica usando
     * Spring Security y devuelve un token JWT si la autenticación es exitosa.
     * <p>
     * Este token puede ser utilizado en el header Authorization de futuras solicitudes
     * para acceder a endpoints protegidos.
     *
     * @param dto DTO que contiene el email y la contraseña del usuario.
     * @return {@link LoginResponseDTO} que contiene el token JWT.
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas.
     */
    @PostMapping("/auth/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {

        try{
            // Autentica las credenciales usando Spring Security
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            // Obtiene el usuario autenticado
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            // Genera un token JWT con la información del usuario
            String token = jwtService.generateToken(userDetails);

            // Devuelve el token al cliente
            return new LoginResponseDTO(token);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Autowired
    UsuarioService usuarioService;

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     * <p>
     * Solo accesible para usuarios con rol ADMIN.
     * <p>
     * Delegará la creación del usuario al {@link UsuarioService}, que validará
     * datos como email único y persistirá el usuario en la base de datos.
     *
     * @param dto DTO con los datos del usuario a registrar.
     * @return {@link UsuarioResponseDTO} con los datos del usuario creado.
     * @throws EmailYaEnUsoException si el email ya está registrado.
     * @throws org.springframework.security.access.AccessDeniedException si el usuario autenticado no tiene rol ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/auth/register")
    public UsuarioResponseDTO register(@RequestBody UsuarioPostDTO dto){
        return usuarioService.createUsuario(dto);
    }
}