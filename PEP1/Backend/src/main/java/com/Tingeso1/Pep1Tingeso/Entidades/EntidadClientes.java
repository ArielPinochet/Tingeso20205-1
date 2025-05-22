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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getFrecuenciaMensual() {
        return frecuenciaMensual;
    }

    public void setFrecuenciaMensual(Integer frecuenciaMensual) {
        this.frecuenciaMensual = frecuenciaMensual;
    }

    public List<EntidadReservas> getReservas() {
        return reservas;
    }

    public void setReservas(List<EntidadReservas> reservas) {
        this.reservas = reservas;
    }

    public EntidadClientes(Long idCliente, String nombre, String email, LocalDate fechaNacimiento, Integer frecuenciaMensual, List<EntidadReservas> reservas) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.frecuenciaMensual = frecuenciaMensual;
        this.reservas = reservas;
    }

    public EntidadClientes(Long idCliente, String nombre, String email, LocalDate fechaNacimiento) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
    }

    public EntidadClientes() {
    }
}
