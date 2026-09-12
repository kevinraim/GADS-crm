package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ar.edu.unlam.crmferretero.empresa.ListaPrecios;

public record ProductoResponse(
        String id,
        String codigo,
        String nombre,
        String marca,
        RubroProducto rubro,
        UnidadVenta unidadVenta,
        String presentacion,
        BigDecimal precioListaReferencia,
        boolean activo,
        Map<ListaPrecios, BigDecimal> preciosPorLista,
        List<EscalonDescuentoResponse> escalonesDescuento
) {}
