package ar.edu.unlam.crmferretero.usuario;

/**
 * Rol del usuario. Se guarda y viaja en el token JWT desde esta entrega, pero todavía no
 * restringe nada: la activación de permisos efectivos (@PreAuthorize) queda para la entrega 2.
 */
public enum Rol {

    ADMIN("Administrador"),
    VENDEDOR("Vendedor de zona"),
    RESPONSABLE_COMERCIAL("Responsable comercial");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String authority() {
        return "ROLE_" + name();
    }
}
