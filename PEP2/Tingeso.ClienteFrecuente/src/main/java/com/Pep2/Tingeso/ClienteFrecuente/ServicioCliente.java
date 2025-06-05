package com.Pep2.Tingeso.ClienteFrecuente;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioCliente {

    private final RepositorioCliente clienteFrecuenteRepository;

    public ServicioCliente(RepositorioCliente clienteFrecuenteRepository) {
        this.clienteFrecuenteRepository = clienteFrecuenteRepository;
    }


    public EntidadCliente guardarCliente(String nombre, String email) {
        // Convertir nombre de usuario eliminando espacios y convirtiéndolo en minúsculas
        String username = nombre.replaceAll("\\s+", "").toLowerCase();
        // Verificar si el nombre ya existe

        if (clienteFrecuenteRepository.existsByNombre(username)) {
            throw new IllegalStateException("Error: El cliente con nombre '" + username + "' ya existe.");
        }

        // Verificar si el email ya existe
        if (clienteFrecuenteRepository.existsByEmail(email)) {
            throw new IllegalStateException("Error: El cliente con email '" + email + "' ya está registrado.");
        }

        // Si el cliente no existe, crear uno nuevo
        EntidadCliente nuevoCliente = new EntidadCliente();
        nuevoCliente.setNombre(username);
        nuevoCliente.setEmail(email);
        nuevoCliente.setCantidadReservas(0);
        nuevoCliente.setDescuentoFrecuente(0.0);

        return clienteFrecuenteRepository.save(nuevoCliente);
    }

    public double obtenerDescuentoFrecuente(String nombre) {
        // Buscar el cliente en la base de datos
        EntidadCliente cliente = clienteFrecuenteRepository.findbyNombre(nombre)
                .orElseThrow(() -> new NoSuchElementException("Error: Cliente con nombre " + nombre + " no encontrado."));
        // Obtener la cantidad de reservas
        int reservas = cliente.getCantidadReservas();
        double descuento;

        // Aplicar la lógica de descuentos según la cantidad de reservas
        if (reservas >= 7) {
            descuento = 30.0; // Muy frecuente (7 o más reservas)
        } else if (reservas >= 5) {
            descuento = 20.0; // Frecuente (5 a 6 reservas)
        } else if (reservas >= 2) {
            descuento = 10.0; // Regular (2 a 4 reservas)
        } else {
            descuento = 0.0; // No frecuente (0 a 1 reserva)
        }

        return descuento;
    }

    public EntidadCliente incrementarReservas(String nombre) {
        // Buscar el cliente
        EntidadCliente cliente = clienteFrecuenteRepository.findbyNombre(nombre)
                .orElseThrow(() -> new NoSuchElementException("Error: nombre con ID " + nombre + " no encontrado."));

        // Incrementar cantidad de reservas
        cliente.setCantidadReservas(cliente.getCantidadReservas() + 1);

        // Guardar los cambios
        return clienteFrecuenteRepository.save(cliente);
    }

    public List<String> obtenerNombresClientes() {
        return clienteFrecuenteRepository.findAll().stream()
                .map(EntidadCliente::getNombre)
                .collect(Collectors.toList());
    }
    
}
