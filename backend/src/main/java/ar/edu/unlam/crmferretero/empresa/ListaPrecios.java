package ar.edu.unlam.crmferretero.empresa;

public enum ListaPrecios {

    A("Lista A"),
    B("Lista B"),
    C("Lista C");

    private final String etiqueta;

    ListaPrecios(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
