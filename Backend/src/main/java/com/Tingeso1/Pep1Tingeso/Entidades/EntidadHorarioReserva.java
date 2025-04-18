package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "HorarioReserva")
@Getter
@Setter
public class EntidadHorarioReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBloque;

    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private LocalDate fecha; // Nuevo atributo para almacenar la fecha de la reserva

    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = true)
    private EntidadReservas reserva;

    public Long getIdBloque() {
        return idBloque;
    }

    public void setIdBloque(Long idBloque) {
        this.idBloque = idBloque;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EntidadReservas getReserva() {
        return reserva;
    }

    public void setReserva(EntidadReservas reserva) {
        this.reserva = reserva;
    }
}
