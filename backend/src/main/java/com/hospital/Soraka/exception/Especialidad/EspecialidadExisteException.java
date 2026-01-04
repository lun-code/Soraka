package com.hospital.Soraka.exception.Especialidad;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EspecialidadExisteException extends RuntimeException {
    public EspecialidadExisteException(String message) {
        super(message);
    }
}