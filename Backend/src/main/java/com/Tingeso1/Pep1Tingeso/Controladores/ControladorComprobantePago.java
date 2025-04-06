package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioComprobantePago;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/comprobante-pago")
public class ControladorComprobantePago {

    private final ServicioComprobantePago servicioComprobantePago;

    public ControladorComprobantePago(ServicioComprobantePago servicioComprobantePago) {
        this.servicioComprobantePago = servicioComprobantePago;
    }

    @PostMapping
    public ResponseEntity<EntidadComprobanteDePago> crearComprobante(@RequestBody EntidadComprobanteDePago comprobante) {
        return ResponseEntity.ok(servicioComprobantePago.guardarComprobante(comprobante));
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
        servicioComprobantePago.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
