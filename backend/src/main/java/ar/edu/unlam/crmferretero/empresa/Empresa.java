package ar.edu.unlam.crmferretero.empresa;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import ar.edu.unlam.crmferretero.shared.BaseDocument;
import ar.edu.unlam.crmferretero.shared.CondicionPago;
import ar.edu.unlam.crmferretero.shared.EstadoRegistro;

/**
 * Comercio cliente de la distribuidora: ferretería minorista, corralón, taller, constructora, etc.
 * El CUIT es único por distribuidora, no global: dos distribuidoras distintas pueden tener cargado
 * el mismo comercio cliente sin conflicto.
 */
@Document("empresas")
@CompoundIndexes(@CompoundIndex(name = "cuit_distribuidora", def = "{'cuit': 1, 'distribuidoraId': 1}", unique = true, sparse = true))
public class Empresa extends BaseDocument {

    private String razonSocial;
    private String nombreFantasia;
    private String cuit;

    private String email;
    private String telefono;
    private String direccion;
    private String localidad;
    private String sitioWeb;
    private EstadoRegistro estado = EstadoRegistro.POTENCIAL;
    private String responsableComercialId;
    private String origenId;
    private String observaciones;

    private TipoComercio tipoComercio;
    private CondicionIva condicionIva;
    private String zonaReparto;
    private CondicionPago condicionPagoHabitual;
    private ListaPrecios listaPrecios;
    private BigDecimal limiteCreditoEstimado;
    private String distribuidoraId;

    public Empresa() {
    }

    /** Nombre de fantasía si existe; si no, la razón social. */
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public String getResponsableComercialId() {
        return responsableComercialId;
    }

    public void setResponsableComercialId(String responsableComercialId) {
        this.responsableComercialId = responsableComercialId;
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

    public TipoComercio getTipoComercio() {
        return tipoComercio;
    }

    public void setTipoComercio(TipoComercio tipoComercio) {
        this.tipoComercio = tipoComercio;
    }

    public CondicionIva getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(CondicionIva condicionIva) {
        this.condicionIva = condicionIva;
    }

    public String getZonaReparto() {
        return zonaReparto;
    }

    public void setZonaReparto(String zonaReparto) {
        this.zonaReparto = zonaReparto;
    }

    public CondicionPago getCondicionPagoHabitual() {
        return condicionPagoHabitual;
    }

    public void setCondicionPagoHabitual(CondicionPago condicionPagoHabitual) {
        this.condicionPagoHabitual = condicionPagoHabitual;
    }

    public ListaPrecios getListaPrecios() {
        return listaPrecios;
    }

    public void setListaPrecios(ListaPrecios listaPrecios) {
        this.listaPrecios = listaPrecios;
    }

    public BigDecimal getLimiteCreditoEstimado() {
        return limiteCreditoEstimado;
    }

    public void setLimiteCreditoEstimado(BigDecimal limiteCreditoEstimado) {
        this.limiteCreditoEstimado = limiteCreditoEstimado;
    }

    public String getDistribuidoraId() {
        return distribuidoraId;
    }

    public void setDistribuidoraId(String distribuidoraId) {
        this.distribuidoraId = distribuidoraId;
    }
}
