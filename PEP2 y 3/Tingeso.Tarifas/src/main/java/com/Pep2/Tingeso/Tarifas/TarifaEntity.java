package com.Pep2.Tingeso.Tarifas;


import jakarta.persistence.*;

@Entity
@Table(name = "tarifas")
public class TarifaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarifa;

    private int numeroVueltas;
    private double precio;
    private int duracionMaxima; // Minutos permitidos en pista
    private int duracionTotal; // Minutos incluyendo preparación

    @Column(name = "id_reserva", unique = true)  // Esto asegura que no haya IDs duplicados
    private Long idReserva;

    public Long getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Long idTarifa) {
        this.idTarifa = idTarifa;
    }

    public int getNumeroVueltas() {
        return numeroVueltas;
    }

    public void setNumeroVueltas(int numeroVueltas) {
        this.numeroVueltas = numeroVueltas;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDuracionMaxima() {
        return duracionMaxima;
    }

    public void setDuracionMaxima(int duracionMaxima) {
        this.duracionMaxima = duracionMaxima;
    }

    public int getDuracionTotal() {
        return duracionTotal;
    }

    public void setDuracionTotal(int duracionTotal) {
        this.duracionTotal = duracionTotal;
    }

    public Long getidReserva() {
        return idReserva;
    }
    public void setidReserva(Long Idreserva) {
        this.idReserva = Idreserva;
    }


    public TarifaEntity(Long idTarifa, int numeroVueltas, double precio, int duracionMaxima, int duracionTotal, Long idReserva) {
        this.idTarifa = idTarifa;
        this.numeroVueltas = numeroVueltas;
        this.precio = precio;
        this.duracionMaxima = duracionMaxima;
        this.duracionTotal = duracionTotal;
        this.idReserva = idReserva;
    }
    public TarifaEntity() {}

}