package com.hospital.Soraka.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailConfirmacion(String email, String token) {
        String enlace = "http://localhost:8080/auth/confirmar?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Confirmación de cuenta");
        mensaje.setText(
                "Para activar tu cuenta, haz clic en el siguiente enlace:\n" + enlace
        );

        mailSender.send(mensaje);
    }
}