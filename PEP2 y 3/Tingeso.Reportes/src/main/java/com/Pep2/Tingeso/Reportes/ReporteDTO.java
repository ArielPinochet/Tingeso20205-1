package com.Pep2.Tingeso.Reportes;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteDTO {
    private LocalDate mes;
    private int numeroVueltas;
    private int cantidadPersonas;
    private BigDecimal totalPago;

    public ReporteDTO(LocalDate mes, int numeroVueltas, int cantidadPersonas, BigDecimal totalPago) {
        this.mes = mes;
        this.numeroVueltas = numeroVueltas;
        this.cantidadPersonas = cantidadPersonas;
        this.totalPago = totalPago;
    }

    public LocalDate getMes() { return mes; }
    public int getNumeroVueltas() { return numeroVueltas; }
    public int getCantidadPersonas() { return cantidadPersonas; }
    public BigDecimal getTotalPago() { return totalPago; }

    public void setMes(LocalDate mes) { this.mes = mes; }
    public void setNumeroVueltas(int numeroVueltas) { this.numeroVueltas = numeroVueltas; }
    public void setCantidadPersonas(int cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
    public void setTotalPago(BigDecimal totalPago) { this.totalPago = totalPago; }
}
