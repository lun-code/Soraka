package com.hospital.Soraka.exception.Especialidad;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EspecialidadNotFoundException extends RuntimeException {
    public EspecialidadNotFoundException(String mensaje) {
        super(mensaje);
    }
}