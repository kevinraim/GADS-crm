package ar.edu.unlam.crmferretero.catalogo;

/**
 * Puente entre la etapa (configurable, vive en Mongo) y el estado de la oportunidad
 * (fijo, es un enum del código: EstadoOportunidad).
 */
public enum TipoEtapa {

    ABIERTA("Abierta"),
    GANADA("Ganada"),
    PERDIDA("Perdida");

    private final String etiqueta;

    TipoEtapa(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
