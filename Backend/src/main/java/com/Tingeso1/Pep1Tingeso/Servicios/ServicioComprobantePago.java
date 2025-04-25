package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioComprobantePago;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
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

    public ServicioComprobantePago(RepositorioComprobantePago repositorioComprobantePago, ServicioReservas servicioReservas) {
        this.repositorioComprobantePago = repositorioComprobantePago;
        this.servicioReservas = servicioReservas;
    }

    public EntidadComprobanteDePago guardarComprobante(EntidadComprobanteDePago comprobante) {
        return repositorioComprobantePago.save(comprobante);
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


    public byte[] generarPDF(EntidadReservas reserva) throws DocumentException {
        // Crea un stream para almacenar los bytes del PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Crea el documento (usa el constructor sin argumentos de iText5)
        Document document = new Document();

        // Asocia el documento con el PdfWriter, que escribe en 'baos'
        PdfWriter.getInstance(document, baos);

        // Abre el documento para agregar contenido
        document.open();

        // Crea una fuente usando el FontFactory y agrega el párrafo con ella
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        document.add(new Paragraph("Detalle de Pago Individual", font));

        // Agrega el resto de la información
        document.add(new Paragraph("Fecha Reserva: " + reserva.getFechaReserva()));
        document.add(new Paragraph("Número de Vueltas: " + reserva.getNumeroVueltas()));
        document.add(new Paragraph("Cantidad de Personas: " + reserva.getCantidadPersonas()));

        // Asegurarse de que no se divida por cero
        double montoPorPersona = (reserva.getCantidadPersonas() != 0)
                ? (reserva.getPrecioTotal() / reserva.getCantidadPersonas())
                : 0;
        document.add(new Paragraph("Monto por Persona: $" + montoPorPersona));

        // Cierra el documento (esto finaliza la escritura)
        document.close();

        return baos.toByteArray();
    }

    public byte[] generarExcel(EntidadReservas reserva) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Resumen de Pago");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Concepto");
            headerRow.createCell(1).setCellValue("Monto");

            sheet.createRow(1).createCell(0).setCellValue("Fecha Reserva");
            sheet.getRow(1).createCell(1).setCellValue(reserva.getFechaReserva());

            sheet.createRow(2).createCell(0).setCellValue("Cantidad de Personas");
            sheet.getRow(2).createCell(1).setCellValue(reserva.getCantidadPersonas());

            sheet.createRow(3).createCell(0).setCellValue("Total a Pagar");
            sheet.getRow(3).createCell(1).setCellValue(reserva.getPrecioTotal());

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

        public EntidadComprobanteDePago generarComprobante(EntidadReservas reserva, List<String> correosClientes) {
            EntidadComprobanteDePago comprobante = new EntidadComprobanteDePago();
            comprobante.setReserva(reserva);
            comprobante.setFechaEmision(LocalDate.now());
            comprobante.setTotalConIva(reserva.getPrecioTotal());
            comprobante.setCorreosClientes(correosClientes);

            return repositorioComprobantePago.save(comprobante);
        }

}