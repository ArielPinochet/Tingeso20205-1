package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Carros")
@Getter
@Setter
public class EntidadCarros {
    @Id
    private String codigoCarros; // Ejemplo: K001, K002...

    private String modelo;
    private String estado; // activo, mantenimiento

    @ManyToMany(mappedBy = "carros")
    private List<EntidadReservas> reservas;

    public String getCodigoCarros() {
        return codigoCarros;
    }

    public void setCodigoCarros(String codigoCarros) {
        this.codigoCarros = codigoCarros;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<EntidadReservas> getReservas() {
        return reservas;
    }

    public void setReservas(List<EntidadReservas> reservas) {
        this.reservas = reservas;
    }

    public EntidadCarros(String codigoCarros, String modelo, String estado, List<EntidadReservas> reservas) {
        this.codigoCarros = codigoCarros;
        this.modelo = modelo;
        this.estado = estado;
        this.reservas = reservas;
    }
    public EntidadCarros() {}

    @Override
    public String toString() {
        return "EntidadCarros{" +
                "codigoCarros='" + codigoCarros + '\'' +
                ", modelo='" + modelo + '\'' +
                ", estado='" + estado + '\'' +
                ", reservas=" + reservas +
                '}';
    }
}