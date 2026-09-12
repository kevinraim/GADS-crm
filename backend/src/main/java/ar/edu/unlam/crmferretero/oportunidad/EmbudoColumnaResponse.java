package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.unlam.crmferretero.catalogo.TipoEtapa;

public record EmbudoColumnaResponse(
        String etapaId,
        String etapaNombre,
        String etapaDescripcion,
        TipoEtapa etapaTipo,
        String color,
        int orden,
        long cantidad,
        BigDecimal valorTotal,
        List<EmbudoOportunidadResponse> oportunidades
) {}
