package ar.edu.unlam.crmferretero.catalogo;

import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;

@Document("origenes")
public class Origen extends BaseDocument {

    private String nombre;
    private int orden;
    private boolean activo = true;

    public Origen() {
    }

    public Origen(String nombre, int orden, boolean activo) {
        this.nombre = nombre;
        this.orden = orden;
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
