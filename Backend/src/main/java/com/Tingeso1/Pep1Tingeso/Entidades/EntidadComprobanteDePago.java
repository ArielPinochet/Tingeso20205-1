package com.Tingeso1.Pep1Tingeso.Entidades;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioComprobantePago;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "comprobante_pago")
@Getter
@Setter
public class EntidadComprobanteDePago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComprobante;

    @OneToOne
    @JoinColumn(name = "id_reserva")
    @JsonIgnore
    private EntidadReservas reserva;

    private LocalDate fechaEmision;
    private Double totalConIva;

    @Column(name = "archivo_pdf", columnDefinition = "TEXT")
    private String archivoPdf;

    public Long getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Long idComprobante) {
        this.idComprobante = idComprobante;
    }

    public EntidadReservas getReserva() {
        return reserva;
    }

    public void setReserva(EntidadReservas reserva) {
        this.reserva = reserva;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Double getTotalConIva() {
        return totalConIva;
    }

    public void setTotalConIva(Double totalConIva) {
        this.totalConIva = totalConIva;
    }

    public String getArchivoPdf() {
        return archivoPdf;
    }

    public void setArchivoPdf(String archivoPdf) {
        this.archivoPdf = archivoPdf;
    }

    public EntidadComprobanteDePago(Long idComprobante, EntidadReservas reserva, LocalDate fechaEmision, Double totalConIva, String archivoPdf) {
        this.idComprobante = idComprobante;
        this.reserva = reserva;
        this.fechaEmision = fechaEmision;
        this.totalConIva = totalConIva;
        this.archivoPdf = archivoPdf;
    }
    public EntidadComprobanteDePago() {}



}
