package ar.edu.unlam.crmferretero.producto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlam.crmferretero.shared.OpcionResponse;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.PatchEstadoActivoRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoMapper productoMapper;

    public ProductoController(ProductoService productoService, ProductoMapper productoMapper) {
        this.productoService = productoService;
        this.productoMapper = productoMapper;
    }

    @GetMapping
    public PageResponse<ProductoResponse> listar(@RequestParam(required = false) String q,
                                                   @RequestParam(required = false) RubroProducto rubro,
                                                   @RequestParam(required = false) String distribuidoraId,
                                                   @RequestParam(required = false) Integer pagina,
                                                   @RequestParam(required = false) Integer tamanio) {
        return productoService.listar(q, rubro, distribuidoraId, pagina, tamanio);
    }

    @GetMapping("/opciones")
    public List<OpcionResponse> opciones() {
        return productoService.opciones();
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable String id) {
        return productoMapper.toResponse(productoService.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public ProductoResponse actualizar(@PathVariable String id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public ProductoResponse cambiarEstado(@PathVariable String id, @Valid @RequestBody PatchEstadoActivoRequest request) {
        return productoService.cambiarEstado(id, request.activo());
    }
}
