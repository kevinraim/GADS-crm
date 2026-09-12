package ar.edu.unlam.crmferretero.distribuidora;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlam.crmferretero.shared.PatchEstadoActivoRequest;
import jakarta.validation.Valid;

/** CRUD de distribuidoras: exclusivo del ADMIN (superadmin de la plataforma). */
@RestController
@RequestMapping("/api/distribuidoras")
@PreAuthorize("hasRole('ADMIN')")
public class DistribuidoraController {

    private final DistribuidoraService distribuidoraService;

    public DistribuidoraController(DistribuidoraService distribuidoraService) {
        this.distribuidoraService = distribuidoraService;
    }

    @GetMapping
    public List<DistribuidoraResponse> listar() {
        return distribuidoraService.listar();
    }

    @GetMapping("/{id}")
    public DistribuidoraResponse obtener(@PathVariable String id) {
        return distribuidoraService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DistribuidoraResponse crear(@Valid @RequestBody DistribuidoraRequest request) {
        return distribuidoraService.crearConAdmin(request);
    }

    @PutMapping("/{id}")
    public DistribuidoraResponse actualizar(@PathVariable String id, @Valid @RequestBody DistribuidoraRequest request) {
        return distribuidoraService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public DistribuidoraResponse cambiarEstado(@PathVariable String id, @Valid @RequestBody PatchEstadoActivoRequest request) {
        return distribuidoraService.cambiarEstado(id, request.activo());
    }
}
