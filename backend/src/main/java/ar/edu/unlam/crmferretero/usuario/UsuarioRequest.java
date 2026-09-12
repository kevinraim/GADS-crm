package ar.edu.unlam.crmferretero.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Alta de un VENDEDOR o RESPONSABLE_COMERCIAL dentro de la propia distribuidora del ADMIN_COMERCIO. */
public record UsuarioRequest(
        @NotBlank(message = "Ingresá el nombre") String nombre,
        @NotBlank(message = "Ingresá el apellido") String apellido,
        @NotBlank(message = "Ingresá el email") @Email(message = "Email inválido") String email,
        @NotBlank(message = "Ingresá una contraseña") String password,
        @NotNull(message = "Elegí un rol") Rol rol
) {}
