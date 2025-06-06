package com.Pep2.Tingeso.ClienteFrecuente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ControladorCliente {

    private final ServicioCliente clienteFrecuenteService;
    private final RepositorioCliente repositorioCliente;

    public ControladorCliente(ServicioCliente clienteFrecuenteService, RepositorioCliente repositorioCliente) {
        this.clienteFrecuenteService = clienteFrecuenteService;
        this.repositorioCliente = repositorioCliente;
    }

    @GetMapping("/buscar/email/{email}")
    public ResponseEntity<?> buscarClientePorEmail(@PathVariable String email) {
        Optional<EntidadCliente> cliente = repositorioCliente.findByEmail(email);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get()); // Retorna el cliente si existe
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Cliente con email " + email + " no encontrado."); // Retorna mensaje de error si no existe
        }
    }


    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<?> buscarClientePorNombre(@PathVariable String nombre) {

        Optional<EntidadCliente> cliente = repositorioCliente.findEntidadClienteByNombre(nombre);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get()); // Retorna el cliente si existe
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Cliente con nombre " + nombre + " no encontrado."); // Retorna mensaje de error si no existe
        }
    }

    @PostMapping("/")
    public EntidadCliente crearCliente(@RequestParam String nombre, @RequestParam String email) {
        return clienteFrecuenteService.guardarCliente(nombre,email);
    }

    @GetMapping("/descuento/{nombre}")
    public ResponseEntity<Double> obtenerDescuentoFrecuente(@PathVariable String nombre) {
        double descuento = clienteFrecuenteService.obtenerDescuentoFrecuente(nombre);
        return ResponseEntity.ok(descuento);
    }

    @PostMapping("/reservas/{nombre}")
    public ResponseEntity<EntidadCliente> incrementarReservas(@PathVariable String nombre) {
        EntidadCliente clienteActualizado = clienteFrecuenteService.incrementarReservas(nombre);
        return ResponseEntity.ok(clienteActualizado);
    }
    @GetMapping("/nombres")
    public ResponseEntity<List<String>> obtenerNombresClientes() {
        List<String> nombres = clienteFrecuenteService.obtenerNombresClientes();
        return ResponseEntity.ok(nombres);
    }

}

