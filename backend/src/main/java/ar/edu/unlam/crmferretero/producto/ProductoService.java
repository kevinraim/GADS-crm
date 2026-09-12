package ar.edu.unlam.crmferretero.producto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.OpcionResponse;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;

/** Catálogo de productos: solo lectura en esta entrega, se carga por el seed. */
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

    public PageResponse<ProductoResponse> listar(String texto, RubroProducto rubro, Integer pagina, Integer tamanio) {
        Pageable pageable = ConsultaUtils.paginar(pagina, tamanio);
        Criteria criteria = new Criteria();
        List<Criteria> condiciones = new java.util.ArrayList<>();

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
        if (!condiciones.isEmpty()) {
            criteria.andOperator(condiciones.toArray(new Criteria[0]));
        }

        long total = mongoTemplate.count(new Query(criteria), Producto.class);
        Query query = new Query(criteria).with(pageable);
        List<Producto> productos = mongoTemplate.find(query, Producto.class);

        List<ProductoResponse> contenido = productos.stream().map(productoMapper::toResponse).toList();
        return PageResponse.de(contenido, pageable.getPageNumber(), pageable.getPageSize(), total);
    }

    public List<OpcionResponse> opciones() {
        return productoRepository.findAll().stream()
                .filter(Producto::isActivo)
                .map(producto -> new OpcionResponse(producto.getId(), producto.nombreVisible()))
                .toList();
    }

    public Producto obtenerPorId(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado (id " + id + ")"));
    }
}
