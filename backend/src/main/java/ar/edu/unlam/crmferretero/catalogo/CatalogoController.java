package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlam.crmferretero.shared.OpcionResponse;
import ar.edu.unlam.crmferretero.shared.PatchEstadoActivoRequest;
import jakarta.validation.Valid;

/**
 * Etapas, orígenes, motivos de pérdida y tipos de actividad: lectura para cualquier autenticado
 * (scoped a su distribuidora), ABM restringido a ADMIN_COMERCIO.
 */
@RestController
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    // ---------------------------------------------------------------- etapas

    @GetMapping("/api/etapas")
    public List<EtapaResponse> etapas() {
        return catalogoService.listarEtapasActivas().stream().map(EtapaResponse::de).toList();
    }

    @GetMapping("/api/etapas/administracion")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public List<EtapaResponse> etapasDeLaDistribuidora() {
        return catalogoService.listarEtapasDeLaDistribuidora().stream().map(EtapaResponse::de).toList();
    }

    @PostMapping("/api/etapas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public EtapaResponse crearEtapa(@Valid @RequestBody EtapaRequest request) {
        return EtapaResponse.de(catalogoService.crearEtapa(request));
    }

    @PutMapping("/api/etapas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public EtapaResponse actualizarEtapa(@PathVariable String id, @Valid @RequestBody EtapaRequest request) {
        return EtapaResponse.de(catalogoService.actualizarEtapa(id, request));
    }

    @PatchMapping("/api/etapas/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public EtapaResponse cambiarEstadoEtapa(@PathVariable String id, @Valid @RequestBody PatchEstadoActivoRequest request) {
        return EtapaResponse.de(catalogoService.cambiarActivaEtapa(id, request.activo()));
    }

    // ---------------------------------------------------------------- orígenes

    @GetMapping("/api/origenes")
    public List<OpcionResponse> origenes() {
        return catalogoService.listarOrigenesActivos().stream()
                .map(origen -> new OpcionResponse(origen.getId(), origen.getNombre()))
                .toList();
    }

    @GetMapping("/api/origenes/administracion")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public List<CatalogoSimpleResponse> origenesDeLaDistribuidora() {
        return catalogoService.listarOrigenesDeLaDistribuidora().stream().map(CatalogoSimpleResponse::deOrigen).toList();
    }

    @PostMapping("/api/origenes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse crearOrigen(@Valid @RequestBody CatalogoSimpleRequest request) {
        return CatalogoSimpleResponse.deOrigen(catalogoService.crearOrigen(request));
    }

    @PutMapping("/api/origenes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse actualizarOrigen(@PathVariable String id, @Valid @RequestBody CatalogoSimpleRequest request) {
        return CatalogoSimpleResponse.deOrigen(catalogoService.actualizarOrigen(id, request));
    }

    @PatchMapping("/api/origenes/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse cambiarEstadoOrigen(@PathVariable String id, @Valid @RequestBody PatchEstadoActivoRequest request) {
        return CatalogoSimpleResponse.deOrigen(catalogoService.cambiarActivoOrigen(id, request.activo()));
    }

    // ---------------------------------------------------------------- motivos de pérdida

    @GetMapping("/api/motivos-perdida")
    public List<OpcionResponse> motivosPerdida() {
        return catalogoService.listarMotivosActivos().stream()
                .map(motivo -> new OpcionResponse(motivo.getId(), motivo.getNombre()))
                .toList();
    }

    @GetMapping("/api/motivos-perdida/administracion")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public List<CatalogoSimpleResponse> motivosDeLaDistribuidora() {
        return catalogoService.listarMotivosDeLaDistribuidora().stream().map(CatalogoSimpleResponse::deMotivo).toList();
    }

    @PostMapping("/api/motivos-perdida")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse crearMotivo(@Valid @RequestBody CatalogoSimpleRequest request) {
        return CatalogoSimpleResponse.deMotivo(catalogoService.crearMotivo(request));
    }

    @PutMapping("/api/motivos-perdida/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse actualizarMotivo(@PathVariable String id, @Valid @RequestBody CatalogoSimpleRequest request) {
        return CatalogoSimpleResponse.deMotivo(catalogoService.actualizarMotivo(id, request));
    }

    @PatchMapping("/api/motivos-perdida/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse cambiarEstadoMotivo(@PathVariable String id, @Valid @RequestBody PatchEstadoActivoRequest request) {
        return CatalogoSimpleResponse.deMotivo(catalogoService.cambiarActivoMotivo(id, request.activo()));
    }

    // ---------------------------------------------------------------- tipos de actividad

    @GetMapping("/api/tipos-actividad")
    public List<OpcionResponse> tiposActividad() {
        return catalogoService.listarTiposActividadActivos().stream()
                .map(tipo -> new OpcionResponse(tipo.getId(), tipo.getNombre()))
                .toList();
    }

    @GetMapping("/api/tipos-actividad/administracion")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public List<CatalogoSimpleResponse> tiposActividadDeLaDistribuidora() {
        return catalogoService.listarTiposActividadDeLaDistribuidora().stream()
                .map(CatalogoSimpleResponse::deTipoActividad).toList();
    }

    @PostMapping("/api/tipos-actividad")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse crearTipoActividad(@Valid @RequestBody CatalogoSimpleRequest request) {
        return CatalogoSimpleResponse.deTipoActividad(catalogoService.crearTipoActividad(request));
    }

    @PutMapping("/api/tipos-actividad/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse actualizarTipoActividad(@PathVariable String id, @Valid @RequestBody CatalogoSimpleRequest request) {
        return CatalogoSimpleResponse.deTipoActividad(catalogoService.actualizarTipoActividad(id, request));
    }

    @PatchMapping("/api/tipos-actividad/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO')")
    public CatalogoSimpleResponse cambiarEstadoTipoActividad(@PathVariable String id, @Valid @RequestBody PatchEstadoActivoRequest request) {
        return CatalogoSimpleResponse.deTipoActividad(catalogoService.cambiarActivoTipoActividad(id, request.activo()));
    }
}
