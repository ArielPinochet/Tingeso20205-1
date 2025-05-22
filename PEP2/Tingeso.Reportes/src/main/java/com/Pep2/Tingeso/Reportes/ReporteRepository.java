package com.Pep2.Tingeso.Reportes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<ReporteEntity, Long> {
    List<ReporteEntity> findByMes(YearMonth mes);
}