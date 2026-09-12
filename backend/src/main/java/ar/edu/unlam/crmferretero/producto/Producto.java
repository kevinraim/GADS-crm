package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;

/** Sin stock: la gestión de inventario está fuera del alcance del producto. Solo lectura en esta entrega. */
@Document("productos")
public class Producto extends BaseDocument {

    @Indexed(unique = true)
    private String codigo;

    private String nombre;
    private String marca;
    private RubroProducto rubro;
    private UnidadVenta unidadVenta;
    private String presentacion;
    private BigDecimal precioListaReferencia;
    private boolean activo = true;

    public Producto() {
    }

    public Producto(String codigo, String nombre, String marca, RubroProducto rubro, UnidadVenta unidadVenta,
                     String presentacion, BigDecimal precioListaReferencia, boolean activo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.rubro = rubro;
        this.unidadVenta = unidadVenta;
        this.presentacion = presentacion;
        this.precioListaReferencia = precioListaReferencia;
        this.activo = activo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public RubroProducto getRubro() {
        return rubro;
    }

    public void setRubro(RubroProducto rubro) {
        this.rubro = rubro;
    }

    public UnidadVenta getUnidadVenta() {
        return unidadVenta;
    }

    public void setUnidadVenta(UnidadVenta unidadVenta) {
        this.unidadVenta = unidadVenta;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public BigDecimal getPrecioListaReferencia() {
        return precioListaReferencia;
    }

    public void setPrecioListaReferencia(BigDecimal precioListaReferencia) {
        this.precioListaReferencia = precioListaReferencia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String nombreVisible() {
        return nombre + " (" + presentacion + ")";
    }
}
