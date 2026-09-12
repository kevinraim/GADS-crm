package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ar.edu.unlam.crmferretero.empresa.ListaPrecios;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductoRequest(
        @NotBlank(message = "Ingresá el código") String codigo,
        @NotBlank(message = "Ingresá el nombre") String nombre,
        String marca,
        @NotNull(message = "Elegí un rubro") RubroProducto rubro,
        @NotNull(message = "Elegí una unidad de venta") UnidadVenta unidadVenta,
        String presentacion,
        BigDecimal precioListaReferencia,
        /** Precio especial por lista de precios; las listas sin entrada acá usan precioListaReferencia. */
        Map<ListaPrecios, BigDecimal> preciosPorLista,
        @Valid List<EscalonDescuentoRequest> escalonesDescuento
) {}
