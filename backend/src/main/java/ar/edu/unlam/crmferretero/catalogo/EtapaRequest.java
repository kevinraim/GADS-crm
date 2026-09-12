package ar.edu.unlam.crmferretero.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EtapaRequest(
        @NotBlank(message = "Ingresá el nombre") String nombre,
        String descripcion,
        int orden,
        @NotNull(message = "Elegí un tipo de etapa") TipoEtapa tipo,
        String color
) {}
