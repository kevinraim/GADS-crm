package ar.edu.unlam.crmferretero.usuario;

public record UsuarioResponse(
        String id,
        String nombre,
        String apellido,
        String nombreCompleto,
        String email,
        Rol rol,
        boolean activo
) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}
