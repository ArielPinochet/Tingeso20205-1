package com.Pep2.Tingeso.ReservaPago;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaPagoService {

    private final ReservaPagoRepository reservaPagoRepository;

    public ReservaPagoService(ReservaPagoRepository reservaPagoRepository) {
        this.reservaPagoRepository = reservaPagoRepository;
    }

    public Optional<ReservaPagoEntity> obtenerPorId(Long idReserva) {
        return reservaPagoRepository.findById(idReserva);
    }

    public List<ReservaPagoEntity> obtenerPorCliente(Long idCliente) {
        return reservaPagoRepository.findByIdCliente(idCliente);
    }

    public ReservaPagoEntity guardarReserva(ReservaPagoEntity reserva) {
        return reservaPagoRepository.save(reserva);
    }
}