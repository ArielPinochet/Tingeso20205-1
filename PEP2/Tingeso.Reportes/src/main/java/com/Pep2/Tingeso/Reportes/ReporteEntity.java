package com.Pep2.Tingeso.Reportes;

import jakarta.persistence.*;
import java.time.YearMonth;
import java.math.BigDecimal;

@Entity
@Table(name = "reporte_ingresos")
public class ReporteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private YearMonth mes;
    private String tipoReporte; // "VUELTAS" o "PERSONAS"
    private String criterio; // "10 vueltas", "3-5 personas", etc.
    private BigDecimal ingresos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public YearMonth getMes() {
        return mes;
    }

    public void setMes(YearMonth mes) {
        this.mes = mes;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public ReporteEntity(Long id, YearMonth mes, String tipoReporte, String criterio, BigDecimal ingresos) {
        this.id = id;
        this.mes = mes;
        this.tipoReporte = tipoReporte;
        this.criterio = criterio;
        this.ingresos = ingresos;
    }
}