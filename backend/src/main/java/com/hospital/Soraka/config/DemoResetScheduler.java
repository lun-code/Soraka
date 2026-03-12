package com.hospital.Soraka.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tarea programada que restaura la base de datos de demostración cada 30 minutos.
 *
 * <p>Usa queries nativas directamente sobre el EntityManager para evitar
 * problemas de sesión de Hibernate con entidades eliminadas en pasos anteriores.</p>
 *
 * <p><strong>Orden del reset:</strong></p>
 * <ol>
 *   <li>Elimina citas de médicos visitantes.</li>
 *   <li>Elimina citas no CONFIRMADAS de médicos demo.</li>
 *   <li>Elimina los médicos creados por visitantes.</li>
 *   <li>Elimina los tokens de confirmación de usuarios visitantes.</li>
 *   <li>Elimina los usuarios creados por visitantes.</li>
 *   <li>Elimina las especialidades creadas por visitantes.</li>
 *   <li>Restaura el estado demo completo.</li>
 * </ol>
 */
@Component
public class DemoResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(DemoResetScheduler.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataInitializer dataInitializer;

    @Scheduled(fixedDelay = 1_800_000)
    @Transactional
    public void resetearDemo() {
        log.info(">>> [DemoReset] Iniciando reset de la base de datos demo...");

        // 1a. Borrar citas de médicos visitantes
        em.createNativeQuery("""
            DELETE FROM citas
            WHERE medico_id NOT IN (
                SELECT id FROM medicos
                WHERE usuario_id IN (
                    SELECT id FROM usuarios
                    WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                )
            )
        """)
        .setParameter("e1", DataInitializer.EMAIL_ADMIN)
        .setParameter("e2", DataInitializer.EMAIL_MEDICO)
        .setParameter("e3", DataInitializer.EMAIL_MEDICO_PEDIATRIA)
        .setParameter("e4", DataInitializer.EMAIL_MEDICO_DERMATO)
        .setParameter("e5", DataInitializer.EMAIL_PACIENTE)
        .executeUpdate();
        log.info(">>> [DemoReset] Citas de médicos visitantes eliminadas.");

        // 1b. Borrar citas no CONFIRMADAS de médicos demo
        em.createNativeQuery("""
            DELETE FROM citas
            WHERE estado != 'CONFIRMADA'
            AND medico_id IN (
                SELECT id FROM medicos
                WHERE usuario_id IN (
                    SELECT id FROM usuarios
                    WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                )
            )
        """)
        .setParameter("e1", DataInitializer.EMAIL_ADMIN)
        .setParameter("e2", DataInitializer.EMAIL_MEDICO)
        .setParameter("e3", DataInitializer.EMAIL_MEDICO_PEDIATRIA)
        .setParameter("e4", DataInitializer.EMAIL_MEDICO_DERMATO)
        .setParameter("e5", DataInitializer.EMAIL_PACIENTE)
        .executeUpdate();
        log.info(">>> [DemoReset] Citas no confirmadas de médicos demo eliminadas.");

        // 2. Borrar médicos de visitantes
        em.createNativeQuery("""
            DELETE FROM medicos
            WHERE usuario_id NOT IN (
                SELECT id FROM usuarios
                WHERE email IN (:e1, :e2, :e3, :e4, :e5)
            )
        """)
        .setParameter("e1", DataInitializer.EMAIL_ADMIN)
        .setParameter("e2", DataInitializer.EMAIL_MEDICO)
        .setParameter("e3", DataInitializer.EMAIL_MEDICO_PEDIATRIA)
        .setParameter("e4", DataInitializer.EMAIL_MEDICO_DERMATO)
        .setParameter("e5", DataInitializer.EMAIL_PACIENTE)
        .executeUpdate();
        log.info(">>> [DemoReset] Médicos de visitantes eliminados.");

        // 3. Borrar tokens de confirmación de usuarios visitantes
        em.createNativeQuery("""
            DELETE FROM token_confirmacion
            WHERE usuario_id NOT IN (
                SELECT id FROM usuarios
                WHERE email IN (:e1, :e2, :e3, :e4, :e5)
            )
        """)
        .setParameter("e1", DataInitializer.EMAIL_ADMIN)
        .setParameter("e2", DataInitializer.EMAIL_MEDICO)
        .setParameter("e3", DataInitializer.EMAIL_MEDICO_PEDIATRIA)
        .setParameter("e4", DataInitializer.EMAIL_MEDICO_DERMATO)
        .setParameter("e5", DataInitializer.EMAIL_PACIENTE)
        .executeUpdate();
        log.info(">>> [DemoReset] Tokens de confirmación eliminados.");

        // 4. Borrar usuarios visitantes
        em.createNativeQuery("""
            DELETE FROM usuarios
            WHERE email NOT IN (:e1, :e2, :e3, :e4, :e5)
        """)
        .setParameter("e1", DataInitializer.EMAIL_ADMIN)
        .setParameter("e2", DataInitializer.EMAIL_MEDICO)
        .setParameter("e3", DataInitializer.EMAIL_MEDICO_PEDIATRIA)
        .setParameter("e4", DataInitializer.EMAIL_MEDICO_DERMATO)
        .setParameter("e5", DataInitializer.EMAIL_PACIENTE)
        .executeUpdate();
        log.info(">>> [DemoReset] Usuarios de visitantes eliminados.");

        // 5. Borrar especialidades de visitantes
        em.createNativeQuery("""
            DELETE FROM especialidades
            WHERE nombre NOT IN (:esp1, :esp2, :esp3)
        """)
        .setParameter("esp1", "Medicina General")
        .setParameter("esp2", "Pediatría")
        .setParameter("esp3", "Dermatología")
        .executeUpdate();
        log.info(">>> [DemoReset] Especialidades de visitantes eliminadas.");

        // 6. Limpiar caché de Hibernate
        em.clear();

        // 7. Restaurar estado demo
        dataInitializer.inicializarDemoData();
        log.info(">>> [DemoReset] Reset completado.");
    }
}