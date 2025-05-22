package com.Pep2.Tingeso.ReservaPago;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reserva_pago")
public class ReservaPagoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    private LocalDateTime fechaReserva;
    private int cantidadPersonas;
    private LocalDateTime horaInicio;
    private String estadoReserva;
    private int duracionTotal;
    private String metodoPago;
    private LocalDateTime fechaEmision;

    @ElementCollection
    private List<String> correosClientes;

    @Lob
    private byte[] archivoPdf;

    private Long idCliente;

    @ElementCollection
    private List<String> carros;
    private Long idTarifa;
    private Long idDescuento;

}