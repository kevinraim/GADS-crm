package ar.edu.unlam.crmferretero.shared;

/** Condición de pago habitual de un comercio, o negociada puntualmente en una oportunidad. */
public enum CondicionPago {

    CONTADO("Contado"),
    CTA_CTE_15("Cuenta corriente 15 días"),
    CTA_CTE_30("Cuenta corriente 30 días"),
    CTA_CTE_60("Cuenta corriente 60 días");

    private final String etiqueta;

    CondicionPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
