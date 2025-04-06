package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadDescuento;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioDescuentos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/descuentos")
public class ControladorDescuentos {

    private final ServicioDescuentos servicioDescuentos;

    public ControladorDescuentos(ServicioDescuentos servicioDescuentos) {
        this.servicioDescuentos = servicioDescuentos;
    }

    @PostMapping
    public ResponseEntity<EntidadDescuento> crearDescuento(@RequestBody EntidadDescuento descuento) {
        return ResponseEntity.ok(servicioDescuentos.guardarDescuento(descuento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadDescuento> obtenerDescuento(@PathVariable Long id) {
        return servicioDescuentos.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EntidadDescuento>> listarDescuentos() {
        return ResponseEntity.ok(servicioDescuentos.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDescuento(@PathVariable Long id) {
        servicioDescuentos.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
