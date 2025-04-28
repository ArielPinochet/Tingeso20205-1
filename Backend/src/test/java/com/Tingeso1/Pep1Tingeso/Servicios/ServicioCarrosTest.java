package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadCarros;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioCarros;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioCarrosTest {

    @Mock
    private RepositorioCarros repositorioCarros; // Simulación del repositorio

    @InjectMocks
    private ServicioCarros servicioCarros; // Probamos la lógica del servicio

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testGuardarCarros() {
        // Crear objeto de prueba
        EntidadCarros carro = new EntidadCarros("K001", "SodiKart RT8", "activo");

        // Definir comportamiento esperado del repositorio
        when(repositorioCarros.save(carro)).thenReturn(carro);

        // Ejecutar método del servicio
        EntidadCarros resultado = servicioCarros.guardarCarros(carro);

        // Validar que se haya guardado correctamente
        assertNotNull(resultado);
        assertEquals("K001", resultado.getCodigoCarros());
        assertEquals("SodiKart RT8", resultado.getModelo());
        assertEquals("activo", resultado.getEstado());

        verify(repositorioCarros, times(1)).save(carro); // Verificar que se ejecutó el método en el repositorio
    }

    @Test
    void testBuscarPorCodigo_Existe() {
        EntidadCarros carro = new EntidadCarros("K002", "SodiKart RT8", "activo");
        when(repositorioCarros.findById("K002")).thenReturn(Optional.of(carro));

        Optional<EntidadCarros> resultado = servicioCarros.buscarPorCodigo("K002");

        assertTrue(resultado.isPresent());
        assertEquals("K002", resultado.get().getCodigoCarros());

        verify(repositorioCarros, times(1)).findById("K002");
    }

    @Test
    void testBuscarPorCodigo_NoExiste() {
        when(repositorioCarros.findById("K999")).thenReturn(Optional.empty());

        Optional<EntidadCarros> resultado = servicioCarros.buscarPorCodigo("K999");

        assertFalse(resultado.isPresent()); // Validamos que devuelve un Optional vacío

        verify(repositorioCarros, times(1)).findById("K999");
    }

    @Test
    void testListarTodos() {
        List<EntidadCarros> carrosMock = List.of(
                new EntidadCarros("K001", "SodiKart RT8", "activo"),
                new EntidadCarros("K002", "SodiKart RT8", "activo")
        );

        when(repositorioCarros.findAll()).thenReturn(carrosMock);

        List<EntidadCarros> resultado = servicioCarros.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("K001", resultado.get(0).getCodigoCarros());
        assertEquals("K002", resultado.get(1).getCodigoCarros());

        verify(repositorioCarros, times(1)).findAll();
    }

    @Test
    void testEliminarPorCodigo() {
        // Ejecutamos el método de eliminación sin esperar retorno
        servicioCarros.eliminarPorCodigo("K003");

        verify(repositorioCarros, times(1)).deleteById("K003");
    }
}
