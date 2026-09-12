package ar.edu.unlam.crmferretero.empresa;

import java.math.BigDecimal;
import java.time.Instant;

import ar.edu.unlam.crmferretero.shared.CondicionPago;
import ar.edu.unlam.crmferretero.shared.EstadoRegistro;

public record EmpresaResponse(
        String id,
        String razonSocial,
        String nombreFantasia,
        String nombreVisible,
        String cuit,
        String email,
        String telefono,
        String direccion,
        String localidad,
        String sitioWeb,
        EstadoRegistro estado,
        TipoComercio tipoComercio,
        CondicionIva condicionIva,
        String zonaReparto,
        CondicionPago condicionPagoHabitual,
        ListaPrecios listaPrecios,
        BigDecimal limiteCreditoEstimado,
        String responsableComercialId,
        String responsableComercialNombre,
        String origenId,
        String origenNombre,
        String observaciones,
        Instant creadoEn,
        Instant modificadoEn
) {}
