package ar.edu.unlam.crmferretero.empresa;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    /**
     * Arma la respuesta resolviendo los nombres relacionados a partir de mapas ya construidos con
     * una consulta masiva (findAllById), nunca con una consulta por fila.
     */
    public EmpresaResponse toResponse(Empresa empresa, Map<String, String> nombresResponsables, Map<String, String> nombresOrigenes) {
        String nombreResponsable = empresa.getResponsableComercialId() == null
                ? null : nombresResponsables.get(empresa.getResponsableComercialId());
        String nombreOrigen = empresa.getOrigenId() == null
                ? null : nombresOrigenes.get(empresa.getOrigenId());

        return new EmpresaResponse(
                empresa.getId(),
                empresa.getRazonSocial(),
                empresa.getNombreFantasia(),
                empresa.nombreVisible(),
                empresa.getCuit(),
                empresa.getEmail(),
                empresa.getTelefono(),
                empresa.getDireccion(),
                empresa.getLocalidad(),
                empresa.getSitioWeb(),
                empresa.getEstado(),
                empresa.getTipoComercio(),
                empresa.getCondicionIva(),
                empresa.getZonaReparto(),
                empresa.getCondicionPagoHabitual(),
                empresa.getListaPrecios(),
                empresa.getLimiteCreditoEstimado(),
                empresa.getResponsableComercialId(),
                nombreResponsable,
                empresa.getOrigenId(),
                nombreOrigen,
                empresa.getObservaciones(),
                empresa.getCreadoEn(),
                empresa.getModificadoEn()
        );
    }
}
