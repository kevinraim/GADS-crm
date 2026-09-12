package ar.edu.unlam.crmferretero.usuario;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;

@Document("usuarios")
public class Usuario extends BaseDocument {

    private String nombre;
    private String apellido;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private Rol rol;
    private boolean activo = true;

    /** null para ADMIN (superadmin, no pertenece a ninguna distribuidora). */
    private String distribuidoraId;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String passwordHash, Rol rol, boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    public Usuario(String nombre, String apellido, String email, String passwordHash, Rol rol, boolean activo,
                    String distribuidoraId) {
        this(nombre, apellido, email, passwordHash, rol, activo);
        this.distribuidoraId = distribuidoraId;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getDistribuidoraId() {
        return distribuidoraId;
    }

    public void setDistribuidoraId(String distribuidoraId) {
        this.distribuidoraId = distribuidoraId;
    }
}
