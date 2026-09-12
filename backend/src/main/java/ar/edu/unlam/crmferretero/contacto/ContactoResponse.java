package ar.edu.unlam.crmferretero.contacto;

import java.time.Instant;

import ar.edu.unlam.crmferretero.shared.EstadoRegistro;

public record ContactoResponse(
        String id,
        String nombre,
        String apellido,
        String nombreCompleto,
        String documento,
        String cargo,
        String email,
        String telefono,
        String whatsapp,
        String empresaId,
        String empresaNombre,
        String responsableComercialId,
        String responsableComercialNombre,
        EstadoRegistro estado,
        String origenId,
        String origenNombre,
        String observaciones,
        Instant creadoEn,
        Instant modificadoEn
) {}
