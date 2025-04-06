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
    private String codigoKart; // Ejemplo: K001, K002...

    private String modelo;
    private String estado; // activo, mantenimiento

    @ManyToMany(mappedBy = "karts")
    private List<EntidadReservas> reservas;
}