package ar.edu.unlam.crmferretero.producto;

import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getMarca(),
                producto.getRubro(),
                producto.getUnidadVenta(),
                producto.getPresentacion(),
                producto.getPrecioListaReferencia(),
                producto.isActivo()
        );
    }
}
