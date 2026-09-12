package ar.edu.unlam.crmferretero.actividad;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ar.edu.unlam.crmferretero.catalogo.TipoActividad;
import ar.edu.unlam.crmferretero.catalogo.TipoActividadRepository;
import ar.edu.unlam.crmferretero.shared.AlcanceUtils;
import ar.edu.unlam.crmferretero.shared.ConsultaUtils;
import ar.edu.unlam.crmferretero.shared.TenantContext;
import ar.edu.unlam.crmferretero.shared.exception.BusinessRuleException;
import ar.edu.unlam.crmferretero.usuario.Usuario;
import ar.edu.unlam.crmferretero.usuario.UsuarioRepository;

/**
 * Historial comercial: llamadas, visitas, WhatsApp, etc. asociados a un comercio, contacto u
 * oportunidad. El acceso al listado ya queda acotado porque siempre se pide con el id de un
 * comercio/contacto/oportunidad puntual, al que el usuario ya tuvo que poder acceder antes
 * (su propio obtenerPorId ya aplica tenant + visibilidad fina); acá se agrega igual el filtro por
 * distribuidora como defensa en profundidad.
 */
@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final MongoTemplate mongoTemplate;
    private final TipoActividadRepository tipoActividadRepository;
    private final UsuarioRepository usuarioRepository;

    public ActividadService(ActividadRepository actividadRepository, MongoTemplate mongoTemplate,
                             TipoActividadRepository tipoActividadRepository, UsuarioRepository usuarioRepository) {
        this.actividadRepository = actividadRepository;
        this.mongoTemplate = mongoTemplate;
        this.tipoActividadRepository = tipoActividadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ActividadResponse> listar(String empresaId, String contactoId, String oportunidadId) {
        List<Criteria> condiciones = new ArrayList<>();
        AlcanceUtils.porDistribuidora(condiciones, null);

        List<Criteria> relacion = new ArrayList<>();
        String empresaNormalizada = ConsultaUtils.normalizar(empresaId);
        if (empresaNormalizada != null) {
            relacion.add(Criteria.where("empresaId").is(empresaNormalizada));
        }
        String contactoNormalizado = ConsultaUtils.normalizar(contactoId);
        if (contactoNormalizado != null) {
            relacion.add(Criteria.where("contactoId").is(contactoNormalizado));
        }
        String oportunidadNormalizada = ConsultaUtils.normalizar(oportunidadId);
        if (oportunidadNormalizada != null) {
            relacion.add(Criteria.where("oportunidadId").is(oportunidadNormalizada));
        }
        if (relacion.isEmpty()) {
            throw new BusinessRuleException("Indicá un comercio, contacto u oportunidad para ver su historial");
        }
        condiciones.add(new Criteria().orOperator(relacion.toArray(new Criteria[0])));

        Criteria criteria = new Criteria().andOperator(condiciones.toArray(new Criteria[0]));
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, "fecha"));
        List<Actividad> actividades = mongoTemplate.find(query, Actividad.class);

        return mapearConNombres(actividades);
    }

    public ActividadResponse crear(ActividadRequest request) {
        if (ConsultaUtils.normalizar(request.empresaId()) == null
                && ConsultaUtils.normalizar(request.contactoId()) == null
                && ConsultaUtils.normalizar(request.oportunidadId()) == null) {
            throw new BusinessRuleException(
                    "La actividad tiene que estar asociada a un comercio, un contacto o una oportunidad");
        }

        Actividad actividad = new Actividad();
        actividad.setTipoActividadId(ConsultaUtils.normalizar(request.tipoActividadId()));
        actividad.setFecha(request.fecha() != null ? request.fecha() : Instant.now());
        actividad.setDescripcion(ConsultaUtils.normalizar(request.descripcion()));
        actividad.setEmpresaId(ConsultaUtils.normalizar(request.empresaId()));
        actividad.setContactoId(ConsultaUtils.normalizar(request.contactoId()));
        actividad.setOportunidadId(ConsultaUtils.normalizar(request.oportunidadId()));
        actividad.setUsuarioId(TenantContext.usuarioId());
        actividad.setDistribuidoraId(AlcanceUtils.distribuidoraIdParaAlta());

        Actividad guardada = actividadRepository.save(actividad);
        return mapearConNombres(List.of(guardada)).get(0);
    }

    private List<ActividadResponse> mapearConNombres(List<Actividad> actividades) {
        Set<String> idsTipos = actividades.stream().map(Actividad::getTipoActividadId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<String> idsUsuarios = actividades.stream().map(Actividad::getUsuarioId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());

        Map<String, String> nombresTipos = tipoActividadRepository.findAllById(idsTipos).stream()
                .collect(Collectors.toMap(TipoActividad::getId, TipoActividad::getNombre));
        Map<String, String> nombresUsuarios = usuarioRepository.findAllById(idsUsuarios).stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombreCompleto));

        return actividades.stream()
                .map(actividad -> new ActividadResponse(
                        actividad.getId(),
                        actividad.getTipoActividadId(),
                        nombresTipos.get(actividad.getTipoActividadId()),
                        actividad.getFecha(),
                        actividad.getDescripcion(),
                        actividad.getUsuarioId(),
                        nombresUsuarios.get(actividad.getUsuarioId()),
                        actividad.getEmpresaId(),
                        actividad.getContactoId(),
                        actividad.getOportunidadId(),
                        actividad.getCreadoEn()))
                .toList();
    }
}
