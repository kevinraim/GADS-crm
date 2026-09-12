package ar.edu.unlam.crmferretero.producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.empresa.ListaPrecios;
import ar.edu.unlam.crmferretero.shared.BaseDocument;

/** Sin stock: la gestión de inventario está fuera del alcance del producto. Catálogo propio de cada distribuidora. */
@Document("productos")
@CompoundIndexes(@CompoundIndex(name = "codigo_distribuidora", def = "{'codigo': 1, 'distribuidoraId': 1}", unique = true))
public class Producto extends BaseDocument {

    private String codigo;
    private String nombre;
    private String marca;
    private RubroProducto rubro;
    private UnidadVenta unidadVenta;
    private String presentacion;
    private BigDecimal precioListaReferencia;
    private boolean activo = true;
    private String distribuidoraId;

    /** Precio especial por lista de precios de la empresa (Empresa.listaPrecios); si una lista no
     * tiene entrada acá, se usa precioListaReferencia como base. */
    private Map<ListaPrecios, BigDecimal> preciosPorLista = new LinkedHashMap<>();

    /** Descuentos por cantidad, se aplica el de mayor cantidadMinima que la cantidad alcance. */
    private List<EscalonDescuento> escalonesDescuento = new ArrayList<>();

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

    public Producto(String codigo, String nombre, String marca, RubroProducto rubro, UnidadVenta unidadVenta,
                     String presentacion, BigDecimal precioListaReferencia, boolean activo, String distribuidoraId) {
        this(codigo, nombre, marca, rubro, unidadVenta, presentacion, precioListaReferencia, activo);
        this.distribuidoraId = distribuidoraId;
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

    public String getDistribuidoraId() {
        return distribuidoraId;
    }

    public void setDistribuidoraId(String distribuidoraId) {
        this.distribuidoraId = distribuidoraId;
    }

    public Map<ListaPrecios, BigDecimal> getPreciosPorLista() {
        return preciosPorLista;
    }

    public void setPreciosPorLista(Map<ListaPrecios, BigDecimal> preciosPorLista) {
        this.preciosPorLista = preciosPorLista != null ? preciosPorLista : new LinkedHashMap<>();
    }

    public List<EscalonDescuento> getEscalonesDescuento() {
        return escalonesDescuento;
    }

    public void setEscalonesDescuento(List<EscalonDescuento> escalonesDescuento) {
        this.escalonesDescuento = escalonesDescuento != null ? escalonesDescuento : new ArrayList<>();
    }
}
