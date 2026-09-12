package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlam.crmferretero.shared.OpcionResponse;

@RestController
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/api/etapas")
    public List<EtapaResponse> etapas() {
        return catalogoService.listarEtapasActivas().stream()
                .map(EtapaResponse::de)
                .toList();
    }

    @GetMapping("/api/origenes")
    public List<OpcionResponse> origenes() {
        return catalogoService.listarOrigenesActivos().stream()
                .map(origen -> new OpcionResponse(origen.getId(), origen.getNombre()))
                .toList();
    }

    @GetMapping("/api/motivos-perdida")
    public List<OpcionResponse> motivosPerdida() {
        return catalogoService.listarMotivosActivos().stream()
                .map(motivo -> new OpcionResponse(motivo.getId(), motivo.getNombre()))
                .toList();
    }
}
