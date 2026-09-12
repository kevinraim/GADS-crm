package ar.edu.unlam.crmferretero.usuario;

/**
 * Rol del usuario, viaja en el token JWT y activa permisos vía @PreAuthorize.
 * ADMIN es el superadmin de la plataforma (no pertenece a ninguna distribuidora).
 * ADMIN_COMERCIO administra su propia distribuidora por completo.
 * VENDEDOR y RESPONSABLE_COMERCIAL tienen los mismos permisos; se mantienen separados solo por
 * vocabulario/reporting dentro de la distribuidora.
 */
public enum Rol {

    ADMIN("Administrador"),
    ADMIN_COMERCIO("Administrador de la distribuidora"),
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
