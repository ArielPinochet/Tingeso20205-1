package com.Pep2.Tingeso.ReservaPago;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {


    private final ReservaRepository repositorioReserva;
    private final RestTemplate restTemplate;


    public ReservaService(ReservaRepository repositorioReserva,RestTemplate restTemplate) {
        this.repositorioReserva = repositorioReserva;
        this.restTemplate = restTemplate;
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

    // 🔹 Métodos internos para crear la tarifa, tarifa especial y descuento
    public void crearTarifaInterna(int numeroVueltas, Long idReserva) {
        restTemplate.postForEntity("http://localhost:8080/api/tarifas/", null,
                ResponseEntity.class, numeroVueltas, idReserva);
    }

    public void crearTarifaEspecialInterna(ReservaEntity reserva) {
        String url = String.format("http://localhost:8080/api/tarifas-especiales/CrearTarifaEspecial/?fecha=%s&esDiaEspecial=%b&IdReserva=%d&CantidadPersonas=%d",
                reserva.getFechaReserva(), reserva.getDiaEspecial(), reserva.getIdReserva(), reserva.getCantidadPersonas());
        restTemplate.postForEntity(url, null, ResponseEntity.class);
    }

    public void crearDescuentoInterno(int numeroPersonas, Long idReserva, String nombreCliente) {
        restTemplate.postForEntity("http://localhost:8080/api/descuentos/", null,
                ResponseEntity.class, numeroPersonas, idReserva, nombreCliente);
    }

}