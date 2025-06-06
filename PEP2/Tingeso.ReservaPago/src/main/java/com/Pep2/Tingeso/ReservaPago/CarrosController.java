package com.Pep2.Tingeso.ReservaPago;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Carros")
public class CarrosController {

    private final CarrosService servicioCarros;

    public CarrosController(CarrosService servicioCarros) {
        this.servicioCarros = servicioCarros;
    }

    @PostMapping
    public ResponseEntity<CarrosEntity> crearCarro(@RequestBody CarrosEntity carro) {
        return ResponseEntity.ok(servicioCarros.guardarCarros(carro));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<CarrosEntity> obtenerCarro(@PathVariable String codigo) {
        return servicioCarros.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CarrosEntity>> listarCarros() {
        return ResponseEntity.ok(servicioCarros.listarTodos());
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminarCarro(@PathVariable String codigo) {
        servicioCarros.eliminarPorCodigo(codigo);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<CarrosEntity> actualizarCarro(@PathVariable String codigo, @RequestBody CarrosEntity carroActualizado) {
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