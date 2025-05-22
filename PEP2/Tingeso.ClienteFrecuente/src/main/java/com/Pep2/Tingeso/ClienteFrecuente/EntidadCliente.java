package com.Pep2.Tingeso.ClienteFrecuente;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cliente")
public class EntidadCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    private String nombre;
    private String email;
    private int cantidadReservas;
    private double descuentoFrecuente;
    private LocalDate fechaUltimaReserva;

    @Enumerated(EnumType.STRING)
    private CategoriaFrecuencia categoriaFrecuencia;

    public enum CategoriaFrecuencia {
        MUY_FRECUENTE, FRECUENTE, REGULAR, NO_FRECUENTE
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCantidadReservas() {
        return cantidadReservas;
    }

    public void setCantidadReservas(int cantidadReservas) {
        this.cantidadReservas = cantidadReservas;
    }

    public double getDescuentoFrecuente() {
        return descuentoFrecuente;
    }

    public void setDescuentoFrecuente(double descuentoFrecuente) {
        this.descuentoFrecuente = descuentoFrecuente;
    }

    public LocalDate getFechaUltimaReserva() {
        return fechaUltimaReserva;
    }

    public void setFechaUltimaReserva(LocalDate fechaUltimaReserva) {
        this.fechaUltimaReserva = fechaUltimaReserva;
    }

    public CategoriaFrecuencia getCategoriaFrecuencia() {
        return categoriaFrecuencia;
    }

    public void setCategoriaFrecuencia(CategoriaFrecuencia categoriaFrecuencia) {
        this.categoriaFrecuencia = categoriaFrecuencia;
    }

    public EntidadCliente(Long idCliente, String nombre, String email, int cantidadReservas, double descuentoFrecuente, LocalDate fechaUltimaReserva, CategoriaFrecuencia categoriaFrecuencia) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.cantidadReservas = cantidadReservas;
        this.descuentoFrecuente = descuentoFrecuente;
        this.fechaUltimaReserva = fechaUltimaReserva;
        this.categoriaFrecuencia = categoriaFrecuencia;
    }

    public EntidadCliente() {}

}
