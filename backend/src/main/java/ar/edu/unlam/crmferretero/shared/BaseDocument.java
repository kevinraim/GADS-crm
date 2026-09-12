package ar.edu.unlam.crmferretero.shared;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Campos de auditoría comunes a (casi) todos los documentos de la colección.
 * historial_etapas es la única excepción: es un registro inmutable con su propia fecha y usuario.
 */
public abstract class BaseDocument {

    @Id
    private String id;

    @CreatedDate
    private Instant creadoEn;

    @CreatedBy
    private String creadoPor;

    @LastModifiedDate
    private Instant modificadoEn;

    @LastModifiedBy
    private String modificadoPor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Instant getModificadoEn() {
        return modificadoEn;
    }

    public void setModificadoEn(Instant modificadoEn) {
        this.modificadoEn = modificadoEn;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }
}
