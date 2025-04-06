package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioCarros;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioCarros {
    
    private final RepositorioCarros repositorioCarros;

    public ServicioCarros(RepositorioCarros repositorioCarros) {
        this.repositorioCarros = repositorioCarros;
    }

    public EntidadCarros guardarCarros(EntidadCarros Carro) {
        return repositorioCarros.save(Carro);
    }

    public Optional<EntidadCarros> buscarPorCodigo(String codigoCarros) {
        return repositorioCarros.findById(codigoCarros);
    }

    public List<EntidadCarros> listarTodos() {
        return repositorioCarros.findAll();
    }

    public void eliminarPorCodigo(String codigoCarros) {
        repositorioCarros.deleteById(codigoCarros);
    }
}