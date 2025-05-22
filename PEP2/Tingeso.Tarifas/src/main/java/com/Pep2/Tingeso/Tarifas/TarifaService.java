package com.Pep2.Tingeso.Tarifas;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    public List<TarifaEntity> obtenerTodasTarifas() {
        return tarifaRepository.findAll();
    }

    public TarifaEntity obtenerTarifaPorVueltas(int numeroVueltas) {
        return tarifaRepository.findByNumeroVueltas(numeroVueltas).orElse(null);
    }

    public TarifaEntity guardarTarifa(TarifaEntity tarifa) {
        return tarifaRepository.save(tarifa);
    }
}