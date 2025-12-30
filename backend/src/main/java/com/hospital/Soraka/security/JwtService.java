package com.hospital.Soraka.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

// Servicio encargado de generar y validar tokens JWT
@Service
public class JwtService {

    // Clave secreta utilizada para firmar los tokens
    // En proyectos reales debería ser mucho más larga y almacenada de forma segura (env vars)
    private final String SECRET_KEY = "SECRETKEY";

    /**
     * Genera un token JWT para un usuario
     * @param user Usuario autenticado (contiene username y roles)
     * @return token JWT como String
     */
    public String generateToken(UserDetails user) {
        return Jwts.builder()
                // Subject: normalmente el identificador del usuario (email o username)
                .setSubject(user.getUsername())
                // Fecha de emisión del token
                .setIssuedAt(new Date())
                // Fecha de expiración (1 hora en este ejemplo)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                // Firmado con la clave secreta usando HMAC SHA
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                // Compila el token y lo devuelve como String
                .compact();
    }

    /**
     * Extrae el username (subject) del token JWT
     * @param token JWT enviado por el cliente
     * @return username del usuario
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                // Se establece la clave secreta para validar la firma del token
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                // Parseamos el token y obtenemos las claims
                .parseClaimsJws(token)
                .getBody()
                // Devolvemos el subject, que en este caso es el username/email
                .getSubject();
    }
}