package com.Pep2.Tingeso.Calendario;


import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendario")
public class ControladorCalendario {

    private final ServicioCalendario calendarioService;

    public ControladorCalendario(ServicioCalendario calendarioService) {
        this.calendarioService = calendarioService;
    }


    @GetMapping("/ocupacion/{fecha}")
    public List<EntidadCalendario> obtenerPorFecha(@PathVariable LocalDate fecha) {
        return calendarioService.obtenerOcupacionPorFecha(fecha);
    }

    @PostMapping("/reservar")
    public EntidadCalendario reservarHorario(@RequestBody EntidadCalendario reserva) {
        return calendarioService.guardarReserva(reserva);
    }

    @GetMapping("/todos")
    public List<EntidadCalendario> obtenerTodosLosCalendarios() {
        return calendarioService.obtenerTodos();
    }

    @PostMapping("/crear")
    public EntidadCalendario crearCalendario(
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime horaInicio,
            @RequestParam int duracionMinutos,
            @RequestParam String clienteNombre,
            @RequestParam Long reservaId) {

        // Crear entidad con los datos proporcionados
        EntidadCalendario nuevoCalendario = new EntidadCalendario();
        nuevoCalendario.setFecha(fecha);
        nuevoCalendario.setHoraInicio(horaInicio);
        nuevoCalendario.setHoraFin(horaInicio.plusMinutes(duracionMinutos)); // 📌 Calcula la hora de fin automáticamente
        nuevoCalendario.setClienteNombre(clienteNombre);
        nuevoCalendario.setReservaId(reservaId);
        nuevoCalendario.setEstado(EntidadCalendario.EstadoOcupacion.RESERVADO);

        // Guardar en la BD
        return calendarioService.guardarReserva(nuevoCalendario);
    }

}
