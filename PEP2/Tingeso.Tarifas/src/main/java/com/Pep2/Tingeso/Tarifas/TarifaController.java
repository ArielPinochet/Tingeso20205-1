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


    @GetMapping("/{idReserva}")
    public TarifaEntity obtenerPorIdReserva(@PathVariable Long idReserva) {
        return tarifaService.obtenerTarifaPorIdReserva(idReserva);
    }

    @GetMapping("/obtener/{idReserva}")
    public double obtenerTarifaPorIdReserva(@PathVariable Long idReserva) {
        return tarifaService.obtenerPrecioTarifaPorIdReserva(idReserva);
    }


    @PostMapping("/")
    public ResponseEntity<?> crearTarifa(@RequestParam int numeroVueltas, @RequestParam Long idReserva) {
        System.out.println("Intentando crear nueva tarifa con ID de reserva: " + idReserva);

        if (tarifaService.existeReserva(idReserva)) {
            System.out.println(" Reserva ya existe: " + idReserva);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La reserva con ID " + idReserva + " ya existe.");
        }

        // Validar número de vueltas
        if (numeroVueltas != 10 && numeroVueltas != 15 && numeroVueltas != 20) {
            System.out.println(" Número de vueltas inválido: " + numeroVueltas);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Número de vueltas inválido. Solo se permiten 10, 15 o 20.");
        }

        TarifaEntity nuevaTarifa = tarifaService.crearTarifa(numeroVueltas, idReserva);
        System.out.println(" Tarifa creada exitosamente para reserva ID: " + idReserva);

        return ResponseEntity.ok(nuevaTarifa);
    }

}