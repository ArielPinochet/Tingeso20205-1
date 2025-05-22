package com.Pep2.Tingeso.Descuento;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class DescuentoService {

    private final DescuentoRepository descuentoRepository;
    private final RestTemplate restTemplate;

    public DescuentoService(DescuentoRepository descuentoRepository, RestTemplate restTemplate) {
        this.descuentoRepository = descuentoRepository;
        this.restTemplate = restTemplate;
    }

    public double calcularDescuento(Long idReserva, int cantidadPersonas, Long idCliente) {
        double descuentoTotal = 0;

        // 1️⃣ Descuento por número de personas
        if (cantidadPersonas >= 3 && cantidadPersonas <= 5) descuentoTotal += 10;
        else if (cantidadPersonas >= 6 && cantidadPersonas <= 10) descuentoTotal += 20;
        else if (cantidadPersonas >= 11 && cantidadPersonas <= 15) descuentoTotal += 30;

        // 2️⃣ Descuento por cliente frecuente
        Integer visitasMensuales = restTemplate.getForObject("http://clientes-service/api/clientes/" + idCliente + "/visitas", Integer.class);
        if (visitasMensuales != null) {
            if (visitasMensuales >= 7) descuentoTotal += 30;
            else if (visitasMensuales >= 5) descuentoTotal += 20;
            else if (visitasMensuales >= 2) descuentoTotal += 10;
        }

        // 3️⃣ Descuento por día especial y fines de semana
        Boolean diaEspecial = restTemplate.getForObject("http://tarifas-especiales-service/api/tarifas-especiales/dia-especial", Boolean.class);
        Boolean finDeSemana = restTemplate.getForObject("http://tarifas-especiales-service/api/tarifas-especiales/fin-de-semana", Boolean.class);
        if (diaEspecial != null && diaEspecial) descuentoTotal += 15;
        if (finDeSemana != null && finDeSemana) descuentoTotal += 10;

        // 4️⃣ Descuento por cumpleaños
        Integer cumpleanosGrupo = restTemplate.getForObject("http://reservas-service/api/reservas/" + idReserva + "/cumpleanos", Integer.class);
        if (cumpleanosGrupo != null) {
            int descuentoCumpleanos = (cumpleanosGrupo == 1) ? 50 : (cumpleanosGrupo >= 2) ? 100 : 0;
            descuentoTotal += descuentoCumpleanos;
        }

        // Guardar en la base de datos
        DescuentoEntity descuentoEntity = new DescuentoEntity();
        descuentoEntity.setIdReserva(idReserva);
        descuentoEntity.setDescuentoTotal(descuentoTotal);
        descuentoRepository.save(descuentoEntity);

        return descuentoTotal;
    }
}