package com.Pep2.Tingeso.ReservaPago;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportePersonasDTO {
    private LocalDate mes;
    private String numeropersonas;
    private BigDecimal totalPago;

    public ReportePersonasDTO(LocalDate mes, String numeropersonas, BigDecimal totalPago) {
        this.mes = mes;
        this.numeropersonas = numeropersonas;
        this.totalPago = totalPago;
    }

    public LocalDate getMes() {
        return mes;
    }

    public void setMes(LocalDate mes) {
        this.mes = mes;
    }

    public String getNumeropersonas() {
        return numeropersonas;
    }

    public void setNumeropersonas(String numeropersonas) {
        this.numeropersonas = numeropersonas;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }
}
