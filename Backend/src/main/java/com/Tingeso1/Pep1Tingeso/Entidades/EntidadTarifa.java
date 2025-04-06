package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tarifas")
@Getter
@Setter
public class EntidadTarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarifa;

    private Integer vueltasMax;
    private Integer tiempoMax;
    private Integer duracionTotalReserva;
    private Double precio;

    @OneToMany(mappedBy = "tarifa")
    private List<EntidadReservas> reservas;
}