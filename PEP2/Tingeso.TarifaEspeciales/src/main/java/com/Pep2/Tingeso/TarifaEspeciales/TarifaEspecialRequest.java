package com.Pep2.Tingeso.TarifaEspeciales;

import java.time.LocalDate;

public class TarifaEspecialRequest {
    private LocalDate fecha;
    private boolean esDiaEspecial;
    private Long idReserva;
    private int cantidadPersonas;

    // Getters y Setters

    public TarifaEspecialRequest(LocalDate fecha, boolean esDiaEspecial, Long idReserva, int cantidadPersonas) {
        this.fecha = fecha;
        this.esDiaEspecial = esDiaEspecial;
        this.idReserva = idReserva;
        this.cantidadPersonas = cantidadPersonas;
    }

    public TarifaEspecialRequest() {
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isEsDiaEspecial() {
        return esDiaEspecial;
    }

    public void setEsDiaEspecial(boolean esDiaEspecial) {
        this.esDiaEspecial = esDiaEspecial;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    @Override
    public String toString() {
        return "TarifaEspecialRequest{" +
                "fecha=" + fecha +
                ", esDiaEspecial=" + esDiaEspecial +
                ", idReserva=" + idReserva +
                ", cantidadPersonas=" + cantidadPersonas +
                '}';
    }
}
