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

    @Transactional
    public ReservaEntity guardarReserva(ReservaEntity reserva) {
        return repositorioReserva.save(reserva);
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

    // 🔹 Métodos internos para crear la tarifa, tarifa especial y descuento
    @Transactional
    public void crearTarifaInterna(int numeroVueltas, Long idReserva) {
        restTemplate.postForEntity("http://localhost:8080/api/tarifas/", null,
                ResponseEntity.class, numeroVueltas, idReserva);
    }

    @Transactional
    public void crearTarifaEspecialInterna(ReservaEntity reserva) {
        String url = String.format("http://localhost:8080/api/tarifas-especiales/CrearTarifaEspecial/?fecha=%s&esDiaEspecial=%b&IdReserva=%d&CantidadPersonas=%d",
                reserva.getFechaReserva(), reserva.getDiaEspecial(), reserva.getIdReserva(), reserva.getCantidadPersonas());
        restTemplate.postForEntity(url, null, ResponseEntity.class);
    }

    @Transactional
    public void crearDescuentoInterno(int numeroPersonas, Long idReserva, String nombreCliente) {
        restTemplate.postForEntity("http://localhost:8080/api/descuentos/", null,
                ResponseEntity.class, numeroPersonas, idReserva, nombreCliente);
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