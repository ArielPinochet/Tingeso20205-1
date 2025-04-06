package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "comprobante_pago")
@Getter
@Setter
public class EntidadComprobanteDePago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComprobante;

    @OneToOne
    @JoinColumn(name = "id_reserva")
    private EntidadReservas reserva;

    private LocalDate fechaEmision;
    private Double totalConIva;
    private String archivoPdf; // URL o blob

}
