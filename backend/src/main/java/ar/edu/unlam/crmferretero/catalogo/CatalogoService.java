package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.TenantContext;
import ar.edu.unlam.crmferretero.shared.exception.BusinessRuleException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;

/**
 * Etapas, orígenes, motivos de pérdida y tipos de actividad viven en colecciones de Mongo (no
 * como enums), scoped por distribuidoraId, con ABM completo para ADMIN_COMERCIO. Al dar de alta
 * una distribuidora nueva se le precarga un set inicial por default (precargarDefaults), editable
 * después.
 */
@Service
public class CatalogoService {

    private final EtapaRepository etapaRepository;
    private final OrigenRepository origenRepository;
    private final MotivoPerdidaRepository motivoPerdidaRepository;
    private final TipoActividadRepository tipoActividadRepository;

    public CatalogoService(EtapaRepository etapaRepository, OrigenRepository origenRepository,
                            MotivoPerdidaRepository motivoPerdidaRepository,
                            TipoActividadRepository tipoActividadRepository) {
        this.etapaRepository = etapaRepository;
        this.origenRepository = origenRepository;
        this.motivoPerdidaRepository = motivoPerdidaRepository;
        this.tipoActividadRepository = tipoActividadRepository;
    }

    // ---------------------------------------------------------------- lectura (scoped)

    public List<Etapa> listarEtapasActivas() {
        return etapaRepository.findByDistribuidoraIdAndActivaTrueOrderByOrdenAsc(distribuidoraIdActual());
    }

    public List<Origen> listarOrigenesActivos() {
        return origenRepository.findByDistribuidoraIdAndActivoTrueOrderByOrdenAsc(distribuidoraIdActual());
    }

    public List<MotivoPerdida> listarMotivosActivos() {
        return motivoPerdidaRepository.findByDistribuidoraIdAndActivoTrueOrderByOrdenAsc(distribuidoraIdActual());
    }

    public List<TipoActividad> listarTiposActividadActivos() {
        return tipoActividadRepository.findByDistribuidoraIdAndActivoTrueOrderByOrdenAsc(distribuidoraIdActual());
    }

    /** Para la pantalla de Configuración: incluye los inactivos. */
    public List<Etapa> listarEtapasDeLaDistribuidora() {
        return etapaRepository.findByDistribuidoraIdOrderByOrdenAsc(distribuidoraIdActual());
    }

    public List<Origen> listarOrigenesDeLaDistribuidora() {
        return origenRepository.findByDistribuidoraIdOrderByOrdenAsc(distribuidoraIdActual());
    }

    public List<MotivoPerdida> listarMotivosDeLaDistribuidora() {
        return motivoPerdidaRepository.findByDistribuidoraIdOrderByOrdenAsc(distribuidoraIdActual());
    }

    public List<TipoActividad> listarTiposActividadDeLaDistribuidora() {
        return tipoActividadRepository.findByDistribuidoraIdOrderByOrdenAsc(distribuidoraIdActual());
    }

    public Etapa obtenerEtapaPorId(String id) {
        Etapa etapa = etapaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Etapa no encontrada (id " + id + ")"));
        verificarPertenece(etapa.getDistribuidoraId());
        return etapa;
    }

    /** Etapa donde arranca toda oportunidad que no indica una etapa explícita al crearse. */
    public Etapa primeraEtapaAbierta() {
        return listarEtapasActivas().stream()
                .filter(etapa -> etapa.getTipo() == TipoEtapa.ABIERTA)
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException(
                        "No hay ninguna etapa abierta configurada. Contactá al administrador de tu distribuidora."));
    }

    // ---------------------------------------------------------------- ABM (ADMIN_COMERCIO)

    public Etapa crearEtapa(EtapaRequest request) {
        Etapa etapa = new Etapa(ConsultaUtils.normalizar(request.nombre()), ConsultaUtils.normalizar(request.descripcion()),
                request.orden(), request.tipo(), ConsultaUtils.normalizar(request.color()), true, distribuidoraIdActual());
        return etapaRepository.save(etapa);
    }

    public Etapa actualizarEtapa(String id, EtapaRequest request) {
        Etapa etapa = obtenerEtapaPorId(id);
        etapa.setNombre(ConsultaUtils.normalizar(request.nombre()));
        etapa.setDescripcion(ConsultaUtils.normalizar(request.descripcion()));
        etapa.setOrden(request.orden());
        etapa.setTipo(request.tipo());
        etapa.setColor(ConsultaUtils.normalizar(request.color()));
        return etapaRepository.save(etapa);
    }

    public Etapa cambiarActivaEtapa(String id, boolean activa) {
        Etapa etapa = obtenerEtapaPorId(id);
        etapa.setActiva(activa);
        return etapaRepository.save(etapa);
    }

    public Origen crearOrigen(CatalogoSimpleRequest request) {
        Origen origen = new Origen(ConsultaUtils.normalizar(request.nombre()), request.orden(), true, distribuidoraIdActual());
        return origenRepository.save(origen);
    }

    public Origen actualizarOrigen(String id, CatalogoSimpleRequest request) {
        Origen origen = origenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Origen no encontrado (id " + id + ")"));
        verificarPertenece(origen.getDistribuidoraId());
        origen.setNombre(ConsultaUtils.normalizar(request.nombre()));
        origen.setOrden(request.orden());
        return origenRepository.save(origen);
    }

    public Origen cambiarActivoOrigen(String id, boolean activo) {
        Origen origen = origenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Origen no encontrado (id " + id + ")"));
        verificarPertenece(origen.getDistribuidoraId());
        origen.setActivo(activo);
        return origenRepository.save(origen);
    }

    public MotivoPerdida crearMotivo(CatalogoSimpleRequest request) {
        MotivoPerdida motivo = new MotivoPerdida(ConsultaUtils.normalizar(request.nombre()), request.orden(), true, distribuidoraIdActual());
        return motivoPerdidaRepository.save(motivo);
    }

    public MotivoPerdida actualizarMotivo(String id, CatalogoSimpleRequest request) {
        MotivoPerdida motivo = motivoPerdidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Motivo de pérdida no encontrado (id " + id + ")"));
        verificarPertenece(motivo.getDistribuidoraId());
        motivo.setNombre(ConsultaUtils.normalizar(request.nombre()));
        motivo.setOrden(request.orden());
        return motivoPerdidaRepository.save(motivo);
    }

    public MotivoPerdida cambiarActivoMotivo(String id, boolean activo) {
        MotivoPerdida motivo = motivoPerdidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Motivo de pérdida no encontrado (id " + id + ")"));
        verificarPertenece(motivo.getDistribuidoraId());
        motivo.setActivo(activo);
        return motivoPerdidaRepository.save(motivo);
    }

    public TipoActividad crearTipoActividad(CatalogoSimpleRequest request) {
        TipoActividad tipo = new TipoActividad(ConsultaUtils.normalizar(request.nombre()), request.orden(), true, distribuidoraIdActual());
        return tipoActividadRepository.save(tipo);
    }

    public TipoActividad actualizarTipoActividad(String id, CatalogoSimpleRequest request) {
        TipoActividad tipo = obtenerTipoActividadPorId(id);
        tipo.setNombre(ConsultaUtils.normalizar(request.nombre()));
        tipo.setOrden(request.orden());
        return tipoActividadRepository.save(tipo);
    }

    public TipoActividad cambiarActivoTipoActividad(String id, boolean activo) {
        TipoActividad tipo = obtenerTipoActividadPorId(id);
        tipo.setActivo(activo);
        return tipoActividadRepository.save(tipo);
    }

    public TipoActividad obtenerTipoActividadPorId(String id) {
        TipoActividad tipo = tipoActividadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de actividad no encontrado (id " + id + ")"));
        verificarPertenece(tipo.getDistribuidoraId());
        return tipo;
    }

    // ---------------------------------------------------------------- alta de distribuidora

    /**
     * Precarga el set inicial de catálogos de una distribuidora nueva (los mismos valores que
     * usaba el SeedRunner de la primera entrega), editable después por su ADMIN_COMERCIO.
     */
    public void precargarDefaults(String distribuidoraId) {
        etapaRepository.saveAll(List.of(
                new Etapa("Consulta recibida", "Entró un pedido de cotización por WhatsApp, teléfono o mostrador",
                        1, TipoEtapa.ABIERTA, "#94A3B8", true, distribuidoraId),
                new Etapa("Cotización enviada", "Se mandó la lista de materiales con precios y plazo de entrega",
                        2, TipoEtapa.ABIERTA, "#64748B", true, distribuidoraId),
                new Etapa("Negociación", "Descuento por volumen, condición de pago, flete, alta de cuenta corriente",
                        3, TipoEtapa.ABIERTA, "#0E5C8A", true, distribuidoraId),
                new Etapa("Pedido confirmado", "El comercio confirmó y el pedido pasa a logística",
                        4, TipoEtapa.GANADA, "#15803D", true, distribuidoraId),
                new Etapa("Perdida", "No se concretó, con motivo registrado",
                        5, TipoEtapa.PERDIDA, "#C2410C", true, distribuidoraId)
        ));

        origenRepository.saveAll(List.of(
                new Origen("Referido de otro comercio", 1, true, distribuidoraId),
                new Origen("Prospección en ruta", 2, true, distribuidoraId),
                new Origen("WhatsApp Business", 3, true, distribuidoraId),
                new Origen("Instagram o Facebook", 4, true, distribuidoraId),
                new Origen("Google", 5, true, distribuidoraId),
                new Origen("Cámara de comercio o feria", 6, true, distribuidoraId),
                new Origen("Cliente existente (recompra)", 7, true, distribuidoraId),
                new Origen("Mostrador", 8, true, distribuidoraId)
        ));

        motivoPerdidaRepository.saveAll(List.of(
                new MotivoPerdida("Precio de la competencia", 1, true, distribuidoraId),
                new MotivoPerdida("Plazo de entrega largo", 2, true, distribuidoraId),
                new MotivoPerdida("No acepta las condiciones de pago", 3, true, distribuidoraId),
                new MotivoPerdida("No califica para cuenta corriente", 4, true, distribuidoraId),
                new MotivoPerdida("No alcanza la compra mínima", 5, true, distribuidoraId),
                new MotivoPerdida("Zona fuera de reparto", 6, true, distribuidoraId),
                new MotivoPerdida("Compró directo al fabricante", 7, true, distribuidoraId),
                new MotivoPerdida("Sin respuesta del cliente", 8, true, distribuidoraId)
        ));

        tipoActividadRepository.saveAll(List.of(
                new TipoActividad("Llamada", 1, true, distribuidoraId),
                new TipoActividad("Visita a obra/depósito", 2, true, distribuidoraId),
                new TipoActividad("WhatsApp", 3, true, distribuidoraId),
                new TipoActividad("Email", 4, true, distribuidoraId),
                new TipoActividad("Reunión", 5, true, distribuidoraId)
        ));
    }

    // ---------------------------------------------------------------- helpers

    private String distribuidoraIdActual() {
        String id = TenantContext.distribuidoraId();
        if (id == null) {
            throw new BusinessRuleException("Elegí una distribuidora para ver o editar sus catálogos.");
        }
        return id;
    }

    private void verificarPertenece(String distribuidoraIdEntidad) {
        if (!TenantContext.esAdmin() && !java.util.Objects.equals(distribuidoraIdEntidad, TenantContext.distribuidoraId())) {
            throw new NotFoundException("Recurso no encontrado");
        }
    }
}
