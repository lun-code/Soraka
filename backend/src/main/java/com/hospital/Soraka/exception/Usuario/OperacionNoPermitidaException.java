package com.hospital.Soraka.exception.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta realizar una operación no permitida
 * sobre una cuenta protegida, como eliminar o modificar las cuentas de demostración.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class OperacionNoPermitidaException extends RuntimeException {
    public OperacionNoPermitidaException(String message) {
        super(message);
    }
}