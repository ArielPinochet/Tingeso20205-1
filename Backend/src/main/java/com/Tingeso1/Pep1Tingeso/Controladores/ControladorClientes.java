package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Servicios.EmailService;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioClientes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*") // acepta todos los orígenes solo en este controlador
public class ControladorClientes {

    private final ServicioClientes servicioClientes;
    private final EmailService emailService;

    public ControladorClientes(ServicioClientes servicioClientes, EmailService emailService) {
        this.servicioClientes = servicioClientes;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<EntidadClientes> crearCliente(@RequestBody EntidadClientes cliente) {
        // Enviar correo de confirmación
        EntidadClientes nuevoCliente = servicioClientes.guardarCliente(cliente);
        emailService.sendClientCreationConfirmation(nuevoCliente);
        return ResponseEntity.ok(nuevoCliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadClientes> obtenerCliente(@PathVariable Long id) {
        return servicioClientes.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EntidadClientes>> listarClientes() {
        return ResponseEntity.ok(servicioClientes.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        servicioClientes.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntidadClientes> actualizarCliente(@PathVariable Long id, @RequestBody EntidadClientes clienteActualizado) {
        return servicioClientes.buscarPorId(id)
                .map(cliente -> {
                    cliente.setNombre(clienteActualizado.getNombre());
                    cliente.setEmail(clienteActualizado.getEmail());
                    cliente.setFechaNacimiento(clienteActualizado.getFechaNacimiento());
                    emailService.sendClientEditedConfirmation(clienteActualizado);
                    return ResponseEntity.ok(servicioClientes.guardarCliente(cliente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
