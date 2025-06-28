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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
                                        Integer numeroVueltas, Integer cantidadPersonas, Boolean diaEspecial, List<String> carros) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        ReservaEntity reserva = new ReservaEntity();
        reserva.SetNombreCliente(nombreCliente);
        reserva.setFechaReserva(LocalDateTime.parse(fechaReserva, formatter));
        reserva.setHoraInicio(LocalDateTime.parse(horaInicio, formatter));
        reserva.setNumeroVueltas(numeroVueltas);
        reserva.setCantidadPersonas(cantidadPersonas);
        reserva.setDiaEspecial(diaEspecial);
        reserva.setCarros(carros);

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

        LocalDateTime fechaReservaDT = LocalDateTime.parse(fechaReserva, formatter);
        LocalDateTime horaInicioDT = LocalDateTime.parse(horaInicio, formatter);

        crearCalendarioDesdeReserva(fechaReservaDT.toLocalDate(), horaInicioDT.toLocalTime(), duracionTotal, nombreCliente, reservaGuardada.getIdReserva());

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

    public void eliminarPorId(Long id){
        repositorioReserva.deleteById(id);
        sincronizarConCalendario("eliminar", id, null, null, null);
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
    public void sincronizarConCalendario(String accion, Long reservaId, LocalDate nuevaFecha, LocalTime nuevaHora, Integer duracionMinutos) {
        String urlBase = "http://localhost:8080/api/calendario/";

        try {
            if ("eliminar".equalsIgnoreCase(accion)) {
                String url = urlBase + "eliminar?reservaId=" + reservaId;
                restTemplate.delete(url);
                System.out.println("🗑️ Calendario eliminado para reservaId=" + reservaId);

            } else if ("editar".equalsIgnoreCase(accion)) {
                String url = String.format(
                        urlBase + "editar-fecha?reservaId=%d&nuevaFecha=%s&nuevaHoraInicio=%s&duracionMinutos=%d",
                        reservaId, nuevaFecha, nuevaHora, duracionMinutos
                );
                restTemplate.put(url, null);
                System.out.println("📝 Calendario actualizado para reservaId=" + reservaId);
            } else {
                System.out.println("⚠️ Acción no reconocida: " + accion);
            }
        } catch (Exception e) {
            System.err.println("🚨 Error al sincronizar con Calendario (" + accion + "): " + e.getMessage());
        }
    }



    public ReservaEntity editarReserva(Long id, String cliente, String fecha, String hora, int vueltas, int personas, boolean especial, List<String> carros) {
        ReservaEntity reserva = repositorioReserva.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID no existe: " + id));

        reserva.SetNombreCliente(cliente);
        reserva.setFechaReserva(LocalDateTime.parse(fecha));
        reserva.setHoraInicio(LocalDateTime.parse(hora));
        reserva.setNumeroVueltas(vueltas);
        reserva.setCantidadPersonas(personas);
        reserva.setDiaEspecial(especial);
        reserva.setCarros(carros); // debes tener setter para la lista de códigos

        // 🔹 Obtener fecha, hora y duración para sincronizar calendario
        LocalDate nuevaFecha = LocalDateTime.parse(fecha).toLocalDate();
        LocalTime nuevaHora = LocalDateTime.parse(hora).toLocalTime();
        int duracion;
        if (vueltas <= 10) {
            duracion = 30;
        } else if (vueltas <= 15) {
            duracion = 35;
        } else if (vueltas <= 20) {
            duracion = 40;
        } else {
            throw new IllegalArgumentException("Número de vueltas inválido. Máximo permitido: 20.");
        }
        sincronizarConCalendario("editar", id, nuevaFecha, nuevaHora, duracion);
        return repositorioReserva.save(reserva);
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

    public void crearCalendarioDesdeReserva(LocalDate fecha, LocalTime horaInicio, int duracionMinutos, String clienteNombre, Long reservaId) {
        String url = String.format("http://localhost:8080/api/calendario/crear?fecha=%s&horaInicio=%s&duracionMinutos=%d&clienteNombre=%s&reservaId=%d",
                fecha, horaInicio, duracionMinutos, clienteNombre, reservaId);

        System.out.println("🔹 Enviando solicitud para crear calendario desde reserva: " + reservaId);

        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, null, ResponseEntity.class);
            System.out.println("✔️ Respuesta del servidor calendario: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("🚨 Error al crear calendario desde reserva: " + e.getMessage());
        }
    }

    public List<ReporteDTO> obtenerGananciasEntreMeses(LocalDate inicio, LocalDate fin) {
        String sql = """
        SELECT DATE_TRUNC('month', r.fecha_reserva) AS mes, 
            SUM(r.numero_vueltas) AS numero_vueltas, 
            SUM(r.cantidad_personas) AS cantidad_personas, 
            SUM(c.total_con_iva) AS total_pago
        FROM reserva r
        INNER JOIN comprobante_pago c ON r.id_reserva = c.id_reserva
        WHERE r.fecha_reserva BETWEEN ? AND ?
       GROUP BY mes
        ORDER BY mes
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

    public List<ReportePersonasDTO> obtenerGananciasEntreMesesPersonas(LocalDate inicio, LocalDate fin) {
        String sql = """
        
                SELECT DATE_TRUNC('month', r.fecha_reserva) AS mes,
                           CASE\s
                               WHEN r.cantidad_personas BETWEEN 1 AND 2 THEN '1-2 personas'
                               WHEN r.cantidad_personas BETWEEN 3 AND 5 THEN '3-5 personas'
                               WHEN r.cantidad_personas BETWEEN 6 AND 10 THEN '6-10 personas'
                               WHEN r.cantidad_personas BETWEEN 11 AND 15 THEN '11-15 personas'
                               ELSE 'Otro'
                           END AS grupo_personas,
                           SUM(c.total_con_iva) AS total_pago
                    FROM reserva r
                    INNER JOIN comprobante_pago c ON r.id_reserva = c.id_reserva
                    WHERE r.fecha_reserva BETWEEN ? AND ?
                    GROUP BY mes, grupo_personas
                    ORDER BY mes;
                   """;

        List<ReportePersonasDTO> reporte = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(inicio));
            stmt.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReportePersonasDTO dto = new ReportePersonasDTO(
                        rs.getDate("mes").toLocalDate(),
                        rs.getString("grupo_personas"),
                        rs.getBigDecimal("total_pago")
                );
                reporte.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reporte;
    }


    public List<ReporteVueltasDTO> obtenerGananciasEntreMesesVueltas(LocalDate inicio, LocalDate fin) {
        String sql = """
        
                SELECT DATE_TRUNC('month', r.fecha_reserva) AS mes,
                       CASE\s
                           WHEN r.numero_vueltas <= 10 THEN '10 vueltas o máx 10 min'
                           WHEN r.numero_vueltas <= 15 THEN '15 vueltas o máx 15 min'
                           WHEN r.numero_vueltas <= 20 THEN '20 vueltas o máx 20 min'
                           ELSE 'Otro'
                       END AS categoria_vueltas,
                       SUM(c.total_con_iva) AS total_pago
                FROM reserva r
                INNER JOIN comprobante_pago c ON r.id_reserva = c.id_reserva
                WHERE r.fecha_reserva BETWEEN ? AND ?
                GROUP BY mes, categoria_vueltas
                ORDER BY mes;
                
                   """;

        List<ReporteVueltasDTO> reporte = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(inicio));
            stmt.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReporteVueltasDTO dto = new ReporteVueltasDTO(
                        rs.getDate("mes").toLocalDate(),
                        rs.getString("categoria_vueltas"),
                        rs.getBigDecimal("total_pago")
                );
                reporte.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reporte;
    }
    private List<String> obtenerListaCompletaDeMeses(String inicio, String fin) {
        List<String> mesesCompletos = new ArrayList<>();
        LocalDate inicioDate = LocalDate.parse(inicio + "-01");
        LocalDate finDate = LocalDate.parse(fin + "-01");

        while (!inicioDate.isAfter(finDate)) {
            // ✅ Obtener el último día del mes correctamente
            LocalDate ultimoDiaDelMes = inicioDate.withDayOfMonth(inicioDate.lengthOfMonth());
            mesesCompletos.add(ultimoDiaDelMes.toString());

            inicioDate = inicioDate.plusMonths(1);
        }

        return mesesCompletos;
    }




}