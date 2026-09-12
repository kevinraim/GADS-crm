package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;

public record EmbudoOportunidadResponse(
        String id,
        String titulo,
        String clienteNombre,
        String responsableNombre,
        BigDecimal valorEstimado,
        EstadoOportunidad estado,
        boolean requiereAltaCuentaCorriente,
        String zonaReparto
) {}
