package ar.edu.unlam.crmferretero.shared;

import jakarta.validation.constraints.NotNull;

/** Cuerpo del PATCH /estado, compartido por empresas y contactos. */
public record PatchEstadoRequest(
        @NotNull(message = "Elegí un estado") EstadoRegistro estado
) {}
