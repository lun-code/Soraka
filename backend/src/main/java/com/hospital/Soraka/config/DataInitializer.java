package com.hospital.Soraka.config;

import com.hospital.Soraka.entity.Cita;
import com.hospital.Soraka.entity.Especialidad;
import com.hospital.Soraka.entity.Medico;
import com.hospital.Soraka.entity.Usuario;
import com.hospital.Soraka.enums.EstadoCita;
import com.hospital.Soraka.enums.Rol;
import com.hospital.Soraka.repository.CitaRepository;
import com.hospital.Soraka.repository.EspecialidadRepository;
import com.hospital.Soraka.repository.MedicoRepository;
import com.hospital.Soraka.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Inicializa y restaura los datos de demostración de la aplicación.
 *
 * <p><strong>Cuentas demo:</strong></p>
 * <ul>
 *   <li>Admin    — admin.demo@soraka.com              / demo1234</li>
 *   <li>Médico   — medico.demo@soraka.com              / demo1234  (Medicina General)</li>
 *   <li>Médico   — medico.pediatria.demo@soraka.com   / demo1234  (Pediatría)</li>
 *   <li>Médico   — medico.dermato.demo@soraka.com     / demo1234  (Dermatología)</li>
 *   <li>Paciente — paciente.demo@soraka.com           / demo1234</li>
 * </ul>
 *
 * <p><strong>Especialidades demo:</strong> Medicina General, Pediatría, Dermatología.</p>
 */
@Component
public class DataInitializer implements ApplicationRunner {

    // ── Emails protegidos ────────────────────────────────────────────────────────
    public static final String EMAIL_ADMIN            = "admin.demo@soraka.com";
    public static final String EMAIL_MEDICO           = "medico.demo@soraka.com";
    public static final String EMAIL_MEDICO_PEDIATRIA = "medico.pediatria.demo@soraka.com";
    public static final String EMAIL_MEDICO_DERMATO   = "medico.dermato.demo@soraka.com";
    public static final String EMAIL_PACIENTE         = "paciente.demo@soraka.com";

    // ── Especialidades protegidas ────────────────────────────────────────────────
    public static final Set<String> ESPECIALIDADES_DEMO = Set.of(
            "Medicina General",
            "Pediatría",
            "Dermatología"
    );

    private static final String PASSWORD_DEMO       = "demo1234";
    private static final String FOTO_DEMO_MEDGEN    =
            "https://randomuser.me/api/portraits/men/32.jpg";
    private static final String FOTO_DEMO_PEDIATRIA =
            "https://randomuser.me/api/portraits/women/44.jpg";
    private static final String FOTO_DEMO_DERMATO   =
            "https://randomuser.me/api/portraits/men/4.jpg";

    @Autowired private UsuarioRepository      usuarioRepository;
    @Autowired private MedicoRepository       medicoRepository;
    @Autowired private EspecialidadRepository especialidadRepository;
    @Autowired private CitaRepository         citaRepository;
    @Autowired private PasswordEncoder        passwordEncoder;

    // ── ApplicationRunner ────────────────────────────────────────────────────────

    @Override
    public void run(ApplicationArguments args) {
        inicializarDemoData();
    }

    // ── Método público llamado por DemoResetScheduler ────────────────────────────

    @Transactional
    public void inicializarDemoData() {

        // 1. Especialidades demo
        Especialidad medGen    = upsertEspecialidad("Medicina General");
        Especialidad pediatria = upsertEspecialidad("Pediatría");
        Especialidad dermato   = upsertEspecialidad("Dermatología");

        // 2. Usuarios demo
        upsertUsuario(EMAIL_ADMIN,            "Administrador Demo",        Rol.ADMIN);
        Usuario uMedGen    = upsertUsuario(EMAIL_MEDICO,           "Dr. Demo Medicina General", Rol.MEDICO);
        Usuario uPediatria = upsertUsuario(EMAIL_MEDICO_PEDIATRIA, "Dra. Demo Pediatría",       Rol.MEDICO);
        Usuario uDermato   = upsertUsuario(EMAIL_MEDICO_DERMATO,   "Dr. Demo Dermatología",     Rol.MEDICO);
        Usuario paciente   = upsertUsuario(EMAIL_PACIENTE,         "Paciente Demo",             Rol.PACIENTE);

        // 3. Entidades Medico
        Medico medicoMedGen    = upsertMedico(uMedGen,    medGen,    FOTO_DEMO_MEDGEN);
        Medico medicoPediatria = upsertMedico(uPediatria, pediatria, FOTO_DEMO_PEDIATRIA);
        Medico medicoDermato   = upsertMedico(uDermato,   dermato,   FOTO_DEMO_DERMATO);

        // 4. Citas futuras (DISPONIBLES) para los próximos 7 días
        generarCitasFuturas(medicoMedGen);
        generarCitasFuturas(medicoPediatria);
        generarCitasFuturas(medicoDermato);

        // 5. Historial de citas pasadas (si aún no existe)
        generarHistorial(medicoMedGen,    paciente);
        generarHistorial(medicoPediatria, paciente);
        generarHistorial(medicoDermato,   paciente);
    }

    // ── Citas futuras ────────────────────────────────────────────────────────────

    /**
     * Genera citas DISPONIBLES de 30 min entre las 08:00 y las 15:00
     * para los próximos 7 días, omitiendo los slots que ya existan.
     */
    private void generarCitasFuturas(Medico medico) {
        LocalDate hoy       = LocalDate.now();
        LocalDate fechaFin  = hoy.plusDays(7);
        LocalDateTime ahora = LocalDateTime.now();

        for (LocalDate fecha = hoy.plusDays(1); !fecha.isAfter(fechaFin);
             fecha = fecha.plusDays(1)) {

            LocalDateTime slot = fecha.atTime(8, 0);
            LocalDateTime fin  = fecha.atTime(15, 0);

            while (slot.isBefore(fin)) {
                if (!citaRepository.existsByMedicoAndFechaHora(medico, slot)) {
                    Cita cita = new Cita();
                    cita.setMedico(medico);
                    cita.setFechaHora(slot);
                    cita.setEstado(EstadoCita.DISPONIBLE);
                    citaRepository.save(cita);
                }
                slot = slot.plusMinutes(30);
            }
        }
    }

    // ── Historial de citas pasadas ───────────────────────────────────────────────

    /**
     * Genera 8 citas pasadas por médico (4 REALIZADAS y 4 CADUCADAS),
     * distribuidas en los últimos 14 días, solo si no existen ya.
     * Las REALIZADAS tienen paciente asignado, las CADUCADAS no.
     */
    private void generarHistorial(Medico medico, Usuario paciente) {
        LocalDate hoy = LocalDate.now();

        // Slots pasados: 1 por día en los últimos 8 días a las 10:00
        for (int i = 1; i <= 8; i++) {
            LocalDateTime slot = hoy.minusDays(i).atTime(10, 0);

            if (!citaRepository.existsByMedicoAndFechaHora(medico, slot)) {
                Cita cita = new Cita();
                cita.setMedico(medico);
                cita.setFechaHora(slot);

                if (i % 2 == 0) {
                    // Par → REALIZADA (con paciente y motivo)
                    cita.setEstado(EstadoCita.REALIZADA);
                    cita.setPaciente(paciente);
                    cita.setMotivo("Consulta de revisión general");
                } else {
                    // Impar → CADUCADA (sin paciente)
                    cita.setEstado(EstadoCita.CADUCADA);
                }

                citaRepository.save(cita);
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private Especialidad upsertEspecialidad(String nombre) {
        return especialidadRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Especialidad nueva = new Especialidad();
                    nueva.setNombre(nombre);
                    return especialidadRepository.save(nueva);
                });
    }

    private Usuario upsertUsuario(String email, String nombre, Rol rol) {
        return usuarioRepository.findByEmail(email)
                .map(u -> {
                    u.setNombre(nombre);
                    u.setPassword(passwordEncoder.encode(PASSWORD_DEMO));
                    u.setActivo(true);
                    u.setRol(rol);
                    return usuarioRepository.save(u);
                })
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario(nombre, email,
                            passwordEncoder.encode(PASSWORD_DEMO), rol);
                    nuevo.setActivo(true);
                    return usuarioRepository.save(nuevo);
                });
    }

    private Medico upsertMedico(Usuario usuario, Especialidad especialidad, String foto) {
        return medicoRepository.findByUsuarioId(usuario.getId())
                .map(m -> {
                    m.setEspecialidad(especialidad);
                    m.setUrlFoto(foto);
                    return medicoRepository.save(m);
                })
                .orElseGet(() -> medicoRepository.save(
                        new Medico(usuario, especialidad, foto)));
    }
}