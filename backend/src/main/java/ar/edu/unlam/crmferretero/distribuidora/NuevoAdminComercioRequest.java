package ar.edu.unlam.crmferretero.distribuidora;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Primer usuario ADMIN_COMERCIO que se crea junto con la distribuidora: no hay autoregistro público. */
public record NuevoAdminComercioRequest(
        @NotBlank(message = "Ingresá el nombre") String nombre,
        @NotBlank(message = "Ingresá el apellido") String apellido,
        @NotBlank(message = "Ingresá el email") @Email(message = "Email inválido") String email,
        @NotBlank(message = "Ingresá una contraseña") String password
) {}
