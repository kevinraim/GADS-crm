package ar.edu.unlam.crmferretero.shared;

import jakarta.validation.constraints.NotNull;

/** Cuerpo del PATCH /estado para entidades con baja lógica simple (activo/inactivo): usuarios, productos, catálogos. */
public record PatchEstadoActivoRequest(
        @NotNull(message = "Indicá el estado") Boolean activo
) {}
