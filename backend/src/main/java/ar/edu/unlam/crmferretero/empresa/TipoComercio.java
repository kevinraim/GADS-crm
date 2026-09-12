package ar.edu.unlam.crmferretero.empresa;

public enum TipoComercio {

    FERRETERIA_MINORISTA("Ferretería minorista"),
    CORRALON("Corralón"),
    TALLER("Taller"),
    CONSTRUCTORA("Constructora"),
    INDUSTRIA("Industria"),
    ORGANISMO_PUBLICO("Organismo público"),
    OTRO("Otro");

    private final String etiqueta;

    TipoComercio(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
