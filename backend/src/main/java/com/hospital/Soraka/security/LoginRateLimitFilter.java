package com.hospital.Soraka.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting para el endpoint de login.
 *
 * <p>
 * Limita el número de intentos de login por IP a 5 intentos
 * cada 1 minuto, protegiéndose frente a ataques de fuerza bruta.
 * </p>
 *
 * <p>
 * Cada IP tiene su propio {@link Bucket} con capacidad de 5 tokens
 * que se recargan completamente cada 60 segundos.
 * Si la IP agota sus tokens, se responde con HTTP 429 Too Many Requests.
 * </p>
 */
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    /**
     * Mapa que almacena un bucket por IP.
     * Se usa {@link ConcurrentHashMap} para garantizar thread-safety.
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Crea o recupera el bucket asociado a una IP.
     * <p>
     * Configuración: 5 intentos máximos, recarga completa cada 60 segundos.
     *
     * @param ip Dirección IP del cliente.
     * @return {@link Bucket} asociado a la IP.
     */
    private Bucket getBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build());
    }

    /**
     * Intercepta únicamente las peticiones POST a {@code /auth/login}.
     * <p>
     * Si la IP tiene tokens disponibles, consume uno y deja pasar la petición.
     * Si no quedan tokens, responde con 429 Too Many Requests.
     *
     * @param request     Request HTTP entrante.
     * @param response    Response HTTP.
     * @param filterChain Cadena de filtros.
     * @throws ServletException cuando ocurre un error del servlet.
     * @throws IOException      cuando ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Bucket bucket = getBucket(ip);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\": \"Demasiados intentos. Espera 1 minuto antes de volver a intentarlo.\"}"
            );
        }
    }

    /**
     * Solo aplica el filtro a POST /auth/login.
     *
     * @param request Request HTTP entrante.
     * @return true si debe omitirse el filtro.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(request.getRequestURI().equals("/auth/login")
                && request.getMethod().equalsIgnoreCase("POST"));
    }
}