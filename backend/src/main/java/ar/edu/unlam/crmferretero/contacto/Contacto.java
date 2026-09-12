package ar.edu.unlam.crmferretero.contacto;

import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;
import ar.edu.unlam.crmferretero.shared.EstadoRegistro;

/** Persona de contacto de un comercio, o cliente individual si empresaId es null. */
@Document("contactos")
public class Contacto extends BaseDocument {

    private String nombre;
    private String apellido;
    private String documento;
    private String cargo;
    private String email;
    private String telefono;
    private String whatsapp;
    private String empresaId;
    private String responsableComercialId;
    private EstadoRegistro estado = EstadoRegistro.POTENCIAL;
    private String origenId;
    private String observaciones;

    public Contacto() {
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
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

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getResponsableComercialId() {
        return responsableComercialId;
    }

    public void setResponsableComercialId(String responsableComercialId) {
        this.responsableComercialId = responsableComercialId;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public String getOrigenId() {
        return origenId;
    }

    public void setOrigenId(String origenId) {
        this.origenId = origenId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
