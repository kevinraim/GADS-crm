package ar.edu.unlam.crmferretero.contacto;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ContactoMapper {

    public ContactoResponse toResponse(Contacto contacto, Map<String, String> nombresEmpresas,
                                        Map<String, String> nombresResponsables, Map<String, String> nombresOrigenes) {
        String nombreEmpresa = contacto.getEmpresaId() == null ? null : nombresEmpresas.get(contacto.getEmpresaId());
        String nombreResponsable = contacto.getResponsableComercialId() == null
                ? null : nombresResponsables.get(contacto.getResponsableComercialId());
        String nombreOrigen = contacto.getOrigenId() == null ? null : nombresOrigenes.get(contacto.getOrigenId());

        return new ContactoResponse(
                contacto.getId(),
                contacto.getNombre(),
                contacto.getApellido(),
                contacto.getNombreCompleto(),
                contacto.getDocumento(),
                contacto.getCargo(),
                contacto.getEmail(),
                contacto.getTelefono(),
                contacto.getWhatsapp(),
                contacto.getEmpresaId(),
                nombreEmpresa,
                contacto.getResponsableComercialId(),
                nombreResponsable,
                contacto.getEstado(),
                contacto.getOrigenId(),
                nombreOrigen,
                contacto.getObservaciones(),
                contacto.getCreadoEn(),
                contacto.getModificadoEn()
        );
    }
}
