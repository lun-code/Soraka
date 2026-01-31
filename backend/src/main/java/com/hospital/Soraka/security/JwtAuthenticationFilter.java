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
     * * Modificaciones para robustez:
     * 1. Captura excepciones de tokens expirados o malformados para no bloquear rutas públicas.
     * 2. Utiliza isTokenValid para asegurar la integridad del usuario antes de autenticar.
     *
     * @param request  Request HTTP entrante
     * @param response Response HTTP
     * @param filterChain Cadena de filtros de Spring Security
     * @throws ServletException cuando ocurre un error del servlet
     * @throws IOException      cuando ocurre un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Obtiene el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", pasamos al siguiente filtro (acceso anónimo)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // 3. Extrae el username/email del token
            final String email = jwtService.extractUsername(token);

            // 4. Si hay email y el usuario no está ya autenticado en el contexto
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Carga los detalles del usuario desde la base de datos
                UserDetails user = userDetailsService.loadUserByUsername(email);

                // 5. Valida que el token sea vigente y pertenezca al usuario
                if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );

                    // Guarda la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            // 6. Si el token es inválido o expiró, no hacemos nada.
            // Simplemente no se establece la autenticación.
            // Spring Security decidirá en SecurityConfig si la ruta requiere permiso o no.
            logger.warn("No se pudo procesar el token JWT: " + e.getMessage());
        }

        // 7. Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}