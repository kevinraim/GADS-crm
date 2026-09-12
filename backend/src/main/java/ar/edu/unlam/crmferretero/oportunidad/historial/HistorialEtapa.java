package ar.edu.unlam.crmferretero.oportunidad.historial;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Registro inmutable de cada cambio de etapa (incluido el alta inicial de la oportunidad, con
 * etapaAnteriorId null). No extiende BaseDocument porque ya lleva su propia fecha y usuario.
 */
@Document("historial_etapas")
public class HistorialEtapa {

    @Id
    private String id;

    @Indexed
    private String oportunidadId;

    private String etapaAnteriorId;
    private String etapaAnteriorNombre;
    private String etapaNuevaId;
    private String etapaNuevaNombre;
    private Instant fechaHora;
    private String usuarioEmail;
    private String observacion;

    public HistorialEtapa() {
    }

    public HistorialEtapa(String oportunidadId, String etapaAnteriorId, String etapaAnteriorNombre,
                           String etapaNuevaId, String etapaNuevaNombre, Instant fechaHora,
                           String usuarioEmail, String observacion) {
        this.oportunidadId = oportunidadId;
        this.etapaAnteriorId = etapaAnteriorId;
        this.etapaAnteriorNombre = etapaAnteriorNombre;
        this.etapaNuevaId = etapaNuevaId;
        this.etapaNuevaNombre = etapaNuevaNombre;
        this.fechaHora = fechaHora;
        this.usuarioEmail = usuarioEmail;
        this.observacion = observacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOportunidadId() {
        return oportunidadId;
    }

    public void setOportunidadId(String oportunidadId) {
        this.oportunidadId = oportunidadId;
    }

    public String getEtapaAnteriorId() {
        return etapaAnteriorId;
    }

    public void setEtapaAnteriorId(String etapaAnteriorId) {
        this.etapaAnteriorId = etapaAnteriorId;
    }

    public String getEtapaAnteriorNombre() {
        return etapaAnteriorNombre;
    }

    public void setEtapaAnteriorNombre(String etapaAnteriorNombre) {
        this.etapaAnteriorNombre = etapaAnteriorNombre;
    }

    public String getEtapaNuevaId() {
        return etapaNuevaId;
    }

    public void setEtapaNuevaId(String etapaNuevaId) {
        this.etapaNuevaId = etapaNuevaId;
    }

    public String getEtapaNuevaNombre() {
        return etapaNuevaNombre;
    }

    public void setEtapaNuevaNombre(String etapaNuevaNombre) {
        this.etapaNuevaNombre = etapaNuevaNombre;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
