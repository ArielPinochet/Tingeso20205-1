package com.Pep2.Tingeso.ClienteFrecuente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
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


    @GetMapping("/email/{nombreCliente}")
    public ResponseEntity<String> obtenerEmailPorNombre(@PathVariable String nombreCliente) {
        Optional<EntidadCliente> cliente = clienteFrecuenteService.buscarPorNombre(nombreCliente);

        return cliente.map(c -> ResponseEntity.ok(c.getEmail()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: No se encontró el cliente con nombre " + nombreCliente));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            clienteFrecuenteService.eliminarCliente(id);
            return ResponseEntity.ok("Cliente con ID " + id + " eliminado correctamente.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ Cliente con ID " + id + " no encontrado.");
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> crearCliente(@RequestParam String nombre, @RequestParam String email) {
        try {
            EntidadCliente nuevoCliente = clienteFrecuenteService.guardarCliente(nombre, email);
            return ResponseEntity.ok(nuevoCliente);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❗ Error: " + e.getMessage());
        }
    }

    @GetMapping("/descuento/{nombre}")
    public ResponseEntity<Double> obtenerDescuentoFrecuente(@PathVariable String nombre) {
        double descuento = clienteFrecuenteService.obtenerDescuentoFrecuente(nombre);
        return ResponseEntity.ok(descuento);
    }

    @GetMapping
    public ResponseEntity<List<EntidadCliente>> listarClientes() {
        return ResponseEntity.ok(clienteFrecuenteService.listarTodos());
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

    @GetMapping("/{id}")
    public ResponseEntity<EntidadCliente> obtenerEntidadCliente(@PathVariable Long id) {
        EntidadCliente cliente = clienteFrecuenteService.obtenerPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/editar-correo")
    public ResponseEntity<?> editarCorreoCliente(
            @RequestParam String nombre,
            @RequestParam String emailNuevo
    ) {
        Optional<EntidadCliente> clienteOpt = repositorioCliente.findEntidadClienteByNombre(nombre);

        if (clienteOpt.isPresent()) {
            EntidadCliente cliente = clienteOpt.get();
            cliente.setEmail(emailNuevo);
            repositorioCliente.save(cliente); // 🔄 Guarda el cambio en el repositorio

            return ResponseEntity.ok(cliente);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Cliente con nombre " + nombre + " no encontrado.");
        }
    }
}

