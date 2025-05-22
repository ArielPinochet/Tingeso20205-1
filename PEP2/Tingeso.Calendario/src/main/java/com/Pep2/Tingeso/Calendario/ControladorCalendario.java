package com.Pep2.Tingeso.Calendario;


import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
}
