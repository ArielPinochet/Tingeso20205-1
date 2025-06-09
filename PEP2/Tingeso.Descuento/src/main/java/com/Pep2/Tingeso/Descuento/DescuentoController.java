package com.Pep2.Tingeso.Descuento;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/descuentos")
public class DescuentoController {

    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        this.descuentoService = descuentoService;
    }

    @PostMapping("/")
    public ResponseEntity<DescuentoEntity> crearDescuento(
            @RequestParam int numeroPersonas,
            @RequestParam Long idReserva,
            @RequestParam String nombreCliente) {

        double descuentoTotal = descuentoService.calcularDescuentoTotal(numeroPersonas, idReserva, nombreCliente);

        DescuentoEntity descuento = new DescuentoEntity();
        descuento.setIdReserva(idReserva);
        descuento.setNombrecliente(nombreCliente);
        descuento.setDescuentoTotal(descuentoTotal);

        return ResponseEntity.ok(descuento);
    }
    @GetMapping("/obtener/{idReserva}")
    public ResponseEntity<DescuentoEntity> obtenerDescuentoPorReserva(@PathVariable Long idReserva) {
        Optional<DescuentoEntity> descuento = descuentoService.obtenerDescuentoPorReserva(idReserva);

        return descuento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }
}
