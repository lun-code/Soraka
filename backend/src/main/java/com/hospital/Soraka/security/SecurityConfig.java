package com.hospital.Soraka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security.
 * Define cifrado de contraseñas, filtros JWT y manejo de endpoints protegidos.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean para encriptar contraseñas con BCrypt.
     * Se utiliza tanto en registro de usuarios como en validación de login.
     * @return PasswordEncoder que aplica BCrypt hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Filtro JWT que intercepta cada request para validar el token.
     * Se inyecta automáticamente con @Autowired.
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configuración de la cadena de seguridad HTTP.
     * Define qué endpoints son públicos y cuáles requieren autenticación.
     *
     * @param http HttpSecurity para configurar la seguridad.
     * @return SecurityFilterChain configurada.
     * @throws Exception si hay error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactiva CSRF, útil para APIs REST (no hay formularios HTML)
                .csrf(AbstractHttpConfigurer::disable)
                // Configura sesiones como STATELESS (sin sesión HTTP)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Define permisos de acceso a endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // login y registro abiertos
                        .anyRequest().authenticated()            // resto requiere autenticación
                )
                // Añade el filtro JWT antes del filtro de login de Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean de AuthenticationManager moderno.
     * Spring Security lo construye usando el UserDetailsService y PasswordEncoder
     * registrados en el contexto.
     *
     * @param config AuthenticationConfiguration que contiene la configuración de autenticación.
     * @return AuthenticationManager listo para autenticar usuarios.
     * @throws Exception si falla la creación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}