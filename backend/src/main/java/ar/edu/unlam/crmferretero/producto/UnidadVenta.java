package ar.edu.unlam.crmferretero.producto;

public enum UnidadVenta {

    UNIDAD("Unidad"),
    CAJA("Caja"),
    BOLSA("Bolsa"),
    ROLLO("Rollo"),
    METRO("Metro"),
    KILO("Kilo"),
    LITRO("Litro"),
    BALDE("Balde");

    private final String etiqueta;

    UnidadVenta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
