package com.Pep2.Tingeso.ReservaPago;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/app")
public class ReservaPagoController {

    private final ReservaService servicioReservas;
    private final CarrosService servicioCarros;
    private final EmailService emailService;
    private final ComprobanteService servicioComprobantePago;
    private final ComprobanteRepository comprobanteRepository;

    public ReservaPagoController(ReservaService servicioReservas, CarrosService servicioCarros, EmailService emailService,
                                 ComprobanteService servicioComprobantePago, ComprobanteRepository comprobanteRepository) {
        this.servicioReservas = servicioReservas;
        this.servicioCarros = servicioCarros;
        this.emailService = emailService;
        this.servicioComprobantePago = servicioComprobantePago;
        this.comprobanteRepository = comprobanteRepository;
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/carros")
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Allow-Credentials", "false")
                .build();
    }

    // 🚗 **Endpoints de Carros**
    @PostMapping("/carros")
    public ResponseEntity<CarrosEntity> crearCarro(@RequestParam String codigoCarros,
                                                   @RequestParam String modelo,
                                                   @RequestParam String estado) {
        CarrosEntity carro = new CarrosEntity();
        carro.setCodigoCarros(codigoCarros);
        carro.setModelo(modelo);
        carro.setEstado(estado);
        return ResponseEntity.ok(servicioCarros.guardarCarros(carro));
    }

    @GetMapping("/carros/{codigo}")
    public ResponseEntity<?> obtenerCarro(@PathVariable String codigo) {
        Optional<CarrosEntity> carroOpt = servicioCarros.buscarPorCodigo(codigo);
        if (carroOpt.isEmpty()) {
            return ResponseEntity.status(404).body("El carro con código " + codigo + " no existe.");
        }
        return ResponseEntity.ok(carroOpt.get());
    }

    @GetMapping("/carros-ocupados")
    public ResponseEntity<List<String>> obtenerCarrosOcupados(
            @RequestParam String fecha,   // formato: yyyy-MM-dd
            @RequestParam String hora     // formato: HH:mm
    ) {
        try {
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora); // Combinar fecha y hora

            List<ReservaEntity> reservas = servicioReservas.listarTodas(); // trae todas las reservas
            List<String> carrosOcupados = new ArrayList<>();

            for (ReservaEntity reserva : reservas) {
                LocalDateTime inicio = reserva.getHoraInicio(); // debe ser LocalDateTime
                LocalDateTime fin = inicio.plusMinutes(reserva.getDuracionTotal());

                if (!fechaHora.isBefore(inicio) && !fechaHora.isAfter(fin)) {
                    carrosOcupados.addAll(reserva.getCarros());
                }
            }

            return ResponseEntity.ok(carrosOcupados);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of("Error al procesar fecha/hora: " + e.getMessage()));
        }
    }

    @GetMapping("/carros")
    public ResponseEntity<List<CarrosEntity>> listarCarros() {
        return ResponseEntity.ok(servicioCarros.listarTodos());
    }

    @DeleteMapping("/carros/{codigo}")
    public ResponseEntity<?> eliminarCarro(@PathVariable String codigo) {
        if (servicioCarros.buscarPorCodigo(codigo).isPresent()) {
            servicioCarros.eliminarPorCodigo(codigo);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).body("El carro con código " + codigo + " no existe.");
    }

    @PutMapping("/carros/{codigo}")
    public ResponseEntity<?> actualizarCarro(@PathVariable String codigo, @RequestBody CarrosEntity carroActualizado) {
        Optional<CarrosEntity> carroOpt = servicioCarros.buscarPorCodigo(codigo);
        if (carroOpt.isEmpty()) {
            return ResponseEntity.status(404).body("El carro con código " + codigo + " no existe.");
        }
        CarrosEntity carro = carroOpt.get();
        carro.setModelo(carroActualizado.getModelo());
        carro.setEstado(carroActualizado.getEstado());
        return ResponseEntity.ok(servicioCarros.guardarCarros(carro));
    }

    // 🧾 **Endpoints de Comprobantes**
    @PostMapping("/comprobantes")
    public ResponseEntity<?> crearComprobante(
            @RequestParam Long idReserva,
            @RequestParam String fechaEmision,
            @RequestParam Double totalConIva,
            @RequestParam String fechaReserva,
            @RequestParam String horaInicio,
            @RequestParam int cantidadPersonas,
            @RequestParam int numeroVueltas,
            @RequestParam int duracionTotal,
            @RequestParam String nombreCliente,
            @RequestParam double precioFinalSinIVA,
            @RequestParam List<String> correosClientes) {
        try {
            // 🔹 Generamos el PDF internamente
            byte[] pdfBytes = servicioComprobantePago.generarComprobantePDF(idReserva, fechaEmision, fechaReserva, horaInicio,
                    cantidadPersonas, numeroVueltas, duracionTotal, nombreCliente, totalConIva, precioFinalSinIVA, correosClientes);

            if (comprobanteRepository.existsByIdReserva(idReserva)) {
                throw new IllegalArgumentException("🚨 Ya existe un comprobante para la reserva ID " + idReserva);
            }

            // 🔹 Guardamos el comprobante en la BD
            ComprobanteEntity comprobante = new ComprobanteEntity();
            comprobante.setIdReserva(idReserva);
            comprobante.setFechaEmision(LocalDate.parse(fechaEmision));
            comprobante.setTotalConIva(totalConIva);
            comprobante.setArchivoPdf(Base64.getEncoder().encodeToString(pdfBytes));
            comprobante.setCorreosClientes(correosClientes);

            ComprobanteEntity comprobanteGuardado = servicioComprobantePago.guardarComprobante(comprobante);

            // 🔹 Enviamos el comprobante por correo
            emailService.enviarComprobantePorCorreo(correosClientes, pdfBytes,nombreCliente);

            return ResponseEntity.ok(comprobanteGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("🚨 Error al generar y enviar el comprobante: " + e.getMessage());
        }
    }


    @GetMapping("/comprobantes/{id}")
    public ResponseEntity<?> obtenerComprobante(@PathVariable Long id) {
        Optional<ComprobanteEntity> comprobanteOpt = servicioComprobantePago.buscarPorId(id);
        if (comprobanteOpt.isEmpty()) {
            return ResponseEntity.status(404).body("El carro con id " + id + " no existe.");
        }
        return ResponseEntity.ok(comprobanteOpt.get());
    }

    @GetMapping("/ganancias")
    public ResponseEntity<List<ReporteDTO>> obtenerGanancias(
            @RequestParam String inicio,
            @RequestParam String fin) {

        LocalDate inicioDate = LocalDate.parse(inicio.trim());
        LocalDate finDate = LocalDate.parse(fin.trim());

        System.out.println("🔹 Generando reporte de ganancias desde " + inicio + " hasta " + fin);

        List<ReporteDTO> reporte = servicioReservas.obtenerGananciasEntreMeses(inicioDate, finDate);

        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/comprobantes")
    public ResponseEntity<List<ComprobanteEntity>> listarComprobantes() {
        return ResponseEntity.ok(servicioComprobantePago.listarTodos());
    }

    @GetMapping("/test")
    public ResponseEntity<?> enviarCorreoDePrueba() {
        try {

            emailService.enviarCorreoPrueba();
            return ResponseEntity.ok("✔️ Correo de prueba enviado correctamente!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("🚨 Error al enviar correo de prueba: " + e.getMessage());
        }
    }

    @DeleteMapping("/comprobantes/{id}")
    public ResponseEntity<?> eliminarComprobante(@PathVariable Long id) {
        if (servicioComprobantePago.buscarPorId(id).isPresent()) {
            servicioComprobantePago.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).body("El comprobante con ID " + id + " no existe.");
    }

    // 🔹 **Endpoints de Reservas**
    @GetMapping("/reservas/{id}")
    public ResponseEntity<?> obtenerReserva(@PathVariable Long id) {
        Optional<ReservaEntity> reservaOpt = servicioReservas.buscarPorId(id);

        // 🔹 Si la reserva no existe, devuelve un error con mensaje personalizado
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse("La reserva con ID " + id + " no existe."));
        }

        // 🔹 Si existe, retorna la reserva
        return ResponseEntity.ok(reservaOpt.get());
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaEntity>> listarReservas() {
        return ResponseEntity.ok(servicioReservas.listarTodas());
    }


    @PostMapping("/reservas")
    public ResponseEntity<?> crearReserva(
            @RequestParam String nombreCliente,
            @RequestParam String fechaReserva,
            @RequestParam String horaInicio,
            @RequestParam Integer numeroVueltas,
            @RequestParam Integer cantidadPersonas,
            @RequestParam Boolean diaEspecial,
            @RequestParam List<String> codigosCarros
    ) {
        try {
            ReservaEntity reserva = servicioReservas.guardarReserva(
                    nombreCliente, fechaReserva, horaInicio, numeroVueltas, cantidadPersonas, diaEspecial,codigosCarros);
            return ResponseEntity.ok(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) {
        if (servicioReservas.buscarPorId(id).isPresent()) {
            servicioReservas.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).body("La reserva con ID " + id + " no existe.");
    }


    @GetMapping("/gananciaspersonas")
    public ResponseEntity<List<ReportePersonasDTO>> obtenerGananciasPersonas(
            @RequestParam String inicio,
            @RequestParam String fin) {

        LocalDate inicioDate = LocalDate.parse(inicio.trim());
        LocalDate finDate = LocalDate.parse(fin.trim());

        System.out.println("🔹 Generando reporte de ganancias de personas desde " + inicio + " hasta " + fin);

        List<ReportePersonasDTO> reporte = servicioReservas.obtenerGananciasEntreMesesPersonas(inicioDate, finDate);

        return ResponseEntity.ok(reporte);
    }
    @GetMapping("/gananciasvueltas")
    public ResponseEntity<List<ReporteVueltasDTO>> obtenerGananciasVueltas(
            @RequestParam String inicio,
            @RequestParam String fin) {

        LocalDate inicioDate = LocalDate.parse(inicio.trim());
        LocalDate finDate = LocalDate.parse(fin.trim());

        System.out.println("🔹 Generando reporte de ganancias de ReporteVueltasDTO desde " + inicio + " hasta " + fin);

        List<ReporteVueltasDTO> reporte = servicioReservas.obtenerGananciasEntreMesesVueltas(inicioDate, finDate);

        return ResponseEntity.ok(reporte);
    }

    @PutMapping("/reservas/editar")
    public ResponseEntity<?> editarReserva(
            @RequestParam Long idReserva,
            @RequestParam String nombreCliente,
            @RequestParam String fechaReserva,
            @RequestParam String horaInicio,
            @RequestParam Integer numeroVueltas,
            @RequestParam Integer cantidadPersonas,
            @RequestParam Boolean diaEspecial,
            @RequestParam List<String> codigosCarros
    ) {
        try {
            if (codigosCarros.size() > 15) {
                return ResponseEntity.badRequest().body("Se permiten como máximo 15 códigos de carros.");
            }

            ReservaEntity reservaActualizada = servicioReservas.editarReserva(
                    idReserva, nombreCliente, fechaReserva, horaInicio,
                    numeroVueltas, cantidadPersonas, diaEspecial, codigosCarros
            );

            return ResponseEntity.ok(reservaActualizada);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Datos inválidos: " + e.getMessage());
        }
    }

    @GetMapping("/reservas/con-comprobante")
    public ResponseEntity<List<Long>> getIdsReservasConComprobante() {
        List<Long> ids = servicioComprobantePago.obtenerIdsReservasConComprobante();
        return ResponseEntity.ok(ids);
    }


}
