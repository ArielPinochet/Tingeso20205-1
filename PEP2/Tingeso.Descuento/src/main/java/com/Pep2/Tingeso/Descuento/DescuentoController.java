package com.Pep2.Tingeso.Descuento;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/descuentos")
public class DescuentoController {

    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        this.descuentoService = descuentoService;
    }

    @GetMapping("/{idReserva}/{cantidadPersonas}/{idCliente}")
    public double calcularDescuento(
            @PathVariable Long idReserva,
            @PathVariable int cantidadPersonas,
            @PathVariable Long idCliente) {
        return descuentoService.calcularDescuento(idReserva, cantidadPersonas, idCliente);
    }
}