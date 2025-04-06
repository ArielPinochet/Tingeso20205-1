package com.Tingeso1.Pep1Tingeso.Repositorios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioComprobantePago extends JpaRepository<EntidadComprobanteDePago, Long> {
}
