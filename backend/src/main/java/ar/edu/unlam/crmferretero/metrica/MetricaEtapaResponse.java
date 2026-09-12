package ar.edu.unlam.crmferretero.metrica;

import java.math.BigDecimal;

public record MetricaEtapaResponse(String etapaId, String etapaNombre, long cantidad, BigDecimal valorTotal) {}
