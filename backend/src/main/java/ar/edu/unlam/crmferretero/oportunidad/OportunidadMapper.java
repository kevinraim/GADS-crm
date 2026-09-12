package ar.edu.unlam.crmferretero.oportunidad;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class OportunidadMapper {

    public OportunidadResponse toResponse(Oportunidad oportunidad,
                                           Map<String, String> nombresEmpresas,
                                           Map<String, String> nombresContactos,
                                           Map<String, String> nombresResponsables,
                                           Map<String, String> nombresEtapas,
                                           Map<String, String> nombresOrigenes,
                                           Map<String, String> nombresMotivos) {

        var items = oportunidad.getItems().stream()
                .map(item -> new ItemOportunidadResponse(
                        item.getProductoId(),
                        item.getProductoNombre(),
                        item.getUnidadVenta(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.subtotal()))
                .toList();

        return new OportunidadResponse(
                oportunidad.getId(),
                oportunidad.getTitulo(),
                oportunidad.getEmpresaId(),
                oportunidad.getEmpresaId() == null ? null : nombresEmpresas.get(oportunidad.getEmpresaId()),
                oportunidad.getContactoId(),
                oportunidad.getContactoId() == null ? null : nombresContactos.get(oportunidad.getContactoId()),
                oportunidad.getResponsableComercialId(),
                nombresResponsables.get(oportunidad.getResponsableComercialId()),
                items,
                oportunidad.getValorEstimado(),
                oportunidad.getEtapaActualId(),
                nombresEtapas.get(oportunidad.getEtapaActualId()),
                oportunidad.getEstado(),
                oportunidad.getOrigenId(),
                oportunidad.getOrigenId() == null ? null : nombresOrigenes.get(oportunidad.getOrigenId()),
                oportunidad.getFechaEstimadaCierre(),
                oportunidad.getFechaRealCierre(),
                oportunidad.getMotivoPerdidaId(),
                oportunidad.getMotivoPerdidaId() == null ? null : nombresMotivos.get(oportunidad.getMotivoPerdidaId()),
                oportunidad.getObservaciones(),
                oportunidad.getCondicionPagoNegociada(),
                oportunidad.isRequiereAltaCuentaCorriente(),
                oportunidad.getCreadoEn(),
                oportunidad.getModificadoEn()
        );
    }
}
