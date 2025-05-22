package com.Pep2.Tingeso.Reportes;

import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/{mes}")
    public List<ReporteEntity> obtenerPorMes(@PathVariable YearMonth mes) {
        return reporteService.obtenerPorMes(mes);
    }

    @PostMapping("/")
    public ReporteEntity crearReporte(@RequestBody ReporteEntity reporte) {
        return reporteService.guardarReporte(reporte);
    }
}