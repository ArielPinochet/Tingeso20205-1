package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_reserva")
@Getter
@Setter
public class EntidadDetalleReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private EntidadReservas reserva;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private EntidadClientes cliente;

    private Double tarifaBase;
    private Double descuentoGrupal;
    private Double descuentoFrecuencia;
    private Double descuentoEspecial;
    private Double iva;
    private Double montoFinal;
}
