package com.Pep2.Tingeso.Tarifas;

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
    public TarifaEntity crearTarifa(@RequestBody TarifaEntity tarifa) {
        return tarifaService.guardarTarifa(tarifa);
    }
}