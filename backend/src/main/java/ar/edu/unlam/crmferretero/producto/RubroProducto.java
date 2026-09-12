package ar.edu.unlam.crmferretero.producto;

public enum RubroProducto {

    ELECTRICIDAD("Electricidad"),
    PLOMERIA("Plomería"),
    HERRAMIENTAS_MANUALES("Herramientas manuales"),
    HERRAMIENTAS_ELECTRICAS("Herramientas eléctricas"),
    FIJACIONES("Fijaciones"),
    PINTURERIA("Pinturería"),
    CONSTRUCCION("Construcción"),
    SEGURIDAD("Seguridad"),
    JARDIN("Jardín");

    private final String etiqueta;

    RubroProducto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
