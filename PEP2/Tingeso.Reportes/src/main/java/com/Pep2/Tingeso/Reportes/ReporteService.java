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
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Service
public class ReporteService {

    private final RestTemplate restTemplate;

    public ReporteService( RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<ReporteDTO> obtenerGananciasEntreMeses(String inicio, String fin) {
        String url = String.format("http://localhost:8080/api/reservas/ganancias?inicio=%s&fin=%s",
                inicio, fin);

        ReporteDTO[] reportes = restTemplate.getForObject(url, ReporteDTO[].class);
        return reportes != null ? Arrays.asList(reportes) : List.of();

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