package com.Pep2.Tingeso.TarifaEspeciales;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tarifas-especiales")
public class TarifaEspecialController {

    private final TarifaEspecialService tarifaEspecialService;

    public TarifaEspecialController(TarifaEspecialService tarifaEspecialService) {
        this.tarifaEspecialService = tarifaEspecialService;
    }

    @GetMapping("/{diaEspecial}/{finDeSemana}")
    public Optional<TarifaEspecialEntity> obtenerPorEstado(@PathVariable boolean diaEspecial, @PathVariable boolean finDeSemana) {
        return tarifaEspecialService.obtenerTarifaEspecial(diaEspecial, finDeSemana);
    }

    @PostMapping("/")
    public TarifaEspecialEntity crearTarifaEspecial(@RequestBody TarifaEspecialEntity tarifaEspecial) {
        return tarifaEspecialService.guardarTarifaEspecial(tarifaEspecial);
    }
}

