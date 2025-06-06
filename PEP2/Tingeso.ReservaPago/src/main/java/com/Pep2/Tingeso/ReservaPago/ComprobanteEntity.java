package com.Pep2.Tingeso.ReservaPago;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "comprobante_pago")
public class ComprobanteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComprobante;

    private Long idReserva;
    private LocalDate fechaEmision;
    private Double totalConIva;
    @ElementCollection
    @CollectionTable(name = "comprobante_correos", joinColumns = @JoinColumn(name = "id_comprobante"))
    @Column(name = "correo_cliente")
    private List<String> correosClientes;
    @Column(name = "archivo_pdf", columnDefinition = "TEXT")
    private String archivoPdf;

    public Long getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Long idComprobante) {
        this.idComprobante = idComprobante;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
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

    public List<String> getCorreosClientes() {
        return correosClientes;
    }

    public void setCorreosClientes(List<String> correosClientes) {
        this.correosClientes = correosClientes;
    }

    public String getArchivoPdf() {
        return archivoPdf;
    }

    public void setArchivoPdf(String archivoPdf) {
        this.archivoPdf = archivoPdf;
    }

    public ComprobanteEntity(Long idComprobante, Long idReserva, LocalDate fechaEmision, Double totalConIva, List<String> correosClientes, String archivoPdf) {
        this.idComprobante = idComprobante;
        this.idReserva = idReserva;
        this.fechaEmision = fechaEmision;
        this.totalConIva = totalConIva;
        this.correosClientes = correosClientes;
        this.archivoPdf = archivoPdf;
    }

    public ComprobanteEntity() {}
}
