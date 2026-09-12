package ar.edu.unlam.crmferretero.catalogo;

public record EtapaResponse(
        String id,
        String nombre,
        String descripcion,
        int orden,
        TipoEtapa tipo,
        String color,
        boolean activa
) {

    public static EtapaResponse de(Etapa etapa) {
        return new EtapaResponse(etapa.getId(), etapa.getNombre(), etapa.getDescripcion(), etapa.getOrden(),
                etapa.getTipo(), etapa.getColor(), etapa.isActiva());
    }
}
