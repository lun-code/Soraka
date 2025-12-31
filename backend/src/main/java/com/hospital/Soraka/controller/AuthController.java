package com.hospital.Soraka.controller;

import com.hospital.Soraka.dto.login.LoginRequestDTO;
import com.hospital.Soraka.dto.login.LoginResponseDTO;
import com.hospital.Soraka.dto.usuario.UsuarioPostDTO;
import com.hospital.Soraka.dto.usuario.UsuarioResponseDTO;
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
 * Controlador que maneja la autenticación de usuarios.
 * Permite realizar login y generar tokens JWT para acceder a endpoints protegidos.
 */
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructor con inyección de dependencias.
     * @param authenticationManager Bean de Spring Security para autenticar credenciales.
     * @param jwtService Servicio para generar y validar tokens JWT.
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint para login de usuarios.
     * Recibe email y password, autentica al usuario y devuelve un token JWT.
     *
     * @param dto Objeto con email y password del usuario.
     * @return LoginResponseDTO que contiene el token JWT.
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/auth/register")
    public UsuarioResponseDTO register(@RequestBody UsuarioPostDTO dto){
        return usuarioService.createUsuario(dto);
    }
}