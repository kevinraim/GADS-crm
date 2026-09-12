package ar.edu.unlam.crmferretero.metrica;

import java.time.Instant;

public record ComercioInactivoResponse(String empresaId, String empresaNombre, Instant ultimaActividad) {}
