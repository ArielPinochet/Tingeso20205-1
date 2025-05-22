package com.Pep2.Tingeso.ReservaPago;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaPagoController {

    private final ReservaPagoService reservaPagoService;

    public ReservaPagoController(ReservaPagoService reservaPagoService) {
        this.reservaPagoService = reservaPagoService;
    }

    @GetMapping("/{idReserva}")
    public Optional<ReservaPagoEntity> obtenerPorId(@PathVariable Long idReserva) {
        return reservaPagoService.obtenerPorId(idReserva);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<ReservaPagoEntity> obtenerPorCliente(@PathVariable Long idCliente) {
        return reservaPagoService.obtenerPorCliente(idCliente);
    }

    @PostMapping("/")
    public ReservaPagoEntity crearReserva(@RequestBody ReservaPagoEntity reserva) {
        return reservaPagoService.guardarReserva(reserva);
    }
}