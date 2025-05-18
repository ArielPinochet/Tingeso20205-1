package com.Tingeso1.Pep1Tingeso.Entidades;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "reservas")
@Data
public class EntidadReservas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private Integer numeroVueltas;
    private Integer cantidadPersonas;
    private Boolean diaEspecial;
    private String estadoReserva;
    private Double precioTotal;
    private String metodoPago;
    private Integer duracionTotal; // Ahora persistente en la BD

    @ManyToOne
    @JoinColumn(name = "id_cliente_responsable")
    private EntidadClientes clienteResponsable;

    @ManyToMany
    @JoinTable(
            name = "detalle_reserva",
            joinColumns = @JoinColumn(name = "id_reserva"),
            inverseJoinColumns = @JoinColumn(name = "id_cliente")
    )
    private List<EntidadClientes> clientes;

    @ManyToMany
    @JoinTable(
            name = "reserva_carros",
            joinColumns = @JoinColumn(name = "id_reserva"),
            inverseJoinColumns = @JoinColumn(name = "id_carro")
    )
    private List<EntidadCarros> carros;

    @OneToOne(mappedBy = "reserva")
    private EntidadComprobanteDePago comprobantePago;


    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getNumeroVueltas() {
        return numeroVueltas;
    }

    public void setNumeroVueltas(Integer numeroVueltas) {
        this.numeroVueltas = numeroVueltas;
    }

    public Integer getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(Integer cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public Boolean getDiaEspecial() {
        return diaEspecial;
    }

    public void setDiaEspecial(Boolean diaEspecial) {
        this.diaEspecial = diaEspecial;
    }

    public Integer getDuracionTotal() {
        return duracionTotal;
    }

    public void setDuracionTotal(Integer duracionTotal) {
        this.duracionTotal = duracionTotal;
    }

    public EntidadClientes getClienteResponsable() {
        return clienteResponsable;
    }

    public void setClienteResponsable(EntidadClientes clienteResponsable) {
        this.clienteResponsable = clienteResponsable;
    }

    public List<EntidadClientes> getClientes() {
        return clientes;
    }

    public void setClientes(List<EntidadClientes> clientes) {
        this.clientes = clientes;
    }


    public List<EntidadCarros> getCarros() {
        return carros;
    }

    public void setCarros(List<EntidadCarros> carros) {
        this.carros = carros;
    }

    public EntidadComprobanteDePago getComprobantePago() {
        return comprobantePago;
    }

    public void setComprobantePago(EntidadComprobanteDePago comprobantePago) {
        this.comprobantePago = comprobantePago;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }


    public EntidadReservas(Long idReserva, LocalDate fechaReserva, LocalTime horaInicio, Integer numeroVueltas, Integer cantidadPersonas, Boolean diaEspecial, String estadoReserva, Double precioTotal, String metodoPago, Integer duracionTotal, EntidadClientes clienteResponsable, List<EntidadClientes> clientes, List<EntidadCarros> carros, EntidadComprobanteDePago comprobantePago) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.numeroVueltas = numeroVueltas;
        this.cantidadPersonas = cantidadPersonas;
        this.diaEspecial = diaEspecial;
        this.estadoReserva = estadoReserva;
        this.precioTotal = precioTotal;
        this.metodoPago = metodoPago;
        this.duracionTotal = duracionTotal;
        this.clienteResponsable = clienteResponsable;
        this.clientes = clientes;
        this.carros = carros;
        this.comprobantePago = comprobantePago;
        }

    public EntidadReservas() {
    }

    public EntidadReservas(Long idReserva) {
        this.idReserva = idReserva;
    }

    public EntidadReservas(Long idReserva, LocalDate fechaReserva, LocalTime horaInicio, Integer numeroVueltas, Integer cantidadPersonas, Boolean diaEspecial, String estadoReserva, Double precioTotal, Integer duracionTotal) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.numeroVueltas = numeroVueltas;
        this.cantidadPersonas = cantidadPersonas;
        this.diaEspecial = diaEspecial;
        this.estadoReserva = estadoReserva;
        this.precioTotal = precioTotal;
        this.duracionTotal = duracionTotal;
    }
}
