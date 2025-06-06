package com.Pep2.Tingeso.ClienteFrecuente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioCliente extends  JpaRepository<EntidadCliente, Long>{
    Optional<EntidadCliente> findByEmail(String email);
    boolean existsByNombre(String nombre);
    boolean existsByEmail(String email);

    Optional<EntidadCliente> findByNombre(String nombreCliente);
    Optional<EntidadCliente> findEntidadClienteByNombre(String nombre);
}
