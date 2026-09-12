package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;

import ar.edu.unlam.crmferretero.producto.UnidadVenta;

public record ItemOportunidadResponse(
        String productoId,
        String productoNombre,
        UnidadVenta unidadVenta,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
