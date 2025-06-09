package com.Pep2.Tingeso.ReservaPago;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/comprobante-pagosssssss")
public class ComprobanteControlador {
    
    private final ComprobanteService servicioComprobantePago;
    private static final Logger logger = LoggerFactory.getLogger(ComprobanteControlador.class);

    public ComprobanteControlador(ComprobanteService servicioComprobantePago) {
        this.servicioComprobantePago = servicioComprobantePago;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComprobanteEntity> crearComprobante(
            @RequestParam("idReserva") Long idReserva,
            @RequestParam("fechaEmision") String fechaEmision,
            @RequestParam("totalConIva") Double totalConIva,
            @RequestParam("archivoPdf") MultipartFile archivoPdf,
            @RequestParam("correosClientes") String correosClientes) {
        try {
            logger.info("Inicio de creación de comprobante para reserva ID: {}", idReserva);

            ComprobanteEntity comprobante = new ComprobanteEntity();
            comprobante.setIdReserva(idReserva);
            comprobante.setFechaEmision(LocalDate.parse(fechaEmision));
            comprobante.setTotalConIva(totalConIva);
            comprobante.setArchivoPdf(Base64.getEncoder().encodeToString(archivoPdf.getBytes()));
            logger.info("Datos básicos del comprobante establecidos");

            List<String> listaCorreos = Arrays.asList(correosClientes.split(","));
            comprobante.setCorreosClientes(listaCorreos);
            logger.info("Se han asignado {} correos al comprobante", listaCorreos.size());

            ComprobanteEntity comprobanteGuardado = servicioComprobantePago.guardarComprobante(comprobante);
            logger.info("Comprobante guardado en la base de datos con id: {}", comprobanteGuardado.getIdComprobante());

            servicioComprobantePago.enviarComprobante(comprobanteGuardado);
            logger.info("Se ha iniciado el envío de correos para el comprobante con id: {}", comprobanteGuardado.getIdComprobante());

            return ResponseEntity.ok(comprobanteGuardado);
        } catch (Exception e) {
            logger.error("Error en crearComprobante: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ComprobanteEntity> editarComprobante(@PathVariable Long id, @RequestBody ComprobanteEntity comprobanteActualizado) {
        return servicioComprobantePago.buscarPorId(id)
                .map(comprobante -> {
                    comprobante.setFechaEmision(comprobanteActualizado.getFechaEmision());
                    comprobante.setTotalConIva(comprobanteActualizado.getTotalConIva());
                    comprobante.    setArchivoPdf(comprobanteActualizado.getArchivoPdf());
                    return ResponseEntity.ok(servicioComprobantePago.guardarComprobante(comprobante));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ComprobanteEntity> obtenerComprobante(@PathVariable Long id) {
        return servicioComprobantePago.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ComprobanteEntity>> listarComprobantes() {
        return ResponseEntity.ok(servicioComprobantePago.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComprobante(@PathVariable Long id) {
        if (servicioComprobantePago.buscarPorId(id).isPresent()) {
            servicioComprobantePago.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/enviar-pago")
    public ResponseEntity<String> procesarPago(@RequestBody ComprobanteEntity datosPago) {
        servicioComprobantePago.generarComprobante(datosPago.getCorreosClientes(),datosPago.getIdReserva());
        return ResponseEntity.ok("Pago procesado y correos guardados.");
    }

}
