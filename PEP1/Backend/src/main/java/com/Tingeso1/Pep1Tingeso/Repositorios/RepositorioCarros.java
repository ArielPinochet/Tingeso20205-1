package com.Tingeso1.Pep1Tingeso.Repositorios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioCarros extends JpaRepository<EntidadCarros, String> {

}
