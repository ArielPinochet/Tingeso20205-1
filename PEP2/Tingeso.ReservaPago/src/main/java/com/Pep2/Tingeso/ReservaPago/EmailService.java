package com.Pep2.Tingeso.ReservaPago;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final RestTemplate restTemplate;


    public EmailService(JavaMailSender mailSender, RestTemplate restTemplate) {
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void enviarComprobantePorCorreo(List<String> correosClientes, byte[] pdfBytes) {
        for (String destinatario : correosClientes) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(destinatario);
                helper.setSubject("Comprobante de Pago");
                helper.setText("Adjunto su comprobante de pago en PDF.");
                helper.addAttachment("Comprobante.pdf", new ByteArrayResource(pdfBytes));

                mailSender.send(message);
                System.out.println("✔️ Comprobante enviado a: " + destinatario);
            } catch (Exception e) {
                System.err.println("🚨 Error al enviar comprobante a " + destinatario + ": " + e.getMessage());
            }
        }


    }
    public void enviarCorreoPrueba() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("ariel125e@gmail.com");
            helper.setSubject("🚀 Correo de Prueba desde ReservaPago");
            helper.setText("¡Hola Ariel! Esto es un correo de prueba para verificar el funcionamiento del envío.");

            mailSender.send(message);
            System.out.println("✔️ Correo de prueba enviado correctamente!");
        } catch (Exception e) {
            System.err.println("🚨 Error al enviar correo de prueba: " + e.getMessage());
            throw new RuntimeException("Error al enviar correo de prueba", e);
        }
    }
}
