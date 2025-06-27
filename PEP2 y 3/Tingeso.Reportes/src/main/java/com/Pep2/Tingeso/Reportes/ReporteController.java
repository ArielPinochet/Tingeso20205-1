package com.Pep2.Tingeso.Reportes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/descargarExcel")
    public ResponseEntity<byte[]> generarReporteExcel(@RequestParam String inicio,
                                                      @RequestParam String fin,
                                                      @RequestParam String tipoReporte) {
        return reporteService.generarReporteExcel(inicio, fin, tipoReporte);
    }

    @GetMapping("/descargarExcelPersonas")
    public ResponseEntity<byte[]> generarReporteExcelPersonas(@RequestParam String inicio,
                                                      @RequestParam String fin) {
        return reporteService.generarReportePersonasExcel(inicio, fin);
    }

    @GetMapping("/descargarExcelVueltas")
    public ResponseEntity<byte[]> generarReporteExcelVueltas(@RequestParam String inicio,
                                                              @RequestParam String fin) {
        return reporteService.generarReporteVueltasExcel(inicio, fin);
    }


}