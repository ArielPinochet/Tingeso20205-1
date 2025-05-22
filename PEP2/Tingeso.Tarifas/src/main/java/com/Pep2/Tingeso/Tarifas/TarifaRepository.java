package com.Pep2.Tingeso.Tarifas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<TarifaEntity, Long> {
    Optional<TarifaEntity> findByNumeroVueltas(int numeroVueltas);
}