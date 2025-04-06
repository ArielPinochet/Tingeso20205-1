package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioCliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioClientes {

    private final RepositorioCliente repositorioCliente;

    public ServicioClientes(RepositorioCliente clienteRepository) {
        this.repositorioCliente = clienteRepository;
    }

    public EntidadClientes guardarCliente(EntidadClientes cliente) {
        return repositorioCliente.save(cliente);
    }

    public Optional<EntidadClientes> buscarPorId(Long id) {
        return repositorioCliente.findById(id);
    }

    public List<EntidadClientes> listarTodos() {
        return repositorioCliente.findAll();
    }

    public void eliminarPorId(Long id) {
        repositorioCliente.deleteById(id);
    }
}
