package com.Tingeso1.Pep1Tingeso.Entidades;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class EntidadClientes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;

    @Transient // Calculado en runtime
    private Integer frecuenciaMensual;

    @ManyToMany(mappedBy = "clientes")
    private List<EntidadReservas> reservas;
}
