package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;

public record ProductoResponse(
        String id,
        String codigo,
        String nombre,
        String marca,
        RubroProducto rubro,
        UnidadVenta unidadVenta,
        String presentacion,
        BigDecimal precioListaReferencia,
        boolean activo
) {}
