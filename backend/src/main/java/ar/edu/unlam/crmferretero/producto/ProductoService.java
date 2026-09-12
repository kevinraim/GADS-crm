package ar.edu.unlam.crmferretero.producto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.shared.AlcanceUtils;
import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.OpcionResponse;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.TenantContext;
import ar.edu.unlam.crmferretero.shared.exception.DuplicateResourceException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;

/** Catálogo de productos, propio de cada distribuidora, con ABM completo para ADMIN_COMERCIO. */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final MongoTemplate mongoTemplate;

    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper, MongoTemplate mongoTemplate) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
        this.mongoTemplate = mongoTemplate;
    }

    public PageResponse<ProductoResponse> listar(String texto, RubroProducto rubro, String distribuidoraId,
                                                  Integer pagina, Integer tamanio) {
        Pageable pageable = ConsultaUtils.paginar(pagina, tamanio);
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, distribuidoraId);

        String textoNormalizado = ConsultaUtils.normalizar(texto);
        if (textoNormalizado != null) {
            condiciones.add(new Criteria().orOperator(
                    Criteria.where("nombre").regex(textoNormalizado, "i"),
                    Criteria.where("marca").regex(textoNormalizado, "i"),
                    Criteria.where("codigo").regex(textoNormalizado, "i")
            ));
        }
        if (rubro != null) {
            condiciones.add(Criteria.where("rubro").is(rubro));
        }
        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));

        long total = mongoTemplate.count(new Query(criteria), Producto.class);
        Query query = new Query(criteria).with(pageable);
        List<Producto> productos = mongoTemplate.find(query, Producto.class);

        List<ProductoResponse> contenido = productos.stream().map(productoMapper::toResponse).toList();
        return PageResponse.de(contenido, pageable.getPageNumber(), pageable.getPageSize(), total);
    }

    public List<OpcionResponse> opciones() {
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, null);
        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));

        return mongoTemplate.find(new Query(criteria), Producto.class).stream()
                .filter(Producto::isActivo)
                .map(producto -> new OpcionResponse(producto.getId(), producto.nombreVisible()))
                .toList();
    }

    public Producto obtenerPorId(String id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado (id " + id + ")"));
        if (!TenantContext.esAdmin() && !java.util.Objects.equals(producto.getDistribuidoraId(), TenantContext.distribuidoraId())) {
            throw new NotFoundException("Producto no encontrado (id " + id + ")");
        }
        return producto;
    }

    public ProductoResponse crear(ProductoRequest request) {
        validarCodigoUnico(request.codigo(), null);
        Producto producto = new Producto();
        aplicarRequest(producto, request);
        producto.setDistribuidoraId(AlcanceUtils.distribuidoraIdParaAlta());
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(String id, ProductoRequest request) {
        Producto producto = obtenerPorId(id);
        validarCodigoUnico(request.codigo(), id);
        aplicarRequest(producto, request);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    public ProductoResponse cambiarEstado(String id, boolean activo) {
        Producto producto = obtenerPorId(id);
        producto.setActivo(activo);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    private void aplicarRequest(Producto producto, ProductoRequest request) {
        producto.setCodigo(ConsultaUtils.normalizar(request.codigo()));
        producto.setNombre(ConsultaUtils.normalizar(request.nombre()));
        producto.setMarca(ConsultaUtils.normalizar(request.marca()));
        producto.setRubro(request.rubro());
        producto.setUnidadVenta(request.unidadVenta());
        producto.setPresentacion(ConsultaUtils.normalizar(request.presentacion()));
        producto.setPrecioListaReferencia(request.precioListaReferencia());
        producto.setPreciosPorLista(request.preciosPorLista());
        producto.setEscalonesDescuento(request.escalonesDescuento() == null ? null
                : request.escalonesDescuento().stream()
                        .map(e -> new EscalonDescuento(e.cantidadMinima(), e.descuentoPorcentaje()))
                        .toList());
    }

    private void validarCodigoUnico(String codigo, String idPropio) {
        String codigoNormalizado = ConsultaUtils.normalizar(codigo);
        productoRepository.findByCodigoAndDistribuidoraId(codigoNormalizado, AlcanceUtils.distribuidoraIdParaAlta())
                .ifPresent(existente -> {
                    if (idPropio == null || !existente.getId().equals(idPropio)) {
                        throw new DuplicateResourceException("Ya existe un producto con el código " + codigoNormalizado);
                    }
                });
    }
}
