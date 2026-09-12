package ar.edu.unlam.crmferretero.distribuidora;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;

/** La distribuidora mayorista que compró el CRM: el tenant. No confundir con Empresa (el comercio cliente). */
@Document("distribuidoras")
public class Distribuidora extends BaseDocument {

    private String razonSocial;
    private String nombreFantasia;

    @Indexed(unique = true, sparse = true)
    private String cuit;

    private String email;
    private String telefono;
    private boolean activa = true;

    public Distribuidora() {
    }

    public String nombreVisible() {
        return (nombreFantasia != null && !nombreFantasia.isBlank()) ? nombreFantasia : razonSocial;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public void setNombreFantasia(String nombreFantasia) {
        this.nombreFantasia = nombreFantasia;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
