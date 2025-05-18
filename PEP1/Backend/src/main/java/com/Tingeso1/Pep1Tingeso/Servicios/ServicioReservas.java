package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Repositorios.*;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ServicioReservas {

    private final RepositorioReserva repositorioReserva;



    public ServicioReservas(RepositorioReserva repositorioReserva) {
        this.repositorioReserva = repositorioReserva;

    }

    public EntidadReservas guardarReserva(EntidadReservas reserva) {
        return repositorioReserva.save(reserva);
    }

    public Optional<EntidadReservas> buscarPorId(Long id) {
        return repositorioReserva.findById(id);
    }

    public List<EntidadReservas> listarTodas() {
        return repositorioReserva.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioReserva.deleteById(id);
    }


}
