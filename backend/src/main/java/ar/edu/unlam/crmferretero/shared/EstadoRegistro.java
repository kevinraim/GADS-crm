package ar.edu.unlam.crmferretero.shared;

/** Estado compartido por empresas y contactos. La baja es siempre lógica: nunca hay un DELETE. */
public enum EstadoRegistro {

    POTENCIAL("Potencial"),
    CLIENTE("Cliente"),
    INACTIVO("Inactivo"),
    NO_CONTACTAR("No contactar");

    private final String etiqueta;

    EstadoRegistro(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
