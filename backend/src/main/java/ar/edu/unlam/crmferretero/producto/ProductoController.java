package ar.edu.unlam.crmferretero.producto;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlam.crmferretero.shared.OpcionResponse;
import ar.edu.unlam.crmferretero.shared.PageResponse;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public PageResponse<ProductoResponse> listar(@RequestParam(required = false) String q,
                                                   @RequestParam(required = false) RubroProducto rubro,
                                                   @RequestParam(required = false) Integer pagina,
                                                   @RequestParam(required = false) Integer tamanio) {
        return productoService.listar(q, rubro, pagina, tamanio);
    }

    @GetMapping("/opciones")
    public List<OpcionResponse> opciones() {
        return productoService.opciones();
    }
}
