package com.Pep2.Tingeso.ReservaPago;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {


    private final ReservaRepository repositorioReserva;



    public ReservaService(ReservaRepository repositorioReserva) {
        this.repositorioReserva = repositorioReserva;

    }

    public ReservaEntity guardarReserva(ReservaEntity reserva) {
        return repositorioReserva.save(reserva);
    }

    public Optional<ReservaEntity> buscarPorId(Long id) {
        return repositorioReserva.findById(id);
    }

    public List<ReservaEntity> listarTodas() {
        return repositorioReserva.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioReserva.deleteById(id);
    }

    public ReservaEntity ObtenerReservaPorId(Long idReserva) {
        return  repositorioReserva.findByIdReserva(idReserva);
    }

}