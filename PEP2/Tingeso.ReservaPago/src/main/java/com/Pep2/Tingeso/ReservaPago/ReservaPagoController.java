package com.Pep2.Tingeso.ReservaPago;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ReservaPagoController {

    private final ReservaService servicioReservas;
    private final CarrosService servicioCarros;
    private final EmailService emailService;
    private final ComprobanteService servicioComprobantePago;

    public ReservaPagoController(ReservaService servicioReservas, CarrosService servicioCarros, EmailService emailService, ComprobanteService servicioComprobantePago) {
        this.servicioReservas = servicioReservas;
        this.servicioCarros = servicioCarros;
        this.emailService = emailService;
        this.servicioComprobantePago = servicioComprobantePago;
    }

    // 🚗 **Endpoints de Carros**
    @PostMapping("/carros")
    public ResponseEntity<CarrosEntity> crearCarro(@RequestBody CarrosEntity carro) {
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
            @RequestParam("idReserva") Long idReserva,
            @RequestParam("fechaEmision") String fechaEmision,
            @RequestParam("totalConIva") Double totalConIva,
            @RequestParam("archivoPdf") MultipartFile archivoPdf,
            @RequestParam("correosClientes") String correosClientes) {
        try {
            ComprobanteEntity comprobante = new ComprobanteEntity();
            comprobante.setIdReserva(idReserva);
            comprobante.setFechaEmision(LocalDate.parse(fechaEmision));
            comprobante.setTotalConIva(totalConIva);
            comprobante.setArchivoPdf(Base64.getEncoder().encodeToString(archivoPdf.getBytes()));

            List<String> listaCorreos = Arrays.asList(correosClientes.split(","));
            comprobante.setCorreosClientes(listaCorreos);

            ComprobanteEntity comprobanteGuardado = servicioComprobantePago.guardarComprobante(comprobante);
            servicioComprobantePago.enviarComprobante(comprobanteGuardado);

            return ResponseEntity.ok(comprobanteGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar comprobante.");
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

    @GetMapping("/comprobantes")
    public ResponseEntity<List<ComprobanteEntity>> listarComprobantes() {
        return ResponseEntity.ok(servicioComprobantePago.listarTodos());
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
    public ResponseEntity<ReservaEntity> crearReserva(@RequestBody ReservaEntity reserva) {
        if (reserva.getNombreCliente() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(servicioReservas.guardarReserva(reserva));
    }

    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) {
        if (servicioReservas.buscarPorId(id).isPresent()) {
            servicioReservas.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).body("La reserva con ID " + id + " no existe.");
    }

    @PutMapping("/reservas/{id}")
    public ResponseEntity<?> editarReserva(@PathVariable Long id, @RequestBody ReservaEntity reservaActualizada) {
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
