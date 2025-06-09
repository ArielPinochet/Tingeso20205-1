package com.Pep2.Tingeso.Tarifas;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;
    private final TarifaRepository tarifaRepository;

    public TarifaController(TarifaService tarifaService, TarifaRepository tarifaRepository) {
        this.tarifaService = tarifaService;
        this.tarifaRepository = tarifaRepository;
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
    public double obtenerPrecioTarifaPorIdReserva(@PathVariable Long idReserva) {
        return tarifaRepository.findByidReserva(idReserva)
                .map(TarifaEntity::getPrecio)
                .orElseThrow(() -> new NoSuchElementException("Error: No se encontró la tarifa para la reserva con ID " + idReserva));
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