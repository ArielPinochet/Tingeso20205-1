package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadHorarioReserva;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadDetalleReserva;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioReserva;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioHorarioReserva;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioReservas {

    private final RepositorioReserva repositorioReserva;
    private final RepositorioHorarioReserva repositorioHorarioReserva;



    public EntidadReservas guardarReservaConDetalles(EntidadReservas reserva, List<EntidadDetalleReserva> detalles) {
        reserva.setDetallesReserva(detalles);
        return repositorioReserva.save(reserva);
    }

    public boolean validarDisponibilidadHorario(LocalDate fecha, LocalTime horaInicio, int duracion) {
        List<EntidadHorarioReserva> ocupados = repositorioHorarioReserva.findByFecha(fecha);

        for (EntidadHorarioReserva bloque : ocupados) {
            if (horaInicio.isBefore(bloque.getHoraFin()) && bloque.getEstado().equals("RESERVADO")) {
                return false; // Horario ya está ocupado
            }
        }
        return true;
    }


    public ServicioReservas(RepositorioReserva repositorioReserva, RepositorioHorarioReserva repositorioHorarioReserva) {
        this.repositorioReserva = repositorioReserva;
        this.repositorioHorarioReserva = repositorioHorarioReserva;
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
