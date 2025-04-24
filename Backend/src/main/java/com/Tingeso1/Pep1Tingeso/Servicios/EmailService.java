package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReservationConfirmation(EntidadReservas reserva) {
        // Validar que la reserva y el cliente responsable no sean nulos
        if (reserva == null || reserva.getClienteResponsable() == null) {
            return; // También se podría lanzar una excepción o registrar un error
        }

        String destinatario = reserva.getClienteResponsable().getEmail();
        // Incorporamos información relevante en el subject (por ejemplo, la fecha de la reserva)
        String subject = "Confirmación de Reserva - " + reserva.getFechaReserva();

        // Construir el cuerpo del mensaje
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Estimado/a ").append(reserva.getClienteResponsable().getNombre()).append(",\n\n")
                .append("La reserva ha sido creada exitosamente con los siguientes datos:\n\n")
                .append("Fecha: ").append(reserva.getFechaReserva()).append("\n")
                .append("Hora de Inicio: ").append(reserva.getHoraInicio()).append("\n")
                .append("Número de Vueltas: ").append(reserva.getNumeroVueltas()).append("\n")
                .append("Cantidad de Personas: ").append(reserva.getCantidadPersonas()).append("\n");

        // Si se han asignado karts, agregarlos al mensaje
        if (reserva.getCarros() != null && !reserva.getCarros().isEmpty()) {
            textBuilder.append("\nKarts arrendados:\n");
            reserva.getCarros().forEach(carro -> {
                textBuilder.append(" - ")
                        .append(carro.getCodigoCarros())
                        .append(" (")
                        .append(carro.getModelo())
                        .append(")\n");
            });
        }

        textBuilder.append("\nMuchas gracias por confiar en nuestro servicio.\nSaludos cordiales.");

        // Crear y enviar el mensaje
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(subject);
        message.setText(textBuilder.toString());

        mailSender.send(message);
    }


        // Método que envía correo de confirmación cuando se crea un cliente
        public void sendClientCreationConfirmation(EntidadClientes client) {
            String destinatario = client.getEmail();
            String subject = "Confirmación de Creación de Cliente";
            String text = "Estimado " + client.getNombre() + ",\n\n"
                    + "Su registro ha sido realizado exitosamente. Estos son sus datos:\n"
                    + "Nombre: " + client.getNombre() + "\n"
                    + "Email: " + client.getEmail() + "\n"
                    + "Fecha de Nacimiento: " + client.getFechaNacimiento() + "\n\n"
                    + "¡Gracias por registrarse en nuestro sistema!";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
        }

    // Método para notificar la edición de un cliente
    public void sendClientEditedConfirmation(EntidadClientes client) {
        String destinatario = client.getEmail();
        String subject = "Confirmación de Actualización de Cliente";
        String text = "Estimado " + client.getNombre() + ",\n\n"
                + "Sus datos han sido actualizados correctamente. Estos son sus datos actualizados:\n"
                + "Nombre: " + client.getNombre() + "\n"
                + "Email: " + client.getEmail() + "\n"
                + "Fecha de Nacimiento: " + client.getFechaNacimiento() + "\n\n"
                + "¡Gracias por actualizar sus datos en nuestro sistema!";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    // Método para notificar la edición de una reserva, incluyendo los karts arrendados
    public void sendReservationEditedConfirmation(EntidadReservas reserva) {
        String destinatario = reserva.getClienteResponsable().getEmail();
        String subject = "Confirmación de Actualización de Reserva";

        // Construir la lista de karts (carros) arrendados. Se imprime el código y el modelo de cada kart.
        String kartsTexto = "";
        if (reserva.getCarros() != null && !reserva.getCarros().isEmpty()) {
            kartsTexto = reserva.getCarros().stream()
                    .map(carro -> carro.getCodigoCarros() + " (" + carro.getModelo() + ")")
                    .collect(Collectors.joining(", "));
        } else {
            kartsTexto = "No se encontraron karts asignados.";
        }

        String text = "Estimado " + reserva.getClienteResponsable().getNombre() + ",\n\n"
                + "Su reserva ha sido actualizada correctamente. Aquí están los detalles:\n"
                + "Fecha Reserva: " + reserva.getFechaReserva() + "\n"
                + "Hora de Inicio: " + reserva.getHoraInicio() + "\n"
                + "Número de Vueltas: " + reserva.getNumeroVueltas() + "\n"
                + "Cantidad de Personas: " + reserva.getCantidadPersonas() + "\n"
                + "Día Especial: " + (reserva.getDiaEspecial() ? "Sí" : "No") + "\n"
                + "Duración Total: " + reserva.getDuracionTotal() + " minutos\n"
                + "Precio Total: $" + reserva.getPrecioTotal() + "\n"
                + "Karts arrendados: " + kartsTexto + "\n\n"
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
