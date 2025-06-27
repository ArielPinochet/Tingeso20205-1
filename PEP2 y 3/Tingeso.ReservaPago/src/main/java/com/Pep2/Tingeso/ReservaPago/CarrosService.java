package com.Pep2.Tingeso.ReservaPago;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarrosService {
    
    private final CarrosRepository CarrosRepository;

    public CarrosService(CarrosRepository CarrosRepository) {
        this.CarrosRepository = CarrosRepository;
    }

    public CarrosEntity guardarCarros(CarrosEntity Carro) {
        return CarrosRepository.save(Carro);
    }

    public Optional<CarrosEntity> buscarPorCodigo(String codigoCarros) {
        return CarrosRepository.findById(codigoCarros);
    }

    public List<CarrosEntity> listarTodos() {
        return CarrosRepository.findAll();
    }

    public void eliminarPorCodigo(String codigoCarros) {
        CarrosRepository.deleteById(codigoCarros);
    }
}