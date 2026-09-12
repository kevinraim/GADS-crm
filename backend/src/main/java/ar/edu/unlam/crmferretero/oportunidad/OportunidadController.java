package ar.edu.unlam.crmferretero.oportunidad;

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

import ar.edu.unlam.crmferretero.oportunidad.historial.HistorialEtapaResponse;
import ar.edu.unlam.crmferretero.shared.PageResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadController {

    private final OportunidadService oportunidadService;

    public OportunidadController(OportunidadService oportunidadService) {
        this.oportunidadService = oportunidadService;
    }

    @GetMapping
    public PageResponse<OportunidadResponse> listar(@RequestParam(required = false) String q,
                                                      @RequestParam(required = false) String etapaId,
                                                      @RequestParam(required = false) String responsableId,
                                                      @RequestParam(required = false) EstadoOportunidad estado,
                                                      @RequestParam(required = false) String empresaId,
                                                      @RequestParam(required = false) Integer pagina,
                                                      @RequestParam(required = false) Integer tamanio) {
        return oportunidadService.listar(q, etapaId, responsableId, estado, empresaId, pagina, tamanio);
    }

    @GetMapping("/{id}")
    public OportunidadResponse obtener(@PathVariable String id) {
        return oportunidadService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadResponse crear(@Valid @RequestBody OportunidadRequest request) {
        return oportunidadService.crear(request);
    }

    @PutMapping("/{id}")
    public OportunidadResponse actualizar(@PathVariable String id, @Valid @RequestBody OportunidadRequest request) {
        return oportunidadService.actualizar(id, request);
    }

    @PatchMapping("/{id}/etapa")
    public OportunidadResponse cambiarEtapa(@PathVariable String id, @Valid @RequestBody CambioEtapaRequest request) {
        return oportunidadService.cambiarEtapa(id, request);
    }

    @GetMapping("/{id}/historial-etapas")
    public List<HistorialEtapaResponse> historialEtapas(@PathVariable String id) {
        return oportunidadService.historialDeEtapas(id);
    }
}
