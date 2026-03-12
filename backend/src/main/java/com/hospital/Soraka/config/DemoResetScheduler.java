package com.hospital.Soraka.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class DemoResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(DemoResetScheduler.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Scheduled(fixedDelay = 1800000)
    public void resetearDemo() {
        log.info(">>> [DemoReset] Iniciando reset seguro de la base de datos...");

        try {
            ejecutarPasoSeguro("Borrar citas visitantes", """
                DELETE FROM citas WHERE medico_id NOT IN (
                    SELECT id FROM medicos WHERE usuario_id IN (
                        SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                    )
                )""");

            ejecutarPasoSeguro("Borrar citas no confirmadas", """
                DELETE FROM citas WHERE estado != 'CONFIRMADA' AND medico_id IN (
                    SELECT id FROM medicos WHERE usuario_id IN (
                        SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                    )
                )""");

            ejecutarPasoSeguro("Borrar médicos visitantes", """
                DELETE FROM medicos WHERE usuario_id NOT IN (
                    SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                )""");

            ejecutarPasoSeguro("Borrar tokens", """
                DELETE FROM token_confirmacion WHERE usuario_id NOT IN (
                    SELECT id FROM usuarios WHERE email IN (:e1, :e2, :e3, :e4, :e5)
                )""");

            ejecutarPasoSeguro("Borrar usuarios visitantes", """
                DELETE FROM usuarios WHERE email NOT IN (:e1, :e2, :e3, :e4, :e5)
                """);

            // Borrado de especialidades
            transactionTemplate.execute(status -> {
                return em.createNativeQuery("""
                    DELETE FROM especialidades 
                    WHERE nombre NOT IN ('Medicina General', 'Pediatría', 'Dermatología')
                """).executeUpdate();
            });

            // Limpieza y restauración
            transactionTemplate.execute(status -> {
                em.clear();
                dataInitializer.inicializarDemoData();
                return null;
            });

            log.info(">>> [DemoReset] Reset completado con éxito.");

        } catch (Exception e) {
            log.error(">>> [DemoReset] Error durante el reset: {}", e.getMessage());
        }
    }

    private void ejecutarPasoSeguro(String nombre, String sql) {
        try {
            transactionTemplate.execute(status -> {
                return em.createNativeQuery(sql)
                    .setParameter("e1", DataInitializer.EMAIL_ADMIN)
                    .setParameter("e2", DataInitializer.EMAIL_MEDICO)
                    .setParameter("e3", DataInitializer.EMAIL_MEDICO_PEDIATRIA)
                    .setParameter("e4", DataInitializer.EMAIL_MEDICO_DERMATO)
                    .setParameter("e5", DataInitializer.EMAIL_PACIENTE)
                    .executeUpdate();
            });
            log.info(">>> [DemoReset] OK: {}", nombre);
        } catch (Exception e) {
            log.warn(">>> [DemoReset] Saltado {}: {}", nombre, e.getMessage());
        }
    }
}