package com.Pep2.Tingeso.ReservaPago;


import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.sql.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {


    private final ReservaRepository repositorioReserva;
    private final RestTemplate restTemplate;
    private final DataSource dataSource;


    public ReservaService(ReservaRepository repositorioReserva,RestTemplate restTemplate, DataSource dataSource) {
        this.repositorioReserva = repositorioReserva;
        this.restTemplate = restTemplate;
        this.dataSource = dataSource;
    }

    public ReservaEntity salvarReserva(ReservaEntity reserva) {
        return repositorioReserva.save(reserva);
    }

    @Transactional
    public ReservaEntity guardarReserva(String nombreCliente, String fechaReserva, String horaInicio,
                                        Integer numeroVueltas, Integer cantidadPersonas, Boolean diaEspecial) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        ReservaEntity reserva = new ReservaEntity();
        reserva.SetNombreCliente(nombreCliente);
        reserva.setFechaReserva(LocalDateTime.parse(fechaReserva, formatter));
        reserva.setHoraInicio(LocalDateTime.parse(horaInicio, formatter));
        reserva.setNumeroVueltas(numeroVueltas);
        reserva.setCantidadPersonas(cantidadPersonas);
        reserva.setDiaEspecial(diaEspecial);

        // 🔹 Cálculo automático de duración total y precio
        int duracionTotal;
        double precioTotal;

        if (numeroVueltas <= 10) {
            duracionTotal = 30;
            precioTotal = 15000.0;
        } else if (numeroVueltas <= 15) {
            duracionTotal = 35;
            precioTotal = 20000.0;
        } else if (numeroVueltas <= 20) {
            duracionTotal = 40;
            precioTotal = 25000.0;
        } else {
            throw new IllegalArgumentException("Número de vueltas inválido. Máximo permitido: 20.");
        }

        reserva.setDuracionTotal(duracionTotal);

        // 🔹 Guardar reserva en la base de datos
        ReservaEntity reservaGuardada = repositorioReserva.save(reserva);

        // 🔹 Llamar servicios externos para tarifas y descuentos
        crearTarifaInterna(numeroVueltas, reservaGuardada.getIdReserva());
        crearTarifaEspecialInterna(reservaGuardada);
        crearDescuentoInterno(cantidadPersonas, reservaGuardada.getIdReserva(), nombreCliente);

        incrementarReservasInterna(nombreCliente);
        return reservaGuardada;
    }

    @Transactional
    public Optional<ReservaEntity> buscarPorId(Long id) {
        return repositorioReserva.findById(id);
    }

    public List<ReservaEntity> listarTodas() {
        return repositorioReserva.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioReserva.deleteById(id);
    }

    public ReservaEntity ObtenerReservaPorId(Long idReserva) {
        return  repositorioReserva.findByIdReserva(idReserva);
    }

    @Transactional
    public void crearTarifaInterna(int numeroVueltas, Long idReserva) {
        String url = String.format("http://localhost:8080/api/tarifas/?numeroVueltas=%d&idReserva=%d", numeroVueltas, idReserva);

        System.out.println("🔹 Enviando tarifa con URL: " + url);

        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, null, ResponseEntity.class);
            System.out.println("✔️ Respuesta del servidor tarifas: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("🚨 Error al enviar tarifa: " + e.getMessage());
        }
    }



    @Transactional
    public void crearTarifaEspecialInterna(ReservaEntity reserva) {
        String fechaFormateada = reserva.getFechaReserva().toLocalDate().toString();  // 🔹 Convierte `LocalDateTime` a `String`

        String url = String.format("http://localhost:8080/api/tarifas-especiales/CrearTarifaEspecial/?fecha=%s&esDiaEspecial=%b&IdReserva=%d&CantidadPersonas=%d",
                fechaFormateada, reserva.getDiaEspecial(), reserva.getIdReserva(), reserva.getCantidadPersonas());

        System.out.println("🔹 Enviando tarifa especial con URL: " + url);

        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, null, ResponseEntity.class);
            System.out.println("✔️ Respuesta del servidor tarifas especiales: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("🚨 Error al enviar tarifa especial: " + e.getMessage());
        }
    }


    @Transactional
    public void crearDescuentoInterno(int numeroPersonas, Long idReserva, String nombreCliente) {
        String url = String.format("http://localhost:8080/api/descuentos/?numeroPersonas=%d&idReserva=%d&nombreCliente=%s",
                numeroPersonas, idReserva, nombreCliente);

        System.out.println("🔹 Enviando descuento con URL: " + url);

        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, null, ResponseEntity.class);
            System.out.println("✔️ Respuesta del servidor descuentos: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("🚨 Error al enviar descuento: " + e.getMessage());
        }
    }


    @Transactional
    public void incrementarReservasInterna(String nombreCliente) {
        String url = String.format("http://localhost:8080/api/clientes/reservas/%s", nombreCliente);

        System.out.println("🔹 Enviando solicitud para incrementar reservas del cliente: " + nombreCliente);

        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, null, ResponseEntity.class);
            System.out.println("✔️ Respuesta del servidor clientes: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("🚨 Error al incrementar reservas del cliente: " + e.getMessage());
        }
    }


    public List<ReporteDTO> obtenerGananciasEntreMeses(LocalDate inicio, LocalDate fin) {
        String sql = """
        SELECT r.fecha_reserva AS mes, r.numero_vueltas, r.cantidad_personas, c.total_pago
        FROM reservas r
        INNER JOIN comprobante_pago c ON r.id_reserva = c.id_reserva
        WHERE r.fecha_reserva BETWEEN ? AND ?
        ORDER BY r.fecha_reserva
    """;

        List<ReporteDTO> reporte = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(inicio));
            stmt.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReporteDTO dto = new ReporteDTO(
                        rs.getDate("mes").toLocalDate(),
                        rs.getInt("numero_vueltas"),
                        rs.getInt("cantidad_personas"),
                        rs.getBigDecimal("total_pago")
                );
                reporte.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reporte;
    }

}