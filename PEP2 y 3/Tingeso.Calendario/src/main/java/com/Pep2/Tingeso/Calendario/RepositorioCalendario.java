package com.Pep2.Tingeso.Calendario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepositorioCalendario extends JpaRepository<EntidadCalendario, Long> {
    List<EntidadCalendario> findByFecha(LocalDate fecha);
}