package ar.edu.unlam.crmferretero.usuario;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioResponse> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(UsuarioResponse::de)
                .toList();
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    public Usuario obtenerPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado (id " + id + ")"));
    }
}
