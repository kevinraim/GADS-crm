package ar.edu.unlam.crmferretero.contacto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.catalogo.Origen;
import ar.edu.unlam.crmferretero.catalogo.OrigenRepository;
import ar.edu.unlam.crmferretero.empresa.Empresa;
import ar.edu.unlam.crmferretero.empresa.EmpresaRepository;
import ar.edu.unlam.crmferretero.shared.AlcanceUtils;
import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.EstadoRegistro;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.TenantContext;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;
import ar.edu.unlam.crmferretero.usuario.Usuario;
import ar.edu.unlam.crmferretero.usuario.UsuarioRepository;

@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;
    private final ContactoMapper contactoMapper;
    private final MongoTemplate mongoTemplate;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrigenRepository origenRepository;

    public ContactoService(ContactoRepository contactoRepository, ContactoMapper contactoMapper, MongoTemplate mongoTemplate,
                            EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository,
                            OrigenRepository origenRepository) {
        this.contactoRepository = contactoRepository;
        this.contactoMapper = contactoMapper;
        this.mongoTemplate = mongoTemplate;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.origenRepository = origenRepository;
    }

    public PageResponse<ContactoResponse> listar(String texto, String empresaId, EstadoRegistro estado,
                                                  String responsableId, String distribuidoraId,
                                                  Integer pagina, Integer tamanio) {
        Pageable pageable = ConsultaUtils.paginar(pagina, tamanio);
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, distribuidoraId);
        AlcanceUtils.porVisibilidadFina(condiciones);

        String textoNormalizado = ConsultaUtils.normalizar(texto);
        if (textoNormalizado != null) {
            condiciones.add(new Criteria().orOperator(
                    Criteria.where("nombre").regex(textoNormalizado, "i"),
                    Criteria.where("apellido").regex(textoNormalizado, "i"),
                    Criteria.where("documento").regex(textoNormalizado, "i")
            ));
        }
        String empresaNormalizada = ConsultaUtils.normalizar(empresaId);
        if (empresaNormalizada != null) {
            condiciones.add(Criteria.where("empresaId").is(empresaNormalizada));
        }
        if (estado != null) {
            condiciones.add(Criteria.where("estado").is(estado));
        }
        String responsableNormalizado = ConsultaUtils.normalizar(responsableId);
        if (responsableNormalizado != null) {
            condiciones.add(Criteria.where("responsableComercialId").is(responsableNormalizado));
        }

        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));

        long total = mongoTemplate.count(new Query(criteria), Contacto.class);
        Query query = new Query(criteria).with(pageable);
        List<Contacto> contactos = mongoTemplate.find(query, Contacto.class);

        List<ContactoResponse> contenido = mapearConNombres(contactos);
        return PageResponse.de(contenido, pageable.getPageNumber(), pageable.getPageSize(), total);
    }

    private List<ContactoResponse> mapearConNombres(List<Contacto> contactos) {
        Set<String> idsEmpresas = contactos.stream().map(Contacto::getEmpresaId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsResponsables = contactos.stream().map(Contacto::getResponsableComercialId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsOrigenes = contactos.stream().map(Contacto::getOrigenId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());

        Map<String, String> nombresEmpresas = empresaRepository.findAllById(idsEmpresas).stream()
                .collect(Collectors.toMap(Empresa::getId, Empresa::nombreVisible));
        Map<String, String> nombresResponsables = usuarioRepository.findAllById(idsResponsables).stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombreCompleto));
        Map<String, String> nombresOrigenes = origenRepository.findAllById(idsOrigenes).stream()
                .collect(Collectors.toMap(Origen::getId, Origen::getNombre));

        return contactos.stream()
                .map(contacto -> contactoMapper.toResponse(contacto, nombresEmpresas, nombresResponsables, nombresOrigenes))
                .toList();
    }

    public ContactoResponse obtenerPorId(String id) {
        Contacto contacto = buscarPorId(id);
        return mapearConNombres(List.of(contacto)).get(0);
    }

    public List<ContactoResponse> listarPorEmpresa(String empresaId) {
        return mapearConNombres(contactoRepository.findByEmpresaId(empresaId));
    }

    public ContactoResponse crear(ContactoRequest request) {
        Contacto contacto = new Contacto();
        aplicarRequest(contacto, request);
        contacto.setEstado(EstadoRegistro.POTENCIAL);
        contacto.setDistribuidoraId(AlcanceUtils.distribuidoraIdParaAlta());
        Contacto guardado = contactoRepository.save(contacto);
        return obtenerPorId(guardado.getId());
    }

    public ContactoResponse actualizar(String id, ContactoRequest request) {
        Contacto contacto = buscarPorId(id);
        aplicarRequest(contacto, request);
        Contacto guardado = contactoRepository.save(contacto);
        return obtenerPorId(guardado.getId());
    }

    public ContactoResponse cambiarEstado(String id, EstadoRegistro estado) {
        Contacto contacto = buscarPorId(id);
        contacto.setEstado(estado);
        Contacto guardado = contactoRepository.save(contacto);
        return obtenerPorId(guardado.getId());
    }

    private void aplicarRequest(Contacto contacto, ContactoRequest request) {
        contacto.setNombre(ConsultaUtils.normalizar(request.nombre()));
        contacto.setApellido(ConsultaUtils.normalizar(request.apellido()));
        contacto.setDocumento(ConsultaUtils.normalizar(request.documento()));
        contacto.setCargo(ConsultaUtils.normalizar(request.cargo()));
        contacto.setEmail(ConsultaUtils.normalizar(request.email()));
        contacto.setTelefono(ConsultaUtils.normalizar(request.telefono()));
        contacto.setWhatsapp(ConsultaUtils.normalizar(request.whatsapp()));
        contacto.setEmpresaId(ConsultaUtils.normalizar(request.empresaId()));
        contacto.setResponsableComercialId(ConsultaUtils.normalizar(request.responsableComercialId()));
        contacto.setOrigenId(ConsultaUtils.normalizar(request.origenId()));
        contacto.setObservaciones(ConsultaUtils.normalizar(request.observaciones()));
    }

    Contacto buscarPorId(String id) {
        Contacto contacto = contactoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contacto no encontrado (id " + id + ")"));
        if (!TenantContext.esAdmin() && !java.util.Objects.equals(contacto.getDistribuidoraId(), TenantContext.distribuidoraId())) {
            throw new NotFoundException("Contacto no encontrado (id " + id + ")");
        }
        return contacto;
    }
}
