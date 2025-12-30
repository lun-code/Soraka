package com.hospital.Soraka.security;

import com.hospital.Soraka.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para cargar usuarios desde la base de datos
 * usando Spring Security. Implementa UserDetailsService.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * @param usuarioRepository Repositorio para acceder a los usuarios.
     */
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su email (username en nuestro caso) para autenticación.
     * Flujo:
     * 1. Recibe un email como parámetro (username).
     * 2. Busca el usuario en la base de datos usando UsuarioRepository.
     * 3. Si se encuentra, devuelve un objeto UserDetails.
     * 4. Si no se encuentra, lanza UsernameNotFoundException.
     *
     * @param email El email del usuario que intenta autenticarse.
     * @return UserDetails del usuario.
     * @throws UsernameNotFoundException Si no se encuentra el usuario.
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
    }
}