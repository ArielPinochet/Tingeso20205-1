package com.Pep2.Tingeso.ReservaPago;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/reservas")
public class ReservasController {

    private final ReservaService servicioReservas;
    private final CarrosService servicioCarros;
    private final EmailService emailService;
    private final ReservaRepository repositorioReserva;

    public ReservasController(ReservaService servicioReservas, CarrosService servicioCarros, EmailService emailService, ReservaRepository repositorioReserva) {
        this.servicioReservas = servicioReservas;
        this.servicioCarros = servicioCarros;
        this.emailService = emailService;
        this.repositorioReserva = repositorioReserva;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReservaEntity> obtenerReserva(@PathVariable Long id) {
        return servicioReservas.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ReservaEntity>> listarReservas() {
        List<ReservaEntity> reservas = servicioReservas.listarTodas();
        return ResponseEntity.ok(reservas);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        servicioReservas.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ReservaEntity> crearReserva(@RequestBody ReservaEntity reserva) {
        if (reserva.getNombreCliente() == null ) {
            return ResponseEntity.badRequest().body(null); // 🔹 Evita guardar sin cliente
        }

        reserva.setCarros(reserva.getCarros()); // 🔹 Guarda directamente los objetos recibidos

        ReservaEntity nuevaReserva = servicioReservas.guardarReserva(reserva);
        // Enviar correo al cliente confirmando la reserva
        emailService.sendReservationConfirmation(nuevaReserva);
        return ResponseEntity.ok(nuevaReserva);
    }




    @PutMapping("/{id}")
    public ResponseEntity<ReservaEntity> editarReserva(@PathVariable Long id, @RequestBody ReservaEntity reservaActualizada) {
        Optional<ReservaEntity> reservaExistente = servicioReservas.buscarPorId(id);

        if (reservaExistente.isEmpty()) {
            return ResponseEntity.notFound().build(); // 🔹 Si la reserva no existe, devuelve 404
        }


        List<String> codigosCarrosSeleccionados = new ArrayList<>(new HashSet<>(reservaActualizada.getCarros())); // 🔹 Elimina duplicados

        List<String> carrosValidos = codigosCarrosSeleccionados.stream()
                .filter(codigo -> servicioCarros.buscarPorCodigo(codigo).isPresent()) // 🔹 Filtra solo los códigos existentes
                .collect(Collectors.toList());

        reservaExistente.get().setCarros(carrosValidos);
        reservaExistente.get().setFechaReserva(reservaActualizada.getFechaReserva());
        reservaExistente.get().setHoraInicio(reservaActualizada.getHoraInicio());
        reservaExistente.get().setNumeroVueltas(reservaActualizada.getNumeroVueltas());
        reservaExistente.get().setCantidadPersonas(reservaActualizada.getCantidadPersonas());
        reservaExistente.get().setDiaEspecial(reservaActualizada.getDiaEspecial());
        reservaExistente.get().setEstadoReserva("Pendiente");
        reservaExistente.get().SetNombreCliente(reservaActualizada.getNombreCliente());

        ReservaEntity reservaGuardada = servicioReservas.guardarReserva(reservaExistente.get());
        emailService.sendReservationEditedConfirmation(reservaGuardada);
        return ResponseEntity.ok(reservaGuardada);
    }

}
