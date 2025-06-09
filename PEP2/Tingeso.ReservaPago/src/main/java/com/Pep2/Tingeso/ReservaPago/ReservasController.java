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
@RequestMapping("/api/reservasssssss")
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
    public ResponseEntity<?> obtenerReserva(@PathVariable Long id) {
        Optional<ReservaEntity> reservaOpt = servicioReservas.buscarPorId(id);

        // 🔹 Si la reserva no existe, devuelve un error con mensaje personalizado
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse("La reserva con ID " + id + " no existe."));
        }

        // 🔹 Si existe, retorna la reserva
        return ResponseEntity.ok(reservaOpt.get());
    }





    @GetMapping("/obtener")
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
        if (reserva.getNombreCliente() == null) {
            return ResponseEntity.badRequest().body(null); // 🔹 Evita guardar sin cliente
        }

        ReservaEntity nuevaReserva = servicioReservas.guardarReserva(reserva);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("reserva", nuevaReserva);

        // 🔹 Intentar crear tarifa
        try {
            servicioReservas.crearTarifaInterna(nuevaReserva.getNumeroVueltas(), nuevaReserva.getIdReserva());
            resultado.put("tarifa", "Creada exitosamente");
        } catch (Exception e) {
            resultado.put("tarifa", "Error al crear la tarifa: " + e.getMessage());
        }

        // 🔹 Intentar crear tarifa especial
        try {
            servicioReservas.crearTarifaEspecialInterna(nuevaReserva);
            resultado.put("tarifaEspecial", "Creada exitosamente");
        } catch (Exception e) {
            resultado.put("tarifaEspecial", "Error al crear la tarifa especial: " + e.getMessage());
        }

        // 🔹 Intentar crear descuento
        try {
            servicioReservas.crearDescuentoInterno(nuevaReserva.getCantidadPersonas(), nuevaReserva.getIdReserva(), nuevaReserva.getNombreCliente());
            resultado.put("descuento", "Creado exitosamente");
        } catch (Exception e) {
            resultado.put("descuento", "Error al crear el descuento: " + e.getMessage());
        }

        return ResponseEntity.ok(nuevaReserva);
    }



    @GetMapping("/ganancias/")
    public ResponseEntity<List<ReporteDTO>> obtenerGanancias(@RequestParam LocalDate inicio,
                                                             @RequestParam LocalDate fin) {
        List<ReporteDTO> reporte = servicioReservas.obtenerGananciasEntreMeses(inicio, fin);
        return ResponseEntity.ok(reporte);
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
