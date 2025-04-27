package com.Tingeso1.Pep1Tingeso.Repositorios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepositorioReserva extends JpaRepository<EntidadReservas, Long> {
    // Reporte de ingresos por número de vueltas
    @Query("SELECT r.numeroVueltas AS numeroVueltas, SUM(r.precioTotal) AS ingresos " +
            "FROM EntidadReservas r " +
            "GROUP BY r.numeroVueltas " +
            "ORDER BY r.numeroVueltas")
    List<ReporteVueltas> findIngresosPorVueltas();

    // Reporte de ingresos por tiempo máximo (duracionTotal)
    @Query("SELECT r.duracionTotal AS duracionTotal, SUM(r.precioTotal) AS ingresos " +
            "FROM EntidadReservas r " +
            "GROUP BY r.duracionTotal " +
            "ORDER BY r.duracionTotal")
    List<ReporteTiempo> findIngresosPorTiempo();

    // Reporte de ingresos por número de personas.
    // Usamos consulta nativa para agrupar en categorías: 1-3, 4-6, 7-9 y 10+.
    @Query(value = "SELECT " +
            "  CASE " +
            "    WHEN cantidad_personas BETWEEN 1 AND 3 THEN '1-3' " +
            "    WHEN cantidad_personas BETWEEN 4 AND 6 THEN '4-6' " +
            "    WHEN cantidad_personas BETWEEN 7 AND 9 THEN '7-9' " +
            "    ELSE '10+' " +
            "  END AS categoria, " +
            "  SUM(precio_total) AS ingresos " +
            "FROM \"reservas\"  " +
            "GROUP BY categoria " +
            "ORDER BY categoria", nativeQuery = true)
    List<ReportePersonas> findIngresosPorPersonas();

    // Reporte de Vueltas filtrado por mes
    @Query(value = "SELECT to_char(r.fecha_reserva, 'YYYY-MM') as mes, " +
            "SUM(r.numero_vueltas) as numeroVueltas, " +
            "SUM(r.precio_total) as ingresos " +
            "FROM \"reservas\" r " +
            "WHERE r.fecha_reserva BETWEEN :startDate AND :endDate " +
            "GROUP BY mes " +
            "ORDER BY mes", nativeQuery = true)
    List<ReporteVueltas> findIngresosPorVueltasMes(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    // Reporte de Tiempo filtrado por mes
    @Query(value = "SELECT to_char(r.fecha_reserva, 'YYYY-MM') as mes, " +
            "SUM(r.duracion_total) as duracionTotal, " +
            "SUM(r.precio_total) as ingresos " +
            "FROM \"reservas\" r " +
            "WHERE r.fecha_reserva BETWEEN :startDate AND :endDate " +
            "GROUP BY mes " +
            "ORDER BY mes", nativeQuery = true)
    List<ReporteTiempo> findIngresosPorTiempoMes(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    // Reporte de Personas filtrado por mes, con desagregación por categorías
    @Query(value = "SELECT to_char(r.fecha_reserva, 'YYYY-MM') as mes, " +
            "CASE " +
            "  WHEN r.cantidad_personas BETWEEN 1 and 2 THEN '1-2' " +
            "  WHEN r.cantidad_personas BETWEEN 3 and 5 THEN '3-5' " +
            "  WHEN r.cantidad_personas BETWEEN 6 and 10 THEN '6-10' " +
            "  WHEN r.cantidad_personas BETWEEN 11 and 15 THEN '11-15' " +
            "  ELSE 'otros' " +
            "END as categoria, " +
            "SUM(r.precio_total) as ingresos " +
            "FROM \"reservas\" r " +
            "WHERE r.fecha_reserva BETWEEN :startDate AND :endDate " +
            "GROUP BY mes, categoria " +
            "ORDER BY mes, categoria", nativeQuery = true)
    List<ReportePersonas> findIngresosPorPersonasMes(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT to_char(r.fecha_reserva, 'YYYY-MM') as mes, SUM(r.precio_total) as ingresos " +
            "FROM \"reservas\" r " +
            "WHERE r.fecha_reserva BETWEEN :startDate AND :endDate " +
            "GROUP BY mes " +
            "ORDER BY mes", nativeQuery = true)
    List<ReporteMes> findIngresosPorMes(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}
