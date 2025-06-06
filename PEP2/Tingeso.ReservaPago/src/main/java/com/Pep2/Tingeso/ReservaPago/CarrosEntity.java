package com.Pep2.Tingeso.ReservaPago;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Carros")
public class CarrosEntity {
    @Id
    private String codigoCarros; // Ejemplo: K001, K002...

    private String modelo;
    private String estado; // activo, mantenimiento

    public CarrosEntity(String codigoCarros, String modelo, String estado) {
        this.codigoCarros = codigoCarros;
        this.modelo = modelo;
        this.estado = estado;
    }

    public CarrosEntity() {
    }

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

    @Override
    public String toString() {
        return "CarrosEntity{" +
                "codigoCarros='" + codigoCarros + '\'' +
                ", modelo='" + modelo + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
