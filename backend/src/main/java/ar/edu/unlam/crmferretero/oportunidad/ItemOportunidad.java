package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;

import ar.edu.unlam.crmferretero.producto.UnidadVenta;

/**
 * Ítem embebido de la lista de materiales. Se guarda el nombre del producto y la unidad de venta
 * además del id: si mañana cambia el producto, la cotización histórica sigue siendo legible.
 */
public class ItemOportunidad {

    private String productoId;
    private String productoNombre;
    private UnidadVenta unidadVenta;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;

    public ItemOportunidad() {
    }

    public ItemOportunidad(String productoId, String productoNombre, UnidadVenta unidadVenta,
                            BigDecimal cantidad, BigDecimal precioUnitario) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.unidadVenta = unidadVenta;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal subtotal() {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return cantidad.multiply(precioUnitario);
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public UnidadVenta getUnidadVenta() {
        return unidadVenta;
    }

    public void setUnidadVenta(UnidadVenta unidadVenta) {
        this.unidadVenta = unidadVenta;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
