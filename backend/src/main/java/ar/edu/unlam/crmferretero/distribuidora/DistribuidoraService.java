package ar.edu.unlam.crmferretero.distribuidora;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.catalogo.CatalogoService;
import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.exception.BusinessRuleException;
import ar.edu.unlam.crmferretero.shared.exception.DuplicateResourceException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;
import ar.edu.unlam.crmferretero.usuario.Rol;
import ar.edu.unlam.crmferretero.usuario.UsuarioRequest;
import ar.edu.unlam.crmferretero.usuario.UsuarioService;

@Service
public class DistribuidoraService {

    private final DistribuidoraRepository distribuidoraRepository;
    private final UsuarioService usuarioService;
    private final CatalogoService catalogoService;

    public DistribuidoraService(DistribuidoraRepository distribuidoraRepository, UsuarioService usuarioService,
                                 CatalogoService catalogoService) {
        this.distribuidoraRepository = distribuidoraRepository;
        this.usuarioService = usuarioService;
        this.catalogoService = catalogoService;
    }

    public List<DistribuidoraResponse> listar() {
        return distribuidoraRepository.findAll().stream().map(DistribuidoraResponse::de).toList();
    }

    public DistribuidoraResponse obtenerPorId(String id) {
        return DistribuidoraResponse.de(buscarPorId(id));
    }

    /**
     * Alta de una distribuidora nueva junto con su primer ADMIN_COMERCIO y el set inicial de
     * catálogos (etapas, orígenes, motivos de pérdida, tipos de actividad). No hay autoregistro
     * público: esto lo hace el ADMIN (superadmin) a mano.
     */
    public DistribuidoraResponse crearConAdmin(DistribuidoraRequest request) {
        if (request.admin() == null) {
            throw new BusinessRuleException("Tenés que cargar los datos del primer administrador de la distribuidora");
        }
        validarCuitUnico(request.cuit(), null);

        Distribuidora distribuidora = new Distribuidora();
        aplicarRequest(distribuidora, request);
        Distribuidora guardada = distribuidoraRepository.save(distribuidora);

        usuarioService.crear(
                new UsuarioRequest(request.admin().nombre(), request.admin().apellido(),
                        request.admin().email(), request.admin().password(), Rol.ADMIN_COMERCIO),
                guardada.getId());

        catalogoService.precargarDefaults(guardada.getId());

        return DistribuidoraResponse.de(guardada);
    }

    public DistribuidoraResponse actualizar(String id, DistribuidoraRequest request) {
        Distribuidora distribuidora = buscarPorId(id);
        validarCuitUnico(request.cuit(), id);
        aplicarRequest(distribuidora, request);
        return DistribuidoraResponse.de(distribuidoraRepository.save(distribuidora));
    }

    public DistribuidoraResponse cambiarEstado(String id, boolean activa) {
        Distribuidora distribuidora = buscarPorId(id);
        distribuidora.setActiva(activa);
        return DistribuidoraResponse.de(distribuidoraRepository.save(distribuidora));
    }

    private void aplicarRequest(Distribuidora distribuidora, DistribuidoraRequest request) {
        distribuidora.setRazonSocial(ConsultaUtils.normalizar(request.razonSocial()));
        distribuidora.setNombreFantasia(ConsultaUtils.normalizar(request.nombreFantasia()));
        distribuidora.setCuit(ConsultaUtils.normalizar(request.cuit()));
        distribuidora.setEmail(ConsultaUtils.normalizar(request.email()));
        distribuidora.setTelefono(ConsultaUtils.normalizar(request.telefono()));
    }

    private void validarCuitUnico(String cuit, String idPropio) {
        String cuitNormalizado = ConsultaUtils.normalizar(cuit);
        if (cuitNormalizado == null) {
            return;
        }
        distribuidoraRepository.findByCuit(cuitNormalizado).ifPresent(existente -> {
            if (idPropio == null || !existente.getId().equals(idPropio)) {
                throw new DuplicateResourceException(
                        "Ya existe una distribuidora con el CUIT " + cuitNormalizado + " (" + existente.nombreVisible() + ")");
            }
        });
    }

    private Distribuidora buscarPorId(String id) {
        return distribuidoraRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Distribuidora no encontrada (id " + id + ")"));
    }
}
