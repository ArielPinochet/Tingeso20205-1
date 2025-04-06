package com.Tingeso1.Pep1Tingeso.Repositorios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadHorarioReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepositorioHorarioReserva extends JpaRepository<EntidadHorarioReserva, Long> {
    List<EntidadHorarioReserva> findByFecha(LocalDate fecha);
}
