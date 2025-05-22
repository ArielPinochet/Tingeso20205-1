package com.Pep2.Tingeso.TarifaEspeciales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaEspecialRepository extends JpaRepository<TarifaEspecialEntity, Long> {
    Optional<TarifaEspecialEntity> findByDiaEspecialAndFinDeSemana(boolean diaEspecial, boolean finDeSemana);
}