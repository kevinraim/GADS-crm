package ar.edu.unlam.crmferretero.metrica;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricasController {

    private final MetricasService metricasService;

    public MetricasController(MetricasService metricasService) {
        this.metricasService = metricasService;
    }

    @GetMapping("/api/metricas")
    public MetricasResponse metricas(@RequestParam(required = false) String distribuidoraId,
                                      @RequestParam(required = false) Integer dias) {
        return metricasService.calcular(distribuidoraId, dias);
    }
}
