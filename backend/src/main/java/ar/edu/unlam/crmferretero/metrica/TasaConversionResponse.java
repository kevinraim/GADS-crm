package ar.edu.unlam.crmferretero.metrica;

import java.util.List;

public record TasaConversionResponse(long ganadas, long perdidas, double tasaGlobal,
                                      List<TasaConversionResponsableResponse> porResponsable) {}
