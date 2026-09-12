package ar.edu.unlam.crmferretero.actividad;

import java.time.Instant;

public record ActividadResponse(
        String id,
        String tipoActividadId,
        String tipoActividadNombre,
        Instant fecha,
        String descripcion,
        String usuarioId,
        String usuarioNombre,
        String empresaId,
        String contactoId,
        String oportunidadId,
        Instant creadoEn
) {}
