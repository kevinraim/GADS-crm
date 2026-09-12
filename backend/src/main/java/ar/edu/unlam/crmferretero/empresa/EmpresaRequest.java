package ar.edu.unlam.crmferretero.empresa;

import java.math.BigDecimal;

import ar.edu.unlam.crmferretero.shared.CondicionPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmpresaRequest(
        @NotBlank(message = "Ingresá la razón social") String razonSocial,
        String nombreFantasia,
        @Pattern(regexp = "^$|^[0-9]{11}$", message = "El CUIT debe tener 11 dígitos, sin guiones") String cuit,
        String email,
        String telefono,
        String direccion,
        String localidad,
        String sitioWeb,
        String responsableComercialId,
        String origenId,
        String observaciones,
        TipoComercio tipoComercio,
        CondicionIva condicionIva,
        String zonaReparto,
        CondicionPago condicionPagoHabitual,
        ListaPrecios listaPrecios,
        BigDecimal limiteCreditoEstimado
) {}
