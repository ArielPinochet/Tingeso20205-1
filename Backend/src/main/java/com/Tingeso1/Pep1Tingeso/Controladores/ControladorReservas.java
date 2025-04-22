package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioClientes;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioReservas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/reservas")
public class ControladorReservas {

    private final ServicioReservas servicioReservas;
    private final ServicioClientes servicioClientes;

    public ControladorReservas(ServicioReservas servicioReservas, ServicioClientes servicioClientes) {
        this.servicioReservas = servicioReservas;
        this.servicioClientes = servicioClientes;
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntidadReservas> obtenerReserva(@PathVariable Long id) {
        return servicioReservas.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EntidadReservas>> listarReservas() {
        List<EntidadReservas> reservas = servicioReservas.listarTodas();
        return ResponseEntity.ok(reservas);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        servicioReservas.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<EntidadReservas> crearReserva(@RequestBody EntidadReservas reserva) {
        Optional<EntidadClientes> cliente = servicioClientes.buscarPorId(reserva.getClienteResponsable().getIdCliente());
        if (cliente.isPresent()) {
            reserva.setClienteResponsable(cliente.get());
            EntidadReservas nuevaReserva = servicioReservas.guardarReserva(reserva);
            return ResponseEntity.ok(nuevaReserva);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntidadReservas> editarReserva(@PathVariable Long id, @RequestBody EntidadReservas reservaActualizada) {
        Optional<EntidadReservas> reservaExistente = servicioReservas.buscarPorId(id);

        if (reservaExistente.isPresent()) {
            if (reservaExistente.get().getFechaReserva().isEqual(LocalDate.now())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 🔹 No se puede editar si es hoy
            }

            reservaExistente.get().setFechaReserva(reservaActualizada.getFechaReserva());
            reservaExistente.get().setHoraInicio(reservaActualizada.getHoraInicio());
            reservaExistente.get().setNumeroVueltas(reservaActualizada.getNumeroVueltas());
            reservaExistente.get().setCantidadPersonas(reservaActualizada.getCantidadPersonas());
            reservaExistente.get().setDiaEspecial(reservaActualizada.getDiaEspecial());
            reservaExistente.get().setEstadoReserva("Pendiente");
            reservaExistente.get().setPrecioTotal(reservaActualizada.getPrecioTotal());
            reservaExistente.get().setClienteResponsable(reservaActualizada.getClienteResponsable());
            reservaExistente.get().setCarros(reservaActualizada.getCarros());

            EntidadReservas reservaGuardada = servicioReservas.guardarReserva(reservaExistente.get());
            return ResponseEntity.ok(reservaGuardada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
