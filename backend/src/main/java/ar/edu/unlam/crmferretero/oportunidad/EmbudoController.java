package ar.edu.unlam.crmferretero.oportunidad;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbudoController {

    private final OportunidadService oportunidadService;

    public EmbudoController(OportunidadService oportunidadService) {
        this.oportunidadService = oportunidadService;
    }

    @GetMapping("/api/embudo")
    public EmbudoResponse embudo(@RequestParam(required = false) String responsableId,
                                  @RequestParam(required = false) String zona) {
        return oportunidadService.embudo(responsableId, zona);
    }
}
