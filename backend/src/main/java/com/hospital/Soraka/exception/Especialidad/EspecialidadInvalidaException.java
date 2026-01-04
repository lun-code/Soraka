package com.hospital.Soraka.exception.Especialidad;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EspecialidadInvalidaException extends RuntimeException {
    public EspecialidadInvalidaException(String message) {
        super(message);
    }
}