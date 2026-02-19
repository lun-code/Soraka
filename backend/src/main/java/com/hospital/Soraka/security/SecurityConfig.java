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
 * Configuración principal de seguridad de la aplicación.
 *
 * <p>
 * Define:
 * <ul>
 *     <li>Codificador de contraseñas.</li>
 *     <li>Política de sesiones (stateless para JWT).</li>
 *     <li>Reglas de autorización por endpoint.</li>
 *     <li>Jerarquía de roles.</li>
 *     <li>Registro del filtro JWT en la cadena de seguridad.</li>
 * </ul>
 * </p>
 *
 * <p>
 * La aplicación utiliza autenticación basada en JWT, por lo que no mantiene
 * sesiones en el servidor.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /** Rol con privilegios máximos dentro del sistema. */
    public static final String ROLE_ADMIN = "ADMIN";

    /** Rol correspondiente a los médicos del sistema. */
    public static final String ROLE_MEDICO = "MEDICO";

    /** Rol correspondiente a los pacientes del sistema. */
    public static final String ROLE_PACIENTE = "PACIENTE";

    /**
     * Filtro encargado de validar el JWT en cada petición
     * y establecer la autenticación en el contexto de seguridad.
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Define el codificador de contraseñas utilizado para
     * almacenar y validar credenciales.
     *
     * @return instancia de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * <p>
     * Se desactiva CSRF por tratarse de una API stateless,
     * se establece política de sesión sin estado y se definen
     * las reglas de autorización por entidad y endpoint.
     * </p>
     *
     * @param http configuración HTTP de Spring Security
     * @return {@link SecurityFilterChain} configurada
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {

                    // -------------------------
                    // ENTIDAD: AUTH
                    // -------------------------
                    auth
                            .requestMatchers("/auth/login").permitAll()
                            .requestMatchers("/auth/confirmar/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/auth/register")
                            .hasAuthority(ROLE_MEDICO);

                    // -------------------------
                    // ENTIDAD: USUARIOS
                    // -------------------------
                    auth
                            .requestMatchers(HttpMethod.GET, "/api/usuarios/publico").permitAll()
                            .requestMatchers("/usuarios/**").authenticated();

                    // -------------------------
                    // ENTIDAD: MEDICOS
                    // -------------------------
                    auth
                            .requestMatchers(HttpMethod.GET, "/api/medicos/publicos").permitAll()
                            .requestMatchers("/medicos/**").authenticated();

                    // -------------------------
                    // ENTIDAD: ESPECIALIDADES
                    // -------------------------
                    auth
                            .requestMatchers(HttpMethod.GET, "/api/especialidades/**").permitAll()
                            .requestMatchers("/especialidades/**").authenticated();

                    // -------------------------
                    // ENTIDAD: CITAS
                    // -------------------------
                    auth
                            .requestMatchers("/citas/**").authenticated();

                    // -------------------------
                    // ENTIDAD: UPLOADS / ARCHIVOS
                    // -------------------------
                    auth
                            .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll();

                    // Cualquier otro endpoint requiere autenticación
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Expone el {@link AuthenticationManager} proporcionado por
     * la configuración interna de Spring Security.
     *
     * @param config configuración de autenticación
     * @return {@link AuthenticationManager} utilizado en el proceso de login
     * @throws Exception si ocurre un error al obtener el manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define la jerarquía de roles del sistema.
     *
     * <p>
     * Jerarquía establecida:
     * <pre>
     * ADMIN   > MEDICO
     * MEDICO  > PACIENTE
     * </pre>
     *
     * Esto implica que un ADMIN hereda permisos de MEDICO y PACIENTE,
     * y un MEDICO hereda permisos de PACIENTE.
     * </p>
     *
     * @return {@link RoleHierarchy} configurada
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(String.format("%s > %s \n %s > %s",
                ROLE_ADMIN, ROLE_MEDICO,
                ROLE_MEDICO, ROLE_PACIENTE));
        return hierarchy;
    }
}