package ar.edu.unlam.crmferretero.metrica;

import java.math.BigDecimal;

public record RankingResponsableResponse(String responsableId, String responsableNombre, long cantidadGanadas,
                                          BigDecimal valorGanado) {}
