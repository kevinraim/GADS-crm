package ar.edu.unlam.crmferretero.auth;

import ar.edu.unlam.crmferretero.usuario.UsuarioResponse;

public record LoginResponse(
        String token,
        long expiraEnSegundos,
        UsuarioResponse usuario
) {}
