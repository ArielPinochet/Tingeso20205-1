package com.Pep2.Tingeso.ReservaPago;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reserva")
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    private LocalDateTime fechaReserva;
    private LocalDateTime horaInicio;
    private Integer numeroVueltas;
    private int cantidadPersonas;
    private Boolean diaEspecial;
    private String estadoReserva;
    private String nombreCliente;
    private Integer duracionTotal;
    @ElementCollection
    private List<String> carros;

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getNumeroVueltas() {
        return numeroVueltas;
    }

    public void setNumeroVueltas(Integer numeroVueltas) {
        this.numeroVueltas = numeroVueltas;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public Boolean getDiaEspecial() {
        return diaEspecial;
    }

    public void setDiaEspecial(Boolean diaEspecial) {
        this.diaEspecial = diaEspecial;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void SetNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Integer getDuracionTotal() {
        return duracionTotal;
    }

    public void setDuracionTotal(Integer duracionTotal) {
        this.duracionTotal = duracionTotal;
    }

    public List<String> getCarros() {
        return carros;
    }

    public void setCarros(List<String> carros) {
        this.carros = carros;
    }

    public ReservaEntity(Long idReserva, LocalDateTime fechaReserva, LocalDateTime horaInicio, Integer numeroVueltas, int cantidadPersonas, Boolean diaEspecial, String estadoReserva, String nombreCliente, Integer duracionTotal, List<String> carros) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.numeroVueltas = numeroVueltas;
        this.cantidadPersonas = cantidadPersonas;
        this.diaEspecial = diaEspecial;
        this.estadoReserva = estadoReserva;
        this.nombreCliente = nombreCliente;
        this.duracionTotal = duracionTotal;
        this.carros = carros;
    }
    public ReservaEntity() {}
}