package ar.edu.unlam.crmferretero.catalogo;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo compartido de alta/edición para Origen, MotivoPerdida y TipoActividad: misma forma. */
public record CatalogoSimpleRequest(
        @NotBlank(message = "Ingresá el nombre") String nombre,
        int orden
) {}
