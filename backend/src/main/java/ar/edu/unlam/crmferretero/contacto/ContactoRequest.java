package ar.edu.unlam.crmferretero.contacto;

import jakarta.validation.constraints.NotBlank;

public record ContactoRequest(
        @NotBlank(message = "Ingresá el nombre") String nombre,
        @NotBlank(message = "Ingresá el apellido") String apellido,
        String documento,
        String cargo,
        String email,
        String telefono,
        String whatsapp,
        String empresaId,
        String responsableComercialId,
        String origenId,
        String observaciones
) {}
