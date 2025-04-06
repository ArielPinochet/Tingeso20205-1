package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioCarros;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Carros")
public class ControladorCarros {

    private final ServicioCarros servicioCarros;

    public ControladorCarros(ServicioCarros servicioCarros) {
        this.servicioCarros = servicioCarros;
    }

    @PostMapping
    public ResponseEntity<EntidadCarros> crearCarro(@RequestBody EntidadCarros carro) {
        return ResponseEntity.ok(servicioCarros.guardarCarros(carro));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<EntidadCarros> obtenerCarro(@PathVariable String codigo) {
        return servicioCarros.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EntidadCarros>> listarCarros() {
        return ResponseEntity.ok(servicioCarros.listarTodos());
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminarCarro(@PathVariable String codigo) {
        servicioCarros.eliminarPorCodigo(codigo);
        return ResponseEntity.noContent().build();
    }
}
