package com.hospital.Soraka.exception.Confirmacion;

public class TokenExpiradoException extends RuntimeException {
    public TokenExpiradoException(String message) {
        super(message);
    }
}