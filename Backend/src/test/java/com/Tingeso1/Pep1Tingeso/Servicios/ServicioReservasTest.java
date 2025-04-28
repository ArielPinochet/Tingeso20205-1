package com.Tingeso1.Pep1Tingeso.Servicios;

import java.time.LocalDate;
import java.time.LocalTime;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioReserva;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioReservasTest {

    @Mock
    private RepositorioReserva repositorioReserva;

    @InjectMocks
    private ServicioReservas servicioReservas;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarReserva() {
        EntidadReservas reserva = new EntidadReservas(
                1L,
                LocalDate.of(2025, 6, 15),
                LocalTime.of(14, 30),
                5,
                4,
                false,
                "Confirmada",
                25000.0,
                60
        );

        when(repositorioReserva.save(reserva)).thenReturn(reserva);

        EntidadReservas resultado = servicioReservas.guardarReserva(reserva);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdReserva());
        assertEquals(LocalDate.of(2025, 6, 15), resultado.getFechaReserva());
        assertEquals(LocalTime.of(14, 30), resultado.getHoraInicio());
        assertEquals(5, resultado.getNumeroVueltas());
        assertEquals(4, resultado.getCantidadPersonas());
        assertFalse(resultado.getDiaEspecial());
        assertEquals("Confirmada", resultado.getEstadoReserva());
        assertEquals(25000.0, resultado.getPrecioTotal());
        assertEquals(60, resultado.getDuracionTotal());

        verify(repositorioReserva, times(1)).save(reserva);
    }

    @Test
    void testBuscarPorId_Existe() {
        EntidadReservas reserva = new EntidadReservas(
                2L,
                LocalDate.of(2025, 7, 10),
                LocalTime.of(10, 45),
                3,
                6,
                true,
                "Pendiente",
                30000.0,
                90
        );

        when(repositorioReserva.findById(2L)).thenReturn(Optional.of(reserva));

        Optional<EntidadReservas> resultado = servicioReservas.buscarPorId(2L);

        assertTrue(resultado.isPresent());
        assertEquals(6, resultado.get().getCantidadPersonas());

        verify(repositorioReserva, times(1)).findById(2L);
    }

    @Test
    void testBuscarPorId_NoExiste() {
        when(repositorioReserva.findById(99L)).thenReturn(Optional.empty());

        Optional<EntidadReservas> resultado = servicioReservas.buscarPorId(99L);

        assertFalse(resultado.isPresent());

        verify(repositorioReserva, times(1)).findById(99L);
    }

    @Test
    void testListarTodas() {
        List<EntidadReservas> reservasMock = List.of(
                new EntidadReservas(1L, LocalDate.of(2025, 5, 20), LocalTime.of(11, 0), 4, 5, true, "Confirmada", 50000.0, 120),
                new EntidadReservas(2L, LocalDate.of(2025, 6, 25), LocalTime.of(15, 30), 6, 3, false, "Cancelada", 20000.0, 60)
        );

        when(repositorioReserva.findAll()).thenReturn(reservasMock);

        List<EntidadReservas> resultado = servicioReservas.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals(LocalDate.of(2025, 5, 20), resultado.get(0).getFechaReserva());
        assertEquals(LocalDate.of(2025, 6, 25), resultado.get(1).getFechaReserva());

        verify(repositorioReserva, times(1)).findAll();
    }

    @Test
    void testEliminarPorId() {
        servicioReservas.eliminarPorId(3L);

        verify(repositorioReserva, times(1)).deleteById(3L);
    }
}
