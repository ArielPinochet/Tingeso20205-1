package com.Pep2.Tingeso.ClienteFrecuente;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServicioCliente {

    private final RepositorioCliente clienteFrecuenteRepository;

    public ServicioCliente(RepositorioCliente clienteFrecuenteRepository) {
        this.clienteFrecuenteRepository = clienteFrecuenteRepository;
    }

    public Optional<EntidadCliente> buscarPorEmail(String email) {
        return clienteFrecuenteRepository.findByEmail(email);
    }

    public EntidadCliente guardarCliente(EntidadCliente cliente) {
        return clienteFrecuenteRepository.save(cliente);
    }
}
