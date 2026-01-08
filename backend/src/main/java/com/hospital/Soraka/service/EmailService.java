package com.hospital.Soraka.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailConfirmacion(String email, String token) {
        String enlace = "http://localhost:8080/auth/confirmar?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Confirmación de cuenta");

            String contenidoHtml = """
                <html>
                    <body>
                        <p>Gracias por registrarte.</p>
                        <p>Haz clic en el botón para activar tu cuenta:</p>
                        <a href="%s"
                           style="
                               display:inline-block;
                               padding:12px 20px;
                               color:white;
                               background-color:#2563eb;
                               text-decoration:none;
                               border-radius:6px;
                               font-weight:bold;
                           ">
                            Activar cuenta
                        </a>
                        <p style="margin-top:20px;">
                            Si no creaste esta cuenta, ignora este email.
                        </p>
                    </body>
                </html>
                """.formatted(enlace);

            helper.setText(contenidoHtml, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando email de confirmación", e);
        }
    }
}