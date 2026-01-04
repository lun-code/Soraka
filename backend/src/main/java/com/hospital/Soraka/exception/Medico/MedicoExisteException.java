package com.hospital.Soraka.exception.Medico;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MedicoExisteException extends RuntimeException {
    public MedicoExisteException(String message) {
        super(message);
    }
}