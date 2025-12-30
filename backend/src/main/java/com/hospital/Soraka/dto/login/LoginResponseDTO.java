package com.hospital.Soraka.dto.login;

public class LoginResponseDTO {

    // ATRIBUTOS
    private String token;


    // CONSTRUCTORES
    public LoginResponseDTO(String token) {
        this.token = token;
    }


    // GETTERS Y SETTERS
    public String getToken() {
        return token;
    }
}