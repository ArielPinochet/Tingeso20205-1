package com.Pep2.Tingeso.Calendario;


import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ServicioCalendario {

    private final RepositorioCalendario calendarioRepository;

    public ServicioCalendario(RepositorioCalendario calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    public List<EntidadCalendario> obtenerOcupacionPorFecha(LocalDate fecha) {
        return calendarioRepository.findByFecha(fecha);
    }
    public EntidadCalendario actualizarFechaYHora(Long reservaId, LocalDate nuevaFecha, LocalTime nuevaHoraInicio,int duracionMinutos) {
        EntidadCalendario calendario = calendarioRepository.findByReservaId(reservaId);
        calendario.setFecha(nuevaFecha);
        calendario.setHoraInicio(nuevaHoraInicio);
        calendario.setHoraFin(nuevaHoraInicio.plusMinutes(duracionMinutos)); // 🕒 recalcular horaFin

        return calendarioRepository.save(calendario);
    }

    public void eliminarPorReservaId(Long reservaId) {
        EntidadCalendario calendario = calendarioRepository.findByReservaId(reservaId);
        calendarioRepository.delete(calendario);
    }


    public List<EntidadCalendario> obtenerTodos(){
        return calendarioRepository.findAll();
    }
    public EntidadCalendario guardarReserva(EntidadCalendario reserva) {
        return calendarioRepository.save(reserva);
    }
}
