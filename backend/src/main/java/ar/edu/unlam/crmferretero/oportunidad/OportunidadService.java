package ar.edu.unlam.crmferretero.oportunidad;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import ar.edu.unlam.crmferretero.catalogo.CatalogoService;
import ar.edu.unlam.crmferretero.catalogo.Etapa;
import ar.edu.unlam.crmferretero.catalogo.MotivoPerdida;
import ar.edu.unlam.crmferretero.catalogo.Origen;
import ar.edu.unlam.crmferretero.catalogo.TipoEtapa;
import ar.edu.unlam.crmferretero.contacto.Contacto;
import ar.edu.unlam.crmferretero.contacto.ContactoRepository;
import ar.edu.unlam.crmferretero.empresa.Empresa;
import ar.edu.unlam.crmferretero.empresa.EmpresaRepository;
import ar.edu.unlam.crmferretero.oportunidad.historial.HistorialEtapaResponse;
import ar.edu.unlam.crmferretero.oportunidad.historial.HistorialEtapaService;
import ar.edu.unlam.crmferretero.producto.Producto;
import ar.edu.unlam.crmferretero.producto.ProductoRepository;
import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.exception.BusinessRuleException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;
import ar.edu.unlam.crmferretero.usuario.Usuario;
import ar.edu.unlam.crmferretero.usuario.UsuarioRepository;

@Service
public class OportunidadService {

    private final OportunidadRepository oportunidadRepository;
    private final OportunidadMapper oportunidadMapper;
    private final MongoTemplate mongoTemplate;
    private final CatalogoService catalogoService;
    private final EmpresaRepository empresaRepository;
    private final ContactoRepository contactoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final HistorialEtapaService historialEtapaService;

    public OportunidadService(OportunidadRepository oportunidadRepository, OportunidadMapper oportunidadMapper,
                               MongoTemplate mongoTemplate, CatalogoService catalogoService,
                               EmpresaRepository empresaRepository, ContactoRepository contactoRepository,
                               UsuarioRepository usuarioRepository, ProductoRepository productoRepository,
                               HistorialEtapaService historialEtapaService) {
        this.oportunidadRepository = oportunidadRepository;
        this.oportunidadMapper = oportunidadMapper;
        this.mongoTemplate = mongoTemplate;
        this.catalogoService = catalogoService;
        this.empresaRepository = empresaRepository;
        this.contactoRepository = contactoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.historialEtapaService = historialEtapaService;
    }

    // ---------------------------------------------------------------- listado

    public PageResponse<OportunidadResponse> listar(String texto, String etapaId, String responsableId,
                                                      EstadoOportunidad estado, String empresaId,
                                                      Integer pagina, Integer tamanio) {
        Pageable pageable = ConsultaUtils.paginar(pagina, tamanio);
        List<Criteria> condiciones = new ArrayList<>();

        String textoNormalizado = ConsultaUtils.normalizar(texto);
        if (textoNormalizado != null) {
            condiciones.add(Criteria.where("titulo").regex(textoNormalizado, "i"));
        }
        String etapaNormalizada = ConsultaUtils.normalizar(etapaId);
        if (etapaNormalizada != null) {
            condiciones.add(Criteria.where("etapaActualId").is(etapaNormalizada));
        }
        String responsableNormalizado = ConsultaUtils.normalizar(responsableId);
        if (responsableNormalizado != null) {
            condiciones.add(Criteria.where("responsableComercialId").is(responsableNormalizado));
        }
        if (estado != null) {
            condiciones.add(Criteria.where("estado").is(estado));
        }
        String empresaNormalizada = ConsultaUtils.normalizar(empresaId);
        if (empresaNormalizada != null) {
            condiciones.add(Criteria.where("empresaId").is(empresaNormalizada));
        }

        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));

        long total = mongoTemplate.count(new Query(criteria), Oportunidad.class);
        Query query = new Query(criteria).with(pageable);
        List<Oportunidad> oportunidades = mongoTemplate.find(query, Oportunidad.class);

        List<OportunidadResponse> contenido = mapearConNombres(oportunidades);
        return PageResponse.de(contenido, pageable.getPageNumber(), pageable.getPageSize(), total);
    }

    public OportunidadResponse obtenerPorId(String id) {
        Oportunidad oportunidad = buscarPorId(id);
        return mapearConNombres(List.of(oportunidad)).get(0);
    }

    private List<OportunidadResponse> mapearConNombres(List<Oportunidad> oportunidades) {
        Set<String> idsEmpresas = oportunidades.stream().map(Oportunidad::getEmpresaId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsContactos = oportunidades.stream().map(Oportunidad::getContactoId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsResponsables = oportunidades.stream().map(Oportunidad::getResponsableComercialId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsEtapas = oportunidades.stream().map(Oportunidad::getEtapaActualId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsOrigenes = oportunidades.stream().map(Oportunidad::getOrigenId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsMotivos = oportunidades.stream().map(Oportunidad::getMotivoPerdidaId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());

        Map<String, String> nombresEmpresas = empresaRepository.findAllById(idsEmpresas).stream()
                .collect(Collectors.toMap(Empresa::getId, Empresa::nombreVisible));
        Map<String, String> nombresContactos = contactoRepository.findAllById(idsContactos).stream()
                .collect(Collectors.toMap(Contacto::getId, Contacto::getNombreCompleto));
        Map<String, String> nombresResponsables = usuarioRepository.findAllById(idsResponsables).stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombreCompleto));
        Map<String, String> nombresEtapas = catalogoService.listarEtapasActivas().stream()
                .collect(Collectors.toMap(Etapa::getId, Etapa::getNombre));
        // por si la etapa quedó inactiva pero la oportunidad la sigue referenciando
        for (String idEtapa : idsEtapas) {
            nombresEtapas.computeIfAbsent(idEtapa, id -> catalogoService.obtenerEtapaPorId(id).getNombre());
        }
        Map<String, String> nombresOrigenes = new java.util.HashMap<>();
        catalogoService.listarOrigenesActivos().forEach(origen -> nombresOrigenes.put(origen.getId(), origen.getNombre()));
        Map<String, String> nombresMotivos = new java.util.HashMap<>();
        catalogoService.listarMotivosActivos().forEach(motivo -> nombresMotivos.put(motivo.getId(), motivo.getNombre()));

        return oportunidades.stream()
                .map(oportunidad -> oportunidadMapper.toResponse(oportunidad, nombresEmpresas, nombresContactos,
                        nombresResponsables, nombresEtapas, nombresOrigenes, nombresMotivos))
                .toList();
    }

    // ---------------------------------------------------------------- alta y edición

    public OportunidadResponse crear(OportunidadRequest request) {
        validarEmpresaOContacto(request.empresaId(), request.contactoId());

        Oportunidad oportunidad = new Oportunidad();
        aplicarDatosGenerales(oportunidad, request);

        Etapa etapa = request.etapaActualId() != null
                ? catalogoService.obtenerEtapaPorId(request.etapaActualId())
                : catalogoService.primeraEtapaAbierta();

        oportunidad.setEtapaActualId(etapa.getId());
        oportunidad.setEstado(estadoDesdeTipo(etapa.getTipo()));
        if (oportunidad.getEstado() != EstadoOportunidad.ABIERTA) {
            oportunidad.setFechaRealCierre(LocalDate.now());
        }

        Oportunidad guardada = oportunidadRepository.save(oportunidad);

        historialEtapaService.registrar(guardada.getId(), null, null, etapa.getId(), etapa.getNombre(), null);

        return obtenerPorId(guardada.getId());
    }

    public OportunidadResponse actualizar(String id, OportunidadRequest request) {
        Oportunidad oportunidad = buscarPorId(id);
        validarEmpresaOContacto(request.empresaId(), request.contactoId());

        // La etapa y el estado no se tocan acá: cambian únicamente por PATCH /etapa, para que
        // todo cambio de etapa quede siempre registrado en el historial.
        aplicarDatosGenerales(oportunidad, request);

        Oportunidad guardada = oportunidadRepository.save(oportunidad);
        return obtenerPorId(guardada.getId());
    }

    private void aplicarDatosGenerales(Oportunidad oportunidad, OportunidadRequest request) {
        oportunidad.setTitulo(ConsultaUtils.normalizar(request.titulo()));
        oportunidad.setEmpresaId(ConsultaUtils.normalizar(request.empresaId()));
        oportunidad.setContactoId(ConsultaUtils.normalizar(request.contactoId()));
        oportunidad.setResponsableComercialId(ConsultaUtils.normalizar(request.responsableComercialId()));
        oportunidad.setOrigenId(ConsultaUtils.normalizar(request.origenId()));
        oportunidad.setFechaEstimadaCierre(request.fechaEstimadaCierre());
        oportunidad.setObservaciones(ConsultaUtils.normalizar(request.observaciones()));
        oportunidad.setCondicionPagoNegociada(request.condicionPagoNegociada());
        oportunidad.setRequiereAltaCuentaCorriente(Boolean.TRUE.equals(request.requiereAltaCuentaCorriente()));

        List<ItemOportunidad> items = construirItems(request.items());
        oportunidad.setItems(items);

        // El valor estimado se calcula a partir de los ítems e ignora lo que mande el cliente;
        // si no hay ítems, se usa el valor cargado a mano en el formulario.
        if (!items.isEmpty()) {
            BigDecimal total = items.stream().map(ItemOportunidad::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            oportunidad.setValorEstimado(total);
        } else {
            oportunidad.setValorEstimado(request.valorEstimado());
        }
    }

    private List<ItemOportunidad> construirItems(List<ItemOportunidadRequest> itemsRequest) {
        if (itemsRequest == null) {
            return new ArrayList<>();
        }
        List<ItemOportunidad> items = new ArrayList<>();
        for (ItemOportunidadRequest itemRequest : itemsRequest) {
            Producto producto = productoRepository.findById(itemRequest.productoId())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado (id " + itemRequest.productoId() + ")"));
            items.add(new ItemOportunidad(producto.getId(), producto.getNombre(), producto.getUnidadVenta(),
                    itemRequest.cantidad(), itemRequest.precioUnitario()));
        }
        return items;
    }

    private void validarEmpresaOContacto(String empresaId, String contactoId) {
        if (ConsultaUtils.normalizar(empresaId) == null && ConsultaUtils.normalizar(contactoId) == null) {
            throw new BusinessRuleException("La oportunidad tiene que estar asociada a un comercio o a un contacto");
        }
    }

    private EstadoOportunidad estadoDesdeTipo(TipoEtapa tipo) {
        return switch (tipo) {
            case ABIERTA -> EstadoOportunidad.ABIERTA;
            case GANADA -> EstadoOportunidad.GANADA;
            case PERDIDA -> EstadoOportunidad.PERDIDA;
        };
    }

    // ---------------------------------------------------------------- cambio de etapa

    public OportunidadResponse cambiarEtapa(String id, CambioEtapaRequest request) {
        Oportunidad oportunidad = buscarPorId(id);
        Etapa etapaNueva = catalogoService.obtenerEtapaPorId(request.etapaId());

        if (etapaNueva.getId().equals(oportunidad.getEtapaActualId())) {
            throw new BusinessRuleException("La oportunidad ya está en la etapa \"" + etapaNueva.getNombre() + "\"");
        }

        Etapa etapaAnterior = catalogoService.obtenerEtapaPorId(oportunidad.getEtapaActualId());
        EstadoOportunidad nuevoEstado = estadoDesdeTipo(etapaNueva.getTipo());

        oportunidad.setEtapaActualId(etapaNueva.getId());
        oportunidad.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoOportunidad.ABIERTA) {
            // Reapertura de una oportunidad cerrada: en esta entrega se permite sin restricción de rol.
            // Entrega 2: exigir rol ADMIN o RESPONSABLE_COMERCIAL para reabrir una oportunidad cerrada.
            oportunidad.setFechaRealCierre(null);
            oportunidad.setMotivoPerdidaId(null);
        } else {
            oportunidad.setFechaRealCierre(LocalDate.now());
            if (nuevoEstado == EstadoOportunidad.PERDIDA && ConsultaUtils.normalizar(request.motivoPerdidaId()) != null) {
                oportunidad.setMotivoPerdidaId(request.motivoPerdidaId());
            }
        }

        Oportunidad guardada = oportunidadRepository.save(oportunidad);

        historialEtapaService.registrar(guardada.getId(), etapaAnterior.getId(), etapaAnterior.getNombre(),
                etapaNueva.getId(), etapaNueva.getNombre(), ConsultaUtils.normalizar(request.observacion()));

        return obtenerPorId(guardada.getId());
    }

    public List<HistorialEtapaResponse> historialDeEtapas(String id) {
        buscarPorId(id);
        return historialEtapaService.listarPorOportunidad(id);
    }

    // ---------------------------------------------------------------- embudo

    public EmbudoResponse embudo(String responsableId, String zona) {
        List<Etapa> etapas = catalogoService.listarEtapasActivas();

        List<Criteria> condiciones = new ArrayList<>();
        String responsableNormalizado = ConsultaUtils.normalizar(responsableId);
        if (responsableNormalizado != null) {
            condiciones.add(Criteria.where("responsableComercialId").is(responsableNormalizado));
        }

        String zonaNormalizada = ConsultaUtils.normalizar(zona);
        Set<String> idsEmpresasDeLaZona = null;
        if (zonaNormalizada != null) {
            idsEmpresasDeLaZona = empresaRepository.findAll().stream()
                    .filter(empresa -> zonaNormalizada.equalsIgnoreCase(empresa.getZonaReparto()))
                    .map(Empresa::getId)
                    .collect(Collectors.toSet());
            condiciones.add(Criteria.where("empresaId").in(idsEmpresasDeLaZona));
        }

        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));
        List<Oportunidad> oportunidades = mongoTemplate.find(new Query(criteria), Oportunidad.class);

        Set<String> idsEmpresas = oportunidades.stream().map(Oportunidad::getEmpresaId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsContactos = oportunidades.stream().map(Oportunidad::getContactoId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsResponsables = oportunidades.stream().map(Oportunidad::getResponsableComercialId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());

        Map<String, Empresa> empresasPorId = empresaRepository.findAllById(idsEmpresas).stream()
                .collect(Collectors.toMap(Empresa::getId, e -> e));
        Map<String, String> nombresContactos = contactoRepository.findAllById(idsContactos).stream()
                .collect(Collectors.toMap(Contacto::getId, Contacto::getNombreCompleto));
        Map<String, String> nombresResponsables = usuarioRepository.findAllById(idsResponsables).stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombreCompleto));

        Map<String, List<Oportunidad>> oportunidadesPorEtapa = oportunidades.stream()
                .collect(Collectors.groupingBy(Oportunidad::getEtapaActualId));

        List<EmbudoColumnaResponse> columnas = new ArrayList<>();
        for (Etapa etapa : etapas) {
            List<Oportunidad> oportunidadesDeLaEtapa = oportunidadesPorEtapa.getOrDefault(etapa.getId(), List.of());

            List<EmbudoOportunidadResponse> tarjetas = oportunidadesDeLaEtapa.stream()
                    .map(oportunidad -> {
                        Empresa empresa = oportunidad.getEmpresaId() == null ? null : empresasPorId.get(oportunidad.getEmpresaId());
                        String clienteNombre = empresa != null ? empresa.nombreVisible()
                                : (oportunidad.getContactoId() != null ? nombresContactos.get(oportunidad.getContactoId()) : null);
                        return new EmbudoOportunidadResponse(
                                oportunidad.getId(),
                                oportunidad.getTitulo(),
                                clienteNombre,
                                nombresResponsables.get(oportunidad.getResponsableComercialId()),
                                oportunidad.getValorEstimado(),
                                oportunidad.getEstado(),
                                oportunidad.isRequiereAltaCuentaCorriente(),
                                empresa != null ? empresa.getZonaReparto() : null);
                    })
                    .toList();

            BigDecimal valorTotal = oportunidadesDeLaEtapa.stream()
                    .map(Oportunidad::getValorEstimado)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            columnas.add(new EmbudoColumnaResponse(etapa.getId(), etapa.getNombre(), etapa.getDescripcion(),
                    etapa.getTipo(), etapa.getColor(), etapa.getOrden(), oportunidadesDeLaEtapa.size(), valorTotal, tarjetas));
        }

        return new EmbudoResponse(columnas);
    }

    Oportunidad buscarPorId(String id) {
        return oportunidadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Oportunidad no encontrada (id " + id + ")"));
    }
}
