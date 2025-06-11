package com.Pep2.Tingeso.Reportes;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReporteService {

    private final RestTemplate restTemplate;

    public ReporteService( RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<ReporteDTO> obtenerGananciasEntreMeses(String inicio, String fin) {
        // 🔹 Asegurar que los parámetros tengan formato YYYY-MM-DD
        String inicioFecha = inicio + "-01";  // 📌 Define el primer día del mes
        String finFecha = fin + "-31";        // 📌 Define el último día del mes (puede ajustarse según el mes)

        String url = String.format("http://localhost:8080/app/ganancias?inicio=%s&fin=%s", inicioFecha, finFecha);

        System.out.println("🔹 Enviando solicitud GET a: " + url);

        try {
            ReporteDTO[] reportes = restTemplate.getForObject(url, ReporteDTO[].class);
            return reportes != null ? Arrays.asList(reportes) : List.of();
        } catch (Exception e) {
            System.err.println("🚨 Error al obtener ganancias: " + e.getMessage());
            return List.of();
        }
    }

    public List<ReportePersonasDTO> obtenerGananciasEntreMesesPersonas(String inicio, String fin) {
        String inicioFecha = inicio + "-01";
        String finFecha = obtenerUltimoDiaDelMes(fin); // ✅ Calcula correctamente el último día del mes

        String url = String.format("http://localhost:8080/app/gananciaspersonas?inicio=%s&fin=%s", inicioFecha, finFecha);
        System.out.println("🔹 Enviando solicitud GET a: " + url);

        try {
            ReportePersonasDTO[] reportes = restTemplate.getForObject(url, ReportePersonasDTO[].class);
            return reportes != null ? Arrays.asList(reportes) : List.of();
        } catch (Exception e) {
            System.err.println("🚨 Error al obtener ganancias: " + e.getMessage());
            return List.of();
        }
    }

    public List<ReportesVueltasDTO> obtenerGananciasEntreMesesVueltas(String inicio, String fin) {
        String inicioFecha = inicio + "-01";
        String finFecha = obtenerUltimoDiaDelMes(fin); // ✅ Calcula correctamente el último día del mes

        String url = String.format("http://localhost:8080/app/gananciasvueltas?inicio=%s&fin=%s", inicioFecha, finFecha);
        System.out.println("🔹 Enviando solicitud GET a: " + url);

        try {
            ReportesVueltasDTO[] reportes = restTemplate.getForObject(url, ReportesVueltasDTO[].class);
            return reportes != null ? Arrays.asList(reportes) : List.of();
        } catch (Exception e) {
            System.err.println("🚨 Error al obtener ganancias: " + e.getMessage());
            return List.of();
        }
    }

    // ✅ Función auxiliar para obtener el último día del mes correctamente
    private String obtenerUltimoDiaDelMes(String mes) {
        YearMonth yearMonth = YearMonth.parse(mes + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return yearMonth.atEndOfMonth().toString();
    }




    public ResponseEntity<byte[]> generarReportePersonasExcel(String inicio, String fin) {
        List<ReportePersonasDTO> reportes = obtenerGananciasEntreMesesPersonas(inicio, fin);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte Personas");

        // 🔹 Encabezado de fechas
        Row tituloRow = sheet.createRow(0);
        tituloRow.createCell(0).setCellValue("Inicio");
        tituloRow.createCell(1).setCellValue(inicio);
        tituloRow.createCell(3).setCellValue("Fin");
        tituloRow.createCell(4).setCellValue(fin);

        // 🔹 Obtener lista completa de meses desde `inicio` hasta `fin`
        List<String> meses = obtenerListaCompletaDeMeses(inicio, fin);

        // 🔹 Encabezado de meses
        Row mesesRow = sheet.createRow(2);
        mesesRow.createCell(0).setCellValue("Número de personas");
        for (int i = 0; i < meses.size(); i++) {
            mesesRow.createCell(i + 1).setCellValue(meses.get(i));
        }
        mesesRow.createCell(meses.size() + 1).setCellValue("TOTAL");

        // 🔹 Procesar datos y asegurar valores faltantes con `0`
        List<String> grupos = List.of("1-2 personas", "3-5 personas", "6-10 personas", "11-15 personas");
        Map<String, Map<String, BigDecimal>> datosAgrupados = new HashMap<>();

        for (String grupo : grupos) {
            datosAgrupados.put(grupo, new HashMap<>());
        }

        for (ReportePersonasDTO reporte : reportes) {
            datosAgrupados.computeIfAbsent(reporte.getNumeropersonas(), k -> new HashMap<>())
                    .put(reporte.getMes().toString(), reporte.getTotalPago());
        }

        // 🔹 Llenar el Excel
        int rowIndex = 3;
        for (String grupo : grupos) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(grupo);

            BigDecimal totalGrupo = BigDecimal.ZERO;
            for (int i = 0; i < meses.size(); i++) {
                BigDecimal valor = datosAgrupados.get(grupo).getOrDefault(meses.get(i), BigDecimal.ZERO);
                row.createCell(i + 1).setCellValue(valor.doubleValue());
                totalGrupo = totalGrupo.add(valor);
            }
            row.createCell(meses.size() + 1).setCellValue(totalGrupo.doubleValue());
        }

        // 🔹 Fila de TOTAL por mes
        Row totalRow = sheet.createRow(rowIndex++);
        totalRow.createCell(0).setCellValue("TOTAL");
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (int i = 1; i <= meses.size(); i++) {
            BigDecimal totalMes = BigDecimal.ZERO;
            for (String grupo : grupos) {
                totalMes = totalMes.add(datosAgrupados.get(grupo).getOrDefault(meses.get(i - 1), BigDecimal.ZERO));
            }
            totalRow.createCell(i).setCellValue(totalMes.doubleValue());
            totalGeneral = totalGeneral.add(totalMes);
        }

        // 🔹 Fila de TOTAL GENERAL
        Row totalGeneralRow = sheet.createRow(rowIndex);
        totalGeneralRow.createCell(0).setCellValue("TOTAL GENERAL");
        totalGeneralRow.createCell(meses.size() + 1).setCellValue(totalGeneral.doubleValue());

        return generarArchivoExcel(workbook, "Reporte_Personas.xlsx");
    }



    public ResponseEntity<byte[]> generarReporteVueltasExcel(String inicio, String fin) {
        List<ReportesVueltasDTO> reportes = obtenerGananciasEntreMesesVueltas(inicio, fin);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte Vueltas");

        // 🔹 Encabezado de fechas
        Row tituloRow = sheet.createRow(0);
        tituloRow.createCell(0).setCellValue("Inicio");
        tituloRow.createCell(1).setCellValue(inicio);
        tituloRow.createCell(3).setCellValue("Fin");
        tituloRow.createCell(4).setCellValue(fin);

        // 🔹 Obtener lista completa de meses para asegurar que estén en el reporte
        List<String> meses = obtenerListaCompletaDeMeses(inicio, fin);

        // 🔹 Encabezado de meses
        Row mesesRow = sheet.createRow(2);
        mesesRow.createCell(0).setCellValue("Número de vueltas o tiempo máximo permitido");
        for (int i = 0; i < meses.size(); i++) {
            mesesRow.createCell(i + 1).setCellValue(meses.get(i));
        }
        mesesRow.createCell(meses.size() + 1).setCellValue("TOTAL");

        // 🔹 Definir categorías de vueltas
        List<String> categoriasVueltas = List.of("10 vueltas o máx 10 min", "15 vueltas o máx 15 min", "20 vueltas o máx 20 min");
        Map<String, Map<String, BigDecimal>> datosAgrupados = new HashMap<>();

        for (String categoria : categoriasVueltas) {
            datosAgrupados.put(categoria, new HashMap<>());
        }

        for (ReportesVueltasDTO reporte : reportes) {
            datosAgrupados.computeIfAbsent(reporte.getcategoriavueltas(), k -> new HashMap<>())
                    .put(reporte.getMes().toString(), reporte.getTotalPago());
        }

        // 🔹 Llenar el Excel con los valores asegurando que se rellenen los meses faltantes con 0
        int rowIndex = 3;
        for (String categoria : categoriasVueltas) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(categoria);

            BigDecimal totalCategoria = BigDecimal.ZERO;
            for (int i = 0; i < meses.size(); i++) {
                BigDecimal valor = datosAgrupados.get(categoria).getOrDefault(meses.get(i), BigDecimal.ZERO);
                row.createCell(i + 1).setCellValue(valor.doubleValue());
                totalCategoria = totalCategoria.add(valor);
            }
            row.createCell(meses.size() + 1).setCellValue(totalCategoria.doubleValue());
        }

        // 🔹 Fila de TOTAL por mes
        Row totalRow = sheet.createRow(rowIndex++);
        totalRow.createCell(0).setCellValue("TOTAL");
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (int i = 1; i <= meses.size(); i++) {
            BigDecimal totalMes = BigDecimal.ZERO;
            for (String categoria : categoriasVueltas) {
                totalMes = totalMes.add(datosAgrupados.get(categoria).getOrDefault(meses.get(i - 1), BigDecimal.ZERO));
            }
            totalRow.createCell(i).setCellValue(totalMes.doubleValue());
            totalGeneral = totalGeneral.add(totalMes);
        }

        // 🔹 Fila de TOTAL GENERAL
        Row totalGeneralRow = sheet.createRow(rowIndex);
        totalGeneralRow.createCell(0).setCellValue("TOTAL GENERAL");
        totalGeneralRow.createCell(meses.size() + 1).setCellValue(totalGeneral.doubleValue());

        return generarArchivoExcel(workbook, "Reporte_Vueltas.xlsx");
    }


    private List<String> obtenerListaCompletaDeMeses(String inicio, String fin) {
        List<String> mesesCompletos = new ArrayList<>();
        LocalDate inicioDate = LocalDate.parse(inicio + "-01");
        LocalDate finDate = LocalDate.parse(fin + "-01");

        while (!inicioDate.isAfter(finDate)) {
            mesesCompletos.add(inicioDate.toString()); // ✅ Se asegura de incluir todos los meses
            inicioDate = inicioDate.plusMonths(1);
        }

        return mesesCompletos;
    }



    private ResponseEntity<byte[]> generarArchivoExcel(Workbook workbook, String nombreArchivo) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Disposition", "attachment; filename=" + nombreArchivo);

            return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }


    public ResponseEntity<byte[]> generarReporteExcel(String inicio, String fin, String tipoReporte) {
        // 🔹 Obtener datos desde `reporte-pago-service`
        List<ReporteDTO> reportes = obtenerGananciasEntreMeses(inicio, fin);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte");

        // 🔹 Crear encabezado
        Row encabezado = sheet.createRow(0);
        encabezado.createCell(0).setCellValue("Fecha");
        encabezado.createCell(1).setCellValue(tipoReporte.equalsIgnoreCase("PERSONAS") ? "Cantidad de Personas" : "Número de Vueltas");
        encabezado.createCell(2).setCellValue("Total Pago");

        // 🔹 Llenar el reporte con los datos obtenidos
        int rowIndex = 1;
        for (ReporteDTO reporte : reportes) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(reporte.getMes().toString());
            row.createCell(1).setCellValue(tipoReporte.equalsIgnoreCase("PERSONAS") ? reporte.getCantidadPersonas() : reporte.getNumeroVueltas());
            row.createCell(2).setCellValue(reporte.getTotalPago().toString());
        }

        // 🔹 Convertir a archivo descargable
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Disposition", "attachment; filename=Reporte_" + tipoReporte + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}