package ar.edu.unlam.crmferretero.usuario;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlam.crmferretero.shared.PatchEstadoActivoRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Usado por los selectores de "responsable comercial" en comercios/contactos/oportunidades. */
    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listarActivos();
    }

    /** Pantalla de ABM de usuarios: solo ADMIN (todas las distribuidoras) y ADMIN_COMERCIO (la propia). */
    @GetMapping("/administracion")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_COMERCIO')")
    public List<UsuarioResponse> listarDeLaDistribuidora() {
        return usuarioService.listarDeLaDistribuidora();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest request) {
        return usuarioService.crearEnDistribuidoraActual(request);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_COMERCIO')")
    public UsuarioResponse cambiarEstado(@PathVariable String id, @Valid @RequestBody PatchEstadoActivoRequest request) {
        return usuarioService.cambiarEstado(id, request.activo());
    }
}
