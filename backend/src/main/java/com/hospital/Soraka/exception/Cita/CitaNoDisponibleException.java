package com.hospital.Soraka.exception.Cita;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CitaNoDisponibleException extends RuntimeException {
    public CitaNoDisponibleException(String message) {
        super(message);
    }
}