package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record EscalonDescuentoRequest(
        @NotNull(message = "Indicá la cantidad mínima") @DecimalMin(value = "0.01", message = "Tiene que ser mayor a 0") BigDecimal cantidadMinima,
        @NotNull(message = "Indicá el descuento") @DecimalMin(value = "0", message = "No puede ser negativo")
        @DecimalMax(value = "100", message = "No puede superar el 100%") BigDecimal descuentoPorcentaje
) {}
