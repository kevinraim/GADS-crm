package ar.edu.unlam.crmferretero.distribuidora;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DistribuidoraRequest(
        @NotBlank(message = "Ingresá la razón social") String razonSocial,
        String nombreFantasia,
        @Pattern(regexp = "^$|^[0-9]{11}$", message = "El CUIT debe tener 11 dígitos, sin guiones") String cuit,
        String email,
        String telefono,
        /** Solo se completa al crear: el primer ADMIN_COMERCIO de la distribuidora. Null en una edición. */
        @Valid NuevoAdminComercioRequest admin
) {}
