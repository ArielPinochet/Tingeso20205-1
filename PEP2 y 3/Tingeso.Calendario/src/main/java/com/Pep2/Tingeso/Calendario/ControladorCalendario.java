package com.Pep2.Tingeso.Calendario;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

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

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarCalendarioPorReservaId(@RequestParam Long reservaId) {
        try {
            calendarioService.eliminarPorReservaId(reservaId);
            return ResponseEntity.ok("Calendario eliminado para la reserva con ID " + reservaId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró calendario con reservaId: " + reservaId);
        }
    }

    @PutMapping("/editar-fecha")
    public ResponseEntity<?> editarFechaYHoraCalendario(
            @RequestParam Long reservaId,
            @RequestParam LocalDate nuevaFecha,
            @RequestParam LocalTime nuevaHoraInicio,
            @RequestParam int duracionMinutos
    ) {
        try {
            EntidadCalendario calendario = calendarioService.actualizarFechaYHora(reservaId, nuevaFecha, nuevaHoraInicio,duracionMinutos);
            return ResponseEntity.ok(calendario);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró calendario con reservaId: " + reservaId);
        }
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
