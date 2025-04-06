package com.Tingeso1.Pep1Tingeso.Entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "reservas")
@Getter
@Setter
public class EntidadReservas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private Integer numeroVueltas;
    private Integer cantidadPersonas;
    private Boolean diaEspecial;

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

    @OneToMany(mappedBy = "reserva")
    private List<EntidadCarros> Carro;

    @OneToOne(mappedBy = "reserva")
    private EntidadComprobanteDePago comprobantePago;

    @OneToMany(mappedBy = "reserva")
    private List<EntidadHorarioReserva> horarios;

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
}
