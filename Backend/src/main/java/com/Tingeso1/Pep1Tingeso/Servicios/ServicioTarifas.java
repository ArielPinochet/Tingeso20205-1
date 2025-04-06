package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadTarifa;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioTarifa;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioTarifas {

    private final RepositorioTarifa repositorioTarifa;

    public ServicioTarifas(RepositorioTarifa repositorioTarifa) {
        this.repositorioTarifa = repositorioTarifa;
    }

    public EntidadTarifa guardarTarifa(EntidadTarifa tarifa) {
        return repositorioTarifa.save(tarifa);
    }

    public Optional<EntidadTarifa> buscarPorId(Long id) {
        return repositorioTarifa.findById(id);
    }

    public List<EntidadTarifa> listarTodas() {
        return repositorioTarifa.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioTarifa.deleteById(id);
    }
}
