package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = true)
    private EntidadReservas reserva;
}
