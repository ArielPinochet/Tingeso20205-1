package com.Pep2.Tingeso.Tarifas;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public boolean existeReserva(Long idReserva) {
        return tarifaRepository.existsByIdReserva(idReserva);
    }

    public TarifaEntity crearTarifa(int numeroVueltas, Long idReserva) {
        if (tarifaRepository.existsByIdReserva(idReserva)) {
            throw new IllegalStateException("Error: La reserva con ID " + idReserva + " ya existe.");
        }

        TarifaEntity tarifa = new TarifaEntity();
        tarifa.setidReserva(idReserva);
        tarifa.setNumeroVueltas(numeroVueltas);

        // Asignar precio y duración según la cantidad de vueltas
        if (numeroVueltas == 10) {
            tarifa.setPrecio(15000);
            tarifa.setDuracionMaxima(10);
            tarifa.setDuracionTotal(30);
        } else if (numeroVueltas == 15) {
            tarifa.setPrecio(20000);
            tarifa.setDuracionMaxima(15);
            tarifa.setDuracionTotal(35);
        } else if (numeroVueltas == 20) {
            tarifa.setPrecio(25000);
            tarifa.setDuracionMaxima(20);
            tarifa.setDuracionTotal(40);
        } else {
            throw new IllegalArgumentException("Número de vueltas no válido. Solo se permiten 10, 15 o 20.");
        }

        return tarifaRepository.save(tarifa);
    }

    public TarifaEntity obtenerTarifaPorIdReserva(Long IdReserva) {
        return tarifaRepository.findByidReserva(IdReserva).orElse(null);
    }

    public double obtenerPrecioTarifaPorIdReserva(Long IdReserva) {
        TarifaEntity tarifa = obtenerTarifaPorIdReserva(IdReserva);
        return tarifa.getPrecio();
    }
}