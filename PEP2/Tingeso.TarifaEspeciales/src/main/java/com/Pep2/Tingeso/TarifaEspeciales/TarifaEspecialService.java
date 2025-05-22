package com.Pep2.Tingeso.TarifaEspeciales;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TarifaEspecialService {

    private final TarifaEspecialRepository tarifaEspecialRepository;

    public TarifaEspecialService(TarifaEspecialRepository tarifaEspecialRepository) {
        this.tarifaEspecialRepository = tarifaEspecialRepository;
    }

    public Optional<TarifaEspecialEntity> obtenerTarifaEspecial(boolean diaEspecial, boolean finDeSemana) {
        return tarifaEspecialRepository.findByDiaEspecialAndFinDeSemana(diaEspecial, finDeSemana);
    }

    public TarifaEspecialEntity guardarTarifaEspecial(TarifaEspecialEntity tarifaEspecial) {
        return tarifaEspecialRepository.save(tarifaEspecial);
    }
}