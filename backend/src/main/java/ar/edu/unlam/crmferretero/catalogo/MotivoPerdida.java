package ar.edu.unlam.crmferretero.catalogo;

import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;

@Document("motivos_perdida")
public class MotivoPerdida extends BaseDocument {

    private String nombre;
    private int orden;
    private boolean activo = true;
    private String distribuidoraId;

    public MotivoPerdida() {
    }

    public MotivoPerdida(String nombre, int orden, boolean activo) {
        this.nombre = nombre;
        this.orden = orden;
        this.activo = activo;
    }

    public MotivoPerdida(String nombre, int orden, boolean activo, String distribuidoraId) {
        this(nombre, orden, activo);
        this.distribuidoraId = distribuidoraId;
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

    public String getDistribuidoraId() {
        return distribuidoraId;
    }

    public void setDistribuidoraId(String distribuidoraId) {
        this.distribuidoraId = distribuidoraId;
    }
}
