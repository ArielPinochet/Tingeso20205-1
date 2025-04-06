package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "descuentos")
@Getter
@Setter
public class EntidadDescuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDescuento;

    private String tipo; // frecuencia, grupo, cumpleaños
    private String rangoAplicable; // personas o visitas
    private Double porcentaje;
}
