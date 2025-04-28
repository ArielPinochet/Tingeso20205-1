package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadClientes;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioClientesTest {

    @Mock
    private RepositorioCliente repositorioCliente; // Simulación del repositorio

    @InjectMocks
    private ServicioClientes servicioClientes; // Probamos la lógica del servicio

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testGuardarCliente() {
        EntidadClientes cliente = new EntidadClientes();
        cliente.setIdCliente(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan@example.com");

        when(repositorioCliente.save(cliente)).thenReturn(cliente);

        EntidadClientes resultado = servicioClientes.guardarCliente(cliente);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCliente());
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan@example.com", resultado.getEmail());

        verify(repositorioCliente, times(1)).save(cliente);
    }

    @Test
    void testBuscarPorId_Existe() {
        EntidadClientes cliente = new EntidadClientes();
        cliente.setIdCliente(2L);
        cliente.setNombre("Ana Gómez");
        cliente.setEmail("ana@example.com");

        when(repositorioCliente.findById(2L)).thenReturn(Optional.of(cliente));

        Optional<EntidadClientes> resultado = servicioClientes.buscarPorId(2L);

        assertTrue(resultado.isPresent());
        assertEquals("Ana Gómez", resultado.get().getNombre());

        verify(repositorioCliente, times(1)).findById(2L);
    }

    @Test
    void testBuscarPorId_NoExiste() {
        when(repositorioCliente.findById(99L)).thenReturn(Optional.empty());

        Optional<EntidadClientes> resultado = servicioClientes.buscarPorId(99L);

        assertFalse(resultado.isPresent());

        verify(repositorioCliente, times(1)).findById(99L);
    }

    @Test
    void testListarTodos() {
        List<EntidadClientes> clientesMock = List.of(
                new EntidadClientes(1L, "Luis Martínez", "luis@example.com", LocalDate.parse("1990-01-01")),
                new EntidadClientes(2L, "Carlos Rodríguez", "carlos@example.com",LocalDate.parse("1990-01-01"))
        );

        when(repositorioCliente.findAll()).thenReturn(clientesMock);

        List<EntidadClientes> resultado = servicioClientes.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Luis Martínez", resultado.get(0).getNombre());
        assertEquals("Carlos Rodríguez", resultado.get(1).getNombre());

        verify(repositorioCliente, times(1)).findAll();
    }

    @Test
    void testEliminarPorId() {
        servicioClientes.eliminarPorId(3L);

        verify(repositorioCliente, times(1)).deleteById(3L);
    }
}
