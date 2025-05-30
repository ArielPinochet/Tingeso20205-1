package com.Pep2.Tingeso.TarifaEspeciales;

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
    public TarifaEspecialEntity crearTarifaEspecial(@RequestParam LocalDate fecha, @RequestParam double porcentajeDescuento, @RequestParam Long IdReserva) {
        return tarifaEspecialService.crearTarifaEspecial(fecha, porcentajeDescuento, IdReserva);
    }

    @GetMapping("/es-feriado/{fecha}")
    public boolean verificarFeriado(@PathVariable LocalDate fecha) {
        return tarifaEspecialService.esFeriado(fecha);
    }
}

