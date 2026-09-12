package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.shared.exception.BusinessRuleException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;

/**
 * Etapas, orígenes y motivos de pérdida viven en colecciones de Mongo (no como enums) para que la
 * entrega 2 solo tenga que agregar las pantallas de configuración, sin tocar el modelo.
 */
@Service
public class CatalogoService {

    private final EtapaRepository etapaRepository;
    private final OrigenRepository origenRepository;
    private final MotivoPerdidaRepository motivoPerdidaRepository;

    public CatalogoService(EtapaRepository etapaRepository, OrigenRepository origenRepository,
                            MotivoPerdidaRepository motivoPerdidaRepository) {
        this.etapaRepository = etapaRepository;
        this.origenRepository = origenRepository;
        this.motivoPerdidaRepository = motivoPerdidaRepository;
    }

    public List<Etapa> listarEtapasActivas() {
        return etapaRepository.findByActivaTrueOrderByOrdenAsc();
    }

    public List<Origen> listarOrigenesActivos() {
        return origenRepository.findByActivoTrueOrderByOrdenAsc();
    }

    public List<MotivoPerdida> listarMotivosActivos() {
        return motivoPerdidaRepository.findByActivoTrueOrderByOrdenAsc();
    }

    public Etapa obtenerEtapaPorId(String id) {
        return etapaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Etapa no encontrada (id " + id + ")"));
    }

    /** Etapa donde arranca toda oportunidad que no indica una etapa explícita al crearse. */
    public Etapa primeraEtapaAbierta() {
        return listarEtapasActivas().stream()
                .filter(etapa -> etapa.getTipo() == TipoEtapa.ABIERTA)
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException(
                        "No hay ninguna etapa abierta configurada. Contactá al administrador del sistema."));
    }
}
