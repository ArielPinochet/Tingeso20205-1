package com.Pep2.Tingeso.ReservaPago;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    public void enviarComprobante(ComprobanteEntity comprobante, byte[] pdfFile) {
        List<String> correos = comprobante.getCorreosClientes();
        logger.info("Se enviarán correos a {} destinatarios", correos.size());
        for (String email : correos) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(email);
                helper.setSubject("Comprobante de Pago - RentKarts");
                helper.setText("Adjunto encontrarás tu comprobante de pago.", true);
                helper.addAttachment("Comprobante_Pago.pdf", new ByteArrayResource(pdfFile));
                mailSender.send(message);
                logger.info("Correo enviado a: {}", email);
            } catch (MessagingException e) {
                logger.error("Error enviando correo a {}: ", email, e);
            }
        }
    }

    public String obtenerEmailCliente(String nombreCliente) {
        String url = "http://localhost:8088/api/clientes/email/" + nombreCliente;
        return restTemplate.getForObject(url, String.class);
    }

    public void sendReservationConfirmation(ReservaEntity reserva) {
        // Validar que la reserva y el cliente responsable no sean nulos
        if (reserva == null || reserva.getNombreCliente() == null) {
            return; // También se podría lanzar una excepción o registrar un error
        }

        String destinatario = obtenerEmailCliente(reserva.getNombreCliente());
        // Incorporamos información relevante en el subject (por ejemplo, la fecha de la reserva)
        String subject = "Confirmación de Reserva - " + reserva.getFechaReserva();

        // Construir el cuerpo del mensaje
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Estimado/a ").append(reserva.getNombreCliente()).append(",\n\n")
                .append("La reserva ha sido creada exitosamente con los siguientes datos:\n\n")
                .append("Fecha: ").append(reserva.getFechaReserva()).append("\n")
                .append("Hora de Inicio: ").append(reserva.getHoraInicio()).append("\n")
                .append("Número de Vueltas: ").append(reserva.getNumeroVueltas()).append("\n")
                .append("Cantidad de Personas: ").append(reserva.getCantidadPersonas()).append("\n");

        textBuilder.append("\nMuchas gracias por confiar en nuestro servicio.\nSaludos cordiales.");

        // Crear y enviar el mensaje
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(subject);
        message.setText(textBuilder.toString());

        mailSender.send(message);
    }





    // Método para notificar la edición de una reserva, incluyendo los karts arrendados
    public void sendReservationEditedConfirmation(ReservaEntity reserva) {
        String destinatario = obtenerEmailCliente(reserva.getNombreCliente());
        String subject = "Confirmación de Actualización de Reserva";


        String text = "Estimado " + reserva.getNombreCliente() + ",\n\n"
                + "Su reserva ha sido actualizada correctamente. Aquí están los detalles:\n"
                + "Fecha Reserva: " + reserva.getFechaReserva() + "\n"
                + "Hora de Inicio: " + reserva.getHoraInicio() + "\n"
                + "Número de Vueltas: " + reserva.getNumeroVueltas() + "\n"
                + "Cantidad de Personas: " + reserva.getCantidadPersonas() + "\n"
                + "Día Especial: " + (reserva.getDiaEspecial() ? "Sí" : "No") + "\n"
                + "Duración Total: " + reserva.getDuracionTotal() + " minutos\n"
                + "¡Gracias por utilizar nuestro servicio!";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendPaymentDetails(String email, byte[] excelFile, byte[] pdfFile) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Resumen de Pago - Karting");
            helper.setText("Adjunto encontrarás el resumen total en Excel y tu detalle personal en PDF.");

            helper.addAttachment("Resumen_Pago.xlsx", new ByteArrayResource(excelFile));
            helper.addAttachment("Detalle_Pago.pdf", new ByteArrayResource(pdfFile));

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
