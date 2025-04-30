package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioCarros;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Carros")
@CrossOrigin(origins = "*") // acepta todos los orígenes solo en este controlador
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

    @PutMapping("/{codigo}")
    public ResponseEntity<EntidadCarros> actualizarCarro(@PathVariable String codigo, @RequestBody EntidadCarros carroActualizado) {
        System.out.println("Carro recibido para actualizar: " + carroActualizado);

        return servicioCarros.buscarPorCodigo(codigo)
                .map(carro -> {
                    carro.setModelo(carroActualizado.getModelo()); // Confirmar que el modelo se actualiza
                    carro.setEstado(carroActualizado.getEstado());
                    return ResponseEntity.ok(servicioCarros.guardarCarros(carro));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
