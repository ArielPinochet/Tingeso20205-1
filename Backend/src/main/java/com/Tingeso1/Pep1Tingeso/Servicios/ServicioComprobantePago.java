package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioComprobantePago;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioComprobantePago {

    private final RepositorioComprobantePago repositorioComprobantePago;

    public ServicioComprobantePago(RepositorioComprobantePago repositorioComprobantePago) {
        this.repositorioComprobantePago = repositorioComprobantePago;
    }

    public EntidadComprobanteDePago guardarComprobante(EntidadComprobanteDePago comprobante) {
        return repositorioComprobantePago.save(comprobante);
    }

    public Optional<EntidadComprobanteDePago> buscarPorId(Long id) {
        return repositorioComprobantePago.findById(id);
    }

    public List<EntidadComprobanteDePago> listarTodos() {
        return repositorioComprobantePago.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioComprobantePago.deleteById(id);
    }

}
