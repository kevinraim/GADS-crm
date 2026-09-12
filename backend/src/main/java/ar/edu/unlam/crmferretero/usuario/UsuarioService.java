package ar.edu.unlam.crmferretero.usuario;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.TenantContext;
import ar.edu.unlam.crmferretero.shared.exception.BusinessRuleException;
import ar.edu.unlam.crmferretero.shared.exception.DuplicateResourceException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Usada por los selectores de responsable comercial de toda la app: activos de la propia distribuidora. */
    public List<UsuarioResponse> listarActivos() {
        List<Usuario> usuarios = TenantContext.esAdmin()
                ? usuarioRepository.findByActivoTrue()
                : usuarioRepository.findByDistribuidoraIdAndActivoTrue(TenantContext.distribuidoraId());
        return usuarios.stream().map(UsuarioResponse::de).toList();
    }

    /** Pantalla de ABM de usuarios: todos los de la distribuidora (activos e inactivos). */
    public List<UsuarioResponse> listarDeLaDistribuidora() {
        List<Usuario> usuarios = TenantContext.esAdmin()
                ? usuarioRepository.findAll()
                : usuarioRepository.findByDistribuidoraId(TenantContext.distribuidoraId());
        return usuarios.stream().map(UsuarioResponse::de).toList();
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    public Usuario obtenerPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado (id " + id + ")"));
    }

    /**
     * Alta de VENDEDOR/RESPONSABLE_COMERCIAL por un ADMIN_COMERCIO en su propia distribuidora.
     * No puede crear otro ADMIN_COMERCIO ni ADMIN: esa regla se valida acá, no solo con roles
     * gruesos en el controlador, porque ADMIN_COMERCIO sí tiene permiso para llamar al endpoint.
     */
    public UsuarioResponse crearEnDistribuidoraActual(UsuarioRequest request) {
        if (request.rol() == Rol.ADMIN || request.rol() == Rol.ADMIN_COMERCIO) {
            throw new BusinessRuleException(
                    "No podés crear usuarios con rol " + request.rol().getEtiqueta() + " desde acá");
        }
        return crear(request, TenantContext.distribuidoraId());
    }

    public UsuarioResponse crear(UsuarioRequest request, String distribuidoraId) {
        validarEmailUnico(request.email());
        Usuario usuario = new Usuario(
                ConsultaUtils.normalizar(request.nombre()),
                ConsultaUtils.normalizar(request.apellido()),
                ConsultaUtils.normalizar(request.email()),
                passwordEncoder.encode(request.password()),
                request.rol(),
                true,
                distribuidoraId);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    public UsuarioResponse cambiarEstado(String id, boolean activo) {
        Usuario usuario = obtenerPorId(id);
        if (!TenantContext.esAdmin() && !java.util.Objects.equals(usuario.getDistribuidoraId(), TenantContext.distribuidoraId())) {
            throw new NotFoundException("Usuario no encontrado (id " + id + ")");
        }
        usuario.setActivo(activo);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    private void validarEmailUnico(String email) {
        String normalizado = ConsultaUtils.normalizar(email);
        usuarioRepository.findByEmail(normalizado).ifPresent(existente -> {
            throw new DuplicateResourceException("Ya existe un usuario con el email " + normalizado);
        });
    }
}
