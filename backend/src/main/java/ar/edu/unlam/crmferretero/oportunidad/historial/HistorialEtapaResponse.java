package ar.edu.unlam.crmferretero.oportunidad.historial;

import java.time.Instant;

public record HistorialEtapaResponse(
        String id,
        String etapaAnteriorId,
        String etapaAnteriorNombre,
        String etapaNuevaId,
        String etapaNuevaNombre,
        Instant fechaHora,
        String usuarioEmail,
        String observacion
) {

    public static HistorialEtapaResponse de(HistorialEtapa historial) {
        return new HistorialEtapaResponse(
                historial.getId(),
                historial.getEtapaAnteriorId(),
                historial.getEtapaAnteriorNombre(),
                historial.getEtapaNuevaId(),
                historial.getEtapaNuevaNombre(),
                historial.getFechaHora(),
                historial.getUsuarioEmail(),
                historial.getObservacion()
        );
    }
}
