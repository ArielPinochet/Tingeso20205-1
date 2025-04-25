package com.Tingeso1.Pep1Tingeso.Controladores;

import com.Tingeso1.Pep1Tingeso.Entidades.EntidadComprobanteDePago;
import com.Tingeso1.Pep1Tingeso.Entidades.EntidadReservas;
import com.Tingeso1.Pep1Tingeso.Servicios.EmailService;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioComprobantePago;
import com.Tingeso1.Pep1Tingeso.Servicios.ServicioReservas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ControladorPago {
    private final ServicioComprobantePago servicioComprobantePago;

    public ControladorPago(ServicioComprobantePago servicioComprobantePago) {
        this.servicioComprobantePago = servicioComprobantePago;
    }

    @PostMapping("/enviar-pago")
    public ResponseEntity<String> procesarPago(@RequestBody EntidadComprobanteDePago datosPago) {
        servicioComprobantePago.generarComprobante(datosPago.getReserva(), datosPago.getCorreosClientes());
        return ResponseEntity.ok("Pago procesado y correos guardados.");
    }
}