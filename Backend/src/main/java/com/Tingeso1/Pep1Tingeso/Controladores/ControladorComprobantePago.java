package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioComprobantePago;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;


@RestController
@RequestMapping("/comprobante-pago")
public class ControladorComprobantePago {

    private final ServicioComprobantePago servicioComprobantePago;

    public ControladorComprobantePago(ServicioComprobantePago servicioComprobantePago) {
        this.servicioComprobantePago = servicioComprobantePago;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntidadComprobanteDePago> crearComprobante(@RequestParam("idReserva") Long idReserva,
                                                                     @RequestParam("fechaEmision") String fechaEmision,
                                                                     @RequestParam("totalConIva") Double totalConIva,
                                                                     @RequestParam("archivoPdf") MultipartFile archivoPdf) {
        try {
            EntidadComprobanteDePago comprobante = new EntidadComprobanteDePago();
            comprobante.setReserva(new EntidadReservas(idReserva));
            comprobante.setFechaEmision(LocalDate.parse(fechaEmision));
            comprobante.setTotalConIva(totalConIva);
            comprobante.setArchivoPdf(Base64.getEncoder().encodeToString(archivoPdf.getBytes())); // 🔹 Guardamos el PDF en base64

            return ResponseEntity.ok(servicioComprobantePago.guardarComprobante(comprobante));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntidadComprobanteDePago> editarComprobante(@PathVariable Long id, @RequestBody EntidadComprobanteDePago comprobanteActualizado) {
        return servicioComprobantePago.buscarPorId(id)
                .map(comprobante -> {
                    comprobante.setFechaEmision(comprobanteActualizado.getFechaEmision());
                    comprobante.setTotalConIva(comprobanteActualizado.getTotalConIva());
                    comprobante.setArchivoPdf(comprobanteActualizado.getArchivoPdf());
                    return ResponseEntity.ok(servicioComprobantePago.guardarComprobante(comprobante));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntidadComprobanteDePago> obtenerComprobante(@PathVariable Long id) {
        return servicioComprobantePago.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EntidadComprobanteDePago>> listarComprobantes() {
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

}
