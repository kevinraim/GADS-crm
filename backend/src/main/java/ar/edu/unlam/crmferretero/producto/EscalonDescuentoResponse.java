package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;

public record EscalonDescuentoResponse(BigDecimal cantidadMinima, BigDecimal descuentoPorcentaje) {

    public static EscalonDescuentoResponse de(EscalonDescuento escalon) {
        return new EscalonDescuentoResponse(escalon.getCantidadMinima(), escalon.getDescuentoPorcentaje());
    }
}
