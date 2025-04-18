package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioClientes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ControladorClientes {

    private final ServicioClientes servicioClientes;

    public ControladorClientes(ServicioClientes servicioClientes) {
        this.servicioClientes = servicioClientes;
    }

    @PostMapping
    public ResponseEntity<EntidadClientes> crearCliente(@RequestBody EntidadClientes cliente) {
        return ResponseEntity.ok(servicioClientes.guardarCliente(cliente));
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
                    return ResponseEntity.ok(servicioClientes.guardarCliente(cliente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
