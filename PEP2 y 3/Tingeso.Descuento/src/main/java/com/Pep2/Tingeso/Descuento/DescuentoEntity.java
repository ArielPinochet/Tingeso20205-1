package com.Pep2.Tingeso.Descuento;

import jakarta.persistence.*;

@Entity
@Table(name = "descuento_reserva")
public class DescuentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDescuento;

    private Long idReserva;

    private String nombrecliente;

    private double descuentoTotal;

    public Long getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(Long idDescuento) {
        this.idDescuento = idDescuento;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public double getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(double descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public String getNombrecliente() {
        return nombrecliente;
    }

    public void setNombrecliente(String nombrecliente) {
        this.nombrecliente = nombrecliente;
    }

    public DescuentoEntity(Long idDescuento, Long idReserva, double descuentoTotal,String nombrecliente) {
        this.idDescuento = idDescuento;
        this.idReserva = idReserva;
        this.nombrecliente = nombrecliente;
        this.descuentoTotal = descuentoTotal;
    }
    public DescuentoEntity() {}
}
