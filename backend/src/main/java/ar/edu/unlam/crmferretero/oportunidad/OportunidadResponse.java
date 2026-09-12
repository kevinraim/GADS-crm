package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import ar.edu.unlam.crmferretero.shared.CondicionPago;

public record OportunidadResponse(
        String id,
        String titulo,
        String empresaId,
        String empresaNombre,
        String contactoId,
        String contactoNombre,
        String responsableComercialId,
        String responsableComercialNombre,
        List<ItemOportunidadResponse> items,
        BigDecimal valorEstimado,
        String etapaActualId,
        String etapaActualNombre,
        EstadoOportunidad estado,
        String origenId,
        String origenNombre,
        LocalDate fechaEstimadaCierre,
        LocalDate fechaRealCierre,
        String motivoPerdidaId,
        String motivoPerdidaNombre,
        String observaciones,
        CondicionPago condicionPagoNegociada,
        boolean requiereAltaCuentaCorriente,
        Instant creadoEn,
        Instant modificadoEn
) {}
