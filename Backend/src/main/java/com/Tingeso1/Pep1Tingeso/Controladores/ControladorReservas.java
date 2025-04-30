package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Repositorios.*;
import com.Tingeso1.Pep1Tingeso.Servicios.EmailService;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioCarros;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioClientes;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioReservas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


@RestController
@RequestMapping("/reservas")
public class ControladorReservas {

    private final ServicioReservas servicioReservas;
    private final ServicioClientes servicioClientes;
    private final ServicioCarros servicioCarros;
    private final EmailService emailService;
    private final RepositorioReserva repositorioReserva;

    public ControladorReservas(ServicioReservas servicioReservas, ServicioClientes servicioClientes, ServicioCarros servicioCarros, EmailService emailService, RepositorioReserva repositorioReserva) {
        this.servicioReservas = servicioReservas;
        this.servicioClientes = servicioClientes;
        this.servicioCarros = servicioCarros;
        this.emailService = emailService;
        this.repositorioReserva = repositorioReserva;
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

    // Endpoint para reporte por número de vueltas
    @GetMapping("/vueltas")
    public List<ReporteVueltas> getReporteVueltas() {
        // Se usa la instancia inyectada, no la interfaz en forma estática.
        return repositorioReserva.findIngresosPorVueltas();
    }

    // Endpoint para reporte por tiempo máximo
    @GetMapping("/tiempo")
    public List<ReporteTiempo> getReportePorTiempo() {
        return repositorioReserva.findIngresosPorTiempo();
    }

    // Endpoint para reporte por número de personas
    @GetMapping("/personas")
    public List<ReportePersonas> getReportePorPersonas() {
        return repositorioReserva.findIngresosPorPersonas();
    }

    // Endpoint para reporte de vueltas de mes a mes
    @GetMapping("/vueltas/mes")
    public ResponseEntity<?> getReporteVueltasMes(@RequestParam String start,
                                                  @RequestParam String end) {
        try {
            System.out.println("Parámetro start: " + start + " - Parámetro end: " + end);

            // Convertir a LocalDate: primer día del mes y último día del mes
            LocalDate startDate = LocalDate.parse(start + "-01");
            LocalDate endDate = LocalDate.parse(end + "-01")
                    .with(TemporalAdjusters.lastDayOfMonth());

            List<ReporteVueltas> reporte = repositorioReserva.findIngresosPorVueltasMes(startDate, endDate);
            return ResponseEntity.ok(reporte);
        } catch (DateTimeParseException ex) {
            System.err.println("Error parseando fechas: " + ex.getMessage());
            return ResponseEntity.badRequest().body("Error en el formato de las fechas. Se espera formato YYYY-MM");
        }
    }

    // Endpoint para reporte de tiempo de mes a mes
    @GetMapping("/tiempo/mes")
    public ResponseEntity<?> getReporteTiempoMes(@RequestParam String start,
                                                 @RequestParam String end) {
        try {
            System.out.println("Parámetro start: " + start + " - Parámetro end: " + end);

            LocalDate startDate = LocalDate.parse(start + "-01");
            LocalDate endDate = LocalDate.parse(end + "-01")
                    .with(TemporalAdjusters.lastDayOfMonth());

            List<ReporteTiempo> reporte = repositorioReserva.findIngresosPorTiempoMes(startDate, endDate);
            return ResponseEntity.ok(reporte);
        } catch (DateTimeParseException ex) {
            System.err.println("Error parseando fechas: " + ex.getMessage());
            return ResponseEntity.badRequest().body("Error en el formato de las fechas. Se espera formato YYYY-MM");
        }
    }

    // Endpoint para reporte de personas de mes a mes, con desglose por categoría
    @GetMapping("/personas/mes")
    public ResponseEntity<?> getReportePersonasMes(@RequestParam String start,
                                                   @RequestParam String end) {
        try {
            System.out.println("Parámetro start: " + start + " - Parámetro end: " + end);

            LocalDate startDate = LocalDate.parse(start + "-01");
            LocalDate endDate = LocalDate.parse(end + "-01")
                    .with(TemporalAdjusters.lastDayOfMonth());

            List<ReportePersonas> reporte = repositorioReserva.findIngresosPorPersonasMes(startDate, endDate);
            return ResponseEntity.ok(reporte);
        } catch (DateTimeParseException ex) {
            System.err.println("Error parseando fechas: " + ex.getMessage());
            return ResponseEntity.badRequest().body("Error en el formato de las fechas. Se espera formato YYYY-MM");
        }
    }

    @GetMapping("/mes")
    public ResponseEntity<?> getReportePorMes(@RequestParam String start,
                                              @RequestParam String end) {
        try {
            // Logging de los parámetros recibidos
            System.out.println("Parámetro start: " + start + " - Parámetro end: " + end);

            // Concatenar "-01" para obtener el primer día del mes y luego calcular el último día del mes del end
            LocalDate startDate = LocalDate.parse(start + "-01");
            LocalDate endDate = LocalDate.parse(end + "-01").with(TemporalAdjusters.lastDayOfMonth());

            List<ReporteMes> reporte = repositorioReserva.findIngresosPorMes(startDate, endDate);
            return ResponseEntity.ok(reporte);
        } catch (DateTimeParseException ex) {
            // En caso de error en el parse, devolver un 400 con un mensaje descriptivo.
            System.err.println("Error parseando fechas: " + ex.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body("Error en el formato de las fechas. Se espera formato YYYY-MM");
        }
    }
}
