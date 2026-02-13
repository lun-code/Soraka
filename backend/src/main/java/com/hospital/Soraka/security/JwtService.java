package com.hospital.Soraka.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Servicio encargado de la generación y validación de tokens JWT.
 * <p>
 * Proporciona métodos para:
 * <ul>
 *     <li>Generar tokens JWT para usuarios autenticados.</li>
 *     <li>Extraer información del token, como el username.</li>
 *     <li>Validar la vigencia y autenticidad del token.</li>
 * </ul>
 * <p>
 * Integra la biblioteca jjwt para manejo de JWT y utiliza una clave secreta
 * configurada mediante propiedades de Spring.
 */
@Service
public class JwtService {

    /**
     * Clave secreta utilizada para firmar los tokens.
     * <p>
     * En entornos de producción, debe ser larga y almacenada de forma segura
     * (por ejemplo, en variables de entorno).
     */
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    /**
     * Genera un token JWT para un usuario autenticado.
     *
     * @param user Usuario autenticado, que contiene username y roles.
     * @param claims Mapa de claims adicionales a incluir en el token.
     * @return Token JWT como {@link String}.
     */
    public String generateToken(UserDetails user, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    /**
     * Extrae el username (subject) contenido en un token JWT.
     *
     * @param token JWT enviado por el cliente.
     * @return Username del usuario contenido en el token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida si un token JWT es válido para un usuario determinado.
     * <p>
     * Comprueba que el username del token coincida con el del {@link UserDetails}
     * y que el token no haya expirado.
     *
     * @param token JWT enviado por el cliente.
     * @param userDetails Información del usuario cargada desde la base de datos.
     * @return {@code true} si el token es válido y vigente, {@code false} en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si un token JWT ha expirado.
     * <p>
     * Compara la fecha de expiración contenida en el token con la fecha actual.
     *
     * @param token JWT a verificar.
     * @return {@code true} si la fecha de expiración es anterior a la actual.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     * <p>
     * Utiliza el parser de Jwts configurado con la clave secreta para acceder a las claims.
     *
     * @param token JWT del cual extraer la fecha de expiración.
     * @return {@link Date} con la fecha de expiración del token.
     */
    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}