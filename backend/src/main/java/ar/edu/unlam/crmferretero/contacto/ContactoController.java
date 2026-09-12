package ar.edu.unlam.crmferretero.contacto;

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
import ar.edu.unlam.crmferretero.shared.PageResponse;
import ar.edu.unlam.crmferretero.shared.PatchEstadoRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contactos")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public PageResponse<ContactoResponse> listar(@RequestParam(required = false) String q,
                                                  @RequestParam(required = false) String empresaId,
                                                  @RequestParam(required = false) EstadoRegistro estado,
                                                  @RequestParam(required = false) String responsableId,
                                                  @RequestParam(required = false) Integer pagina,
                                                  @RequestParam(required = false) Integer tamanio) {
        return contactoService.listar(q, empresaId, estado, responsableId, pagina, tamanio);
    }

    @GetMapping("/{id}")
    public ContactoResponse obtener(@PathVariable String id) {
        return contactoService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactoResponse crear(@Valid @RequestBody ContactoRequest request) {
        return contactoService.crear(request);
    }

    @PutMapping("/{id}")
    public ContactoResponse actualizar(@PathVariable String id, @Valid @RequestBody ContactoRequest request) {
        return contactoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public ContactoResponse cambiarEstado(@PathVariable String id, @Valid @RequestBody PatchEstadoRequest request) {
        return contactoService.cambiarEstado(id, request.estado());
    }
}
