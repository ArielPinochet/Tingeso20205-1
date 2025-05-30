package com.Pep2.Tingeso.TarifaEspeciales;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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
            List<String> feriados = restTemplate.getForObject(url, List.class);
            return feriados != null && feriados.contains(fecha.toString());
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

}