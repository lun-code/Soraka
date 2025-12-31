package com.hospital.Soraka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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

                        // Endpoints públicos

                        // Login
                        .requestMatchers("/auth/login").permitAll()


                        // Endpoints protegidos por autenticación o rol

                        // Registro
                        .requestMatchers(HttpMethod.POST, "/auth/register").hasAuthority("MEDICO")

                        // Usuarios
                        .requestMatchers("/usuarios/**").authenticated()

                        // Medicos
                        .requestMatchers("/medicos/**").authenticated()

                        // Citas
                        .requestMatchers("/citas/**").authenticated()

                        // Especialidades
                        .requestMatchers("/especialidades/**").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
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

    /**
     * Bean que define la jerarquía de roles para la aplicación.
     * <p>
     * Permite que Spring Security interprete que ciertos roles heredan
     * automáticamente los privilegios de otros roles. Por ejemplo:
     * <ul>
     *     <li>ADMIN > MEDICO: un ADMIN tiene todos los privilegios de un MEDICO.</li>
     *     <li>MEDICO > PACIENTE: un MEDICO tiene todos los privilegios de un PACIENTE.</li>
     * </ul>
     * Esto se aplica automáticamente en los checks de autorización,
     * como hasRole() o hasAuthority().
     *
     * @return RoleHierarchy configurada con la jerarquía de roles definida.
     */
    @Bean
    public RoleHierarchy roleHierarchy() {

        // Se crea la implementación de la jerarquía de roles
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();

        // Se define la jerarquía: ADMIN > MEDICO, MEDICO > PACIENTE
        hierarchy.setHierarchy("ADMIN > MEDICO \n MEDICO > PACIENTE");

        // Se devuelve el bean para que Spring Security lo use
        return hierarchy;
    }
}