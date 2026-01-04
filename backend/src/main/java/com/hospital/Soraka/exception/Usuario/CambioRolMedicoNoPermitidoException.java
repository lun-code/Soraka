package com.hospital.Soraka.exception.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CambioRolMedicoNoPermitidoException extends RuntimeException {
    public CambioRolMedicoNoPermitidoException(String message) {
        super(message);
    }
}