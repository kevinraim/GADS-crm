package ar.edu.unlam.crmferretero.catalogo;

/** Respuesta compartida de ABM para Origen, MotivoPerdida y TipoActividad: misma forma. */
public record CatalogoSimpleResponse(String id, String nombre, int orden, boolean activo) {

    public static CatalogoSimpleResponse deOrigen(Origen origen) {
        return new CatalogoSimpleResponse(origen.getId(), origen.getNombre(), origen.getOrden(), origen.isActivo());
    }

    public static CatalogoSimpleResponse deMotivo(MotivoPerdida motivo) {
        return new CatalogoSimpleResponse(motivo.getId(), motivo.getNombre(), motivo.getOrden(), motivo.isActivo());
    }

    public static CatalogoSimpleResponse deTipoActividad(TipoActividad tipo) {
        return new CatalogoSimpleResponse(tipo.getId(), tipo.getNombre(), tipo.getOrden(), tipo.isActivo());
    }
}
