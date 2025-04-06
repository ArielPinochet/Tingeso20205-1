package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Servicios.ServicioTarifas;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadTarifa;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tarifas")
public class ControladorTarifas {

    private final ServicioTarifas servicioTarifas;

    public ControladorTarifas(ServicioTarifas servicioTarifas) {
        this.servicioTarifas = servicioTarifas;
    }

    @PostMapping
    public ResponseEntity<EntidadTarifa> crearTarifa(@RequestBody EntidadTarifa tarifa) {
        return ResponseEntity.ok(servicioTarifas.guardarTarifa(tarifa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadTarifa> obtenerTarifa(@PathVariable Long id) {
        return servicioTarifas.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EntidadTarifa>> listarTarifas() {
        return ResponseEntity.ok(servicioTarifas.listarTodas());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(@PathVariable Long id) {
        servicioTarifas.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
