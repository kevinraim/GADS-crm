package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ar.edu.unlam.crmferretero.shared.CondicionPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record OportunidadRequest(
        @NotBlank(message = "Ingresá el título") String titulo,
        String empresaId,
        String contactoId,
        @NotBlank(message = "Elegí un responsable comercial") String responsableComercialId,
        @Valid List<ItemOportunidadRequest> items,
        BigDecimal valorEstimado,
        String etapaActualId,
        String origenId,
        LocalDate fechaEstimadaCierre,
        String observaciones,
        CondicionPago condicionPagoNegociada,
        Boolean requiereAltaCuentaCorriente
) {}
