package com.Pep2.Tingeso.Descuento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DescuentoRepository extends JpaRepository<DescuentoEntity, Long> {
    Optional<DescuentoEntity> findByIdReserva(Long idReserva);
}