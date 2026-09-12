package ar.edu.unlam.crmferretero.shared;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Centraliza el filtrado por distribuidora (multi-tenancy) y por visibilidad fina
 * (VENDEDOR/RESPONSABLE_COMERCIAL solo ven lo propio o lo asignado) para no repetirlo en cada
 * servicio que arma listados con MongoTemplate + Criteria.
 */
public final class AlcanceUtils {

    private AlcanceUtils() {
    }

    /**
     * Agrega el filtro de distribuidora a la lista de condiciones.
     * ADMIN no tiene distribuidoraId propia: si manda distribuidoraIdParam filtra por esa, si no,
     * ve el agregado de todas. Cualquier otro rol siempre queda acotado a la suya, ignorando
     * cualquier valor que mande el cliente.
     */
    public static void porDistribuidora(List<Criteria> condiciones, String distribuidoraIdParam) {
        if (TenantContext.esAdmin()) {
            String normalizado = ConsultaUtils.normalizar(distribuidoraIdParam);
            if (normalizado != null) {
                condiciones.add(Criteria.where("distribuidoraId").is(normalizado));
            }
        } else {
            condiciones.add(Criteria.where("distribuidoraId").is(TenantContext.distribuidoraId()));
        }
    }

    /**
     * Agrega, solo para VENDEDOR/RESPONSABLE_COMERCIAL, la restricción de que el registro haya
     * sido creado por el usuario actual o esté asignado a él. ADMIN y ADMIN_COMERCIO no tienen
     * esta restricción.
     */
    public static void porVisibilidadFina(List<Criteria> condiciones) {
        if (!TenantContext.tieneVisibilidadAmplia()) {
            condiciones.add(new Criteria().orOperator(
                    Criteria.where("creadoPor").is(TenantContext.email()),
                    Criteria.where("responsableComercialId").is(TenantContext.usuarioId())
            ));
        }
    }

    /** distribuidoraId a asociar a un documento nuevo: siempre el del contexto, nunca el del body. */
    public static String distribuidoraIdParaAlta() {
        return TenantContext.distribuidoraId();
    }
}
