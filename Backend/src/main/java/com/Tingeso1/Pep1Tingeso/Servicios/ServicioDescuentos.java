package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadDescuento;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioDescuento;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioDescuentos {

    private final RepositorioDescuento repositorioDescuento;

    public ServicioDescuentos(RepositorioDescuento repositorioDescuento) {
        this.repositorioDescuento = repositorioDescuento;
    }

    public EntidadDescuento guardarDescuento(EntidadDescuento descuento) {
        return repositorioDescuento.save(descuento);
    }

    public Optional<EntidadDescuento> buscarPorId(Long id) {
        return repositorioDescuento.findById(id);
    }

    public List<EntidadDescuento> listarTodos() {
        return repositorioDescuento.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioDescuento.deleteById(id);
    }
}
