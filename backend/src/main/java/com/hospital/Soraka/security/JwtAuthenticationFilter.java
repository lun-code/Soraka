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
 * <p>
 * Este filtro se ejecuta una vez por request (hereda de OncePerRequestFilter)
 * y se encarga de:
 * <ul>
 *     <li>Extraer el JWT del header Authorization.</li>
 *     <li>Validar que el token sea correcto y no haya expirado.</li>
 *     <li>Establecer la autenticación en el contexto de seguridad de Spring.</li>
 * </ul>
 * <p>
 * Para rutas públicas (como login y confirmación de email),
 * este filtro se omite mediante {@link #shouldNotFilter(HttpServletRequest)}.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param jwtService Servicio encargado de generar y validar tokens JWT.
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
     * Define los endpoints que deben excluirse del filtro JWT.
     * <p>
     * Esto permite que rutas públicas como login, registro y confirmación
     * no requieran un token válido para ser accedidas.
     *
     * @param request Request HTTP entrante.
     * @return true si la ruta es pública y debe omitirse el filtro.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/confirmar");
    }

    /**
     * Método principal del filtro que valida el JWT de cada request.
     * <p>
     * Pasos:
     * <ol>
     *     <li>Extrae el token JWT del header Authorization.</li>
     *     <li>Si no hay token o no empieza con "Bearer ", pasa al siguiente filtro.</li>
     *     <li>Extrae el username/email del token.</li>
     *     <li>Si el usuario no está autenticado, lo carga de la base de datos.</li>
     *     <li>Valida que el token sea válido y pertenece al usuario.</li>
     *     <li>Si todo es correcto, establece la autenticación en el contexto de Spring Security.</li>
     *     <li>Continúa con la cadena de filtros.</li>
     * </ol>
     *
     * @param request      Request HTTP entrante.
     * @param response     Response HTTP.
     * @param filterChain  Cadena de filtros de Spring Security.
     * @throws ServletException cuando ocurre un error del servlet.
     * @throws IOException      cuando ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no hay token, dejamos pasar la request (puede ser pública)
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String email = jwtService.extractUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Carga el usuario desde la base de datos
                UserDetails user = userDetailsService.loadUserByUsername(email);

                // Valida que el token sea correcto y vigente
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
            // Si el token es inválido o expiró, no autenticamos.
            // Spring Security decide si la ruta requiere acceso o no.
            logger.warn("No se pudo procesar el token JWT: " + e.getMessage());
        }

        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}