package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioComprobantePago;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import com.itextpdf.text.Document;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;



import java.util.Optional;

@Service
public class ServicioComprobantePago {

    private final RepositorioComprobantePago repositorioComprobantePago;
    private final ServicioReservas servicioReservas;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(ServicioComprobantePago.class);

    public ServicioComprobantePago(RepositorioComprobantePago repositorioComprobantePago, ServicioReservas servicioReservas, EmailService emailService) {
        this.repositorioComprobantePago = repositorioComprobantePago;
        this.servicioReservas = servicioReservas;
        this.emailService = emailService;
    }


    public Optional<EntidadComprobanteDePago> buscarPorId(Long id) {
        return repositorioComprobantePago.findById(id);
    }

    public List<EntidadComprobanteDePago> listarTodos() {
        return repositorioComprobantePago.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioComprobantePago.deleteById(id);
    }





        public EntidadComprobanteDePago generarComprobante(EntidadReservas reserva, List<String> correosClientes) {
            EntidadComprobanteDePago comprobante = new EntidadComprobanteDePago();
            comprobante.setReserva(reserva);
            comprobante.setFechaEmision(LocalDate.now());
            comprobante.setTotalConIva(reserva.getPrecioTotal());
            comprobante.setCorreosClientes(correosClientes);

            return repositorioComprobantePago.save(comprobante);
        }

    public EntidadComprobanteDePago guardarComprobante(EntidadComprobanteDePago comprobante) {
        logger.info("Guardando comprobante en la base de datos...");
        EntidadComprobanteDePago guardado = repositorioComprobantePago.save(comprobante);
        logger.info("Comprobante guardado correctamente, id: {}", guardado.getIdComprobante());
        return guardado;
    }

    public void enviarComprobante(EntidadComprobanteDePago comprobante) {
        try {
            logger.info("Iniciando envío de comprobante con id: {}", comprobante.getIdComprobante());
            byte[] pdfBytes = Base64.getDecoder().decode(comprobante.getArchivoPdf());
            emailService.enviarComprobante(comprobante, pdfBytes);
            logger.info("Envío de comprobante completado exitosamente");
        } catch(Exception e) {
            logger.error("Error al enviar comprobante: ", e);
        }
    }
}