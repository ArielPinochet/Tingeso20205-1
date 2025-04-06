package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioReservas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reservas")
public class ControladorReservas {

    private final ServicioReservas servicioReservas;

    public ControladorReservas(ServicioReservas servicioReservas) {
        this.servicioReservas = servicioReservas;
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntidadReservas> obtenerReserva(@PathVariable Long id) {
        return servicioReservas.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EntidadReservas>> listarReservas() {
        return ResponseEntity.ok(servicioReservas.listarTodas());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        servicioReservas.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<EntidadReservas> crearReserva(@RequestBody EntidadReservas reserva) {
        boolean disponible = servicioReservas.validarDisponibilidadHorario(reserva.getFechaReserva(), reserva.getHoraInicio(), reserva.getDuracionTotal());

        if (!disponible) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        return ResponseEntity.ok(servicioReservas.guardarReserva(reserva));
    }


}
