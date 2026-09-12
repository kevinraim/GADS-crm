package ar.edu.unlam.crmferretero.shared;

/**
 * Par valor/etiqueta reutilizado para selectores de todo tipo: opciones de enums (GET /api/enums),
 * y listas reducidas de entidades para combos (GET /api/empresas/opciones, etc.), donde
 * "valor" es el id y "etiqueta" el nombre visible.
 */
public record OpcionResponse(String valor, String etiqueta) {}
