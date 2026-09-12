package ar.edu.unlam.crmferretero.shared;

import java.time.Instant;
import java.util.Map;

/** Formato único de error de la API. Lo arma GlobalExceptionHandler (y SecurityConfig para 401/403). */
public record ApiError(
        Instant momento,
        int estado,
        String codigo,
        String mensaje,
        String ruta,
        Map<String, String> erroresDeCampo
) {}
