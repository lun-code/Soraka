package com.hospital.Soraka.exception.Medico;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MedicoNotFoundException extends RuntimeException {
    public MedicoNotFoundException(String mensaje) {
        super(mensaje);
    }
}