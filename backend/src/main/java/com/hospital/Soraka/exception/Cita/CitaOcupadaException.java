package com.hospital.Soraka.exception.Cita;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CitaOcupadaException extends RuntimeException {
    public CitaOcupadaException(String mensaje) {
        super(mensaje);
    }
}