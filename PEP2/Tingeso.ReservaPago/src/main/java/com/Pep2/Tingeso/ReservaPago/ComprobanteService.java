package com.Pep2.Tingeso.ReservaPago;

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


    public ComprobanteEntity generarComprobante( List<String> correosClientes, Long idReserva) {

        ComprobanteEntity comprobante = new ComprobanteEntity();
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

    public void enviarComprobante(ComprobanteEntity comprobante) {
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