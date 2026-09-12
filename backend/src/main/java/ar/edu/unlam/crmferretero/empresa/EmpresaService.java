package ar.edu.unlam.crmferretero.empresa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.shared.AlcanceUtils;
import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.EstadoRegistro;
import ar.edu.unlam.crmferretero.shared.OpcionResponse;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.TenantContext;
import ar.edu.unlam.crmferretero.shared.exception.DuplicateResourceException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;
import ar.edu.unlam.crmferretero.usuario.Usuario;
import ar.edu.unlam.crmferretero.usuario.UsuarioRepository;
import ar.edu.unlam.crmferretero.catalogo.Origen;
import ar.edu.unlam.crmferretero.catalogo.OrigenRepository;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;
    private final MongoTemplate mongoTemplate;
    private final UsuarioRepository usuarioRepository;
    private final OrigenRepository origenRepository;

    public EmpresaService(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper, MongoTemplate mongoTemplate,
                           UsuarioRepository usuarioRepository, OrigenRepository origenRepository) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
        this.mongoTemplate = mongoTemplate;
        this.usuarioRepository = usuarioRepository;
        this.origenRepository = origenRepository;
    }

    public PageResponse<EmpresaResponse> listar(String texto, EstadoRegistro estado, TipoComercio tipoComercio,
                                                 String zona, String responsableId, String distribuidoraId,
                                                 Integer pagina, Integer tamanio) {
        Pageable pageable = ConsultaUtils.paginar(pagina, tamanio);
        Criteria criteria = construirCriteria(texto, estado, tipoComercio, zona, responsableId, distribuidoraId);

        long total = mongoTemplate.count(new Query(criteria), Empresa.class);
        Query query = new Query(criteria).with(pageable);
        List<Empresa> empresas = mongoTemplate.find(query, Empresa.class);

        List<EmpresaResponse> contenido = mapearConNombres(empresas);
        return PageResponse.de(contenido, pageable.getPageNumber(), pageable.getPageSize(), total);
    }

    private Criteria construirCriteria(String texto, EstadoRegistro estado, TipoComercio tipoComercio,
                                        String zona, String responsableId, String distribuidoraId) {
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, distribuidoraId);
        AlcanceUtils.porVisibilidadFina(condiciones);

        String textoNormalizado = ConsultaUtils.normalizar(texto);
        if (textoNormalizado != null) {
            condiciones.add(new Criteria().orOperator(
                    Criteria.where("razonSocial").regex(textoNormalizado, "i"),
                    Criteria.where("nombreFantasia").regex(textoNormalizado, "i"),
                    Criteria.where("cuit").regex(textoNormalizado, "i")
            ));
        }
        if (estado != null) {
            condiciones.add(Criteria.where("estado").is(estado));
        }
        if (tipoComercio != null) {
            condiciones.add(Criteria.where("tipoComercio").is(tipoComercio));
        }
        String zonaNormalizada = ConsultaUtils.normalizar(zona);
        if (zonaNormalizada != null) {
            condiciones.add(Criteria.where("zonaReparto").regex(zonaNormalizada, "i"));
        }
        String responsableNormalizado = ConsultaUtils.normalizar(responsableId);
        if (responsableNormalizado != null) {
            condiciones.add(Criteria.where("responsableComercialId").is(responsableNormalizado));
        }

        return condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));
    }

    private List<EmpresaResponse> mapearConNombres(List<Empresa> empresas) {
        Set<String> idsResponsables = empresas.stream()
                .map(Empresa::getResponsableComercialId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> idsOrigenes = empresas.stream()
                .map(Empresa::getOrigenId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, String> nombresResponsables = usuarioRepository.findAllById(idsResponsables).stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombreCompleto));
        Map<String, String> nombresOrigenes = origenRepository.findAllById(idsOrigenes).stream()
                .collect(Collectors.toMap(Origen::getId, Origen::getNombre));

        return empresas.stream()
                .map(empresa -> empresaMapper.toResponse(empresa, nombresResponsables, nombresOrigenes))
                .toList();
    }

    public EmpresaResponse obtenerPorId(String id) {
        Empresa empresa = buscarPorId(id);
        return mapearConNombres(List.of(empresa)).get(0);
    }

    public List<OpcionResponse> opciones() {
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, null);
        AlcanceUtils.porVisibilidadFina(condiciones);
        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));

        return mongoTemplate.find(new Query(criteria), Empresa.class).stream()
                .filter(empresa -> empresa.getEstado() != EstadoRegistro.NO_CONTACTAR)
                .map(empresa -> new OpcionResponse(empresa.getId(), empresa.nombreVisible()))
                .toList();
    }

    public EmpresaResponse crear(EmpresaRequest request) {
        validarCuitUnico(request.cuit(), null);

        Empresa empresa = new Empresa();
        aplicarRequest(empresa, request);
        empresa.setEstado(EstadoRegistro.POTENCIAL);
        empresa.setDistribuidoraId(AlcanceUtils.distribuidoraIdParaAlta());

        Empresa guardada = empresaRepository.save(empresa);
        return obtenerPorId(guardada.getId());
    }

    public EmpresaResponse actualizar(String id, EmpresaRequest request) {
        Empresa empresa = buscarPorId(id);
        validarCuitUnico(request.cuit(), id);

        aplicarRequest(empresa, request);

        Empresa guardada = empresaRepository.save(empresa);
        return obtenerPorId(guardada.getId());
    }

    public EmpresaResponse cambiarEstado(String id, EstadoRegistro estado) {
        Empresa empresa = buscarPorId(id);
        empresa.setEstado(estado);
        Empresa guardada = empresaRepository.save(empresa);
        return obtenerPorId(guardada.getId());
    }

    private void aplicarRequest(Empresa empresa, EmpresaRequest request) {
        empresa.setRazonSocial(ConsultaUtils.normalizar(request.razonSocial()));
        empresa.setNombreFantasia(ConsultaUtils.normalizar(request.nombreFantasia()));
        empresa.setCuit(ConsultaUtils.normalizar(request.cuit()));
        empresa.setEmail(ConsultaUtils.normalizar(request.email()));
        empresa.setTelefono(ConsultaUtils.normalizar(request.telefono()));
        empresa.setDireccion(ConsultaUtils.normalizar(request.direccion()));
        empresa.setLocalidad(ConsultaUtils.normalizar(request.localidad()));
        empresa.setSitioWeb(ConsultaUtils.normalizar(request.sitioWeb()));
        empresa.setResponsableComercialId(ConsultaUtils.normalizar(request.responsableComercialId()));
        empresa.setOrigenId(ConsultaUtils.normalizar(request.origenId()));
        empresa.setObservaciones(ConsultaUtils.normalizar(request.observaciones()));
        empresa.setTipoComercio(request.tipoComercio());
        empresa.setCondicionIva(request.condicionIva());
        empresa.setZonaReparto(ConsultaUtils.normalizar(request.zonaReparto()));
        empresa.setCondicionPagoHabitual(request.condicionPagoHabitual());
        empresa.setListaPrecios(request.listaPrecios());
        empresa.setLimiteCreditoEstimado(request.limiteCreditoEstimado());
    }

    private void validarCuitUnico(String cuit, String idPropio) {
        String cuitNormalizado = ConsultaUtils.normalizar(cuit);
        if (cuitNormalizado == null) {
            return;
        }
        empresaRepository.findByCuitAndDistribuidoraId(cuitNormalizado, AlcanceUtils.distribuidoraIdParaAlta()).ifPresent(existente -> {
            if (idPropio == null || !existente.getId().equals(idPropio)) {
                throw new DuplicateResourceException(
                        "Ya existe un comercio con el CUIT " + cuitNormalizado + " (" + existente.nombreVisible() + ")");
            }
        });
    }

    Empresa buscarPorId(String id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comercio no encontrado (id " + id + ")"));
        if (!TenantContext.esAdmin() && !java.util.Objects.equals(empresa.getDistribuidoraId(), TenantContext.distribuidoraId())) {
            throw new NotFoundException("Comercio no encontrado (id " + id + ")");
        }
        return empresa;
    }
}
