package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Servicios.EmailService;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioCarros;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioClientes;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioReservas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/reservas")
public class ControladorReservas {

    private final ServicioReservas servicioReservas;
    private final ServicioClientes servicioClientes;
    private final ServicioCarros servicioCarros;
    private final EmailService emailService;

    public ControladorReservas(ServicioReservas servicioReservas, ServicioClientes servicioClientes, ServicioCarros servicioCarros, EmailService emailService) {
        this.servicioReservas = servicioReservas;
        this.servicioClientes = servicioClientes;
        this.servicioCarros = servicioCarros;
        this.emailService = emailService;
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
        if (reserva.getClienteResponsable() == null || reserva.getClienteResponsable().getIdCliente() == null) {
            return ResponseEntity.badRequest().body(null); // 🔹 Evita guardar sin cliente
        }

        Optional<EntidadClientes> cliente = servicioClientes.buscarPorId(reserva.getClienteResponsable().getIdCliente());
        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // 🔹 Si el cliente no existe, rechaza la solicitud
        }

        // ✅ Ya no es necesario buscar los carros en la BD, pues llegan como objetos completos
        reserva.setClienteResponsable(cliente.get());
        reserva.setCarros(reserva.getCarros()); // 🔹 Guarda directamente los objetos recibidos

        EntidadReservas nuevaReserva = servicioReservas.guardarReserva(reserva);
        // Enviar correo al cliente confirmando la reserva
        emailService.sendReservationConfirmation(nuevaReserva);
        return ResponseEntity.ok(nuevaReserva);
    }




    @PutMapping("/{id}")
    public ResponseEntity<EntidadReservas> editarReserva(@PathVariable Long id, @RequestBody EntidadReservas reservaActualizada) {
        Optional<EntidadReservas> reservaExistente = servicioReservas.buscarPorId(id);

        if (reservaExistente.isEmpty()) {
            return ResponseEntity.notFound().build(); // 🔹 Si la reserva no existe, devuelve 404
        }

        if (reservaExistente.get().getFechaReserva().isEqual(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 🔹 No se puede editar si es hoy
        }

        // 🔹 Procesar lista de carros sin duplicados
        Set<String> codigosCarrosSeleccionados = new HashSet<>(reservaActualizada.getCarros().stream().map(EntidadCarros::getCodigoCarros).toList());
        List<EntidadCarros> carrosValidos = new ArrayList<>();

        for (String codigo : codigosCarrosSeleccionados) {
            Optional<EntidadCarros> carroEncontrado = servicioCarros.buscarPorCodigo(codigo);
            carroEncontrado.ifPresent(carrosValidos::add);
        }

        reservaExistente.get().setCarros(carrosValidos);
        reservaExistente.get().setFechaReserva(reservaActualizada.getFechaReserva());
        reservaExistente.get().setHoraInicio(reservaActualizada.getHoraInicio());
        reservaExistente.get().setNumeroVueltas(reservaActualizada.getNumeroVueltas());
        reservaExistente.get().setCantidadPersonas(reservaActualizada.getCantidadPersonas());
        reservaExistente.get().setDiaEspecial(reservaActualizada.getDiaEspecial());
        reservaExistente.get().setEstadoReserva("Pendiente");
        reservaExistente.get().setPrecioTotal(reservaActualizada.getPrecioTotal());
        reservaExistente.get().setClienteResponsable(reservaActualizada.getClienteResponsable());

        EntidadReservas reservaGuardada = servicioReservas.guardarReserva(reservaExistente.get());
        emailService.sendReservationEditedConfirmation(reservaGuardada);
        return ResponseEntity.ok(reservaGuardada);
    }

}
