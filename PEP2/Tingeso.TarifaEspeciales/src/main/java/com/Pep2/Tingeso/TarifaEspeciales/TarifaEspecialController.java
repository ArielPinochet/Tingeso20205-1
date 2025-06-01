package com.Pep2.Tingeso.TarifaEspeciales;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/tarifas-especiales")
public class TarifaEspecialController {

    private final TarifaEspecialService tarifaEspecialService;

    public TarifaEspecialController(TarifaEspecialService tarifaEspecialService) {
        this.tarifaEspecialService = tarifaEspecialService;
    }


    @GetMapping("/{IdReserva}")
    public TarifaEspecialEntity obtenerPorReserva(@PathVariable Long IdReserva) {
        return tarifaEspecialService.obtenerTarifaEspecialPorIdReserva(IdReserva);
    }



    @PostMapping("/")
    public ResponseEntity<?> crearTarifaEspecial(@RequestBody TarifaEspecialRequest request) {
        System.out.println("Se creara nueva reserva con id: " + request.getIdReserva());
        if (tarifaEspecialService.existeReserva(request.getIdReserva())) {
            System.out.println("Reserva ya existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La reserva con ID " + request.getIdReserva() + " ya existe.");
        }

        TarifaEspecialEntity nuevaTarifa = tarifaEspecialService.crearTarifaEspecial2(
                request.getFecha(), request.isEsDiaEspecial(), request.getIdReserva(), request.getCantidadPersonas()
        );
        return ResponseEntity.ok(nuevaTarifa);
    }


    @PostMapping("/CrearTarifaEspecial/")
    public ResponseEntity<?> crearTarifaEspecial2(@RequestBody LocalDate fecha,
                                                  @RequestParam boolean esDiaEspecial,
                                                  @RequestParam Long IdReserva,
                                                  @RequestParam int CantidadPersonas) {
        if (tarifaEspecialService.existeReserva(IdReserva)) {
            System.out.println("Reserva ya existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La reserva con ID " + IdReserva + " ya existe.");
        }

        System.out.println("Se creara nueva reserva");
        TarifaEspecialEntity nuevaTarifa = tarifaEspecialService.crearTarifaEspecial2(fecha, esDiaEspecial, IdReserva, CantidadPersonas);
        return ResponseEntity.ok(nuevaTarifa);
    }


    @GetMapping("/es-feriado/{fecha}")
    public boolean verificarFeriado(@PathVariable LocalDate fecha) {
        return tarifaEspecialService.esFeriado(fecha);
    }
}

