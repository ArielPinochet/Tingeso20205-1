package com.Pep2.Tingeso.ReservaPago;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ComprobanteService {

    private final ComprobanteRepository repositorioComprobantePago;
    private final ReservaService servicioReservas;
    private final EmailService emailService;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ComprobanteService.class);

    public ComprobanteService(ComprobanteRepository repositorioComprobantePago, ReservaService servicioReservas,
                              EmailService emailService, RestTemplate restTemplate) {
        this.repositorioComprobantePago = repositorioComprobantePago;
        this.servicioReservas = servicioReservas;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }


    public Optional<ComprobanteEntity> buscarPorId(Long id) {
        return repositorioComprobantePago.findById(id);
    }

    public List<ComprobanteEntity> listarTodos() {
        return repositorioComprobantePago.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioComprobantePago.deleteById(id);
    }



    public double obtenerPrecioTotalReserva(Long idReserva) {
        String url = "http://localhost:8087/api/tarifas/obtener/" + idReserva;
        return restTemplate.getForObject(url, Double.class);
    }



    @Transactional
    public byte[] generarComprobantePDF(Long idReserva, String fechaEmision, String fechaReserva, String horaInicio,
                                        int cantidadPersonas, int numeroVueltas, int duracionTotal, String nombreCliente,
                                        double precioFinalConIVA, double precioFinalSinIVA, List<String> correosClientes) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

            document.add(new Paragraph("Comprobante de Pago"));
            document.add(new Paragraph("ID Reserva: " + idReserva));
            document.add(new Paragraph("Fecha de Emisión: " + fechaEmision));
            document.add(new Paragraph("Fecha de Reserva: " + fechaReserva));
            document.add(new Paragraph("Hora de Inicio: " + horaInicio));
            document.add(new Paragraph("Cantidad de Personas: " + cantidadPersonas));
            document.add(new Paragraph("Número de Vueltas: " + numeroVueltas));
            document.add(new Paragraph("Duración Total: " + duracionTotal + " min"));
            document.add(new Paragraph("Cliente Responsable: " + nombreCliente));
            document.add(new Paragraph("Precio Final (sin IVA): $" + precioFinalSinIVA));
            document.add(new Paragraph("Precio Final (con IVA): $" + precioFinalConIVA));

            // Correos de participantes
            document.add(new Paragraph("\nCorreos de Participantes:"));
            for (int i = 0; i < correosClientes.size(); i++) {
                document.add(new Paragraph((i + 1) + ". " + correosClientes.get(i)));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el comprobante PDF", e);
        }
    }

    public ComprobanteEntity generarComprobante( List<String> correosClientes, Long idReserva) {

        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setIdReserva(idReserva);
        comprobante.setFechaEmision(LocalDate.now());
        comprobante.setTotalConIva(obtenerPrecioTotalReserva(idReserva));
        comprobante.setCorreosClientes(correosClientes);
        return repositorioComprobantePago.save(comprobante);
    }

    public ComprobanteEntity guardarComprobante(ComprobanteEntity comprobante) {
        logger.info("Guardando comprobante en la base de datos...");
        ComprobanteEntity guardado = repositorioComprobantePago.save(comprobante);
        logger.info("Comprobante guardado correctamente, id: {}", guardado.getIdComprobante());
        return guardado;
    }


}