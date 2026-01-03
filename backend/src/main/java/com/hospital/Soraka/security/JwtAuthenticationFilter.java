package com.hospital.Soraka.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada request HTTP para validar el token JWT.
 * Se ejecuta una vez por request (OncePerRequestFilter) y establece
 * la autenticación en el contexto de seguridad de Spring.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor con inyección de dependencias.
     * @param jwtService Servicio para generar y validar tokens JWT.
     * @param userDetailsService Servicio para cargar usuarios desde la base de datos.
     */
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Método principal del filtro que valida el JWT de cada request.
     * Flujo:
     * 1. Obtiene el header Authorization de la request.
     * 2. Si no existe o no empieza con "Bearer ", pasa al siguiente filtro.
     * 3. Extrae el token JWT (quitando "Bearer ").
     * 4. Obtiene el username (email) del token usando JwtService.
     * 5. Carga el usuario desde la base de datos usando UserDetailsService.
     * 6. Crea un objeto Authentication y lo guarda en SecurityContextHolder.
     * 7. Continúa con la cadena de filtros.
     *
     * @param request Request HTTP entrante
     * @param response Response HTTP
     * @param filterChain Cadena de filtros
     * @throws ServletException cuando ocurre un error del servlet
     * @throws IOException cuando ocurre un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Obtiene el header Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no hay token o no empieza con "Bearer ", pasa al siguiente filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrae el token quitando "Bearer "
        String token = authHeader.substring(7);

        // Obtiene el email del token
        String email = jwtService.extractUsername(token);

        // Carga el usuario desde la base de datos
        UserDetails user = userDetailsService.loadUserByUsername(email);

        // Crea un objeto Authentication con el usuario y sus roles
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        // Establece la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}