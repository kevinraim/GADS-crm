package ar.edu.unlam.crmferretero.actividad;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public record ActividadRequest(
        @NotBlank(message = "Elegí un tipo de actividad") String tipoActividadId,
        Instant fecha,
        @NotBlank(message = "Ingresá una descripción") String descripcion,
        String empresaId,
        String contactoId,
        String oportunidadId
) {}
