package com.Pep2.Tingeso.TarifaEspeciales;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.time.DayOfWeek;


@Service
public class TarifaEspecialService {

    private final TarifaEspecialRepository tarifaEspecialRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public TarifaEspecialService(TarifaEspecialRepository tarifaEspecialRepository) {
        this.tarifaEspecialRepository = tarifaEspecialRepository;
    }


    public boolean esFeriado(LocalDate fecha) {
        String url = "https://api.boostr.cl/holidays.json";

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            List<Map<String, Object>> feriados = (List<Map<String, Object>>) response.getBody().get("data");

            return feriados != null && feriados.stream()
                    .anyMatch(f -> f.get("date").equals(fecha.toString()));
        } catch (Exception e) {
            System.err.println("Error al obtener los feriados: " + e.getMessage());
            return false;
        }
    }




    public TarifaEspecialEntity crearTarifaEspecial(LocalDate fecha, double porcentajeDescuento, Long IdReserva) {
        boolean esFinDeSemanaFestivo = calcularFinDeSemanaFestivo(fecha);

        TarifaEspecialEntity tarifaEspecial = new TarifaEspecialEntity();
        tarifaEspecial.setFinDeSemanaFestivo(esFinDeSemanaFestivo);
        tarifaEspecial.setPorcentajeDescuento(porcentajeDescuento);
        tarifaEspecial.setIdReserva(IdReserva);

        return tarifaEspecialRepository.save(tarifaEspecial);
    }

    public TarifaEspecialEntity crearTarifaEspecial2(LocalDate fecha, boolean esDiaEspecial , Long IdReserva, int CantidadPersonas) {
        TarifaEspecialEntity tarifaEspecial = new TarifaEspecialEntity();
        tarifaEspecial.setIdReserva(IdReserva);
        boolean  esFinDeSemanaFestivo = calcularFinDeSemanaFestivo(fecha);
        tarifaEspecial.setFinDeSemanaFestivo(esFinDeSemanaFestivo);
        tarifaEspecial.setDiaEspecial(esDiaEspecial);
        double porcentajeDescuento = calcularPorcentajeDescuento(esFinDeSemanaFestivo,esDiaEspecial,CantidadPersonas);
        tarifaEspecial.setPorcentajeDescuento(porcentajeDescuento);
        return tarifaEspecialRepository.save(tarifaEspecial);
    }

    private boolean calcularFinDeSemanaFestivo(LocalDate fecha) {
        boolean esFinDeSemana = fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean esFeriado = esFeriado(fecha);

        return esFinDeSemana || esFeriado;
    }

    public TarifaEspecialEntity obtenerTarifaEspecialPorIdReserva(Long IdReserva) {
        return tarifaEspecialRepository.findByIdReserva(IdReserva).orElseGet(() -> {
            TarifaEspecialEntity tarifaDefault = new TarifaEspecialEntity();
            tarifaDefault.setIdReserva(IdReserva);
            tarifaDefault.setPorcentajeDescuento(0.0);
            tarifaDefault.setFinDeSemanaFestivo(false);
            tarifaDefault.setDiaEspecial(false);
            return tarifaDefault;
        });
    }


    public double calcularPorcentajeDescuento(boolean esFinDeSemanaFestivo, boolean esDiaEspecial, int cantidadPersonas) {
        double porcentajeDescuento = 0.0;

        // Aplicar descuento por día especial (cumpleaños)
        if (esDiaEspecial) {
            if (cantidadPersonas >= 3 && cantidadPersonas <= 5) {
                porcentajeDescuento = -50.0 / cantidadPersonas; // Descuento del 50% a una persona
            } else if (cantidadPersonas >= 6 && cantidadPersonas <= 10) {
                porcentajeDescuento = -(2 * 50.0) / cantidadPersonas; // Descuento del 50% a dos personas
            }
        }
        System.out.println("Descuento de Personas y cumpleaños es: " + porcentajeDescuento);

        // Aplicar incremento por fin de semana o festivo
        if (esFinDeSemanaFestivo) {
            porcentajeDescuento += 10.0; // Aumento del 10% para todos
        }
        System.out.println("Descuento total es: " + porcentajeDescuento);

        return porcentajeDescuento;
    }

    public boolean existeReserva(Long idReserva) {
        return tarifaEspecialRepository.existsByIdReserva(idReserva);
    }
}