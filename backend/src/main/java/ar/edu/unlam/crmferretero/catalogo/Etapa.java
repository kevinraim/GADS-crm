package ar.edu.unlam.crmferretero.catalogo;

import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;

@Document("etapas")
public class Etapa extends BaseDocument {

    private String nombre;
    private String descripcion;
    private int orden;
    private TipoEtapa tipo;
    private String color;
    private boolean activa = true;

    public Etapa() {
    }

    public Etapa(String nombre, String descripcion, int orden, TipoEtapa tipo, String color, boolean activa) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
        this.tipo = tipo;
        this.color = color;
        this.activa = activa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public TipoEtapa getTipo() {
        return tipo;
    }

    public void setTipo(TipoEtapa tipo) {
        this.tipo = tipo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
