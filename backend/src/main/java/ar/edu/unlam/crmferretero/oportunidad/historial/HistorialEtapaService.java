package ar.edu.unlam.crmferretero.oportunidad.historial;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/** Cada cambio de etapa se escribe acá, incluido el registro inicial al crear la oportunidad. */
@Service
public class HistorialEtapaService {

    private final HistorialEtapaRepository historialEtapaRepository;

    public HistorialEtapaService(HistorialEtapaRepository historialEtapaRepository) {
        this.historialEtapaRepository = historialEtapaRepository;
    }

    public void registrar(String oportunidadId, String etapaAnteriorId, String etapaAnteriorNombre,
                           String etapaNuevaId, String etapaNuevaNombre, String observacion) {
        HistorialEtapa historial = new HistorialEtapa(
                oportunidadId, etapaAnteriorId, etapaAnteriorNombre, etapaNuevaId, etapaNuevaNombre,
                Instant.now(), usuarioActual(), observacion);
        historialEtapaRepository.save(historial);
    }

    public List<HistorialEtapaResponse> listarPorOportunidad(String oportunidadId) {
        return historialEtapaRepository.findByOportunidadIdOrderByFechaHoraDesc(oportunidadId).stream()
                .map(HistorialEtapaResponse::de)
                .toList();
    }

    private String usuarioActual() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !autenticacion.isAuthenticated()
                || "anonymousUser".equals(autenticacion.getPrincipal())) {
            return "sistema";
        }
        return autenticacion.getName();
    }
}
