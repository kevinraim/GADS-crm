package ar.edu.unlam.crmferretero.distribuidora;

import java.time.Instant;

public record DistribuidoraResponse(
        String id,
        String razonSocial,
        String nombreFantasia,
        String nombreVisible,
        String cuit,
        String email,
        String telefono,
        boolean activa,
        Instant creadoEn
) {
    public static DistribuidoraResponse de(Distribuidora distribuidora) {
        return new DistribuidoraResponse(
                distribuidora.getId(),
                distribuidora.getRazonSocial(),
                distribuidora.getNombreFantasia(),
                distribuidora.nombreVisible(),
                distribuidora.getCuit(),
                distribuidora.getEmail(),
                distribuidora.getTelefono(),
                distribuidora.isActiva(),
                distribuidora.getCreadoEn()
        );
    }
}
