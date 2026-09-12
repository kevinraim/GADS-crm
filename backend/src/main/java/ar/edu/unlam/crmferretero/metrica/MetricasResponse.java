package ar.edu.unlam.crmferretero.metrica;

import java.math.BigDecimal;
import java.util.List;

public record MetricasResponse(
        List<MetricaEtapaResponse> oportunidadesPorEtapa,
        TasaConversionResponse tasaConversion,
        BigDecimal pipelineAbierto,
        List<RankingResponsableResponse> rankingResponsables,
        List<MotivoFrecuenteResponse> motivosPerdidaFrecuentes,
        List<ComercioInactivoResponse> comerciosSinActividadReciente,
        List<ComercioPorMesResponse> comerciosNuevosPorMes
) {}
