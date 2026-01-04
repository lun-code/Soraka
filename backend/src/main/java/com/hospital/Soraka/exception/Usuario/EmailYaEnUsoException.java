package com.hospital.Soraka.exception.Usuario;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailYaEnUsoException extends RuntimeException {
    public EmailYaEnUsoException(String mensaje) {
        super(mensaje);
    }
}