package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender; // Simulamos el envío de correos

    @InjectMocks
    private EmailService emailService; // Probamos el servicio

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testEnviarComprobante() {
        // Simulamos un comprobante con correos de clientes
        EntidadComprobanteDePago comprobante = new EntidadComprobanteDePago();
        comprobante.setCorreosClientes(List.of("cliente1@example.com", "cliente2@example.com"));
        byte[] pdf = "PDF Mock".getBytes();

        // Simular el envío de correos sin realizarlo realmente
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarComprobante(comprobante, pdf);

        verify(mailSender, times(2)).send(any(MimeMessage.class)); // Verificamos que se enviaron dos correos
    }

    @Test
    void testSendReservationConfirmation() {
        EntidadReservas reserva = new EntidadReservas();
        EntidadClientes cliente = new EntidadClientes();
        cliente.setEmail("cliente@example.com");
        cliente.setNombre("Juan Pérez");
        reserva.setClienteResponsable(cliente);
        reserva.setFechaReserva(LocalDate.parse("2025-05-01"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        reserva.setHoraInicio(LocalTime.parse("10:00", formatter));
        reserva.setNumeroVueltas(5);
        reserva.setCantidadPersonas(2);

        emailService.sendReservationConfirmation(reserva);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendClientCreationConfirmation() {
        EntidadClientes cliente = new EntidadClientes();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("cliente@example.com");
        cliente.setFechaNacimiento(LocalDate.parse("1990-01-01"));

        emailService.sendClientCreationConfirmation(cliente);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendClientEditedConfirmation() {
        EntidadClientes cliente = new EntidadClientes();
        cliente.setNombre("María López");
        cliente.setEmail("maria@example.com");
        cliente.setFechaNacimiento(LocalDate.parse("1985-06-15"));

        emailService.sendClientEditedConfirmation(cliente);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendReservationEditedConfirmation() {
        EntidadReservas reserva = new EntidadReservas();
        EntidadClientes cliente = new EntidadClientes();
        cliente.setNombre("Pedro Rodríguez");
        cliente.setEmail("pedro@example.com");
        reserva.setClienteResponsable(cliente);

        reserva.setFechaReserva(LocalDate.parse("2025-06-10"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        reserva.setHoraInicio(LocalTime.parse("12:30", formatter));
        reserva.setDiaEspecial(false);

        // Crear lista de carros y asignarla a la reserva
        List<EntidadCarros> carros = List.of(
                new EntidadCarros("K001", "SodiKart RT8", "activo"),
                new EntidadCarros("K002", "SodiKart RT8", "activo")
        );

        reserva.setCarros(carros); // 🔹 Aquí aseguramos que la reserva tiene carros

        reserva.setNumeroVueltas(3);
        reserva.setCantidadPersonas(4);

        emailService.sendReservationEditedConfirmation(reserva);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }


    @Test
    void testSendPaymentDetails() {
        byte[] excelMock = "Excel Mock".getBytes();
        byte[] pdfMock = "PDF Mock".getBytes();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPaymentDetails("test@example.com", excelMock, pdfMock);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
