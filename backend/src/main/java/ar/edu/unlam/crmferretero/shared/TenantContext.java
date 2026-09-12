package ar.edu.unlam.crmferretero.shared;

import ar.edu.unlam.crmferretero.usuario.Rol;

/**
 * Datos del usuario autenticado en el request actual, poblados por JwtAuthFilter a partir de los
 * claims del JWT y limpiados al final de cada request. ADMIN es el único rol con distribuidoraId
 * null: no pertenece a ninguna distribuidora y ve todas.
 */
public final class TenantContext {

    public record Datos(String usuarioId, String email, Rol rol, String distribuidoraId) {
    }

    private static final ThreadLocal<Datos> ACTUAL = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(Datos datos) {
        ACTUAL.set(datos);
    }

    public static void clear() {
        ACTUAL.remove();
    }

    public static Datos get() {
        return ACTUAL.get();
    }

    public static Rol rol() {
        Datos datos = ACTUAL.get();
        return datos == null ? null : datos.rol();
    }

    public static String usuarioId() {
        Datos datos = ACTUAL.get();
        return datos == null ? null : datos.usuarioId();
    }

    public static String email() {
        Datos datos = ACTUAL.get();
        return datos == null ? null : datos.email();
    }

    public static String distribuidoraId() {
        Datos datos = ACTUAL.get();
        return datos == null ? null : datos.distribuidoraId();
    }

    public static boolean esAdmin() {
        return rol() == Rol.ADMIN;
    }

    /** ADMIN y ADMIN_COMERCIO ven todo lo de la distribuidora, sin restricción por responsable. */
    public static boolean tieneVisibilidadAmplia() {
        Rol rol = rol();
        return rol == Rol.ADMIN || rol == Rol.ADMIN_COMERCIO;
    }
}
