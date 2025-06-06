package com.Pep2.Tingeso.Descuento;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class DescuentoService {

    private final DescuentoRepository descuentoRepository;
    private final RestTemplate restTemplate;

    public DescuentoService(DescuentoRepository descuentoRepository, RestTemplate restTemplate) {
        this.descuentoRepository = descuentoRepository;
        this.restTemplate = restTemplate;
    }

    public double calcularDescuentoTotal(int numeroPersonas, Long idReserva, String nombreCliente) {
        double descuentoPorPersonas = calcularDescuentoPorPersonas(numeroPersonas);
        double descuentoClienteFrecuente = obtenerDescuentoClienteFrecuente(nombreCliente);
        double descuentoTarifaEspecial = obtenerDescuentoTarifaEspecial(idReserva);

        // Cálculo final
        double descuentoTotal = descuentoPorPersonas + descuentoClienteFrecuente + descuentoTarifaEspecial;

        return Math.min(descuentoTotal, 100.0); // No puede superar el 100%
    }

    private double calcularDescuentoPorPersonas(int numeroPersonas) {
        if (numeroPersonas >= 1 && numeroPersonas <= 2) return 0.0;
        if (numeroPersonas >= 3 && numeroPersonas <= 5) return 10.0;
        if (numeroPersonas >= 6 && numeroPersonas <= 10) return 20.0;
        if (numeroPersonas >= 11 && numeroPersonas <= 15) return 30.0;
        return 0.0;
    }

    private double obtenerDescuentoClienteFrecuente(String nombreCliente) {
        String url = "http://localhost:8088/api/clientes/descuento/" + nombreCliente;
        return restTemplate.getForObject(url, Double.class);
    }

    private double obtenerDescuentoTarifaEspecial(Long idReserva) {
        String url = "http://localhost:8087/api/tarifas-especiales/obtener/" + idReserva;
        return restTemplate.getForObject(url, Double.class);
    }

    public Optional<DescuentoEntity> obtenerDescuentoPorReserva(Long idReserva) {
        return descuentoRepository.findByIdReserva(idReserva);
    }

}