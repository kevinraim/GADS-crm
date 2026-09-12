package ar.edu.unlam.crmferretero.metrica;

public record TasaConversionResponsableResponse(String responsableId, String responsableNombre, long ganadas,
                                                  long perdidas, double tasa) {}
