package com.Pep2.Tingeso.Tarifas;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @GetMapping("/")
    public List<TarifaEntity> obtenerTodas() {
        return tarifaService.obtenerTodasTarifas();
    }

    @GetMapping("/{numeroVueltas}")
    public TarifaEntity obtenerPorVueltas(@PathVariable int numeroVueltas) {
        return tarifaService.obtenerTarifaPorVueltas(numeroVueltas);
    }

    @PostMapping("/")
    public ResponseEntity<?> crearTarifa(@RequestParam int numeroVueltas, @RequestParam Long IdReserva) {
        System.out.println("Se creara nueva reserva con id: " + IdReserva);
        if (tarifaService.existeReserva(IdReserva)) {
            System.out.println("Reserva ya existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La reserva con ID " + IdReserva + " ya existe.");
        }

        TarifaEntity nuevaTarifa = tarifaService.crearTarifa(
                numeroVueltas, IdReserva
        );
        return ResponseEntity.ok(nuevaTarifa);
    }
}