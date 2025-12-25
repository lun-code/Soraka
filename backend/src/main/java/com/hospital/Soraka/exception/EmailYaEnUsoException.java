package com.hospital.Soraka.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailYaEnUsoException extends RuntimeException {
    public EmailYaEnUsoException(String mensaje) {
        super(mensaje);
    }
}