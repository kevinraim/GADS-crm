package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemOportunidadRequest(
        @NotBlank(message = "Elegí un producto") String productoId,
        @NotNull(message = "Indicá la cantidad")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero") BigDecimal cantidad,
        @NotNull(message = "Indicá el precio unitario")
        @DecimalMin(value = "0", message = "El precio no puede ser negativo") BigDecimal precioUnitario
) {}
