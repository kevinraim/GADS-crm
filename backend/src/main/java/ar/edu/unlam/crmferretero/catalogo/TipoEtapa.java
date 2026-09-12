package ar.edu.unlam.crmferretero.catalogo;

/**
 * Puente entre la etapa (configurable, vive en Mongo) y el estado de la oportunidad
 * (fijo, es un enum del código: EstadoOportunidad).
 */
public enum TipoEtapa {
    ABIERTA, GANADA, PERDIDA
}
