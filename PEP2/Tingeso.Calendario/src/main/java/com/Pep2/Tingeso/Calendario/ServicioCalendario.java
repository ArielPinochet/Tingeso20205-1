package com.Pep2.Tingeso.Calendario;


import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioCalendario {

    private final RepositorioCalendario calendarioRepository;

    public ServicioCalendario(RepositorioCalendario calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    public List<EntidadCalendario> obtenerOcupacionPorFecha(LocalDate fecha) {
        return calendarioRepository.findByFecha(fecha);
    }

    public EntidadCalendario guardarReserva(EntidadCalendario reserva) {
        return calendarioRepository.save(reserva);
    }
}
