package ar.edu.unlam.crmferretero.metrica;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.actividad.Actividad;
import ar.edu.unlam.crmferretero.catalogo.CatalogoService;
import ar.edu.unlam.crmferretero.catalogo.Etapa;
import ar.edu.unlam.crmferretero.catalogo.MotivoPerdida;
import ar.edu.unlam.crmferretero.catalogo.MotivoPerdidaRepository;
import ar.edu.unlam.crmferretero.empresa.Empresa;
import ar.edu.unlam.crmferretero.oportunidad.EstadoOportunidad;
import ar.edu.unlam.crmferretero.oportunidad.Oportunidad;
import ar.edu.unlam.crmferretero.shared.AlcanceUtils;
import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.TenantContext;
import ar.edu.unlam.crmferretero.usuario.Usuario;
import ar.edu.unlam.crmferretero.usuario.UsuarioRepository;

/**
 * Todas las métricas se calculan sobre el mismo alcance (distribuidora + visibilidad fina) que el
 * resto de la app: ver AlcanceUtils. Si es ADMIN y no eligió una distribuidora puntual, las
 * métricas por etapa se agrupan por estado (ABIERTA/GANADA/PERDIDA) en vez de por etapa
 * configurada, igual que el embudo: cada distribuidora tiene sus propias etapas.
 */
@Service
public class MetricasService {

    private static final int DIAS_INACTIVIDAD_DEFAULT = 30;
    private static final int MESES_TENDENCIA = 6;
    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("yyyy-MM");

    private final MongoTemplate mongoTemplate;
    private final MotivoPerdidaRepository motivoPerdidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CatalogoService catalogoService;

    public MetricasService(MongoTemplate mongoTemplate, MotivoPerdidaRepository motivoPerdidaRepository,
                            UsuarioRepository usuarioRepository, CatalogoService catalogoService) {
        this.mongoTemplate = mongoTemplate;
        this.motivoPerdidaRepository = motivoPerdidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.catalogoService = catalogoService;
    }

    public MetricasResponse calcular(String distribuidoraId, Integer dias) {
        int diasInactividad = (dias == null || dias <= 0) ? DIAS_INACTIVIDAD_DEFAULT : dias;
        boolean agregadoSinDistribuidora = TenantContext.esAdmin() && ConsultaUtils.normalizar(distribuidoraId) == null;

        List<Oportunidad> oportunidades = buscarOportunidadesScoped(distribuidoraId);
        List<Empresa> empresas = buscarEmpresasScoped(distribuidoraId);

        Set<String> idsResponsables = oportunidades.stream().map(Oportunidad::getResponsableComercialId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> nombresResponsables = usuarioRepository.findAllById(idsResponsables).stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombreCompleto));

        return new MetricasResponse(
                oportunidadesPorEtapa(oportunidades, agregadoSinDistribuidora),
                tasaConversion(oportunidades, nombresResponsables),
                pipelineAbierto(oportunidades),
                rankingResponsables(oportunidades, nombresResponsables),
                motivosPerdidaFrecuentes(oportunidades),
                comerciosSinActividadReciente(empresas, oportunidades, diasInactividad),
                comerciosNuevosPorMes(empresas)
        );
    }

    // ---------------------------------------------------------------- 1. oportunidades por etapa

    private List<MetricaEtapaResponse> oportunidadesPorEtapa(List<Oportunidad> oportunidades, boolean agregado) {
        if (agregado) {
            Map<EstadoOportunidad, List<Oportunidad>> porEstado = oportunidades.stream()
                    .collect(Collectors.groupingBy(Oportunidad::getEstado));
            return porEstado.entrySet().stream()
                    .map(entrada -> new MetricaEtapaResponse(null, etiquetaEstado(entrada.getKey()),
                            entrada.getValue().size(), sumarValor(entrada.getValue())))
                    .toList();
        }

        Map<String, List<Oportunidad>> porEtapa = oportunidades.stream()
                .collect(Collectors.groupingBy(Oportunidad::getEtapaActualId));
        List<Etapa> etapas = catalogoService.listarEtapasActivas();
        return etapas.stream()
                .map(etapa -> {
                    List<Oportunidad> deLaEtapa = porEtapa.getOrDefault(etapa.getId(), List.of());
                    return new MetricaEtapaResponse(etapa.getId(), etapa.getNombre(), deLaEtapa.size(), sumarValor(deLaEtapa));
                })
                .toList();
    }

    private String etiquetaEstado(EstadoOportunidad estado) {
        return switch (estado) {
            case ABIERTA -> "Abiertas";
            case GANADA -> "Ganadas";
            case PERDIDA -> "Perdidas";
        };
    }

    // ---------------------------------------------------------------- 2. tasa de conversión

    private TasaConversionResponse tasaConversion(List<Oportunidad> oportunidades, Map<String, String> nombresResponsables) {
        long ganadas = oportunidades.stream().filter(o -> o.getEstado() == EstadoOportunidad.GANADA).count();
        long perdidas = oportunidades.stream().filter(o -> o.getEstado() == EstadoOportunidad.PERDIDA).count();
        double tasaGlobal = (ganadas + perdidas) == 0 ? 0 : (double) ganadas / (ganadas + perdidas);

        Map<String, List<Oportunidad>> porResponsable = oportunidades.stream()
                .filter(o -> o.getEstado() == EstadoOportunidad.GANADA || o.getEstado() == EstadoOportunidad.PERDIDA)
                .filter(o -> o.getResponsableComercialId() != null)
                .collect(Collectors.groupingBy(Oportunidad::getResponsableComercialId));

        List<TasaConversionResponsableResponse> detalle = porResponsable.entrySet().stream()
                .map(entrada -> {
                    long g = entrada.getValue().stream().filter(o -> o.getEstado() == EstadoOportunidad.GANADA).count();
                    long p = entrada.getValue().stream().filter(o -> o.getEstado() == EstadoOportunidad.PERDIDA).count();
                    double tasa = (g + p) == 0 ? 0 : (double) g / (g + p);
                    return new TasaConversionResponsableResponse(entrada.getKey(), nombresResponsables.get(entrada.getKey()), g, p, tasa);
                })
                .sorted(Comparator.comparingDouble(TasaConversionResponsableResponse::tasa).reversed())
                .toList();

        return new TasaConversionResponse(ganadas, perdidas, tasaGlobal, detalle);
    }

    // ---------------------------------------------------------------- 3. pipeline abierto

    private BigDecimal pipelineAbierto(List<Oportunidad> oportunidades) {
        return sumarValor(oportunidades.stream().filter(o -> o.getEstado() == EstadoOportunidad.ABIERTA).toList());
    }

    // ---------------------------------------------------------------- 4. ranking de responsables

    private List<RankingResponsableResponse> rankingResponsables(List<Oportunidad> oportunidades, Map<String, String> nombresResponsables) {
        Map<String, List<Oportunidad>> ganadasPorResponsable = oportunidades.stream()
                .filter(o -> o.getEstado() == EstadoOportunidad.GANADA && o.getResponsableComercialId() != null)
                .collect(Collectors.groupingBy(Oportunidad::getResponsableComercialId));

        return ganadasPorResponsable.entrySet().stream()
                .map(entrada -> new RankingResponsableResponse(entrada.getKey(), nombresResponsables.get(entrada.getKey()),
                        entrada.getValue().size(), sumarValor(entrada.getValue())))
                .sorted(Comparator.comparing(RankingResponsableResponse::valorGanado).reversed())
                .limit(10)
                .toList();
    }

    // ---------------------------------------------------------------- 5. motivos de pérdida más frecuentes

    private List<MotivoFrecuenteResponse> motivosPerdidaFrecuentes(List<Oportunidad> oportunidades) {
        Map<String, Long> conteoPorMotivo = oportunidades.stream()
                .filter(o -> o.getEstado() == EstadoOportunidad.PERDIDA && o.getMotivoPerdidaId() != null)
                .collect(Collectors.groupingBy(Oportunidad::getMotivoPerdidaId, Collectors.counting()));

        Map<String, String> nombresMotivos = motivoPerdidaRepository.findAllById(conteoPorMotivo.keySet()).stream()
                .collect(Collectors.toMap(MotivoPerdida::getId, MotivoPerdida::getNombre));

        return conteoPorMotivo.entrySet().stream()
                .map(entrada -> new MotivoFrecuenteResponse(entrada.getKey(), nombresMotivos.get(entrada.getKey()), entrada.getValue()))
                .sorted(Comparator.comparingLong(MotivoFrecuenteResponse::cantidad).reversed())
                .limit(5)
                .toList();
    }

    // ---------------------------------------------------------------- 6. comercios sin actividad reciente

    private List<ComercioInactivoResponse> comerciosSinActividadReciente(List<Empresa> empresas, List<Oportunidad> oportunidades,
                                                                           int diasInactividad) {
        Instant corte = Instant.now().minus(java.time.Duration.ofDays(diasInactividad));

        List<Criteria> condicionesActividad = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condicionesActividad, null);
        List<Actividad> actividades = mongoTemplate.find(
                new Query(new Criteria().andOperator(condicionesActividad.toArray(new Criteria[0]))), Actividad.class);

        Map<String, Instant> ultimaActividadPorEmpresa = new java.util.HashMap<>();
        for (Oportunidad oportunidad : oportunidades) {
            if (oportunidad.getEmpresaId() == null) continue;
            Instant fecha = oportunidad.getCreadoEn();
            ultimaActividadPorEmpresa.merge(oportunidad.getEmpresaId(), fecha, (a, b) -> a.isAfter(b) ? a : b);
        }
        for (Actividad actividad : actividades) {
            if (actividad.getEmpresaId() == null) continue;
            Instant fecha = actividad.getFecha() != null ? actividad.getFecha() : actividad.getCreadoEn();
            if (fecha == null) continue;
            ultimaActividadPorEmpresa.merge(actividad.getEmpresaId(), fecha, (a, b) -> a.isAfter(b) ? a : b);
        }

        return empresas.stream()
                .filter(empresa -> {
                    Instant ultima = ultimaActividadPorEmpresa.get(empresa.getId());
                    return ultima == null || ultima.isBefore(corte);
                })
                .map(empresa -> new ComercioInactivoResponse(empresa.getId(), empresa.nombreVisible(),
                        ultimaActividadPorEmpresa.get(empresa.getId())))
                .sorted(Comparator.comparing(ComercioInactivoResponse::ultimaActividad,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .toList();
    }

    // ---------------------------------------------------------------- 7. comercios nuevos por mes

    private List<ComercioPorMesResponse> comerciosNuevosPorMes(List<Empresa> empresas) {
        LocalDate hoy = LocalDate.now(ZoneOffset.UTC);
        List<String> meses = new ArrayList<>();
        for (int i = MESES_TENDENCIA - 1; i >= 0; i--) {
            meses.add(hoy.minusMonths(i).format(FORMATO_MES));
        }

        Map<String, Long> conteoPorMes = empresas.stream()
                .filter(empresa -> empresa.getCreadoEn() != null)
                .collect(Collectors.groupingBy(
                        empresa -> FORMATO_MES.format(empresa.getCreadoEn().atZone(ZoneOffset.UTC)),
                        Collectors.counting()));

        return meses.stream()
                .map(mes -> new ComercioPorMesResponse(mes, conteoPorMes.getOrDefault(mes, 0L)))
                .toList();
    }

    // ---------------------------------------------------------------- helpers de alcance

    private List<Oportunidad> buscarOportunidadesScoped(String distribuidoraId) {
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, distribuidoraId);
        AlcanceUtils.porVisibilidadFina(condiciones);
        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));
        return mongoTemplate.find(new Query(criteria), Oportunidad.class);
    }

    private List<Empresa> buscarEmpresasScoped(String distribuidoraId) {
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, distribuidoraId);
        AlcanceUtils.porVisibilidadFina(condiciones);
        Criteria criteria = condiciones.isEmpty() ? new Criteria() : new Criteria().andOperator(condiciones.toArray(new Criteria[0]));
        return mongoTemplate.find(new Query(criteria), Empresa.class);
    }

    private BigDecimal sumarValor(List<Oportunidad> oportunidades) {
        return oportunidades.stream().map(Oportunidad::getValorEstimado)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
