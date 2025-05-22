package com.Pep2.Tingeso.TarifaEspeciales;

import jakarta.persistence.*;

@Entity
@Table(name = "tarifa_especial")
public class TarifaEspecialEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarifaEspecial;

    private boolean diaEspecial; // True si es feriado o día especial
    private boolean finDeSemana; // True si es sábado o domingo

    private double porcentajeDescuento; // Descuento aplicado

    public Long getIdTarifaEspecial() {
        return idTarifaEspecial;
    }

    public void setIdTarifaEspecial(Long idTarifaEspecial) {
        this.idTarifaEspecial = idTarifaEspecial;
    }

    public boolean isDiaEspecial() {
        return diaEspecial;
    }

    public void setDiaEspecial(boolean diaEspecial) {
        this.diaEspecial = diaEspecial;
    }

    public boolean isFinDeSemana() {
        return finDeSemana;
    }

    public void setFinDeSemana(boolean finDeSemana) {
        this.finDeSemana = finDeSemana;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

}

