package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;
import ar.edu.unlam.crmferretero.shared.CondicionPago;

/** No es "un producto": es una lista de materiales, con varios ítems de cantidad y precio. */
@Document("oportunidades")
public class Oportunidad extends BaseDocument {

    private String titulo;
    private String empresaId;
    private String contactoId;
    private String responsableComercialId;
    private List<ItemOportunidad> items = new ArrayList<>();
    private BigDecimal valorEstimado;
    private String etapaActualId;
    private EstadoOportunidad estado = EstadoOportunidad.ABIERTA;
    private String origenId;
    private LocalDate fechaEstimadaCierre;
    private LocalDate fechaRealCierre;
    private String motivoPerdidaId;
    private String observaciones;
    private CondicionPago condicionPagoNegociada;
    private boolean requiereAltaCuentaCorriente;

    public Oportunidad() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getResponsableComercialId() {
        return responsableComercialId;
    }

    public void setResponsableComercialId(String responsableComercialId) {
        this.responsableComercialId = responsableComercialId;
    }

    public List<ItemOportunidad> getItems() {
        return items;
    }

    public void setItems(List<ItemOportunidad> items) {
        this.items = items;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getEtapaActualId() {
        return etapaActualId;
    }

    public void setEtapaActualId(String etapaActualId) {
        this.etapaActualId = etapaActualId;
    }

    public EstadoOportunidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoOportunidad estado) {
        this.estado = estado;
    }

    public String getOrigenId() {
        return origenId;
    }

    public void setOrigenId(String origenId) {
        this.origenId = origenId;
    }

    public LocalDate getFechaEstimadaCierre() {
        return fechaEstimadaCierre;
    }

    public void setFechaEstimadaCierre(LocalDate fechaEstimadaCierre) {
        this.fechaEstimadaCierre = fechaEstimadaCierre;
    }

    public LocalDate getFechaRealCierre() {
        return fechaRealCierre;
    }

    public void setFechaRealCierre(LocalDate fechaRealCierre) {
        this.fechaRealCierre = fechaRealCierre;
    }

    public String getMotivoPerdidaId() {
        return motivoPerdidaId;
    }

    public void setMotivoPerdidaId(String motivoPerdidaId) {
        this.motivoPerdidaId = motivoPerdidaId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public CondicionPago getCondicionPagoNegociada() {
        return condicionPagoNegociada;
    }

    public void setCondicionPagoNegociada(CondicionPago condicionPagoNegociada) {
        this.condicionPagoNegociada = condicionPagoNegociada;
    }

    public boolean isRequiereAltaCuentaCorriente() {
        return requiereAltaCuentaCorriente;
    }

    public void setRequiereAltaCuentaCorriente(boolean requiereAltaCuentaCorriente) {
        this.requiereAltaCuentaCorriente = requiereAltaCuentaCorriente;
    }
}
