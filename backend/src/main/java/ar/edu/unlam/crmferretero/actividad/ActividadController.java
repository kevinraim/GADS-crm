package ar.edu.unlam.crmferretero.actividad;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public List<ActividadResponse> listar(@RequestParam(required = false) String empresaId,
                                           @RequestParam(required = false) String contactoId,
                                           @RequestParam(required = false) String oportunidadId) {
        return actividadService.listar(empresaId, contactoId, oportunidadId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_COMERCIO','VENDEDOR','RESPONSABLE_COMERCIAL')")
    public ActividadResponse crear(@Valid @RequestBody ActividadRequest request) {
        return actividadService.crear(request);
    }
}
