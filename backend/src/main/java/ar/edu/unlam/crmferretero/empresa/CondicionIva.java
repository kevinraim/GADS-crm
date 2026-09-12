package ar.edu.unlam.crmferretero.empresa;

public enum CondicionIva {

    RESPONSABLE_INSCRIPTO("Responsable inscripto"),
    MONOTRIBUTO("Monotributo"),
    EXENTO("Exento"),
    CONSUMIDOR_FINAL("Consumidor final");

    private final String etiqueta;

    CondicionIva(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
