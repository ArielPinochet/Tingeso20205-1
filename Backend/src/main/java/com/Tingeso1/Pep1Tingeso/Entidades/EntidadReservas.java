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

    @OneToMany(mappedBy = "reserva")
    private List<EntidadDetalleReserva> detallesReserva;

    @ManyToMany
    @JoinTable(
            name = "reserva_carros",
            joinColumns = @JoinColumn(name = "id_reserva"),
            inverseJoinColumns = @JoinColumn(name = "id_carro")
    )
    private List<EntidadCarros> carros;

    @OneToOne(mappedBy = "reserva")
    private EntidadComprobanteDePago comprobantePago;

    @OneToMany(mappedBy = "reserva")
    private List<EntidadHorarioReserva> horarios;

    @ManyToOne
    @JoinColumn(name = "id_tarifa")
    private EntidadTarifa tarifa;

    public EntidadTarifa getTarifa() {
        return tarifa;
    }

    public void setTarifa(EntidadTarifa tarifa) {
        this.tarifa = tarifa;
    }

    // Método para calcular la duración total basado en la cantidad de vueltas
    public void calcularDuracionTotal() {
        if (numeroVueltas != null) {
            this.duracionTotal = switch (numeroVueltas) {
                case 10 -> 30; // Minutos
                case 15 -> 35;
                case 20 -> 40;
                default -> 0; // Valor por defecto
            };
        }
    }

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

    public List<EntidadDetalleReserva> getDetallesReserva() {
        return detallesReserva;
    }

    public void setDetallesReserva(List<EntidadDetalleReserva> detallesReserva) {
        this.detallesReserva = detallesReserva;
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

    public List<EntidadHorarioReserva> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<EntidadHorarioReserva> horarios) {
        this.horarios = horarios;
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


    public EntidadReservas(Long idReserva, LocalDate fechaReserva, LocalTime horaInicio, Integer numeroVueltas, Integer cantidadPersonas, Boolean diaEspecial, String estadoReserva, Double precioTotal, String metodoPago, Integer duracionTotal, EntidadClientes clienteResponsable, List<EntidadClientes> clientes, List<EntidadDetalleReserva> detallesReserva, List<EntidadCarros> carros, EntidadComprobanteDePago comprobantePago, List<EntidadHorarioReserva> horarios, EntidadTarifa tarifa) {
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
        this.detallesReserva = detallesReserva;
        this.carros = carros;
        this.comprobantePago = comprobantePago;
        this.horarios = horarios;
        this.tarifa = tarifa;
    }

    public EntidadReservas() {
    }

    public EntidadReservas(Long idReserva) {
        this.idReserva = idReserva;
    }
}
