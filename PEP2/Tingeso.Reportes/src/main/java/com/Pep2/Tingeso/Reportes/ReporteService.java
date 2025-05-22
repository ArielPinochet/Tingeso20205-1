package com.Pep2.Tingeso.Reportes;

import org.springframework.stereotype.Service;
import java.time.YearMonth;
import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public List<ReporteEntity> obtenerPorMes(YearMonth mes) {
        return reporteRepository.findByMes(mes);
    }

    public ReporteEntity guardarReporte(ReporteEntity reporte) {
        return reporteRepository.save(reporte);
    }
}