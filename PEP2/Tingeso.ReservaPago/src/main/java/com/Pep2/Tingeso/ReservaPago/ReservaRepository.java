package com.Pep2.Tingeso.ReservaPago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
    List<ReservaEntity> findByIdCliente(Long idCliente);
    ReservaEntity findByIdReserva(Long idReserva);
}