@Component
public class DemoResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(DemoResetScheduler.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataInitializer dataInitializer;

    // Aumentamos el delay a 1 hora o lo que prefieras, 30 min es muy agresivo si hay tráfico
    @Scheduled(fixedDelay = 1_800_000) 
    public void resetearDemo() {
        log.info(">>> [DemoReset] Iniciando reset seguro de la base de datos...");

        try {
            // Ejecutamos cada paso en su propia mini-transacción para no bloquear la DB
            ejecutarPaso("Borrar citas visitantes", """
                DELETE FROM citas WHERE medico_id NOT IN (
                    SELECT id FROM medicos WHERE usuario_id IN (
                        SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                    )
                )""");

            ejecutarPaso("Borrar citas no confirmadas", """
                DELETE FROM citas WHERE estado != 'CONFIRMADA' AND medico_id IN (
                    SELECT id FROM medicos WHERE usuario_id IN (
                        SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                    )
                )""");

            ejecutarPaso("Borrar médicos visitantes", """
                DELETE FROM medicos WHERE usuario_id NOT IN (
                    SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                )""");

            ejecutarPaso("Borrar tokens", """
                DELETE FROM token_confirmacion WHERE usuario_id NOT IN (
                    SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                )""");

            ejecutarPaso("Borrar usuarios visitantes", """
                DELETE FROM usuarios WHERE email NOT IN (:e1, :e2, :e3, :e4, :e5)
                """);

            // Para las especialidades usamos parámetros específicos
            ejecutarBorradoEspecialidades();

            // Limpieza de caché y restauración
            em.clear();
            dataInitializer.inicializarDemoData();
            
            log.info(">>> [DemoReset] Reset completado exitosamente.");

        } catch (Exception e) {
            log.error(">>> [DemoReset] CRITICAL ERROR durante el reset: {}", e.getMessage());
            // Al capturar aquí, la app NO se cae y Railway no da 502
        }
    }

    @Transactional
    protected void ejecutarPaso(String nombre, String sql) {
        try {
            em.createNativeQuery(sql)
                .setParameter("e1", DataInitializer.EMAIL_ADMIN)
                .setParameter("e2", DataInitializer.EMAIL_MEDICO)
                .setParameter("e3", DataInitializer.EMAIL_MEDICO_PEDIATRIA)
                .setParameter("e4", DataInitializer.EMAIL_MEDICO_DERMATO)
                .setParameter("e5", DataInitializer.EMAIL_PACIENTE)
                .executeUpdate();
            log.info(">>> [DemoReset] Paso completado: {}", nombre);
        } catch (Exception e) {
            log.warn(">>> [DemoReset] No se pudo completar el paso {}: {}", nombre, e.getMessage());
        }
    }

    @Transactional
    protected void ejecutarBorradoEspecialidades() {
        try {
            em.createNativeQuery("""
                DELETE FROM especialidades 
                WHERE nombre NOT IN (:esp1, :esp2, :esp3)
            """)
            .setParameter("esp1", "Medicina General")
            .setParameter("esp2", "Pediatría")
            .setParameter("esp3", "Dermatología")
            .executeUpdate();
            log.info(">>> [DemoReset] Especialidades limpias.");
        } catch (Exception e) {
            log.warn(">>> [DemoReset] Error borrando especialidades: {}", e.getMessage());
        }
    }
}