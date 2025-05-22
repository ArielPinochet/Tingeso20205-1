package com.Pep2.Tingeso.ClienteFrecuente;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ControladorCliente {

    private final ServicioCliente clienteFrecuenteService;

    public ControladorCliente(ServicioCliente clienteFrecuenteService) {
        this.clienteFrecuenteService = clienteFrecuenteService;
    }

    @GetMapping("/{email}")
    public Optional<EntidadCliente> obtenerPorEmail(@PathVariable String email) {
        return clienteFrecuenteService.buscarPorEmail(email);
    }

    @PostMapping("/")
    public EntidadCliente crearCliente(@RequestBody EntidadCliente cliente) {
        return clienteFrecuenteService.guardarCliente(cliente);
    }
}

