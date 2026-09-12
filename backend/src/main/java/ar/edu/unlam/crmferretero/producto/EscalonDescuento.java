package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;

/**
 * Descuento por cantidad embebido en el producto: a partir de "cantidadMinima" unidades se aplica
 * "descuentoPorcentaje" (0-100) sobre el precio de lista resuelto. Si varios escalones aplican para
 * una cantidad, se usa el de mayor cantidadMinima (el más beneficioso).
 */
public class EscalonDescuento {

    private BigDecimal cantidadMinima;
    private BigDecimal descuentoPorcentaje;

    public EscalonDescuento() {
    }

    public EscalonDescuento(BigDecimal cantidadMinima, BigDecimal descuentoPorcentaje) {
        this.cantidadMinima = cantidadMinima;
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public BigDecimal getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(BigDecimal cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }
}
