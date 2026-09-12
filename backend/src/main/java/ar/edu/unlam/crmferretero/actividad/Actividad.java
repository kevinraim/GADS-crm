package ar.edu.unlam.crmferretero.actividad;

import java.time.Instant;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;

/**
 * Registro de historial comercial (llamada, visita, WhatsApp, etc.). Inmutable una vez cargada:
 * no tiene PUT ni baja lógica, igual que historial_etapas.
 * Al menos uno de empresaId/contactoId/oportunidadId tiene que estar presente (validado en el
 * servicio, 409 si no).
 */
@Document("actividades")
public class Actividad extends BaseDocument {

    private String tipoActividadId;
    private Instant fecha;
    private String descripcion;
    private String usuarioId;

    @Indexed
    private String empresaId;

    @Indexed
    private String contactoId;

    @Indexed
    private String oportunidadId;

    private String distribuidoraId;

    public Actividad() {
    }

    public String getTipoActividadId() {
        return tipoActividadId;
    }

    public void setTipoActividadId(String tipoActividadId) {
        this.tipoActividadId = tipoActividadId;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getContactoId() {
        return contactoId;
    }

    public void setContactoId(String contactoId) {
        this.contactoId = contactoId;
    }

    public String getOportunidadId() {
        return oportunidadId;
    }

    public void setOportunidadId(String oportunidadId) {
        this.oportunidadId = oportunidadId;
    }

    public String getDistribuidoraId() {
        return distribuidoraId;
    }

    public void setDistribuidoraId(String distribuidoraId) {
        this.distribuidoraId = distribuidoraId;
    }
}
