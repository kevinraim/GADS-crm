package ar.edu.unlam.crmferretero.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.security.JwtService;
import ar.edu.unlam.crmferretero.usuario.Usuario;
import ar.edu.unlam.crmferretero.usuario.UsuarioResponse;
import ar.edu.unlam.crmferretero.usuario.UsuarioService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UsuarioService usuarioService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        // Si las credenciales son inválidas, lanza AuthenticationException, que el
        // GlobalExceptionHandler traduce a 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioService.obtenerPorEmail(request.email());
        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name(),
                usuario.getNombreCompleto(), usuario.getId());

        return new LoginResponse(token, jwtService.getVencimientoSegundos(), UsuarioResponse.de(usuario));
    }

    public UsuarioResponse usuarioActual(String email) {
        return UsuarioResponse.de(usuarioService.obtenerPorEmail(email));
    }
}
