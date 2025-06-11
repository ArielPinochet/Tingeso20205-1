package com.Pep2.Tingeso.Reportes;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportesVueltasDTO {
    private LocalDate mes;
    private String categoriavueltas;
    private BigDecimal totalPago;

    public ReportesVueltasDTO(LocalDate mes, String categoriavueltas, BigDecimal totalPago) {
        this.mes = mes;
        this.categoriavueltas = categoriavueltas;
        this.totalPago = totalPago;
    }

    public LocalDate getMes() {
        return mes;
    }

    public void setMes(LocalDate mes) {
        this.mes = mes;
    }

    public String getcategoriavueltas() {
        return categoriavueltas;
    }

    public void setcategoriavueltas(String numeropersonas) {
        this.categoriavueltas = numeropersonas;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }
}
