package ar.edu.unlam.crmferretero.oportunidad;

import jakarta.validation.constraints.NotBlank;

public record CambioEtapaRequest(
        @NotBlank(message = "Elegí una etapa") String etapaId,
        String observacion,
        String motivoPerdidaId
) {}
