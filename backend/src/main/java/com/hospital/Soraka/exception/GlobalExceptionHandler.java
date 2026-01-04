package com.hospital.Soraka.exception;

import com.hospital.Soraka.exception.Especialidad.EspecialidadExisteException;
import com.hospital.Soraka.exception.Especialidad.EspecialidadInvalidaException;
import com.hospital.Soraka.exception.Especialidad.EspecialidadNotFoundException;
import com.hospital.Soraka.exception.Medico.MedicoExisteException;
import com.hospital.Soraka.exception.Usuario.*;
import com.hospital.Soraka.exception.Medico.MedicoNotFoundException;
import com.hospital.Soraka.exception.Cita.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejo global de excepciones para la API.
 * Centraliza la conversión de excepciones de negocio a respuestas HTTP.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // =======================
    // Excepciones de Usuario
    // =======================
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarUsuarioNoEncontrado(UsuarioNotFoundException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailYaEnUsoException.class)
    public ResponseEntity<Map<String, Object>> manejarEmailEnUso(EmailYaEnUsoException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsuarioAsignadoException.class)
    public ResponseEntity<Map<String, Object>> manejarUsuarioAsignado(UsuarioAsignadoException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RolNoValidoException.class)
    public ResponseEntity<Map<String, Object>> manejarRolNoValido(RolNoValidoException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CambioRolMedicoNoPermitidoException.class)
    public ResponseEntity<Map<String, Object>> manejarCambioRolMedicoNoPermitido(CambioRolMedicoNoPermitidoException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // =======================
    // Excepciones de Medico
    // =======================
    @ExceptionHandler(MedicoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarMedicoNoEncontrado(MedicoNotFoundException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MedicoExisteException.class)
    public ResponseEntity<Map<String, Object>> manejarMedicoExiste(MedicoExisteException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // =======================
    // Excepciones de Cita
    // =======================
    @ExceptionHandler(CitaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarCitaNoEncontrada(CitaNotFoundException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CitaNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> manejarCitaNoDisponible(CitaNoDisponibleException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CitaOcupadaException.class)
    public ResponseEntity<Map<String, Object>> manejarCitaOcupada(CitaOcupadaException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CitaNoCancelableException.class)
    public ResponseEntity<Map<String, Object>> manejarCitaNoCancelable(CitaNoCancelableException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // =======================
    // Excepciones de Especialidad
    // =======================
    @ExceptionHandler(EspecialidadNotFoundException.class)
    public ResponseEntity<Map<String, Object>>  manejarEspecialidadNoEncontrado(EspecialidadNotFoundException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EspecialidadInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarEspecialidadInvalida(EspecialidadInvalidaException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EspecialidadExisteException.class)
    public ResponseEntity<Map<String, Object>>  manejarEspecialidadExiste(EspecialidadExisteException ex) {
        return construirResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // =======================
    // Excepciones de seguridad
    // =======================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> manejarAccessDenied(AccessDeniedException ex) {
        return construirResponse("No tienes permisos para realizar esta acción", HttpStatus.FORBIDDEN);
    }

    // =======================
    // Excepciones de concurrencia / Optimistic Lock
    // =======================
    @ExceptionHandler(jakarta.persistence.OptimisticLockException.class)
    public ResponseEntity<Map<String, Object>> manejarOptimisticLock() {
        return construirResponse("La entidad ha sido modificada por otro usuario. Intentalo de nuevo.", HttpStatus.CONFLICT);
    }

    // =======================
    // Excepciones generales
    // =======================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarOtrosErrores(Exception ex) {
        ex.printStackTrace(); // opcional: log para debugging
        return construirResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // =======================
    // Método helper para construir la respuesta
    // =======================
    private ResponseEntity<Map<String, Object>> construirResponse(String mensaje, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("mensaje", mensaje);
        body.put("status", status.value());
        return new ResponseEntity<>(body, status);
    }
}