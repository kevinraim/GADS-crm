package ar.edu.unlam.crmferretero.empresa;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import ar.edu.unlam.crmferretero.shared.EstadoRegistro;
import ar.edu.unlam.crmferretero.shared.OpcionResponse;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.PatchEstadoRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public PageResponse<EmpresaResponse> listar(@RequestParam(required = false) String q,
                                                 @RequestParam(required = false) EstadoRegistro estado,
                                                 @RequestParam(required = false) TipoComercio tipoComercio,
                                                 @RequestParam(required = false) String zona,
                                                 @RequestParam(required = false) String responsableId,
                                                 @RequestParam(required = false) Integer pagina,
                                                 @RequestParam(required = false) Integer tamanio) {
        return empresaService.listar(q, estado, tipoComercio, zona, responsableId, pagina, tamanio);
    }

    @GetMapping("/opciones")
    public List<OpcionResponse> opciones() {
        return empresaService.opciones();
    }

    @GetMapping("/{id}")
    public EmpresaResponse obtener(@PathVariable String id) {
        return empresaService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse crear(@Valid @RequestBody EmpresaRequest request) {
        return empresaService.crear(request);
    }

    @PutMapping("/{id}")
    public EmpresaResponse actualizar(@PathVariable String id, @Valid @RequestBody EmpresaRequest request) {
        return empresaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public EmpresaResponse cambiarEstado(@PathVariable String id, @Valid @RequestBody PatchEstadoRequest request) {
        return empresaService.cambiarEstado(id, request.estado());
    }
}
