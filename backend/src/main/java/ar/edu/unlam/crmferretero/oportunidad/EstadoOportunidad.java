package ar.edu.unlam.crmferretero.oportunidad;

public enum EstadoOportunidad {

    ABIERTA("Abierta"),
    GANADA("Ganada"),
    PERDIDA("Perdida");

    private final String etiqueta;

    EstadoOportunidad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
