package com.Tingeso1.Pep1Tingeso.Servicios;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Repositorios.RepositorioComprobantePago;
import com.itextpdf.text.DocumentException;
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

class ServicioComprobantePagoTest {

    @Mock
    private RepositorioComprobantePago repositorioComprobantePago;

    @Mock
    private ServicioReservas servicioReservas;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ServicioComprobantePago servicioComprobantePago;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuscarPorId_Existe() {
        EntidadComprobanteDePago comprobante = new EntidadComprobanteDePago();
        comprobante.setIdComprobante(1L);

        when(repositorioComprobantePago.findById(1L)).thenReturn(Optional.of(comprobante));

        Optional<EntidadComprobanteDePago> resultado = servicioComprobantePago.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdComprobante());

        verify(repositorioComprobantePago, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NoExiste() {
        when(repositorioComprobantePago.findById(99L)).thenReturn(Optional.empty());

        Optional<EntidadComprobanteDePago> resultado = servicioComprobantePago.buscarPorId(99L);

        assertFalse(resultado.isPresent());

        verify(repositorioComprobantePago, times(1)).findById(99L);
    }

    @Test
    void testListarTodos() {
        List<EntidadComprobanteDePago> comprobantesMock = List.of(new EntidadComprobanteDePago(), new EntidadComprobanteDePago());

        when(repositorioComprobantePago.findAll()).thenReturn(comprobantesMock);

        List<EntidadComprobanteDePago> resultado = servicioComprobantePago.listarTodos();

        assertEquals(2, resultado.size());

        verify(repositorioComprobantePago, times(1)).findAll();
    }

    @Test
    void testEliminarPorId() {
        servicioComprobantePago.eliminarPorId(3L);

        verify(repositorioComprobantePago, times(1)).deleteById(3L);
    }

    @Test
    void testGenerarComprobante() {
        EntidadReservas reserva = new EntidadReservas();
        reserva.setFechaReserva(LocalDate.of(2025, 5, 20));
        reserva.setPrecioTotal(50000.0);

        List<String> correosClientes = List.of("cliente1@example.com", "cliente2@example.com");

        // 🔹 Mockeamos el comportamiento del repositorio para que devuelva el comprobante guardado
        when(repositorioComprobantePago.save(any(EntidadComprobanteDePago.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntidadComprobanteDePago resultado = servicioComprobantePago.generarComprobante(reserva, correosClientes);

        assertNotNull(resultado);
        assertEquals(reserva, resultado.getReserva());
        assertEquals(LocalDate.now(), resultado.getFechaEmision());
        assertEquals(50000.0, resultado.getTotalConIva());
        assertEquals(correosClientes, resultado.getCorreosClientes());
    }


    @Test
    void testGuardarComprobante() {
        EntidadComprobanteDePago comprobante = new EntidadComprobanteDePago();
        comprobante.setIdComprobante(5L);

        when(repositorioComprobantePago.save(comprobante)).thenReturn(comprobante);

        EntidadComprobanteDePago resultado = servicioComprobantePago.guardarComprobante(comprobante);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getIdComprobante());

        verify(repositorioComprobantePago, times(1)).save(comprobante);
    }

    @Test
    void testEnviarComprobante() {
        EntidadComprobanteDePago comprobante = new EntidadComprobanteDePago();
        comprobante.setIdComprobante(7L);
        comprobante.setArchivoPdf("VGhpcyBpcyBhIHRlc3Q="); // Base64 de "This is a test"

        servicioComprobantePago.enviarComprobante(comprobante);

        verify(emailService, times(1)).enviarComprobante(eq(comprobante), any(byte[].class));
    }
}
